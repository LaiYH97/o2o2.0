package com.imooc.o2o.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathUtil {

	//获取适应系统的路径斜杠
	private static String seperator = System.getProperty("file.separator");
	//设置创建随机文件名的时间的格式
	private static final SimpleDateFormat sDateFormat = new SimpleDateFormat(
			"yyyyMMddHHmmss"); // 时间格式化的格式
	private static final Random r = new Random();

	private static String winPath;
	
	private static String linuxPath;
	
	private static String shopPath;
	
	@Value("${win.base.path}")
	public void setWinPath(String winPath) {
		PathUtil.winPath = winPath;
	}

	@Value("${linux.base.path}")
	public void setLinuxPath(String linuxPath) {
		PathUtil.linuxPath = linuxPath;
	}

	@Value("${shop.relevant.path}")
	public void setShopPath(String shopPath) {
		PathUtil.shopPath = shopPath;
	}

	/**
	 * 获取项目图片的根路径
	 * @return
	 */
	public static String getImgBasePath() {
		//获取操作系统名称
		String os = System.getProperty("os.name");
		String basePath = "";
		if (os.toLowerCase().startsWith("win")) {
			basePath = winPath;
		} else {
			basePath = linuxPath;
		}
		//修改适应本系统环境的路径方式
		basePath = basePath.replace("/", seperator);
		return basePath;
	}

	/**
	 * 获取店铺图片存储路径
	 * @return
	 */
	public static String getShopImagePath(long shopId) {
		StringBuilder shopImagePathBuilder = new StringBuilder();
		shopImagePathBuilder.append(shopPath + shopId + "/");
		String shopImagePath = shopImagePathBuilder.toString().replace("/",seperator);
		return shopImagePath;
	}

	/**
	 * 生成随机文件名，避免图片名字重复
	 * @return
	 */
	public static String getRandomFileName() {
		//生成随机文件名：当前年月日时分秒+五位随机数（为了在实际项目中防止文件同名而进行的处理）
		//获取五位随机数
		int rannum = (int) (r.nextDouble() * (99999 - 10000 + 1)) + 10000; 
		//设置当前时间
		String nowTimeStr = sDateFormat.format(new Date()); 
		return nowTimeStr + rannum;
	}
	
	public static String getHeadLineImagePath() {
		String headLineImagePath = "/upload/images/item/headtitle/";
		headLineImagePath = headLineImagePath.replace("/", seperator);
		return headLineImagePath;
	}

	public static String getShopCategoryImagePath() {
		String shopCategoryImagePath = "/upload/images/item/shopcategory/";
		shopCategoryImagePath = shopCategoryImagePath.replace("/", seperator);
		return shopCategoryImagePath;
	}
	
	public static String getPersonInfoImagePath() {
		String personInfoImagePath = "/upload/images/item/personinfo/";
		personInfoImagePath = personInfoImagePath.replace("/", seperator);
		return personInfoImagePath;
	}
	

	public static void deleteFile(String storePath) {
		File file = new File(getImgBasePath() + storePath);
		if (file.exists()) {
			if (file.isDirectory()) {
				File files[] = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					files[i].delete();
				}
			}
			file.delete();
		}
	}
}
