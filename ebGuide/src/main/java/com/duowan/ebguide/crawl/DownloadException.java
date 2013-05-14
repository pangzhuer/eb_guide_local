package com.duowan.ebguide.crawl;

public class DownloadException extends RuntimeException{
	private static final long serialVersionUID = 7742568836256505701L;
	
	private String url;
	
	public DownloadException(Exception e, String url){
		super(e);
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
}
