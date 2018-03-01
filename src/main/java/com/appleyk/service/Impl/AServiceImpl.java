package com.appleyk.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.appleyk.entity.A;
import com.appleyk.mapepr.AMapper;
import com.appleyk.service.AService;

@Service
@Primary
public class AServiceImpl implements AService {

	@Autowired
	private AMapper aMapper;

	@Override
	public boolean SaveA(A a) {

		return aMapper.insert(a) > 0;
	}

}
