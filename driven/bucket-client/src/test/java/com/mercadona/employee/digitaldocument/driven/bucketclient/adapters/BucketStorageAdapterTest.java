package com.mercadona.employee.digitaldocument.driven.bucketclient.adapters;

import com.google.common.io.ByteSource;
import com.mercadona.framework.cna.lib.bucket.service.BucketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link BucketStorageAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class BucketStorageAdapterTest {

    private static final String BUCKET_NAME  = "test-bucket";
    private static final String DOCUMENT_ID  = "doc-uuid-001";
    private static final String EMPLOYEE_ID  = "EMP001";
    private static final byte[] PDF_BYTES    = new byte[]{1, 2, 3};

    @Mock
    private BucketService bucketService;

    private BucketStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BucketStorageAdapter(bucketService, BUCKET_NAME);
    }

    @Test
    void upload_validInput_shouldReturnStoragePath() {
        when(bucketService.upload(eq(BUCKET_NAME), any(ByteSource.class), anyString(), anyString(), any(Map.class))).thenReturn(true);

        var result = adapter.upload(DOCUMENT_ID, EMPLOYEE_ID, PDF_BYTES);

        assertThat(result).isEqualTo("documents/EMP001/doc-uuid-001.pdf");
    }

    @Test
    void upload_validInput_shouldCallBucketServiceWithCorrectPath() {
        when(bucketService.upload(eq(BUCKET_NAME), any(ByteSource.class), anyString(), anyString(), any(Map.class))).thenReturn(true);

        adapter.upload(DOCUMENT_ID, EMPLOYEE_ID, PDF_BYTES);

        verify(bucketService).upload(eq(BUCKET_NAME), any(ByteSource.class),
                eq("documents/EMP001/doc-uuid-001.pdf"), eq("application/pdf"), any(Map.class));
    }

    @Test
    void download_validPath_shouldReturnPdfBytes() throws Exception {
        var expectedBytes = new byte[]{1, 2, 3};
        when(bucketService.getInputStream(eq(BUCKET_NAME), eq("documents/EMP001/doc-uuid-001.pdf")))
                .thenReturn(new ByteArrayInputStream(expectedBytes));

        var result = adapter.download("documents/EMP001/doc-uuid-001.pdf");

        assertThat(result).isEqualTo(expectedBytes);
    }

    @Test
    void download_bucketServiceThrows_shouldWrapAsRuntimeException() throws Exception {
        when(bucketService.getInputStream(any(), any())).thenThrow(new RuntimeException("bucket error"));

        assertThatThrownBy(() -> adapter.download("documents/EMP001/doc.pdf"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void upload_bucketServiceThrows_shouldWrapAsRuntimeException() {
        when(bucketService.upload(any(String.class), any(ByteSource.class), any(String.class), any(String.class), any(Map.class)))
                .thenThrow(new RuntimeException("bucket error"));

        assertThatThrownBy(() -> adapter.upload(DOCUMENT_ID, EMPLOYEE_ID, PDF_BYTES))
                .isInstanceOf(RuntimeException.class);
    }
}
