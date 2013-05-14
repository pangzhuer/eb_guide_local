package com.duowan.ebguide.dao;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.duowan.ebguide.model.SnatchProduct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-context.xml")
public class SnatchMapperTest {

	@Autowired
	SnatchProductMapper snatchProductMapper;


	public void setSnatchProductMapper(SnatchProductMapper snatchProductMapper) {
		this.snatchProductMapper = snatchProductMapper;
	}

	
	@Test
	public void testSelect(){
		List<SnatchProduct> products = snatchProductMapper.selectByLimit(0, 10, null);
		Assert.assertEquals(10, products.size());
	}
}
