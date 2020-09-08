package com.imooc.o2o.dto;

import java.util.List;

import com.imooc.o2o.entity.ShopAuthMap;
import com.imooc.o2o.enums.ShopAuthMapStateEnum;

public class ShopAuthMapExecution {

	private int state;
    private String statrInfo;
    private Integer count;
    private ShopAuthMap shopAuthMap;
    //授权列表<查询专用>
    private List<ShopAuthMap> shopAuthMapList;

    public ShopAuthMapExecution() {
    }

    public ShopAuthMapExecution(ShopAuthMapStateEnum stateEnum) {
        this.state = stateEnum.getState();
        this.statrInfo = stateEnum.getStateInfo();
    }

    public ShopAuthMapExecution (ShopAuthMapStateEnum stateEnum, ShopAuthMap shopAuthMap) {
        this(stateEnum);
        this.shopAuthMap = shopAuthMap;
    }

    public ShopAuthMapExecution (ShopAuthMapStateEnum stateEnum, List<ShopAuthMap> shopAuthMapList) {
        this(stateEnum);
        this.shopAuthMapList = shopAuthMapList;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStatrInfo() {
        return statrInfo;
    }

    public void setStatrInfo(String statrInfo) {
        this.statrInfo = statrInfo;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public ShopAuthMap getShopAuthMap() {
        return shopAuthMap;
    }

    public void setShopAuthMap(ShopAuthMap shopAuthMap) {
        this.shopAuthMap = shopAuthMap;
    }

    public List<ShopAuthMap> getShopAuthMapList() {
        return shopAuthMapList;
    }

    public void setShopAuthMapList(List<ShopAuthMap> shopAuthMapList) {
        this.shopAuthMapList = shopAuthMapList;
    }
}
