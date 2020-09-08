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

import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping(value="frontend")
public class ShopListController {

	@Autowired
	private AreaService areaService;
	@Autowired
	private ShopCategoryService shopCategoryService;
	@Autowired
	private ShopService shopService;
	/**
	 * 返回商铺分类和区域列表，用于用户筛选
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/listshopspageinfo", method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> listShopsPageInfo(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//尝试从请求中获取parentId
		long parentId = HttpServletRequestUtil.getLong(request, "parentId");
		List<ShopCategory> shopCategoryList = null;
		if(parentId>-1) {
			//如果parentId存在，则取出该商铺分类下的次商铺分类
			try {
				ShopCategory shopCategoryCondition = new ShopCategory();
				ShopCategory parent = new ShopCategory();
				parent.setShopCategoryId(parentId);
				shopCategoryCondition.setParent(parent);
				shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
				
			}catch(Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
		}else {
			try {
				//如果parentId不存在，则取出所有一级商铺分类下的全部商铺（用户在首页点击“全部商店”）
				shopCategoryList  = shopCategoryService.getShopCategoryList(null);
			}catch (Exception e) {
				modelMap.put("success",false);
				modelMap.put("errMsg",e.getMessage());
			}
		}
		modelMap.put("shopCategoryList", shopCategoryList);
		List<Area> areaList = null;
		try {
			//获取区域列表信息
			areaList = areaService.getAreaList();
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
			return modelMap;
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
		
	}
	
	@RequestMapping(value="/listshops",method=RequestMethod.GET)
	@ResponseBody
	private Map<String,Object> listShops(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//获取页数
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		//获取每页的条数
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		//非空判断
		if((pageIndex > -1)&&(pageSize>-1)) {
			//试着获取一级类别Id
			long parentId = HttpServletRequestUtil.getLong(request, "parentId");
			//试着获取二级类别
			long shopCategoryId = HttpServletRequestUtil.getLong(request, "shopCategoryId");
			//获取区域
			int areaId = HttpServletRequestUtil.getInt(request, "areaId");
			//获取店铺名称
			String shopName = HttpServletRequestUtil.getString(request, "shopName");
			//获取组合之后的查询条件
			Shop shopCondition = compactShopCondition4Search(parentId,shopCategoryId,areaId,shopName);
			ShopExecution se = shopService.getShopList(shopCondition, pageIndex, pageSize);
			modelMap.put("success", true);
			modelMap.put("shopList", se.getShopList());
			modelMap.put("count", se.getCount());
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex");
		}
		return modelMap;
	}
	private Shop compactShopCondition4Search(long parentId,long shopCategoryId,int areaId,String shopName) {
		Shop shopCondition = new Shop();
		if(parentId != -1) {
			ShopCategory childCategory = new ShopCategory();
			ShopCategory parentCategory = new ShopCategory();
			parentCategory.setShopCategoryId(parentId);
			childCategory.setParent(parentCategory);
			shopCondition.setShopCategory(childCategory);
		}
		if(shopCategoryId != -1L) {//存在二级类别，就取二级类别，一级类别会被覆盖
			ShopCategory shopCategory = new ShopCategory();
			shopCategory.setShopCategoryId(shopCategoryId);
			shopCondition.setShopCategory(shopCategory);
		}
		if(areaId != -1L) {
			Area area = new Area();
			area.setAreaId(areaId);
			shopCondition.setArea(area);
		}
		if(shopName != null) {
			shopCondition.setShopName(shopName);
		}
		shopCondition.setEnableStatus(1);
		return shopCondition;
		
	}
}
