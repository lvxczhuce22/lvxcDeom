package hisense.fdp.cfa.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * config.properites文件处理类
 * 
 * 
 */
public class ConfigPropertiesUtil {
	// 类实例
	private static final ConfigPropertiesUtil configPropertiesUtil = new ConfigPropertiesUtil();
	// 属性
	Properties properties = new Properties();
	public String propertiesUrl = "/config/config.properties";

	/**
	 * 初始化config.properties解析类
	 */
	private ConfigPropertiesUtil() {
		InputStream in = null;

		try {
			in = ConfigPropertiesUtil.class.getResourceAsStream(propertiesUrl);
			properties.load(in);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 获取config.properties解析类
	 * 
	 * @return config.properties解析类
	 */
	public static ConfigPropertiesUtil getInstance() {
		return configPropertiesUtil;
	}

	/**
	 * 根据属性名获取config.properties文件的属性值
	 * 
	 * @param propertyName
	 *            属性名
	 * @return config.properties文件的属性值
	 */
	public String getPropertyValue(String propertyName) {
		return properties.getProperty(propertyName);
	}
}
