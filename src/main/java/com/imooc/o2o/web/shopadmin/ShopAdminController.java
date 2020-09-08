package com.imooc.o2o.web.shopadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="shopadmin", method=RequestMethod.GET)
public class ShopAdminController {

	/**
	 * 路由到店铺操作页面
	 * @return
	 */
	@RequestMapping(value = "/shopoperation")
	public String shopOperation() {
		return "shop/shopoperation"; //返回中间路径
	}
	
	/**
	 * 路由到店铺列表页面
	 * @return
	 */
	@RequestMapping(value = "/shoplist")
	public String shopList() {
		return "shop/shoplist"; //返回中间路径
	}
	
	/**
	 * 路由到店铺管理页面
	 * @return
	 */
	@RequestMapping(value = "/shopmanagement")
	public String shopManagement() {
		return "shop/shopmanagement"; //返回中间路径
	}
	
	/**
	 * 路由到商品类别页面
	 * @return
	 */
	@RequestMapping(value="/productcategorymanagement")
	private String productCategoryManage() {
		return "shop/productcategorymanagement";
	}
	
	/**
	 *  路由到商品操纵页面（添加或修改）
	 * @return
	 */
	@RequestMapping(value="/productoperation")
	private String productOperation() {
		return "shop/productoperation";
	}
	
	/**
	 *  路由到商品列表展示页面（编辑、上下架、预览）
	 * @return
	 */
	@RequestMapping(value="/productmanagement")
	public String productManagement() {
		//转发至商品管理页
		return "shop/productmanagement";
	}
	
	/**
	 *  路由到店铺授权页面
	 * @return
	 */
    @RequestMapping(value = "/shopauthmanagement")
    public String shopAuthManagement() {
        return "shop/shopauthmanagement";
    }
    
    /**
	 *  路由到授权信息修改页面
	 * @return
	 */
    @RequestMapping(value = "/shopauthedit")
    public String shopAuthEdit() {
        return "shop/shopauthedit";
    }
    
    /**
	 *  路由到添加员工成功
	 * @return
	 */
    @RequestMapping(value = "/operationsuccess")
    public String operationSuccess() {
        return "shop/operationsuccess";
    }

    /**
	 *  路由到添加员工失败
	 * @return
	 */
    @RequestMapping(value = "/operationfail")
    public String operationFail() {
        return "shop/operationfail";
    }
    
    /*
             * 转发至店铺的消费记录的页面
     */
    @RequestMapping(value = "/productbuycheck")
    public String productBuyCheck() {
        return "shop/productbuycheck";
    }
    
    /*
             * 店铺用户积分统计路由
     */
    @RequestMapping(value = "/usershopcheck")
    public String userShopCheck() {
        return "shop/usershopcheck";
    }
    
    /*
             * 店铺用户积分兑换路由
     */
    @RequestMapping(value = "/awarddelivercheck")
    public String awardDeliverCheck() {    
        return "shop/awarddelivercheck";
    }
    
    /*
             *  奖品管理页路由
     */
    @RequestMapping(value = "/awardmanagement")
    public String awardManagement() {
        return "shop/awardmanagement";
    }

    /*
             *  奖品编辑页路由
     */
    @RequestMapping(value = "/awardoperation")
    public String awardEdit() {
        return "shop/awardoperation";
    }
}
