package com.ipang.wansha.model;

import java.io.Serializable;

public class Download implements Serializable {

	private static final long serialVersionUID = -928802265406019594L;
	
	public static final int NOTSTARTED = 0;
	public static final int STARTED = 1;
	public static final int STOPPED = 2;
	public static final int COMPLETED = 3;
	public static final int ERROR = 4;
	
	
	private int downloadId;
	private int productId;
	private String productName;
	private int fileSize;
	private int downloadedSize;
	private String productImage;
	private int status;

	public int getDownloadId() {
		return downloadId;
	}

	public void setDownloadId(int downloadId) {
		this.downloadId = downloadId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getDownloadedSize() {
		return downloadedSize;
	}

	public void setDownloadedSize(int downloadedSize) {
		this.downloadedSize = downloadedSize;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
