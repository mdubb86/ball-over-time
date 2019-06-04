package com.meridian.ball.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.google.api.client.http.InputStreamContent;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.Storage.Objects.Get;
import com.google.api.services.storage.model.StorageObject;

@Service
public class CloudStorageService {
    private static boolean IS_APP_ENGINE = false;

    private final Logger logger = LoggerFactory.getLogger(CloudStorageService.class);

    public enum Bucket {
        DATABASE_BACKUP,
        PLAYER_IMAGES;

        private String bucketName() {
            return "ballovertime-" + this.name().toLowerCase().replace("_", "-");
        }
    }

    private final Storage googleCloudStorage;

    @Autowired
    public CloudStorageService(Storage storage) {
        this.googleCloudStorage = storage;
    }

    public Get download(Bucket bucket, String objectName, File destination) throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(destination));
        Get getObject = googleCloudStorage.objects().get(bucket.bucketName(), objectName);
        getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!IS_APP_ENGINE);
        getObject.executeMediaAndDownloadTo(os); 
        os.flush();
        os.close();
        return getObject;
    }

    public StorageObject upload(Bucket bucket, String mimeType, String filename, byte[] data) throws IOException {
        logger.info("Uploading {} bytes to {}/{} ({})", data.length, bucket, filename, mimeType);
        return this.uploadHelper(bucket, mimeType, filename, new ByteArrayInputStream(data));
    }
    
    public StorageObject upload(Bucket bucket, String mimeType, String filename, File file) throws FileNotFoundException, IOException {
        logger.info("Uploading {} to {}/{} ({})", file, bucket, filename, mimeType);
        return this.uploadHelper(bucket, mimeType, filename, new FileInputStream(file));
    }
    
    public Future<Boolean> uploadAsync(Bucket bucket, String mimeType, String filename, File file) {
        try {
            this.upload(bucket, mimeType, filename, file);
            return AsyncResult.forValue(true);
        } catch (IOException e) {
            return AsyncResult.forExecutionException(e);
        }
    }

    private StorageObject uploadHelper(Bucket bucket, String mimeType, String filename, InputStream inputStream) throws IOException {
        InputStreamContent mediaContent = new InputStreamContent(mimeType, inputStream);
        Storage.Objects.Insert insertObject = googleCloudStorage.objects().insert(bucket.bucketName(), null, mediaContent)
                .setName(filename).setPredefinedAcl("publicRead");
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);
        return insertObject.execute();
    }
}
