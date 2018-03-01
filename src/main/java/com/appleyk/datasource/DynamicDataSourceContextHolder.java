package com.appleyk.datasource;
import java.util.ArrayList;  
import java.util.List;  
  
/**
 * 当前线程数据源
 * @author yukun24@126.com
 * @blob   http://blog.csdn.net/appleyk
 * @date   2018年2月27日-上午11:36:27
 */
  
public class DynamicDataSourceContextHolder {  
 
	 /*
     * 当使用ThreadLocal维护变量时，ThreadLocal为每个使用该变量的线程提供独立的变量副本，
     * 所以每一个线程都可以独立地改变自己的副本，而不会影响其它线程所对应的副本。
     */
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>(); 
	
    public static List<String> dataSourceIds = new ArrayList<>();  
  
    //设置数据源名
    public static void setDataSourceType(String dataSourceType) {  
    	System.err.println("切换到"+dataSourceType+"数据源");
        contextHolder.set(dataSourceType);  
    }  
    
    //获取数据源名
    public static String getDataSourceType() {  
        return contextHolder.get();  
    }  
  
    //清除数据源名
    public static void clearDataSourceType() {  
        contextHolder.remove();  
    }  
  
    /** 
     * 判断指定DataSrouce当前是否存在 
     * 
     * @param dataSourceId 
     * @return   
     */  
    public static boolean containsDataSource(String dataSourceId) {  
        return dataSourceIds.contains(dataSourceId);  
    }  
}  