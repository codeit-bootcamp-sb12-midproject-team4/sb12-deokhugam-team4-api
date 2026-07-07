package com.codeit.deokhugam.domain.book.dto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public class CustomMultipartFile implements MultipartFile {
	private final byte[] imgContent;
	private final String headerName;
	private final String originalFilename;
	private final String contentType;

	public CustomMultipartFile(byte[] imgContent, String headerName, String originalFilename, String contentType) {
		this.imgContent = imgContent;
		this.headerName = headerName;
		this.originalFilename = originalFilename;
		this.contentType = contentType;
	}

	@Override
	public String getName() { return this.headerName; }

	@Override
	public String getOriginalFilename() { return this.originalFilename; }

	@Override
	public String getContentType() { return this.contentType; }

	@Override
	public boolean isEmpty() { return imgContent == null || imgContent.length == 0; }

	@Override
	public long getSize() { return imgContent.length; }

	@Override
	public byte[] getBytes() throws IOException { return imgContent; }

	@Override
	public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(imgContent); }

	@Override
	public void transferTo(File dest) throws IOException, IllegalStateException {
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			fos.write(imgContent);
		}
	}
}
