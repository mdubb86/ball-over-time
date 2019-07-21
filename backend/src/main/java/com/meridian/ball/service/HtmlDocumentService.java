package com.meridian.ball.service;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HtmlDocumentService {
    
    private final Logger logger = LoggerFactory.getLogger(HtmlDocumentService.class);
    
    public Document get(String url) throws IOException {
        int attempts = 1;
        Throwable failureReason = null;
        while (attempts <= 5) {
            beRespectful();
            try {
                return Jsoup.connect(url).validateTLSCertificates(false).get();
            } catch (SocketException | SocketTimeoutException | HttpStatusException ex) {
                failureReason = ex;
                logger.warn("Network exception fetching {} on attempt {}", url, attempts);
                attempts++;
            }
        }
        throw new IOException("Failed to fetch " + url, failureReason);
    }

    private void beRespectful() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) { }
    }
}
