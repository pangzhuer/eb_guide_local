package com.duowan.ebguide.crawl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.duowan.ebguide.crawl.Crawl;
import com.duowan.ebguide.crawl.Fclub;
import com.duowan.ebguide.crawl.Ihush;
import com.duowan.ebguide.crawl.ThreadCrawl;
import com.duowan.ebguide.crawl.Tianpin;
import com.duowan.ebguide.crawl.Vipshop;
import com.duowan.ebguide.dao.SnatchProductMapper;
import com.duowan.ebguide.model.SnatchProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-context.xml")
public class SnatchProductTest {
	
	
	@Autowired
	SnatchProductMapper snatchProductMapper;


	public void setSnatchProductMapper(SnatchProductMapper snatchProductMapper) {
		this.snatchProductMapper = snatchProductMapper;
	}
	
	@Test
	public void crawlTianPin() throws Exception{
		crawl(new Tianpin(), "http://www.tianpin.com/");
	}

	
	@Test
	public void crawlVipshop() throws Exception{
		crawl(new Vipshop(), "http://shop.vipshop.com/gz_new_visitor.html");
	}
	
	@Test
	public void crawlFclub() throws Exception{
		crawl(new Fclub(), "http://www.fclub.cn/");
	}
	
	@Test
	public void crawlIhush() throws Exception{
		crawl(new Ihush(), "http://www.ihush.com/");
	}
	
	
	
	public void crawl(Crawl crawl, String url) throws Exception{
		Set<String> listUrls = crawl.getProductListUrls(url, 1);
		for(String listUrl : listUrls){
			Set<String> set= crawl.getProductUrls(listUrl, 100);
			Iterator<String> iterator = set.iterator();
			int index = 0;
			int poolSize = 40;
			ArrayList<String> poolUrls = new ArrayList<String>();
			while(iterator.hasNext()){
				index += 1;
				if(index % poolSize == 0){
					crawlPoolUrls(poolUrls, crawl);
					poolUrls = new ArrayList<String>();
				}else{
					poolUrls.add(iterator.next());
				}
			}
			crawlPoolUrls(poolUrls, crawl);
		}
	}
	
	
	private void crawlPoolUrls(ArrayList<String> poolUrls, Crawl crawl) throws InterruptedException, ExecutionException {
		ExecutorService exec = Executors.newCachedThreadPool();
		ArrayList<Future<SnatchProduct>> results = new ArrayList<Future<SnatchProduct>>();
		for(String url : poolUrls){
			results.add(exec.submit(new ThreadCrawl(crawl, url)));
		}
		for(Future<SnatchProduct> fs : results){
			SnatchProduct product = fs.get();
			if(product != null){
				snatchProductMapper.replaceByProductUrl(product);
			}
		}
		exec.shutdown();
	}
	

}
