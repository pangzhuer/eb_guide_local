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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.ebguide.model.SnatchProduct;

/**
 * 天平网抓取类
 * http://www.tianpin.com/
 * @author pangzhu
 *
 */
public class Tianpin extends Crawl {

	// private static final String WEBSITE_URL = "http://www.tianpin.com/";

	private static final String WEBSITE = "TIANPIN";
	
	public static final Logger logger = LoggerFactory.getLogger(Tianpin.class);

	@Override
	public Set<String> getProductListUrls(String crawlIndexUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productListUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(crawlIndexUrl);
				Elements links = doc.select("ul#brands-list li a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.tianpin.com/brand/")) {
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
				Elements links = doc.select("div#item-list li a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://www.tianpin.com/item/")) {
						productUrls.add(href);
					}
				}
				Elements nextPage = doc.select("div#paging a.next");
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

	@Override
	public SnatchProduct parseProduct(String productUrl){
		return parseProduct(productUrl, null);
	}
	
	/**
	 * 主要是为了方便测试
	 * @param productUrl
	 * @param doc
	 * @return
	 */
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

			// 价格和折扣信息
			Element priceInfo = doc.select("ul.item-about").get(0);
			snatchProduct.setDiscountPrice(parseDiscountPrice(priceInfo));
			snatchProduct.setDiscountRate(parseDiscountRate(priceInfo));
			snatchProduct.setOriginalPrice(parseOriginalPrice(priceInfo));

			// 商品详细
			// 有两种情况：1、正常的例如 http://www.tianpin.com/item/20216191-22438-2001114/
			// 2化妆品 例如http://www.tianpin.com/item/18057986-0-2006094/
			Element detailTable = doc.select("div#desc-tab table").get(0);
			if (detailTable.hasClass("ser-ask")) {
				detailTable = doc.select("div#desc-tab dl.clearfix").get(0);
			}
			snatchProduct.setProductBrand(parseProductBrand(detailTable));
			snatchProduct.setProductCategory(parseProductCategory(detailTable));
			snatchProduct.setProductName(parseProductName(detailTable));

			snatchProduct.setProductRemainCount(parseProductRemainCount());
			snatchProduct.setProductSoldCount(parseProductSoldCount());
			snatchProduct.setStartTime(parseStartTime());
			snatchProduct.setUpdateTime(new Date());
			return snatchProduct;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(productUrl);
			logger.error(e.getStackTrace().toString());
			return null;
		}

	}


	private Float parseDiscountPrice(Element priceInfo) {
		Element price = priceInfo.select("#J_RealPrice").get(0);
		return Float.parseFloat(price.text().replace(",", ""));
	}

	private Float parseDiscountRate(Element priceInfo) {
		Element discountRate = priceInfo.select("li.cost-price span").get(0);
		String tmp = discountRate.text();
		return Float.parseFloat(tmp.substring(0, tmp.length() - 1).replace(",", ""));
	}

	private Date parseEndTime(Document doc) {
		Long endTime = System.currentTimeMillis() + 3 * 24 * 3600 * 1000;
		Elements scripts = doc.select("script");
		for(Element script : scripts){
			String js = script.toString();
			Pattern p = Pattern.compile("countdownTime:\"(\\d{13})\"");
			Matcher m = p.matcher(js);
			if(m.find()){
				endTime = Long.parseLong(m.group(1));
				break;
			}
		}
		return new Date(endTime);
	}

	private String parseProductCategory(Element detailTable) {
		Elements productBefore = detailTable.select("td:contains(商品分类)");
		if(productBefore.isEmpty()){
			return "化妆品";
		}
		Element element = productBefore.get(0).nextElementSibling();
		return element.text();
	}

	private String parseProductBrand(Element detailTable) {
		return parseDetailTable(detailTable, "td:contains(品牌名称)",
				"dt:contains(品牌)");

	}

	private String parseDetailTable(Element detailTable, String select1,
			String select2) {
		Elements productBefore = detailTable.select(select1);
		if (productBefore.isEmpty()) {
			productBefore = detailTable.select(select2);
		}
		Element element = productBefore.get(0).nextElementSibling();
		return element.text();
	}

	private Float parseOriginalPrice(Element priceInfo) {
		Element discountRate = priceInfo.select("li.cost-price del").get(0);
		String tmp = discountRate.text();
		return Float.parseFloat(tmp.substring("原价：￥".length()).replace(",", ""));
	}

	/**
	 * 判断商品性别
	 * 
	 * @param doc
	 * @return
	 */
	private String parseProductGender(Document doc) {
		Element crumbs = doc.select("div.crumbs").get(0);
		Element gender = crumbs.children().get(2);
		if (gender.hasText() && gender.text().equals("男士")) {
			return "MALE";
		} else {
			return "FEMALE";
		}
	}

	private String parseProductImages(Document doc) {
		StringBuilder sb = new StringBuilder();
		Elements elements = doc.select("div#J_SmallPics li:lt(4)");
		for (Element element : elements) {
			sb.append(element.attr("data-bigimage"));
			sb.append(",");
		}
		return sb.toString();
	}

	private Date parseStartTime() {
		return new Date();
	}

	private Integer parseProductSoldCount() {
		return 0;
	}

	private Integer parseProductRemainCount() {
		return 0;
	}

	private String parseProductName(Element detailTable) {
		return parseDetailTable(detailTable, "td:contains(商品名称)",
				"dt:contains(名称)");
	}
	
	public static void main(String[] args) throws IOException {
		//Document doc = Jsoup.connect("http://www.tianpin.com/item/20548557-21509-2004114/").get();
		new Tianpin().parseProduct("http://www.tianpin.com/item/18057986-0-2006094/");
	}
}
