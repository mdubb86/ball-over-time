package com.meridian.ball.service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.meridian.ball.service.CloudStorageService.Bucket;

@Service
public class DatabaseBackupService {

    private final Logger logger = LoggerFactory.getLogger(DatabaseBackupService.class);
    
    private final CloudStorageService cloudStorageService;
    private final AtomicBoolean locked;

    public DatabaseBackupService(CloudStorageService cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
        this.locked = new AtomicBoolean(false);
    }
    
    public void test() throws InterruptedException, IOException {
        this.backup(Paths.get("/tmp/nba.dump.gz"));
    }

    public boolean backup(Path path) throws InterruptedException, IOException {
        if (locked.compareAndSet(false, true)) {
            logger.info("Starting database backup");
            List<String> args = Arrays.asList("/usr/bin/pg_dump", "nba", "-h", "localhost", "-U", "nba");
            ProcessBuilder pb = new ProcessBuilder(args);
            Map<String, String> env = pb.environment();
            env.put("PGPASSWORD", "nba");
            OutputStream stdoutStream = new GZIPOutputStream(new FileOutputStream(path.toFile()));
            ByteArrayOutputStream stderrStream = new ByteArrayOutputStream();
            Process p = pb.start();
            StreamCopier stdoutCopier = new StreamCopier(p.getInputStream(), stdoutStream);
            StreamCopier stderrCopier = new StreamCopier(p.getErrorStream(), stderrStream);

            Thread stdoutThread = new Thread(stdoutCopier, "stdout-copier");
            stdoutThread.setDaemon(true);
            stdoutThread.start();

            Thread stderrThread = new Thread(stderrCopier, "stderr-copier");
            stderrThread.setDaemon(true);
            stderrThread.start();

            boolean exited = p.waitFor(2, TimeUnit.MINUTES);
            if (exited) {
                stderrStream.close();
                int exitValue = p.exitValue();
                if (exitValue != 0) {
                    logger.error("Database backup process failed with exit code {}. Stderr: {}", exitValue, stderrStream.toString("UTF-8").trim());
                }
            } else {
                logger.error("Database backup process appears to be hung, forcibly terminating");
                p.destroyForcibly();
                boolean exitedAfterDestroy = p.waitFor(1, TimeUnit.MINUTES);
                if (exitedAfterDestroy) {
                    stderrStream.close();
                    logger.warn("Database backup process successfully destroyed. Stderr: {}", stderrStream.toString("UTF-8").trim());
                } else {
                    logger.error("Unable to terminate database backup process");
                }
                return false;
            }
            
            logger.info("Successfully completed pg_dump");
            stdoutStream.flush();
            stdoutStream.close();
            
            cloudStorageService.upload(Bucket.DATABASE_BACKUP, "application/gzip", "nba.dump.gz", path.toFile());

            logger.info("Done all");
            boolean unlocked = locked.compareAndSet(true, false);
            if (!unlocked) {
                logger.warn("Unable to unlock database backup");
            }

            return true;
        } else {
            logger.info("Skipping backup, unable to acquire lock");
            return true;
        }
    }

    private class StreamCopier implements Runnable {

        private final InputStream is;
        private final OutputStream os;
        private Throwable throwable;

        public StreamCopier(InputStream is, OutputStream os) {
            this.is = is;
            this.os = os;
        }

        @Override
        public void run() {
            try {
                FileCopyUtils.copy(is, os);
            } catch (Throwable t) {
                logger.error("Error copying stream", t);
                this.throwable = t;
            }
        }

        public boolean hasException() {
            return throwable != null;
        }

        public Throwable getException() {
            return throwable;
        }
    }
}
