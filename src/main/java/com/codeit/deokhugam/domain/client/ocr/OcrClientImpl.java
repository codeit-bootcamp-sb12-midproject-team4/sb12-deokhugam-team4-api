package com.codeit.deokhugam.domain.client.ocr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Component
public class OcrClientImpl implements OcrClient{

	@Override
	public String getIsbnFromImage(MultipartFile img) {
		if (img.isEmpty()) {
			throw new IllegalArgumentException("이미지 파일이 비어 있습니다.");
		}

		return "";
	}

}
