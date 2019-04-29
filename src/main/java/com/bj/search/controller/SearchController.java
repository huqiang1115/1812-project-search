package com.bj.search.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bj.common.pojo.Product;
import com.bj.common.utils.ObjectUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@RestController
public class SearchController {
	//注入已经创建好的TransportClient
	@Autowired
	private TransportClient search;
	//根据productName实现商品的数据查询
	@RequestMapping("search")
	public List<Product> queryProduct(Integer page,Integer rows,String q) throws JsonParseException, JsonMappingException, IOException{
		//生成query对象
		MatchQueryBuilder query = 
			QueryBuilders.matchQuery("productName", q);
		//调用客户端的search方法,将query传递,将分页条件传递
		//调整起始位置
		int start=(page-1)*rows;
		SearchResponse response = search.prepareSearch("emindex").setQuery(query)
		.setFrom(start).setSize(rows).get();
		//获取响应中的hits对象
		SearchHits hits = response.getHits();
		System.out.println("一共查到"+hits.getTotalHits());
		List<Product> pList=new ArrayList<Product>();
		//循环遍历查到的结果
		for (SearchHit hit : hits) {
			//封装一个product对象,获取hit中的source字符串
			String json=hit.getSourceAsString();//product的json字符串
			Product product=
			ObjectUtil.mapper.readValue(json, Product.class);
			pList.add(product);
		}
		return pList;
	}
}
