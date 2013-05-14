package com.duowan.ebguide.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;

import com.duowan.ebguide.model.SnatchProduct;

public class CrawlTest {
	
	private static final String testFilePath = System.getProperty("user.dir") + "/src/main/resources/test/";
	private static final String defaultEncoding = "UTF-8";
	
	/**
	 * 测试天品网商品页面
	 * @throws IOException
	 */
	@Test
	public void testTianpinNormal() throws IOException{
		Tianpin tp = new Tianpin();
		File destFile = saveUrlToFile("http://www.tianpin.com/item/20776335-7615-2008122/", defaultEncoding);
		Document doc = Jsoup.parse(destFile, defaultEncoding, "http://www.tianpin.com/");
		SnatchProduct product = tp.parseProduct("http://www.tianpin.com/item/20776335-7615-2008122/", doc);
		Assert.assertEquals("品牌解析错误", "朵牧 dumoo", product.getProductBrand());
		Assert.assertEquals("商品分类解析错误", "单鞋", product.getProductCategory());
		Assert.assertEquals("商品性别解析错误", "FEMALE", product.getProductGender());
		Assert.assertEquals("商品图片解析错误", "http://p4.tianping.com/uploads/goods/PO1002196/5819.H2/heise/1739606_338_440.jpg", product.getProductImages().split(",")[0]);
		Assert.assertEquals("商品名称解析错误", "镂空漆皮厚底鱼嘴凉鞋", product.getProductName());
		Assert.assertEquals("商品剩余数量解析错误", 0, product.getProductRemainCount().intValue());
		Assert.assertEquals("商品销售数量解析错误", 0, product.getProductSoldCount().intValue());
		Assert.assertEquals("商品链接解析错误", "http://www.tianpin.com/item/20776335-7615-2008122/", product.getProductUrl());
		Assert.assertEquals("折扣价解析错误", 72.00, product.getDiscountPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣率析错误", 2.5, product.getDiscountRate().floatValue(), 0.001);
		Assert.assertEquals("原价解析错误", 288.00, product.getOriginalPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣价解析错误", new Date(1369065599000L), product.getEndTme());
	}
	
	/**
	 * 测试天品网化妆品页面
	 * 页面结构有些不一样
	 * @throws IOException
	 */
	@Test
	public void testTianpinMakeup() throws IOException{
		Tianpin tp = new Tianpin();
		File destFile = saveUrlToFile("http://www.tianpin.com/item/18057986-0-2006094/", defaultEncoding);
		Document doc = Jsoup.parse(destFile, defaultEncoding, "http://www.tianpin.com/");
		SnatchProduct product = tp.parseProduct("http://www.tianpin.com/item/18057986-0-2006094/", doc);
		Assert.assertEquals("品牌解析错误", "EsteeLauder/雅诗兰黛", product.getProductBrand());
		Assert.assertEquals("商品分类解析错误", "化妆品", product.getProductCategory());
		Assert.assertEquals("商品性别解析错误", "FEMALE", product.getProductGender());
		Assert.assertEquals("商品图片解析错误", "http://p4.tianping.com/uploads/goods/PO1003143/ELR120891/1/1628555_338_440.jpg", product.getProductImages().split(",")[0]);
		Assert.assertEquals("商品名称解析错误", "细致焕采化妆水 - 中性或混合性肌肤 200ml / 6.7oz", product.getProductName());
		Assert.assertEquals("商品剩余数量解析错误", 0, product.getProductRemainCount().intValue());
		Assert.assertEquals("商品销售数量解析错误", 0, product.getProductSoldCount().intValue());
		Assert.assertEquals("商品链接解析错误", "http://www.tianpin.com/item/18057986-0-2006094/", product.getProductUrl());
		Assert.assertEquals("折扣价解析错误", 166.00, product.getDiscountPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣率析错误", 6.4, product.getDiscountRate().floatValue(), 0.001);
		Assert.assertEquals("原价解析错误", 260.00, product.getOriginalPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣价解析错误", new Date(1369238399000L), product.getEndTme());
	}
	
	/**
	 * 测试唯品会抓取脚本
	 * @throws IOException
	 */
	@Test
	public void testVipshop() throws IOException{
		Vipshop vp = new Vipshop();
		File destFile = saveUrlToFile("http://shop.vipshop.com/detail-77145-9409059.html", defaultEncoding);
		Document doc = Jsoup.parse(destFile, defaultEncoding, "http://shop.vipshop.com/");
		SnatchProduct product = vp.parseProduct("http://shop.vipshop.com/detail-77145-9409059.html", doc);
		Assert.assertEquals("品牌解析错误", "卡帕", product.getProductBrand());
		Assert.assertEquals("商品分类解析错误", "男款Polo", product.getProductCategory());
		Assert.assertEquals("商品性别解析错误", "MALE", product.getProductGender());
		Assert.assertEquals("商品图片解析错误", "http://a.vimage2.com/upload/merchandise/77145/kappa-K0132PD09-527-1_1.jpg", product.getProductImages().split(",")[0]);
		Assert.assertEquals("商品名称解析错误", "玫红色印图短袖POLO衫", product.getProductName());
		Assert.assertEquals("商品剩余数量解析错误", 0, product.getProductRemainCount().intValue());
		Assert.assertEquals("商品销售数量解析错误", 2781, product.getProductSoldCount().intValue());
		Assert.assertEquals("商品链接解析错误", "http://shop.vipshop.com/detail-77145-9409059.html", product.getProductUrl());
		Assert.assertEquals("折扣价解析错误", 85, product.getDiscountPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣率析错误", 2.5, product.getDiscountRate().floatValue(), 0.001);
		Assert.assertEquals("原价解析错误", 338, product.getOriginalPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣价解析错误", new Date(1368547199000L), product.getEndTme());
	}
	
	
	/**
	 * 测试聚尚网抓取脚本
	 * @throws IOException
	 */
	@Test
	public void testFclub() throws IOException{
		Fclub vp = new Fclub();	
		File destFile = saveUrlToFile("http://www.fclub.cn/goods-709130-438-4_12667.html", defaultEncoding);
		Document doc = Jsoup.parse(destFile, defaultEncoding, "http://www.fclub.cn/");
		SnatchProduct product = vp.parseProduct("http://www.fclub.cn/goods-709130-438-4_12667.html", doc);
		Assert.assertEquals("品牌解析错误", "DEYEE", product.getProductBrand());
		Assert.assertEquals("商品分类解析错误", "衬衫", product.getProductCategory());
		Assert.assertEquals("商品性别解析错误", "MALE", product.getProductGender());
		Assert.assertEquals("商品图片解析错误", "http://image2.fclubimg.com/data2/new_goodsimg/000/709/130-438_342x455_1.jpg", product.getProductImages().split(",")[0]);
		Assert.assertEquals("商品名称解析错误", "DEYEE / 纯棉翻领短袖衬衫", product.getProductName());
		Assert.assertEquals("商品剩余数量解析错误", 0, product.getProductRemainCount().intValue());
		Assert.assertEquals("商品销售数量解析错误", 0, product.getProductSoldCount().intValue());
		Assert.assertEquals("商品链接解析错误", "http://www.fclub.cn/goods-709130-438-4_12667.html", product.getProductUrl());
		Assert.assertEquals("折扣价解析错误", 69, product.getDiscountPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣率析错误", 0.9, product.getDiscountRate().floatValue(), 0.001);
		Assert.assertEquals("原价解析错误", 750, product.getOriginalPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣价解析错误", new Date(1368547200000L), product.getEndTme());
	}
	
	
	/**
	 * 测试悄悄物语抓取脚本
	 * @throws IOException
	 */
	@Test
	public void testIhush() throws IOException{
		Ihush vp = new Ihush();	
		File destFile = saveUrlToFile("http://www.ihush.com/product_815785_f2_gcHNwaWQ9Njc4OSYlMT0m.html", defaultEncoding);
		Document doc = Jsoup.parse(destFile, defaultEncoding, "http://www.fclub.cn/");
		SnatchProduct product = vp.parseProduct("http://www.ihush.com/product_815785_f2_gcHNwaWQ9Njc4OSYlMT0m.html", doc);
		Assert.assertEquals("品牌解析错误", "Fancy Chic", product.getProductBrand());
		Assert.assertEquals("商品分类解析错误", "钱包", product.getProductCategory());
		Assert.assertEquals("商品性别解析错误", "FEMALE", product.getProductGender());
		Assert.assertEquals("商品图片解析错误", "http://f7a1.ihushcdn.com/cnproduct/product/s60/2013/05/07/32/af2/35f056fc87a67e578507795a117.jpg", product.getProductImages().split(",")[0]);
		Assert.assertEquals("商品名称解析错误", "暗红色时尚铆钉缀饰拼接 牛皮零钱包 BG04018-1211040182", product.getProductName());
		Assert.assertEquals("商品剩余数量解析错误", 0, product.getProductRemainCount().intValue());
		Assert.assertEquals("商品销售数量解析错误", 0, product.getProductSoldCount().intValue());
		Assert.assertEquals("商品链接解析错误", "http://www.ihush.com/product_815785_f2_gcHNwaWQ9Njc4OSYlMT0m.html", product.getProductUrl());
		Assert.assertEquals("折扣价解析错误", 199, product.getDiscountPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣率析错误", 1.2, product.getDiscountRate().floatValue(), 0.001);
		Assert.assertEquals("原价解析错误", 1699, product.getOriginalPrice().floatValue(), 0.001);
		Assert.assertEquals("折扣价解析错误", new Date(1368633540000L), product.getEndTme());
	}
	
	/**
	 * 下载网页到文件，如果已经存在则不下载
	 * @param urlStr
	 * @param encoding
	 * @return
	 */
	public File saveUrlToFile(String urlStr, String encoding) {
		String fileName = urlToFileName(urlStr);
		File destFile = new File(testFilePath, fileName);
		if(destFile.exists() && destFile.isFile()){
			return destFile;
		}
		if(!urlStr.startsWith("http://")){
			urlStr = urlStr + "http://";
		}
		BufferedReader br = null;
		PrintWriter pw = null;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection(); 
			br = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
			pw = new PrintWriter(destFile);
			String line = null; 
			while((line = br.readLine()) != null){
				pw.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(br != null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(pw != null){
				pw.close();
			}
		}
		return destFile;
	}
	
	public String urlToFileName(String url){
		if(url.startsWith("http://")){
			url = url.substring("http://".length());
		}else if(url.startsWith("https://")){
			url = url.substring("https://".length());
		}
		String result = url.replaceAll("[/]", "_");
		return result;
	}
	
}
