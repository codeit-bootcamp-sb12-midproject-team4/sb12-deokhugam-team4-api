package com.codeit.deokhugam.domain.client.s3;

import java.io.IOException;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.uuid.Generators;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3FileStorageClient implements FileStorageClient {
	private static final String PREFIX = "thumbnail";

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	@Value("${AWS_S3_BUCKET_NAME_DEV}")
	private String bucket;
	@Value("${AWS_S3_PRESIGNED_URL_EXPIRATION:600}")
	private long presignedUrlExpiration; // Presigned URL 만료 시간 (초, 기본 10분)

	@Override
	public String uploadImage(MultipartFile image) {
		String originalName = image.getOriginalFilename();
		String ext = (originalName != null && originalName.contains("."))
			? originalName.substring(originalName.lastIndexOf('.'))
			: "";

		String renamed = Generators.timeBasedEpochGenerator().generate() + ext;
		String key = PREFIX + "/" + renamed;

		uploadToS3(key, image);
		log.info("첨부파일 S3 업로드 완료: {}", key);

		return key;
	}
	private void uploadToS3(String key, MultipartFile file) {
		try {
			PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.contentType(file.getContentType())
				.build();
			s3Client.putObject(putReq, RequestBody.fromInputStream(
				file.getInputStream(), file.getSize()));
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드 실패: " + key, e);
		}
	}

	@Override
	public String getAttachFileUrl(String imgKey) {
		GetObjectRequest getReq = GetObjectRequest.builder()
			.bucket(bucket)
			.key(imgKey)
			.build();

		GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
			.getObjectRequest(getReq)
			.build();

		return s3Presigner.presignGetObject(presignReq).url().toExternalForm();
	}

	@Override
	public void deleteImage(String imgKey) {
		try {
			s3Client.deleteObject(DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(imgKey)
				.build());
		} catch (S3Exception e) {
			log.error("S3 삭제 실패 (권한/설정 에러). Key: {}, 원인: {}", imgKey, e.awsErrorDetails().errorMessage());
		} catch (Exception e) {
			log.error("S3 삭제 중 알 수 없는 에러 발생. Key: {}", imgKey, e);
		}
	}
}
