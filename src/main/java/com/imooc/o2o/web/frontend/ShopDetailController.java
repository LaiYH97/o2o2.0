package com.imooc.o2o.web.frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.service.ProductCategoryService;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping(value="/frontend")
public class ShopDetailController {

	@Autowired
	private ShopService shopService;
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;
	
	/**
	 * 查询店铺信息和店铺下的商品分类
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/listshopdetailpageinfo",method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> listShopDetailInfo(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取前台传过来的shopId
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		Shop shop = null;
		List<ProductCategory> productCategoryList = null;
		if(shopId != -1) {
			//获取店铺信息
			shop = shopService.getByShopId(shopId);
			//获取店铺的商品分类
			productCategoryList = productCategoryService.getProductCategoryList(shopId);
			modelMap.put("shop", shop);
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		}else {
			modelMap.put("success","false");
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;
	}
	
	/**
	 * 分页展示商铺下的商品列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="listproductsbyshop",method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> listProductsByShop(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取页码
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		//空值判断
		if((pageIndex > -1)&&(pageSize > -1)&&(shopId > -1)) {
			//获取筛选条件
			long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
			String productName = HttpServletRequestUtil.getString(request, "productName");
			//组合查询
			Product productCondition = compactProductCondition4Search(shopId,productCategoryId,productName);
			//
			ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
			modelMap.put("productList", pe.getProductList());
			modelMap.put("count", pe.getCount());
			modelMap.put("success", true);
		}else {
			modelMap.put("success", "false");
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}
	
	private Product compactProductCondition4Search(long shopId,long productCategoryId,String productName) {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		if(productCategoryId != -1) {
			//查询某个商品类别下面的商品列表
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		if(productName != null) {
			productCondition.setProductName(productName);
		}
		//只允许选出状态为上架的商品
		productCondition.setEnableStatus(1);
		return productCondition;
	}
}
