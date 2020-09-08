package com.imooc.o2o.util;


import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imooc.o2o.dto.ImageHolder;

import ch.qos.logback.classic.Logger;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;


public class ImageUtil {

	private static String basePath = PathUtil.getImgBasePath();
	private static final Logger logger = (Logger) LoggerFactory.getLogger(ImageUtil.class);
	
	/**
	 * 将文件流CommonsMultipartFile转换成File
	 * @param cFile
	 * @return
	 */
	public static File transferCommonsMultipartFileToFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
		}catch(IllegalStateException e) {
			logger.error(e.toString());
			e.printStackTrace();;
		}catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return newFile;
	}

	/**
	 * 根据文件，处理缩略图，并返回新生成图片的相对路径
	 * @param thumbnail
	 * @param targetAddr
	 * @return
	 */
	public static String generateThumbnail(ImageHolder thumbnail ,String targetAddr) {
		String realFileName = PathUtil.getRandomFileName();
		String extension = getFileExtension(thumbnail.getImageName());
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is:" + relativeAddr);
		File dest = new File(PathUtil.getImgBasePath()+relativeAddr);
		logger.debug("current complete addr is:" + PathUtil.getImgBasePath()+relativeAddr);
		try {
			Thumbnails.of(thumbnail.getImage()).size(200,200).watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath+"/watermark2.png")),0.8f)
				.outputQuality(0.8f).toFile(dest);
		}catch (IOException e) {
			logger.error(e.toString());
			e.printStackTrace();
		}
		return relativeAddr;
	}
	
	/**
	 * 根据输入流，生成详情图片，并返回新生成图片的相对地址
	 * @param thumbnail
	 * @param targetAddr
	 * @return
	 */
	public static String generateNormalImg(ImageHolder thumbnail ,String targetAddr) {
		//获取不重复的随机名
		String realFileName = PathUtil.getRandomFileName();
		//获取文件的扩展名如png,jpg等
		String extension = getFileExtension(thumbnail.getImageName());
		//如果目标路径不存在，即自动创建
		makeDirPath(targetAddr);
		//获取文件存储的相对路径（带文件名）
		String relativeAddr = targetAddr + realFileName + extension;
		logger.debug("current relativeAddr is:" + relativeAddr);
		//获取文件要保存到的目标路径
		File dest = new File(PathUtil.getImgBasePath()+relativeAddr);
		logger.debug("current complete addr is:" + PathUtil.getImgBasePath()+relativeAddr);
		//调用Thumbnails生成带有水印的图片
		try {
			Thumbnails.of(thumbnail.getImage()).size(337,640).watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath+"/watermark3.png")),0.8f)
				.outputQuality(0.9f).toFile(dest);
		}catch (IOException e) {
			logger.error(e.toString());
			throw new RuntimeException("创建详情图片失败："+e.toString());
		}
		return relativeAddr;
	}
	/**
	 * 创建目标路径所涉及到的目录，例如"D:/Javaproject/images/xxx.jpg"，则自动创建Javaproject、images等目录
	 * @param targetAddr
	 */
	private static void makeDirPath(String targetAddr) {
		//1 获取全路径
		String realFileParentPath = PathUtil.getImgBasePath() + targetAddr;
		File dirPath = new File(realFileParentPath);
		//2 判断路径是否存在，如不存在则递归依次创建出来
		if (!dirPath.exists()) {
			dirPath.mkdirs();
		}
	}

	/**
	 * 获取输入文件流的扩展名
	 * @param String
	 * @return
	 */
	private static String getFileExtension(String fileName) {
		//1 输入的图片，只需或获取最后一个 . 后面的字符即可
		return fileName.substring(fileName.lastIndexOf("."));
	}
	
	/**
	 * storePath是文件的路径还是目录的路径
	 * 如果storePath是文件路径则删除该文件；
	 * 如果storePath是目录路径则删除该目录下的所有文件
	 * @param storePath
	 */
	public static void deleteFileOrPath(String storePath) {
		File fileOrPath = new File(PathUtil.getImgBasePath() + storePath);
		if(fileOrPath.exists()) {
			if(fileOrPath.isDirectory()) {
				File[] files = fileOrPath.listFiles();
				for(int i=0;i<files.length;i++) {
					files[i].delete();
				}
			}
			fileOrPath.delete();
		}
	}

	//测试增加图片水印
	/*
	 * public static void main(String[] args) throws IOException { 
	 * String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	 * Thumbnails.of(new File("D:/images/1.jpg")).size(1000,1000)
	 * .watermark(Positions.BOTTOM_RIGHT,ImageIO.read(new File(basePath +
	 * "/watermark1.png")),0.25f) .outputQuality(0.8f).toFile("D:/images/1.jpg"); 
	 * }
	 */
}
