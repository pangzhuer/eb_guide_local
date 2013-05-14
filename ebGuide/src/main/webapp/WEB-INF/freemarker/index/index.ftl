<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<title>正品商品导购</title>
	<style>
	body{
		padding : 0;
		margin: 0 auto;
		width:1003px;
	}
	#head {
		width : 100%;
		height : 100px;
		background-color: #ccc;
	}
	#category {
		border-bottom: 1px solid blue;
		margin: 10px 0;
		padding-top: 10px;
		padding-bottom: 10px;
	}
	.detail{
		width : 330px;
		overflow: hidden;
		display:inline;
		float:left;
	}
	
	
	</style>
</head>
<body>
	
	<div id="head">
		
	</div>
	<div id="category">
		<div class="websiteCategory">
			<span> 按网站分类:</span>
			<span><a href="/index/list?website=VIPSHOP">唯品会</a><span>
			<span><a href="/index/list?website=TIANPIN">天品网</a><span>
			<span><a href="/index/list?website=IHUSH">悄悄物语</a><span>
			<span><a href="/index/list?website=FCLUB">聚尚网</a><span>
		</div>
	</div>
	<div id="content">
		
		<#list products as product>
			<div class="detail">
				<#assign imgList=product.productImages?split(",")>
				<a href="${product.productUrl}" target="_blank">
				<img src="${imgList[0]}" width="310px" height="350px"></img>
				</a>
				<div>
				<div>${product.productName}</div>
				<div>折扣价：￥${product.discountPrice}（${product.discountRate}折）</div>
				<del>原价：￥${product.originalPrice}</del>
				</div>
			</div>
		</#list>
	</div>
</body>