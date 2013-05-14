package com.duowan.ebguide.crawl;

import java.util.concurrent.Callable;

import com.duowan.ebguide.model.SnatchProduct;

public class ThreadCrawl implements Callable<SnatchProduct> {
	
	private Crawl crawl;
	
	private String productUrl;
	
	public ThreadCrawl(Crawl crawl, String productUrl){
		this.crawl = crawl;
		this.productUrl = productUrl;
	}

	@Override
	public SnatchProduct call() throws Exception {
		return crawl.parseProduct(productUrl);
	}

}
