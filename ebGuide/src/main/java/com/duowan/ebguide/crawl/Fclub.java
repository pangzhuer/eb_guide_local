package com.duowan.ebguide.crawl;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duowan.ebguide.model.SnatchProduct;

/**
 * crawl website http://www.fclub.cn/
 * http://www.fclub.cn/
 * @author pangzhu
 *
 */
public class Fclub extends Crawl {
	
	private static final String WEBSITE = "FCLUB";

	@Override
	public Set<String> getProductListUrls(String crawlIndexUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productListUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(crawlIndexUrl);
				Elements links = doc.select("ul#mainMenuUl a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.fclub.cn/rush")) {
						productListUrls.add(href);
					}
				}
				hasNext = false;
			} catch (Exception e) {
				throw new DownloadException(e, crawlIndexUrl);
			}
		}
		return productListUrls;
	}

	@Override
	public Set<String> getProductUrls(String listUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(listUrl);
				Elements links = doc.select("ul#goods_list li > a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.fclub.cn/goods")) {
						productUrls.add(href);
					}
				}
				Elements nextPage = doc.select("div#pagelist a.next_page");
				if(nextPage.isEmpty()){
					hasNext = false;
				}else{
					listUrl = nextPage.get(0).attr("abs:href");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new DownloadException(e, listUrl);
			}
		}
		return productUrls;
	}
	
	public SnatchProduct parseProduct(String productUrl) {
		return parseProduct(productUrl, null);
	}
	

	public SnatchProduct parseProduct(String productUrl, Document doc) {
		SnatchProduct snatchProduct = new SnatchProduct();
		snatchProduct.setWebsite(WEBSITE);
		snatchProduct.setProductUrl(productUrl);
		try {
			if(doc == null) {
				doc = tryAndGetDocument(productUrl);
			}
			snatchProduct.setEndTme(parseEndTime(doc));
			snatchProduct.setProductGender(parseProductGender(doc));
			snatchProduct.setProductImages(parseProductImages(doc));

			snatchProduct.setDiscountPrice(parseDiscountPrice(doc));
			snatchProduct.setDiscountRate(parseDiscountRate(doc));
			snatchProduct.setOriginalPrice(parseOriginalPrice(doc));

			snatchProduct.setProductBrand(parseProductBrand(doc));
			snatchProduct.setProductCategory(parseProductCategory(doc));
			snatchProduct.setProductName(parseProductName(doc));

			snatchProduct.setProductRemainCount(parseProductRemainCount());
			snatchProduct.setProductSoldCount(parseProductSoldCount());
			snatchProduct.setStartTime(parseStartTime());
			snatchProduct.setUpdateTime(new Date());
			return snatchProduct;
		} catch (Exception e) {
			logger.error(e.toString());
			e.printStackTrace();
			return null;
		}
	}

	private Date parseStartTime() {
		return new Date();
	}


	private Date parseEndTime(Document doc) {
		Long endTime = System.currentTimeMillis() + 3 * 24 * 3600 * 1000;
		Elements scripts = doc.select("script");
		for(Element script : scripts){
			String js = script.toString();
			Pattern p = Pattern.compile("time : \"(\\d{10})\"");
			Matcher m = p.matcher(js);
			if(m.find()){
				endTime = Long.parseLong(m.group(1));
			}
		}
		return new Date(endTime * 1000);
	}
	
	
	/**
	 * 判断商品性别
	 * 
	 * @param doc
	 * @return
	 */
	private String parseProductGender(Document doc) {
		Element gender = doc.select("div.nav_link").get(0);
		if (gender.hasText() && (gender.text().indexOf("男士") != -1 || 
				gender.text().indexOf("男装") != -1 || 
				gender.text().indexOf("南包") != -1)) {
			return "MALE";
		} else {
			return "FEMALE";
		}
	}

	
	private String parseProductImages(Document doc) {
		StringBuilder sb = new StringBuilder();
		Elements elements = doc.select("ul.smallimgs img");
		for (Element element : elements) {
			sb.append(element.attr("bsrc"));
			sb.append(",");
		}
		return sb.toString();
	}

	private Float parseDiscountPrice(Element doc) {
		//String price = priceInfo.text();
		Element element = doc.select("p.goods_infop strong").get(0);
		String tmp = element.text();
		return Float.parseFloat(tmp.substring("￥".length()));
	}

	private Float parseDiscountRate(Element doc) {
		Element element = doc.select("p.goods_infop span.black").get(0);
		String price = element.text();
		Pattern p = Pattern.compile("（(.*?)折）");
		Matcher m = p.matcher(price);
		if(m.find()){
			return Float.parseFloat(m.group(1));
		}
		return (float) 0.0;
	}
	
	private Float parseOriginalPrice(Element doc) {
		Element discountRate = doc.select("p.goods_infop del").get(0);
		String price = discountRate.text();
		return Float.parseFloat(price.substring("市场价：￥".length()));
	}
	
	

	private String parseProductBrand(Element doc) {
		Element element = doc.select("div.goods_introduce p b").get(0);
		return element.text();
	}
	
	private String parseProductName(Element doc) {
		return doc.select("div.goods_introduce h3").get(0).text();
	}
	
	private String parseProductCategory(Element doc) {
		Element category = doc.select("div.nav_link font").get(0);
		return category.text();
	}
	
	private Integer parseProductRemainCount() {
		return 0;
	}

	private Integer parseProductSoldCount() {
		return 0;
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Fclub fclub = new Fclub();
		Set<String> sets = fclub.getProductUrls("http://www.fclub.cn/rush-12580.html", 1);
		for(String s: sets){
			System.out.println(s);
		}
	}

}
