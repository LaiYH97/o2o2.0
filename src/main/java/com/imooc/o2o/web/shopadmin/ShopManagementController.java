package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exception.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

/* 首先在Controller里面定义了SpringMVC相关的标签，这个标签包含了Controller的访问路径
 * 以及registerregisterShop方法的访问路径*/
@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {

	// 同时给它在执行的时候通过Spring容器注入之前实现好的ShopService实现类，用来提供addShop的服务。
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private ShopCategoryService shopCategoryService;
	
	@Autowired
	private AreaService areaService;
		
	/**
	 * 在商店信息页面的店铺类别和区域下拉框展示数据库的数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getshopinitinfo",method = RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> getShopInitInfo(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		List<Area> areaList = new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
			areaList = areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success",true);
		}catch(Exception e) {
			modelMap.put("success",false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;		
	}
	
	/**
	 * 在商店信息页面完成店铺信息的注册
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/registershop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> registerShop(HttpServletRequest request){
		//0 先定义一个返回值
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//1 判断验证码的情况
		if(!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", "false");
			modelMap.put("message", "输入了错误的验证码");
			return modelMap;
		}
		//2 接收并转换相应的参数，包括店铺信息以及图片信息
		//2.1 获取请求头的店铺信息
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		//2.2 将json转换为Shop实例
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		}catch(Exception e) {
			modelMap.put("success",false);
			modelMap.put("errMeg",e.getMessage());
			return modelMap;
		}
		//2.3 将请求中的文件流剥离出来，通过CommonsMultipartFile去接收
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if(commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		}else {
			modelMap.put("success",false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		//3 注册店铺
		if(shop != null && shopImg != null) {
			PersonInfo owner = (PersonInfo)request.getSession().getAttribute("user");
			shop.setOwner(owner);
			//3.1 将店铺缩略图添加进shop对象中
			ShopExecution se;
			try {
				ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
				se = shopService.addShop(shop, imageHolder);
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					/*
					 * 3.2 在店铺添加完成后，还需要做Session的操作。用户和店铺的关系是一对多的，即一个owner能够创建多个店铺。
					 * 因此需要在Session里面保存一个店铺列表来显示用户可以操作的店铺。
					 */
					@SuppressWarnings("unchecked")
					List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
					if(shopList == null || shopList.size() == 0) {
						shopList = new ArrayList<Shop>();
					}
					shopList.add(se.getShop());
					request.getSession().setAttribute("shopList", shopList);
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());	
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());	
			}
			return modelMap;
		}else {
			modelMap.put("success",false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
	}
	
	/**
	 * 根据shop_id获取shop对象的信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		Long shopId = HttpServletRequestUtil.getLong(request, "shopid");
		if(shopId > -1) {
			try {
				Shop shop = shopService.getByShopId(shopId);
				List<Area> areaList = areaService.getAreaList();
				modelMap.put("shop", shop);
				modelMap.put("areaList", areaList);
				modelMap.put("success", true);
			}catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;	
	}
	
	/**
	 * 在商店信息页面完成店铺信息的修改
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request){
		//0 先定义一个返回值
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//1 判断验证码的情况
		if(!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", "false");
			modelMap.put("message", "输入了错误的验证码");
			return modelMap;
		}
		//2 接收并转换相应的参数，包括店铺信息以及图片信息
		//2.1 获取请求头的店铺信息
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		//2.2 将json转换为Shop实例
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			shop = mapper.readValue(shopStr, Shop.class);
		}catch(Exception e) {
			modelMap.put("success",false);
			modelMap.put("errMeg",e.getMessage());
			return modelMap;
		}
		//2.3 将请求中的文件流剥离出来，通过CommonsMultipartFile去接收
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if(commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest.getFile("shopImg");
		}
		
		//3 修改店铺，由于图片是可上传、可不上传的，因此图片非空判断去除；取而代之确保shopId不为空
		if(shop != null && shop.getShopId() != null) {
			//3.1 将店铺缩略图添加进shop对象中
			ShopExecution se;
			try {
				if(shopImg == null) {
					se = shopService.modifyShop(shop, null);
				}else {
					ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
					se = shopService.modifyShop(shop, imageHolder);
				}
				if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());	
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());	
			}
			return modelMap;
		}else {
			modelMap.put("success",false);
			modelMap.put("errMsg", "请输入店铺Id");
			return modelMap;
		}
	}
	
	/**
	 * 根据用户的信息返回用户创建的店铺列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getshoplist",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getShopList(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<>();
		//确保user不为空
		PersonInfo user = new PersonInfo();
		//user.setUserId(1L);
		//user.setName("测试");
		//request.getSession().setAttribute("user", user);
		user = (PersonInfo) request.getSession().getAttribute("user");
		try {
			Shop shopCondition = new Shop();
			shopCondition.setOwner(user);
			ShopExecution se = shopService.getShopList(shopCondition, 0, 100);
			modelMap.put("shopList", se.getShopList());
			//列出店铺成功后，将店铺放入Session中作为权限验证的依据，即该账号只能操作自己的店铺
			request.getSession().setAttribute("shopList", se.getShopList());
			modelMap.put("user", user);
			modelMap.put("success", true);
		}catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	/**
	 * 管理Session相关的操作
	 * 当你直接访问这个页面且不经过登录，或不从店铺列表进来，就重定向到店铺列表。说明这次是违规操作，不能直接访问这个页面。
	如果之前已经登录过这个系统，就有权限访问店铺管理页，我们就将重定向设为false。一旦传入店铺ID，就有权限对店铺做操作。
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getshopmanagementinfo",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getShopManagementInfo(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<>();
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if(shopId <= 0) {
			Object currentShopObj = request.getSession().getAttribute("currentShop");
			if(currentShopObj == null) {
				modelMap.put("redirect",true);
				modelMap.put("url","/o2o/shopadmin/shoplist");
			}else {
				Shop currentShop = (Shop) currentShopObj;
				modelMap.put("redirect", false);
				modelMap.put("shopId",currentShop.getShopId());
			}
		}else {
			Shop currentShop = new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", currentShop);
			modelMap.put("redirect", false);
		}
		return modelMap;
		
	}
	
	/**
	 * 将file类型转化成inputStream类型
	 * @param inputStream
	 * @param file
	 */
	/*
	private static void inputStreamToFile(InputStream inputStream, File file) {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = inputStream.read(buffer)) > 0) {
				os.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			throw new RuntimeException("调用inputStreamToFile产生异常：" + e.getMessage());
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				throw new RuntimeException("调用inputStreamToFile产生异常：" + e.getMessage());
			}
		}
	}
	*/
}
