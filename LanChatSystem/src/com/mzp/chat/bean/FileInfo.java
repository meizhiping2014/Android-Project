package com.mzp.chat.bean;


/**
 * 文件信息
 *	2013-12-25
 */
public class FileInfo {
	
	private String fileName;//名称
	private String filePath;//当前路径
	//private String fileSize;//文件大小
	private boolean isCheck;//是否被选中
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	/*
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	}*/
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
	
	
}
