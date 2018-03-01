package com.appleyk.pojo;

public class DataSourceInfo {

	private String type;
	private String driverClassName;
	private String url;
	private String userName;
	private String passWord;
	/*
	 * 数据源
	 */
	private String dsName;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDriverClassName() {
		return driverClassName;
	}
	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getDsName() {
		return dsName;
	}
	public void setDsName(String dsName) {
		this.dsName = dsName;
	}
	
}
