package com.codeit.deokhugam.domain.client.s3;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {

	String uploadImage(MultipartFile image);

	String getAttachFileUrl(String imgKey);

	void deleteImage(String imgKey);

}
