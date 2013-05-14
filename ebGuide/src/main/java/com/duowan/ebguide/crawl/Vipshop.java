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
 * 
 * @author pangzhu
 *
 */
public class Vipshop extends Crawl{
	
	private static final String WEBSITE = "VIPSHOP";

	@Override
	public Set<String> getProductListUrls(String crawlIndexUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productListUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(crawlIndexUrl);
				Elements links = doc.select("div#J_head_category a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://shop.vipshop.com/cate")) {
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
	
	
	/**
	 * 解析商品url,保存该商品的类型
	 * 
	 */
	@Override
	public Set<String> getProductUrls(String listUrl, int page)
			throws DownloadException {
		boolean hasNext = true;
		Set<String> productUrls = new HashSet<String>();
		while (hasNext && page-- > 0) {
			try {
				Document doc = tryAndGetDocument(listUrl);
				String title = doc.select("title").get(0).text();
				String category = title.substring(0,title.indexOf("【"));
				Elements links = doc.select("div.wrap a");
				for (Element link : links) {
					String href = link.attr("abs:href");
					if (href.startsWith("http://shop.vipshop.com/detail")) {
						productUrls.add(href);
					}
				}
				Elements nextPage = doc.select("div.page a.page_next");
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
	public SnatchProduct parseProduct(String productUrl) {
		return parseProduct(productUrl,null);
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
			Element priceInfo = doc.select("span.goods_price_sale").get(0);
			snatchProduct.setDiscountPrice(parseDiscountPrice(priceInfo));
			snatchProduct.setDiscountRate(parseDiscountRate(priceInfo));
			snatchProduct.setOriginalPrice(parseOriginalPrice(doc));

			// 商品详细
			// 有两种情况：1、正常的例如 http://www.tianpin.com/item/20216191-22438-2001114/
			// 2化妆品 例如http://www.tianpin.com/item/18057986-0-2006094/
			Element detail = doc.select("h1.goods_protit").get(0);
			snatchProduct.setProductBrand(parseProductBrand(detail));
			snatchProduct.setProductName(parseProductName(detail));
			snatchProduct.setProductCategory(parseProductCategory(doc));

			snatchProduct.setProductRemainCount(parseProductRemainCount());
			snatchProduct.setProductSoldCount(parseProductSoldCount(doc));
			snatchProduct.setStartTime(parseStartTime());
			snatchProduct.setUpdateTime(new Date());
			return snatchProduct;
		} catch (Exception e) {
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
			Pattern p = Pattern.compile("end_time\\s*:\\s*(\\d{10})");
			Matcher m = p.matcher(js);
			if(m.find()){
				endTime = Long.parseLong(m.group(1));
				break;
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
		Element gender = doc.select("div.pro_nav").get(0);
		if (gender.hasText() && (gender.text().indexOf("男士") != -1 || gender.text().indexOf("男装") != -1)) {
			return "MALE";
		} else {
			return "FEMALE";
		}
	}

	
	private String parseProductImages(Document doc) {
		StringBuilder sb = new StringBuilder();
		Elements elements = doc.select("a.J_mer_bigImgZoom img");
		for (Element element : elements) {
			sb.append(element.attr("src"));
			sb.append(",");
		}
		return sb.toString();
	}

	private Float parseDiscountPrice(Element priceInfo) {
		String price = priceInfo.text();
		Pattern p = Pattern.compile("¥\\s*(\\d+)\\s*\\(([\\d.]+)折\\)");
		Matcher m = p.matcher(price);
		if(m.find()){
			return Float.parseFloat(m.group(1).replace(",", ""));
		}
		return (float) 0.0;
	}

	private Float parseDiscountRate(Element priceInfo) {
		String price = priceInfo.text();
		Pattern p = Pattern.compile("¥\\s*(\\d+)\\s*\\(([\\d.]+)折\\)");
		Matcher m = p.matcher(price);
		if(m.find()){
			return Float.parseFloat(m.group(2).replace(",", ""));
		}
		return (float) 0.0;
	}
	
	private Float parseOriginalPrice(Element doc) {
		Element discountRate = doc.select("span.goods_price_dis del").get(0);
		String price = discountRate.text();
		Pattern p = Pattern.compile("¥ (\\d+)");
		Matcher m = p.matcher(price);
		if(m.find()){
			return Float.parseFloat(m.group(1).replace(",", ""));
		}
		return (float) 0.0;
	}
	
	

	private String parseProductBrand(Element detail) {
		String goods = detail.text();
		Pattern p = Pattern.compile("\\[(.*?)\\]");
		Matcher m = p.matcher(goods);
		if(m.find()){
			//FIXME 有个空格没去掉，暂时先简单截取
			return m.group(1).trim().substring(1);
		}
		return "未知品牌";
	}
	
	private String parseProductName(Element detail) {
		return detail.select("font").get(0).text();
	}
	
	private String parseProductCategory(Element doc) {
		Elements scripts = doc.select("script");
		String category = "未知分类";
		for(Element script : scripts){
			String js = script.toString();
			Pattern p = Pattern.compile("catName\\s*:\\s*\"(.*?)\"");
			Matcher m = p.matcher(js);
			if(m.find()){
				category = m.group(1);
				break;
			}
		}
		return category;
	}
	
	private Integer parseProductRemainCount() {
		return 0;
	}

	private Integer parseProductSoldCount(Document doc) {
		return Integer.parseInt(doc.select("p.fr font").get(0).text());
	}
	
	public static void main(String[] args) throws Exception {
		Vipshop vp = new Vipshop();
		Document doc = Jsoup.connect("http://shop.vipshop.com/detail-77145-9409059.html").get();
		System.out.println(vp.parseOriginalPrice(doc));
	}

	
	
}
