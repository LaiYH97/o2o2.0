package com.imooc.o2o.config.web;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class KaptchaConfigurantion {

	@Bean
    public DefaultKaptcha getDefaultKaptche () {
		DefaultKaptcha captchaProducer = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border","no");
        properties.setProperty("kaptcha.textproducer.font.color","red");
        properties.setProperty("kaptcha.image.width","135");
        properties.setProperty("kaptcha.textproducer.char.string", "ACDEFHKGPRSTWX23456789");
        properties.setProperty("kaptcha.image.height","50");    
        properties.setProperty("kaptcha.textproducer.font.size","43");
        properties.setProperty("kaptcha.noise.color","black");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        properties.setProperty("kaptcha.textproducer.font.names","Arial");
        Config config = new Config(properties);
        captchaProducer.setConfig(config);

        return captchaProducer;
    }
}
