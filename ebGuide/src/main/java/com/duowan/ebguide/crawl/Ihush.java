package com.duowan.ebguide.crawl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.duowan.ebguide.model.SnatchProduct;

public class Ihush extends Crawl {

	private static final String WEBSITE = "IHUSH";

	@Override
	public Set<String> getProductListUrls(String crawlIndexUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productListUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(crawlIndexUrl);
				Elements links = doc.select("div#subCategory div.cate-cont a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.ihush.com/list")) {
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
				Elements links = doc.select("div.wareListBox p.pic a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.ihush.com/product")) {
						productUrls.add(href);
					}
				}
				Elements nextPage = doc.select("div.pagelist a.next");
				if (nextPage.isEmpty()) {
					hasNext = false;
				} else {
					listUrl = nextPage.get(0).attr("abs:href");
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new DownloadException(e, listUrl);
			}
		}
		return productUrls;
	}

	@Override
	public SnatchProduct parseProduct(String productUrl) {
		return parseProduct(productUrl, null);
	}

	@Override
	public SnatchProduct parseProduct(String productUrl, Document doc) {
		SnatchProduct snatchProduct = new SnatchProduct();
		snatchProduct.setWebsite(WEBSITE);
		snatchProduct.setProductUrl(productUrl);
		try {
			if (doc == null) {
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
			e.printStackTrace();
			logger.error(e.toString());
			return null;
		}
	}

	private Date parseStartTime() {
		return new Date();
	}

	private Date parseEndTime(Document doc) {
		Long endTime = System.currentTimeMillis() + 3 * 24 * 3600 * 1000;
		Elements scripts = doc.select("script");
		String js = scripts.last().toString();
		Pattern p = Pattern.compile("time:\"(\\d{10})\"");
		Matcher m = p.matcher(js);
		if (m.find()) {
			endTime = Long.parseLong(m.group(1));
		}else{
			for(Element script: scripts){
				m = p.matcher(script.toString());
				if (m.find()) {
					endTime = Long.parseLong(m.group(1));
				}
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
		Element gender = doc.select("div.Discrump").get(0);
		if (gender.hasText()
				&& (gender.text().indexOf("男士") != -1
						|| gender.text().indexOf("男装") != -1 || gender.text()
						.indexOf("南包") != -1)) {
			return "MALE";
		} else {
			return "FEMALE";
		}
	}

	private String parseProductImages(Document doc) {
		StringBuilder sb = new StringBuilder();
		Elements elements = doc.select("#warePiclist a");
		for (Element element : elements) {
			sb.append(element.attr("rel"));
			sb.append(",");
		}
		return sb.toString();
	}

	private Float parseDiscountPrice(Element doc) {
		Element element = doc.select("div.price strong").get(0);
		String tmp = element.text();
		return Float.parseFloat(tmp.substring("￥".length()));
	}

	private Float parseDiscountRate(Element doc) {
		Element element = doc.select("ul.discounting strong").get(0);
		return Float.parseFloat(element.text());
	}

	private Float parseOriginalPrice(Element doc) {
		Element discountRate = doc.select("ul.discounting del").get(0);
		String price = discountRate.text();
		return Float.parseFloat(price.substring("￥".length()));
	}

	private String parseProductBrand(Element doc) {
		String brand = "未知品牌";
		Elements scripts = doc.select("script");
		String js = scripts.last().toString();
		Pattern p = Pattern.compile("brand:\"(.*?)\"");
		Matcher m = p.matcher(js);
		if (m.find()) {
			brand = m.group(1);
		}else{
			for(Element script: scripts){
				m = p.matcher(script.toString());
				if (m.find()) {
					brand = m.group(1);
					break;
				}
			}
		}
		return brand;
	}

	private String parseProductName(Element doc) {
		return doc.select("h1.title").get(0).text();
	}

	private String parseProductCategory(Element doc) {
		Element category = doc.select("div.Discrump a").last();
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
		Ihush ihush = new Ihush();
		Document doc = ihush.tryAndGetDocument("http://www.ihush.com/product_815785_f2_gcHNwaWQ9Njc4OSYlMT0m.html");
		System.out.println(ihush.parseProductBrand(doc));

	}

}
