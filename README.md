# spring-boot-dynamicdatasource


mysql脚本语句包含了本项目中的主从两个数据库中涉及的表的创建脚本语句，其中slave表中的url的值应该如下：

jdbc:mysql://localhost:3306/slave?useUnicode=true&autoReconnect=true&useSSL=false&characterEncoding=utf-8&useSSL=true

利用AOP+注解实现多数据源的动态切换


其中为了避免同一个事物下数据源切换失效，在动态数据源注册的时候，同时为不同的数据源创建自己的事务，demo片段如下

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
