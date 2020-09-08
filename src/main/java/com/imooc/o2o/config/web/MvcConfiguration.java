package com.imooc.o2o.config.web;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.imooc.o2o.interceptor.ShopLoginInterceptor;
import com.imooc.o2o.interceptor.ShopPermissionInterceptor;


/**
 * 
   *   开启MVC，自动注入Spring容器
 * WebMvcConfigurerAdapter：配置试图解析器
   *  当一个类实现了ApplicationContextAware接口， 这个类方便获得ApplicationContext中的所有bean
 * @author Administrator
 *
 */
@Configuration
//将mvc交给我们自己管理
@EnableWebMvc
public class MvcConfiguration implements WebMvcConfigurer, ApplicationContextAware {

	private ApplicationContext applicationContext;
	
	@Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
	
	/**
             * 静态资源配置，本地运行用win，上传服务器用Linux
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/upload/**").addResourceLocations("file:/D:/Javaproject/images/upload/");
    	registry.addResourceHandler("/upload/**").addResourceLocations("file:/home/laiyanhongpro/images/upload/");
    }
    /**
             * 定义默认的请求处理器
     */
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    	configurer.enable();
    }
    
    /*
            * 创建viewResolver
    */
    @Bean(name = "viewResolver")
    public ViewResolver createViewResolver(){
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        //设置Spring容器
        viewResolver.setApplicationContext(this.applicationContext);
        //取消缓存
        viewResolver.setCache(false);
        //设置解析的前缀
        viewResolver.setPrefix("/WEB-INF/html/");
        //设置视图解析的后缀
        viewResolver.setSuffix(".html");
        return viewResolver;
     }
    
    /*
            * 文件上传解析器
    */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver multipartResolver(){

        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("utf-8");
        //1024*1024*20=20M
        multipartResolver.setMaxInMemorySize(20971520);
        multipartResolver.setMaxUploadSize(20971520);
        return multipartResolver;
     }
    
    /*
           * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	String intercrptPath = "/shopadmin/**";
    	//注册拦截器
    	InterceptorRegistration loginIR = registry.addInterceptor(new ShopLoginInterceptor());
    	//配置拦截路径
    	loginIR.addPathPatterns(intercrptPath);
    	//shopauthmanagement page
        loginIR.excludePathPatterns("/shopadmin/addshopauthmap");
    	//注册其他拦截器
    	InterceptorRegistration permissionIR = registry.addInterceptor(new ShopPermissionInterceptor());
        permissionIR.addPathPatterns(intercrptPath);
        //配置不拦截的路径
        //shoplist page
        permissionIR.excludePathPatterns("/shopadmin/shoplist");
        permissionIR.excludePathPatterns("/shopadmin/getshoplist");
        //shopregister page
        permissionIR.excludePathPatterns("/shopadmin/getshopinitinfo");
        permissionIR.excludePathPatterns("/shopadmin/registershop");
        permissionIR.excludePathPatterns("/shopadmin/shopoperation");
        //shopmanage page
        permissionIR.excludePathPatterns("/shopadmin/shopmanagement");
        permissionIR.excludePathPatterns("/shopadmin/getshopmanagementinfo");
        //shopauthmanagement page
        permissionIR.excludePathPatterns("/shopadmin/addshopauthmap");
    }
    
}
