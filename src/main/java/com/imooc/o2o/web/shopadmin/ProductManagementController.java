package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.service.ProductCategoryService;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shopadmin")
public class ProductManagementController {

	Logger logger = LoggerFactory.getLogger(ProductManagementController.class);
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductCategoryService productCategoryService;
	
	//支持上传图片详情图的最大数值
	private static final int IMAGEMAXCOUNT = 6;
	
	/**
	 * 添加商品
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String,Object> addProduct(HttpServletRequest request){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		//验证码校验
		if(!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接收前端参数的变量的初始化，包括商品、缩略图、详情图列表实体类
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		MultipartHttpServletRequest multipartRequest = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		
		try {
			//若请求中存在文件流，则取出相关的文件（包括缩略图和详情图）
			if(multipartResolver.isMultipart(request)) {
				multipartRequest = (MultipartHttpServletRequest) request;
				//取出缩略图并构建ImageHolder的对象
				CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
				if(thumbnailFile != null) {
					thumbnail = handleImage(request,thumbnail,productImgList);
				}
			}else {
				modelMap.put("success", false);
				modelMap.put("errMsg", "上传图片不为空");
				return modelMap;
			}
		}catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		
		try {
			//
			product = mapper.readValue(productStr,Product.class);
			
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//若product信息，缩略图、详情图不为空，则开始进行商品添加操作
		if(product != null && thumbnail != null && productImgList.size() != 0) {
			try {
				//从session中获取当前店铺的ID，并赋值给product，减少对前端数据的依赖
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				product.setShop(currentShop);
				//执行添加操作
				ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);
				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			}catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		}else {
			modelMap.put("success",false);
			modelMap.put("errMsg", "请输入商品信息");
		}
		return modelMap;
	}
	
	/**
	 * 通过商品id查询唯一的商品信息和商品类别列表
	 * @param productId
	 * @return
	 */
	@RequestMapping(value="/getproductbyid", method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> getProductById(@RequestParam Long productId){
		Map<String, Object> modelMap = new HashMap<>();
		//非空判断
		if(productId > -1) {
			//获取商品信息
			Product product = productService.getProductById(productId);
			System.out.println(product.getShop().getShopId());
			//获取该店铺下的商品类别列表
			List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(product.getShop().getShopId());
			modelMap.put("product", product);
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty productId");
		}
		return modelMap;
	}
	
	
	/**
	 * 编辑商品信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/modifyproduct", method=RequestMethod.POST)
	@ResponseBody
	private Map<String,Object> modifyProduct(HttpServletRequest request){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//是商品编辑时候调用,还是上下架操作时候调用
		//若为前者则进行验证码判断，后者则跳过验证码判断
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		//验证码判断
		if(!statusChange && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接收前端参数的变量的初始化，包括商品，缩略图，详情图列表实体类
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		//若请求中存在文件流，则取出相关的文件（包括缩略图和详情图）
		try {
			if(multipartResolver.isMultipart(request)) {
				thumbnail = handleImage(request, thumbnail, productImgList);
			}
		}catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		try {
			String productStr = HttpServletRequestUtil.getString(request, "productStr");
			//尝试获取从前端传过来的表单String流，并将其转化为Product对象
			product = mapper.readValue(productStr, Product.class);
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//非空判断
		if(product != null) {
			try {
				//从session中获取当前店铺的ID并赋值给product，减少对前端数据的依赖
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				product.setShop(currentShop);
				//开始进行商品信息变更操作
				ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);
				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("false", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			}catch(RuntimeException e) {
				modelMap.put("success", false);
				return modelMap;
			}
		}
		return modelMap;
	}
	
	/**
	 * 完成图片添加
	 * @param request
	 * @param thumbnail
	 * @param productImgList
	 * @return
	 * @throws IOException
	 */
	private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail, List<ImageHolder> productImgList) throws IOException{
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		//取出缩略图并构建ImageHolder对象
		CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
		if(thumbnailFile != null) {
			thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());
		}
		//取出详情图并构建List<ImageHolder>列表对象，最多支持6张图片上传
		for(int i=0;i<IMAGEMAXCOUNT;i++) {
			CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartRequest.getFile("productImg"+i);
			if(productImgFile != null) {
				//若取出的第i个详情图片流不为空，则将其加入详情图列表
				ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(),productImgFile.getInputStream());
				productImgList.add(productImg);
			}else {
				//若取出的第i个图片文件流为空，则终止循环
				break;
			}
		}
		return thumbnail;
	}
	
	@RequestMapping(value="/getproductlistbyshop",method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> getProductListByShop(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取从前台传过来的页码
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		//获取从前台传过来的每页要求返回的商品数上线
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		//从当前session中获取店铺信息，主要是shopId
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		
		//控制判断
		if((pageIndex > -1) && (pageSize > -1)&& (currentShop != null) && (currentShop.getShopId() != null)) {
			//获取传入的需要检索的条件，包括是否需要从某个商品类别以及模糊查找商品，去筛选某个店铺下的商品列表
			//筛选的条件可以进行排列组合
			long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
			String productName = HttpServletRequestUtil.getString(request, "productName");
			Product productCondition = compactProductCondition(currentShop.getShopId(),productCategoryId,productName);
			//传入查询条件以及分页信息进行查询，返回相应商品列表以及总数
			ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
			modelMap.put("productList", pe.getProductList());
			modelMap.put("count", pe.getCount());
			modelMap.put("success", true);
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
			logger.info("页码"+pageIndex+"\t"+pageSize);
			logger.info("店铺"+currentShop.getShopId());
		}
		return modelMap;
	}
	
	private Product compactProductCondition(long shopId,long productCategoryId,String productName) {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		//若有指定类别的要求，则添加进入
		if(productCategoryId != -1L) {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		//若有商品名模糊查询的要求，则添加进去
		if(productName != null) {
			productCondition.setProductName(productName);
		}
		return productCondition;
	}
}
