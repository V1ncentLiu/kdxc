/*
 * 文件名：CaptchaConfig.java 描述： 作者：liubaogang 版本: 修改内容： 修改时间：2017年2月6日 项目名称： 版权：www.sicdt.com
 */

package com.kuaidao.manageweb.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

/**
 * 验证码图片 <br>
 * 类 名: CaptchaConfig <br>
 * 描 述: 描述类完成的主要功能 <br>
 * 作 者: liubaogang <br>
 * 创 建: 2017年2月6日 <br>
 * 版 本: v1.0.0 <br>
 * <br>
 * 历 史: (版本) 作者 时间 注释
 */
@Configuration
public class CaptchaConfig {

	@Bean(name = "captchaProducer")
	public DefaultKaptcha getKaptchaBean() {

		DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
		Properties properties = new Properties();
		properties.setProperty("kaptcha.border", "yes");
		properties.setProperty("kaptcha.border.color", "105,179,90");
		properties.setProperty("kaptcha.textproducer.font.color", "blue");
		properties.setProperty("kaptcha.image.width", "120");
		properties.setProperty("kaptcha.image.height", "50");
		properties.setProperty("kaptcha.session.key", "code");
		properties.setProperty("kaptcha.textproducer.char.length", "4");
		properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");

		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		return defaultKaptcha;
	}
}
