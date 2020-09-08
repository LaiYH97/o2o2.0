package com.imooc.o2o.web.frontend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value="/frontend")
public class FrontendController {

	/**
	 * 建立路由转发到前台首页
	 * @return
	 */
	@RequestMapping(value="/index",method=RequestMethod.GET)
	private String index() {
		return "frontend/index";
	}
	
	/**
	 * 建立路由转发到商品列表页
	 * @return
	 */
	@RequestMapping(value="/shoplist",method=RequestMethod.GET)
	private String shopList() {
		return "frontend/shoplist";
	}
	
	/**
	 * 建立路由转发到商店详情页
	 * @return
	 */
	@RequestMapping(value="/shopdetail",method=RequestMethod.GET)
	private String shopDetail() {
		return "frontend/shopdetail";
	}
	
	/**
	 * 建立路由转发到商品详情页
	 * @return
	 */
	@RequestMapping(value="/productdetail",method=RequestMethod.GET)
	private String productDetail() {
		return "frontend/productdetail";
	}
	
	/**
             * 店铺的奖品列表页路由
     * @return
     */
    @RequestMapping(value = "/awardlist", method = RequestMethod.GET)
    public String showAwardList() {
        return "frontend/awardlist";
    }
    
    /**
             * 奖品兑换列表页路由
     * @return
     */
    @RequestMapping(value = "/pointrecord", method = RequestMethod.GET)
    public String showPointRecord() {
        return "frontend/pointrecord";
    }
    
    /**
             * 奖品详情页路由
     * @return
     */
    @RequestMapping(value = "/myawarddetail", method = RequestMethod.GET)
    public String showMyAwardDetail() {
        return "frontend/myawarddetail";
    }
    
    /**
             * 消费记录列表页路由
     *
     * @return
     */
    @RequestMapping(value = "/myrecord", method = RequestMethod.GET)
    public String showMyRecord() {
        return "frontend/myrecord";
    }

    /**
             * 用户各店铺积分信息页路由
     *
     * @return
     */
    @RequestMapping(value = "/mypoint", method = RequestMethod.GET)
    public String showMyPoint() {
        return "frontend/mypoint";
    }
}
