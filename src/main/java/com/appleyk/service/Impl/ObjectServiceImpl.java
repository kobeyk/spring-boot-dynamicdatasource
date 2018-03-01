package com.appleyk.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appleyk.entity.A;
import com.appleyk.entity.B;
import com.appleyk.service.AService;
import com.appleyk.service.BService;
import com.appleyk.service.ObjectService;

@Service
@Primary
public class ObjectServiceImpl implements ObjectService {

	
	@Autowired
	private AService aService;
	
	@Autowired
	private BService bService;
	
	@Override
	@Transactional(value="masterTransactionManager")
	public boolean Save(A a) {
		
		if(!aService.SaveA(a)){
			return false;
		}
		
		B b = new B(a);
		
		if(!bService.SaveB(b)){
			return false;
		}
		
		
		return true;
	}

}
