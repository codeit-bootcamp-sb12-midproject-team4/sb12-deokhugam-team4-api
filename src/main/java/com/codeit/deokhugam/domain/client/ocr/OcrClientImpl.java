package com.codeit.deokhugam.domain.client.ocr;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class OcrClientImpl implements OcrClient{

	@Override
	public String getIsbnFromImage(MultipartFile img) {
		return "";
	}

}
