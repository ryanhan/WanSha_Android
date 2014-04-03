package com.ipang.wansha.dao.impl;

import java.util.ArrayList;

import com.ipang.wansha.dao.ProductDao;
import com.ipang.wansha.model.Currency;
import com.ipang.wansha.model.Language;
import com.ipang.wansha.model.Product;
import com.ipang.wansha.model.TimeUnit;

public class ProductDaoImpl implements ProductDao {

	@Override
	public ArrayList<Product> getProductList(String cityName) {

		ArrayList<Product> products = new ArrayList<Product>();
		Product product1 = getProductDetail("");
		products.add(product1);
		products.add(product1);
		products.add(product1);
		return products;
	}

	@Override
	public int getProductCount(String cityName) {
		return 10;
	}

	@Override
	public Product getProductDetail(String productName) {

		Product detail = new Product();
		detail.setProductName("梵蒂冈博物馆一日游");
		detail.setDuration(1);
		detail.setTimeUnit(TimeUnit.DAY);
		int[] starCount = new int[5];
		starCount[0] = 100;
		starCount[1] = 18;
		starCount[2] = 12;
		starCount[3] = 5;
		starCount[4] = 0;
		detail.setStarCount(starCount);

		detail.setPrice(1000);
		detail.setCurrency(Currency.CHINESEYUAN);
		ArrayList<String> images = new ArrayList<String>();
		images.add("vatican");
		images.add("rome");
		images.add("vatican1");
		images.add("vatican2");
		images.add("vatican3");
		detail.setProductImages(images);
		detail.setCityName("罗马");
		detail.setCountryName("意大利");

		ArrayList<Language> lans = new ArrayList<Language>();
		lans.add(Language.CHINESE);
		lans.add(Language.ENGLISH);
		detail.setSupportLanguage(lans);

		detail.setOverview("梵蒂冈博物馆位于罗马市中心的天主教国家梵蒂冈，是世界上最小的国家的博物馆。");
		detail.setHighlight("梵蒂冈博物馆的起源可以追溯到500年前购买的一座大理石雕像。这座名为拉奥孔与儿子们的雕像于1506年1月14日在圣母玛利亚主教堂附近的一个葡萄园里发掘出来。教皇儒略二世派Giuliano da Sangallo和米开朗琪罗去查看发掘成果。在他们的推荐下，教皇当机立断从葡萄园主那里买下了雕像。在发掘出雕像整一个月后，教皇就在梵蒂冈向公众进行了展示。"
				+ "\n2006年10月梵蒂冈博物馆庆祝建馆500周年之际，向公众开放了梵蒂冈山丘上公墓的发掘现场。"
				+ "\n梵蒂冈博物馆(The Vatican Museum)是西欧收费最贵的博物馆之一，而且开馆时间奇短——当地时间中午13:30就闭馆了。每个月末的周日，梵蒂冈博物馆免票，可以节省下来十几欧元。于是很多游客都会排长队进馆。在这里，我见识了人生中最壮观的一次排队。这只是队伍中间的一段，在它首尾还有非常非常长的一大段。"
				+ "\n梵蒂冈博物馆，是世界上最早的博物馆之一，早在公元5世纪就有了雏形。16世纪时，博物馆与圣彼得大教堂同时扩建，总面积为5.5公顷，为故宫博物院的 1/13，展出面积与故宫相仿。"
				+ "\n梵蒂冈博物馆，拥有12个陈列馆和5条艺术长廊，汇集了希腊、罗马的古代遗物以及文艺复兴时期的艺术精华，大都是无价之宝。");

		return detail;
	}

}
