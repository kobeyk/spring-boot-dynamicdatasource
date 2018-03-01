package com.appleyk.datasource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;  


public class DynamicDataSource extends AbstractRoutingDataSource {  
  
    @Override  
    protected Object determineCurrentLookupKey() {  
    	  if(DynamicDataSourceContextHolder.getDataSourceType()==null){
    		  System.err.println("数据源为:===="+"master");  
    	  }else{
    		  System.err.println("数据源为:===="+DynamicDataSourceContextHolder.getDataSourceType());
    	  }
    	  
          return DynamicDataSourceContextHolder.getDataSourceType();  
    }  
  
}  