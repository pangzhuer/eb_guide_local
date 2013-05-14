package com.duowan.ebguide.crawl;

import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.ebguide.model.SnatchProduct;

public abstract class Crawl {
	
	public static final Logger logger = LoggerFactory.getLogger(Tianpin.class);
	
	/**
	 * 获取商品列表的url的集合
	 * @param crawlIndexUrl 最开始的抓取页面
	 * @param page 抓取的页数
	 * @return
	 */
	public abstract Set<String> getProductListUrls(String crawlIndexUrl, int page) throws DownloadException;
	
	/**
	 * 获取商品url的集合
	 * @param listUrl 商品列表的url
	 * @param page 抓取的页数
	 * @return
	 */
	public abstract Set<String> getProductUrls(String listUrl, int page) throws DownloadException;
	
	
	/**
	 * 获取商品信息
	 * @param productUrl 商品的url
	 * @return
	 */
	public abstract SnatchProduct parseProduct(String productUrl);
	
	/**
	 * 获取商品信息
	 * @param productUrl 商品的url
	 * @return
	 */
	public abstract SnatchProduct parseProduct(String productUrl, Document doc);
	
	/**
	 * 尝试获取document对象
	 * @param productUrl
	 * @return
	 */
	protected Document tryAndGetDocument(String productUrl) {
		Document doc = null;
		int tryTimes = 4;
		while(--tryTimes > 0){
			try {
				doc = Jsoup.connect(productUrl).get();
				break;
			} catch (Exception e) {
				logger.error(productUrl);
				logger.error(e.getStackTrace().toString());
				continue;
			}
		}
		return doc;
	}
}
