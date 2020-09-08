package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.imooc.o2o.dto.ShopAuthMapExecution;
import com.imooc.o2o.dto.UserAccessToken;
import com.imooc.o2o.dto.WechatInfo;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.entity.WechatAuth;
import com.imooc.o2o.enums.ShopAuthMapStateEnum;
import com.imooc.o2o.exception.ShopAuthMapOperationException;
import com.imooc.o2o.service.PersonInfoService;
import com.imooc.o2o.service.ShopAuthMapService;
import com.imooc.o2o.service.WechatAuthService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;
import com.imooc.o2o.util.wechat.WechatUtil;


@Controller
@RequestMapping("/shopadmin")
public class ShopAuthManagementController {

	@Autowired
	private ShopAuthMapService shopAuthMapService;
	@Autowired
    private WechatAuthService wechatAuthService;
	@Autowired
    private PersonInfoService personInfoService;

    @RequestMapping(value = "/listshopauthmapsbyshop", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> listShopAuthMapsByShop(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
        int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
        Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
        if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
            ShopAuthMapExecution se = shopAuthMapService.listShopAuthMapByShopId(currentShop.getShopId(), pageIndex, pageSize);
            modelMap.put("shopAuthMapList", se.getShopAuthMapList());
            modelMap.put("success", true);
            modelMap.put("count", se.getCount());
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取数据出错");
        }
        return modelMap;
    }

