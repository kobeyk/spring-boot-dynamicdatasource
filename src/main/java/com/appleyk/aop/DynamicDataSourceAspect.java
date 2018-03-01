package com.appleyk.aop;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.appleyk.annotation.DataSource;
import com.appleyk.datasource.DynamicDataSourceContextHolder;  
  
@Aspect  
@Component  
public class DynamicDataSourceAspect {  
    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);  
  
    @Before("@annotation(ds)")  
    public void changeDataSource(JoinPoint point, DataSource ds) throws Throwable {  
        String dsId = ds.name();  
        System.err.println(dsId);
        if (!DynamicDataSourceContextHolder.containsDataSource(dsId)) {  
            System.err.println("数据源[{"+ds.name()+"}]不存在，使用默认数据源 >"+point.getSignature());
        } else {  
            System.err.println("Use DataSource : "+ds.name()+">"+point.getSignature() );
            DynamicDataSourceContextHolder.setDataSourceType(ds.name());  
        }       
    }  
  
    @After("@annotation(ds)")  
    public void restoreDataSource(JoinPoint point, DataSource ds) {  
        System.err.println("Revert DataSource : "+ds.name()+" > "+point.getSignature());
        DynamicDataSourceContextHolder.clearDataSourceType();
              
    }  
}  