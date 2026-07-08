package com.codeit.deokhugam.domain.client.s3;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageClient {

	public String uploadImage(MultipartFile image, ImgType type);

	public String getAttachFileUrl(String imgKey);

	public void deleteImage(String imgKey);

}