    @RequestMapping(value = "/getshopauthmapbyid", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopAuthMapById(@RequestParam Long shopAuthId) {
        Map<String, Object> modelMap = new HashMap<>();
        if (shopAuthId != null && shopAuthId > -1) {
            ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId);
            modelMap.put("shopAuthMap", shopAuthMap);
            modelMap.put("success", true);
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "获取数据出错");
        }
        return modelMap;
    }

    @RequestMapping(value = "/modifyshopauthmap", method = RequestMethod.POST)
    @ResponseBody
    private Map<String, Object> modifyShopAuthMap(String shopAuthMapStr, HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<>();
        boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
        //判断是编辑还是删除/恢复授权操作
        if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
            modelMap.put("success", false);
            modelMap.put("errMsg", "验证码错误");
            return modelMap;
        }

        ObjectMapper mapper = new ObjectMapper();
        ShopAuthMap shopAuthMap = null;
        try {
            //将前台传入的json
            shopAuthMap = mapper.readValue(shopAuthMapStr, ShopAuthMap.class);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.toString());
            return modelMap;
        }
        //非空判断
        if (shopAuthMap != null && shopAuthMap.getShopAuthId() != null) {
            try {
                if (!checkPermission(shopAuthMap.getShopAuthId())) {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", "无法对店家本身权限做操作");
                    return modelMap;
                }
                ShopAuthMapExecution se = shopAuthMapService.modifyShopAuthMap(shopAuthMap);
                if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    modelMap.put("success", true);
                } else {
                    modelMap.put("success", false);
                    modelMap.put("errMsg", se.getStatrInfo());
                }
            } catch (ShopAuthMapOperationException e) {
                modelMap.put("success", false);
                modelMap.put("errMsg", e.toString());
                return modelMap;
            }
        } else {
            modelMap.put("success", false);
            modelMap.put("errMsg", "输入数需要修改的权限信息");
        }
        return modelMap;
    }

    private boolean checkPermission(Long shopAuthId) {
        ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId);
        if (shopAuthMap.getTitleFlag() == 0) {
            //店家本身
            return false;
        }
        return true;
    }

    //微信获取用户信息的前缀
    private static String urlPrefix;
    //微信获取用户信息的中间部分
    private static String urlMiddle;
    //微信获取用户信息的后缀
    private static String urlSuffix;
    //微信回传给的响应添加授权新的的url
    private static String authUrl;

    @Value("${wechat.prefix}")
    public void setUrlPrefix(String urlPrefix) {
        ShopAuthManagementController.urlPrefix = urlPrefix;
    }

    @Value("${wechat.middle}")
    public void setUrlMiddle(String urlMiddle) {
        ShopAuthManagementController.urlMiddle = urlMiddle;
    }

    @Value("${wechat.suffix}")
    public void setUrlSuffix(String urlSuffix) {
        ShopAuthManagementController.urlSuffix = urlSuffix;
    }

    @Value("${wechat.auth.url}")
    public void setAuthUrl(String authUrl) {
        ShopAuthManagementController.authUrl = authUrl;
    }
   

    /**
     * 生成带有url的二维码，微信扫一扫就能链接到对应的url
     */
    @RequestMapping(value = "/generateqrcode4shopauth", method = RequestMethod.GET)
    @ResponseBody
    private void generateQRCode4ShopAuth(HttpServletRequest request, HttpServletResponse response) {
        Shop shop = (Shop) request.getSession().getAttribute("currentShop");
        if (shop != null && shop.getShopId() != null) {
            //获取当前时间戳，以保证二维码的时间有效性，精确到ms
            long timeStamp = System.currentTimeMillis();
            //将店铺id和timeStamp传入content，复制到state中，这样微信获取到这些信息后回会回传到授权信息的添加方法
            //加上aaa是为了在添加信息的方法里面替换这些信息使用
            String content = "{aaashopIdaaa:" + shop.getShopId() + ",aaacreateTimeaaa:" + timeStamp + "}";
            try {
                //将content先进行base64编码，以避免特殊字符的干扰
                String longUrl = urlPrefix + authUrl + urlMiddle + URLEncoder.encode(content,"UTF-8") + urlSuffix;
                //String shortUrl = ShortNetAddressUtil.getShortURL(longUrl);
                //String shortUrl = "www.baidu.com";
                BitMatrix qRcodeImg = CodeUtil.generateQRCodeStream(longUrl, response);
                MatrixToImageWriter.writeToStream(qRcodeImg, "png", response.getOutputStream());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 根据文献回传回来的参数添加店铺的授权信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/addshopauthmap", method = RequestMethod.GET)
    private String addShopAuthMap(HttpServletRequest request, HttpServletResponse response) throws IOException{
        //从request获取微信用户信息
        WechatAuth auth = getEmployee(request);
        if (auth != null) {
            //根据userid获取用户信息
            PersonInfo user = personInfoService.getPersonInfoById(auth.getPersonInfo().getUserId());
            request.getSession().setAttribute("user", user);
            //解析微信回传过来的自定义参数
            String qrCodeInfo = new String(
                    URLDecoder.decode(HttpServletRequestUtil.getString(request, "state"), "UTF-8"));
            ObjectMapper mapper = new ObjectMapper();
            WechatInfo wechatInfo = null;
            try {
                wechatInfo = mapper.readValue(qrCodeInfo.replace("aaa", "\""), WechatInfo.class);
            } catch (Exception e) {
                return "shop/operationfail";
            }

            //校验二维码是否过期
            if (!checkQRCodeInfo(wechatInfo)) {
                return "shop/operationfail";
            }

            // 去重校验
            // 获取该店铺下所有的授权信息
            ShopAuthMapExecution allMapList = shopAuthMapService.listShopAuthMapByShopId(wechatInfo.getShopId(), 1, 999);
            List<ShopAuthMap> shopAuthList = allMapList.getShopAuthMapList();
            for (ShopAuthMap sm : shopAuthList) {
                if (sm.getEmployee().getUserId().equals(user.getUserId()))
                    return "shop/operationfail";
            }

            try {
                //根据获取到的内容，添加微信授权信息
                ShopAuthMap shopAuthMap = new ShopAuthMap();
                Shop shop = new Shop();
                shop.setShopId(wechatInfo.getShopId());
                shopAuthMap.setShop(shop);
                shopAuthMap.setEmployee(user);
                shopAuthMap.setTitle("员工");
                shopAuthMap.setTitleFlag(1);
                ShopAuthMapExecution se = shopAuthMapService.addShopAuthMap(shopAuthMap);
                if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
                    return "shop/operationsuccess";
                } else {
                    return "shop/operationfail";
                }
            } catch (RuntimeException e) {
                return "shop/operationfail";
            }
        }
        return "shop/operationfail";
    }

    /**
     * 检查时间是否过期
     * @param wechatInfo
     * @return
     */
    private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
        if (wechatInfo != null && wechatInfo.getShopId() != null
                && wechatInfo.getCreateTime() != null) {
            long nowTime = System.currentTimeMillis();
            return nowTime - wechatInfo.getCreateTime() <= 600000;
        }
        return false;
    }

    private WechatAuth getEmployee(HttpServletRequest request) {
        //获取微信回传的code
        String code = request.getParameter("code");
        WechatAuth auth = null;
        if (null != code) {
            UserAccessToken token = null;
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
  
}
