package com.codeit.deokhugam.domain.client.ocr;

import org.springframework.web.multipart.MultipartFile;

public interface OcrClient {

	String getIsbnFromImage(MultipartFile img);

}
