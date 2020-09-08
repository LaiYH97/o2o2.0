package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.EchartSeries;
import com.imooc.o2o.dto.EchartXAxis;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.UserAccessToken;
import com.imooc.o2o.dto.UserProductMapExecution;
import com.imooc.o2o.dto.WechatInfo;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductSellDaily;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.UserProductMap;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.enums.UserProductMapStateEnum;
import com.imooc.o2o.service.ProductSellDailyService;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.service.ShopAuthMapService;
import com.imooc.o2o.service.UserProductMapService;
import com.imooc.o2o.service.WechatAuthService;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.wechat.WechatUtil;

@Controller
@RequestMapping("/shopadmin")
public class UserProductManagementController {

	@Autowired
    private UserProductMapService userProductMapService;
	
    @Autowired
    private ProductSellDailyService productSellDailyService;
    
    @Autowired
    private WechatAuthService wechatAuthService;
    
    @Autowired
    private ShopAuthMapService shopAuthMapService;
    
    @Autowired
    private ProductService productService;


    @RequestMapping(value = "/listuserproductmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listUserProductMapsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        //获取当前店铺信息
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null)
            && (currentShop.getShopId() != null)) {
            UserProductMap userProductMapCondition = new UserProductMap();
            userProductMapCondition.setShop(currentShop);
            String productName = HttpServletRequestUtil.getString(request, "productName");

            //启用模糊查询
            if (productName != null) {
                Product product = new Product();
                product.setProductName(productName);
                userProductMapCondition.setProduct(product);
            }
            UserProductMapExecution ue = userProductMapService.listUserProductMap(userProductMapCondition, pageIndex, pageSize);
            modelMap.put("userProductMapList", ue.getUserProductMapList());
            modelMap.put("count", ue.getCount());
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取统计数据失败");
        }
        return modelMap;
    }

    @RequestMapping(value = "/listproductselldailyinfobyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listProductSellDailyInfobyShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        //获取当前店铺信息
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        if (currentShop != null && currentShop.getShopId() != null){
            ProductSellDaily productSellDailyCondition = new ProductSellDaily();
            productSellDailyCondition.setShop(currentShop);
            Calendar calendar = Calendar.getInstance();
            //获取昨天的日期
            calendar.add(Calendar.DATE, -1);
            Date endTime = calendar.getTime();
            //获取七天前的日期
            calendar.add(Calendar.DATE, -6);
            Date beginTime = calendar.getTime();
            //获取过去七天的销售情况
            List<ProductSellDaily> productSellDailyList = productSellDailyService.listProductSellDaily(productSellDailyCondition, beginTime, endTime);
            //指定日期格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            // 商品名列表，保证唯一性
            LinkedHashSet<String> legendData = new LinkedHashSet<String>();
            // x轴数据
            LinkedHashSet<String> xData = new LinkedHashSet<String>();
            //定义series
            List<EchartSeries> series = new ArrayList<>();
            //日销量
            List<Integer> totalList = new ArrayList<>();
            //当前商品名，默认为空
            String currentProductName = "";
            for (int i = 0; i < productSellDailyList.size(); i++) {
                ProductSellDaily productSellDaily = productSellDailyList.get(i);
                // 自动去重
                legendData.add(productSellDaily.getProduct().getProductName());
                xData.add(sdf.format(productSellDaily.getCreateTime()));
                if (!currentProductName.equals(productSellDaily.getProduct().getProductName()) && !currentProductName.isEmpty()) {
                    //如果currentProductName不等去获取的商品名，或者已经遍历到末尾，且currentProductName不为空
                    //则是遍历了到下一个商品的日销售信息，
                    //包括了商品名以及商品对应的统计日期以及销量
                    EchartSeries es = new EchartSeries();
                    es.setName(currentProductName);
                    //等同于克隆出一个新的list
                    es.setData(totalList.subList(0, totalList.size()));
                    series.add(es);
                    //重置totalList
                    totalList = new ArrayList<>();
                    currentProductName = productSellDaily.getProduct().getProductName();
                    totalList.add(productSellDaily.getTotal());
                } else {
                    //如果晒是当前productId则继续遍历
                    totalList.add(productSellDaily.getTotal());
                    currentProductName = productSellDaily.getProduct().getProductName();
                }
                //到达队列末尾
                if (i == productSellDailyList.size() - 1) {
                    EchartSeries es = new EchartSeries();
                    es.setName(currentProductName);
                    es.setData(totalList.subList(0, totalList.size()));
                    series.add(es);
                }
            }
            modelMap.put("series", series);
            modelMap.put("legendData", legendData);
            //拼接处xAixs
            List<EchartXAxis> xAxis = new ArrayList<>();
            EchartXAxis exa = new EchartXAxis();
            exa.setData(xData);
            xAxis.add(exa);
            modelMap.put("xAxis", xAxis);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "empty shopId");
        }
        return modelMap;
    }

    @RequestMapping(value = "/adduserproductmap", method = RequestMethod.GET)
    private String addUserProductMap(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取微信授权信息
        WechatAuth auth = getOperatorInfo(request);
        if (auth != null) {
            PersonInfo operator = auth.getPersonInfo();
            request.getSession().setAttribute("user", operator);
            // 获取二维码里state携带的content信息并解码
            String qrCodeinfo = new String(
                    URLDecoder.decode(HttpServletRequestUtil.getString(request, "state"), "UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                // 将解码后的内容用aaa去替换掉之前生成二维码的时候加入的aaa前缀，转换成WechatInfo实体类
                wechatInfo = mapper.readValue(qrCodeinfo.replace("aaa", "\""), WechatInfo.class);
            } catch (Exception e) {
                return "shop/operationfail";
            }
            // 校验二维码是否已经过期
            if (!checkQRCodeInfo(wechatInfo)) {
                return "shop/operationfail";
            }
            // 获取添加消费记录所需要的参数并组建成userproductmap实例
            Long productId = wechatInfo.getProductId();
            Long customerId = wechatInfo.getCustomerId();
            UserProductMap userProductMap = compactUserProductMap4Add(customerId, productId, auth.getPersonInfo());
            // 空值校验
            if (userProductMap != null && customerId != -1) {
                try {
                    if (!checkShopAuth(operator.getUserId(), userProductMap)) {
                        return "shop/operationfail";
                    }
                    // 添加消费记录
                    UserProductMapExecution se = userProductMapService.addUserProductMap(userProductMap);
                    if (se.getState() == UserProductMapStateEnum.SUCCESS.getState()) {
                        return "shop/operationsuccess";
                    }
                } catch (RuntimeException e) {
                    return "shop/operationfail";
                }

            }
        }
        return "shop/operationfail";
    }


    /**
     * 根据code获取UserAccessToken，进而通过token里的openId获取微信用户信息
     *
     * @param request
     * @return
     */
    private WechatAuth getOperatorInfo(HttpServletRequest request) {
        String code = request.getParameter("code");
        WechatAuth auth = null;
        if (null != code) {
            UserAccessToken token;
            try {
                token = WechatUtil.getUserAccessToken(code);
                String openId = token.getOpenId();
                request.getSession().setAttribute("openId", openId);
                auth = wechatAuthService.getWechatAuthByOpenId(openId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return auth;
    }


    /**
     * 根据二维码携带的createTime判断其是否超过了10分钟，超过十分钟则认为过期
     *
     * @param wechatInfo
     * @return
     */
    private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
        if (wechatInfo != null && wechatInfo.getProductId() != null && wechatInfo.getCustomerId() != null
                && wechatInfo.getCreateTime() != null) {
            long nowTime = System.currentTimeMillis();
            if ((nowTime - wechatInfo.getCreateTime()) <= 600000) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    
    /**
     * 根据传入的customerId, productId以及操作员信息组建用户消费记录
     *
     * @param customerId
     * @param productId
     * @param operator
     * @return
     */
    private UserProductMap compactUserProductMap4Add(Long customerId, Long productId, PersonInfo operator) {
        UserProductMap userProductMap = null;
        if (customerId != null && productId != null) {
            userProductMap = new UserProductMap();
            PersonInfo customer = new PersonInfo();
            customer.setUserId(customerId);
            // 主要为了获取商品积分
            Product product = productService.getProductById(productId);
            userProductMap.setProduct(product);
            userProductMap.setShop(product.getShop());
            userProductMap.setUser(customer);
            userProductMap.setPoint(product.getPoint());
            userProductMap.setCreateTime(new Date());
            userProductMap.setOperator(operator);
        }
        return userProductMap;
    }
    
    /**
     * 检查扫码的人员是否有操作权限
     *
     * @param userId
     * @param userProductMap
     * @return
     */
    private boolean checkShopAuth(long userId, UserProductMap userProductMap) {
        // 获取该店铺的所有授权信息
        ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService
                .listShopAuthMapByShopId(userProductMap.getShop().getShopId(), 1, 1000);
        for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
            // 看看是否给过该人员进行授权
            if (shopAuthMap.getEmployee().getUserId() == userId) {
                return true;
            }
        }
        return false;
    }
}
