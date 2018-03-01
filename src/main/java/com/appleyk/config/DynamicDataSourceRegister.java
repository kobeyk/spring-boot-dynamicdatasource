package com.appleyk.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.appleyk.datasource.DynamicDataSource;
import com.appleyk.datasource.DynamicDataSourceContextHolder;
import com.appleyk.pojo.DataSourceInfo;

/**
 * 
 * 功能描述：动态数据源注册 启动动态数据源请在启动类中（如Start）
 * 添加 @Import(DynamicDataSourceRegister.class)
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(DataSourceProperties.class)
@MapperScan("com.appleyk")
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceRegister.class);

	private ConversionService conversionService = new DefaultConversionService();
	private PropertyValues dataSourcePropertyValues;

	// 如配置文件中未指定数据源类型，使用该默认值
	private static final Object DATASOURCE_TYPE_DEFAULT = "org.apache.tomcat.jdbc.pool.DataSource";

	// 数据源
	private DataSource defaultDataSource;
	private Map<String, DataSource> customDataSources = new HashMap<>();

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Map<Object, Object> targetDataSources = new HashMap<Object, Object>();
		// 将主数据源添加到更多数据源中
		targetDataSources.put("dataSource", defaultDataSource);
		DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");

		// 添加更多数据源
		targetDataSources.putAll(customDataSources);
		for (String key : customDataSources.keySet()) {
			DynamicDataSourceContextHolder.dataSourceIds.add(key);
		}

		// 创建DynamicDataSource
		GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
		beanDefinition.setBeanClass(DynamicDataSource.class);
		beanDefinition.setSynthetic(true);
		MutablePropertyValues mpv = beanDefinition.getPropertyValues();
		mpv.addPropertyValue("defaultTargetDataSource", defaultDataSource);
		mpv.addPropertyValue("targetDataSources", targetDataSources);
		registry.registerBeanDefinition("dataSource", beanDefinition);

		System.err.println("动态数据源注册成功,从数据源个数 == " + customDataSources.size());
	}

	/**
	 * 创建DataSource
	 * 
	 * @param type
	 * @param driverClassName
	 * @param url
	 * @param username
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public DataSource buildDataSource(Map<String, Object> dsMap) {
		try {
			Object type = dsMap.get("type");

			if (type == null)
				type = DATASOURCE_TYPE_DEFAULT;// 默认DataSource

			Class<? extends DataSource> dataSourceType;
			dataSourceType = (Class<? extends DataSource>) Class.forName((String) type);

			String driverClassName = dsMap.get("driver-class-name").toString();
			String url = dsMap.get("url").toString();
			String username = dsMap.get("username").toString();
			String password = dsMap.get("password").toString();

			DataSourceBuilder factory = DataSourceBuilder.create().driverClassName(driverClassName).url(url)
					.username(username).password(password).type(dataSourceType);

			return factory.build();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 加载多数据源配置
	 */
	@Override
	public void setEnvironment(Environment env) {
		initDefaultDataSource(env);
		initCustomDataSources(env);
	}

	/**
	 * 初始化主数据源
	 * 
	 */
	private void initDefaultDataSource(Environment env) {

		// 读取主数据源
		RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(env, "spring.datasource.");
		Map<String, Object> dsMap = new HashMap<>();
		dsMap.put("type", propertyResolver.getProperty("type"));
		dsMap.put("driver-class-name", propertyResolver.getProperty("driver-class-name"));
		dsMap.put("url", propertyResolver.getProperty("url"));
		dsMap.put("username", propertyResolver.getProperty("username"));
		dsMap.put("password", propertyResolver.getProperty("password"));

		defaultDataSource = buildDataSource(dsMap);

		dataBinder(defaultDataSource, env);

	}

	/**
	 * 为DataSource绑定更多数据
	 * 
	 * @param dataSource
	 * @param env
	 */
	private void dataBinder(DataSource dataSource, Environment env) {
		RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
		dataBinder.setConversionService(conversionService);
		dataBinder.setIgnoreNestedProperties(false);// false
		dataBinder.setIgnoreInvalidFields(false);// false
		dataBinder.setIgnoreUnknownFields(true);// true

		if (dataSourcePropertyValues == null) {
			Map<String, Object> rpr = new RelaxedPropertyResolver(env, "spring.datasource").getSubProperties(".");
			Map<String, Object> values = new HashMap<>(rpr);
			// 排除已经设置的属性
			values.remove("type");
			values.remove("driver-class-name");
			values.remove("url");
			values.remove("username");
			values.remove("password");
			dataSourcePropertyValues = new MutablePropertyValues(values);
		}
		dataBinder.bind(dataSourcePropertyValues);
	}

	/**
	 * 初始化更多数据源
	 * 
	 */
	private void initCustomDataSources(Environment env) {
		// 读取库表中datasource获取更多数据源

		Map<String, Map<String, Object>> customInfo = getCustomDataSourceInfo();
		for (String key : customInfo.keySet()) {
			Map<String, Object> dsMap = customInfo.get(key);
			DataSource ds = buildDataSource(dsMap);
			try {
				// 判断一下 数据源是否连接成功
				ds.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			customDataSources.put(key, ds);
			dataBinder(ds, env);
		}
	}

	private Map<String, Map<String, Object>> getCustomDataSourceInfo() {
		Map<String, Map<String, Object>> customMap = new HashMap<>();
		// 从主库的slave表中，读取出从库slave的连接信息
		String sql = "select url,username,password from slave";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(defaultDataSource);
		List<DataSourceInfo> infos = jdbcTemplate.query(sql, new RowMapper<DataSourceInfo>() {
			@Override
			public DataSourceInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
				DataSourceInfo info = new DataSourceInfo();
				info.setType("com.alibaba.druid.pool.DruidDataSource");
				info.setDriverClassName("com.mysql.jdbc.Driver");
				info.setUrl(rs.getString("url"));
				info.setPassWord(rs.getString("password"));
				info.setUserName(rs.getString("username"));
				// 从库名称：slave -- 对应后面的数据源注解里面的name属性
				info.setDsName("slave");
				return info;
			}
		});
		for (DataSourceInfo info : infos) {
			Map<String, Object> dsMap = new HashMap<>();
			dsMap.put("type", info.getType());
			dsMap.put("driver-class-name", info.getDriverClassName());
			dsMap.put("url", info.getUrl());
			dsMap.put("username", info.getUserName());
			dsMap.put("password", info.getPassWord());
			customMap.put(info.getDsName(), dsMap);
		}
		return customMap;
	}

	@Bean
	public PlatformTransactionManager masterTransactionManager() {
		System.err.println("masterTransactionManager=========配置主数据库的事务");
		return new DataSourceTransactionManager(defaultDataSource);
	}

	@Bean
	public PlatformTransactionManager slaveTransactionManager() {
		System.err.println("slaveTransactionManager=========配置从数据库的事务");
		return new DataSourceTransactionManager(customDataSources.get("slave"));
	}

}
