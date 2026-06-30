package com.mercadona.employee.digitaldocument.driven.bucketclient.adapters;

import com.google.common.io.ByteSource;
import com.mercadona.employee.digitaldocument.application.ports.driven.BucketStoragePort;
import com.mercadona.framework.cna.lib.bucket.service.BucketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;


@Slf4j
@Service
public class BucketStorageAdapter implements BucketStoragePort {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final String PATH_TEMPLATE = "documents/%s/%s.pdf";

    private final BucketService bucketService;
    private final String bucketName;

    public BucketStorageAdapter(BucketService bucketService,
                                @Value("${fwkcna.buckets[0].bucket-name}") String bucketName) {
        this.bucketService = bucketService;
        this.bucketName = bucketName;
    }

    @Override
    public String upload(String documentId, String employeeId, byte[] pdfBytes) {
        var destinationPath = String.format(PATH_TEMPLATE, employeeId, documentId);
        log.info("Uploading PDF to bucket: path={}", destinationPath);

        try {
            bucketService.upload(bucketName, ByteSource.wrap(pdfBytes), destinationPath,
                    CONTENT_TYPE_PDF, Map.of());
            return destinationPath;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] download(String bucketPath) {
        log.info("Downloading PDF from bucket: path={}", bucketPath);

        try {
            var inputStream = bucketService.getInputStream(bucketName, bucketPath);
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
