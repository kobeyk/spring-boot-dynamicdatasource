package com.appleyk.service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.appleyk.annotation.DataSource;
import com.appleyk.entity.B;
import com.appleyk.mapepr.BMapper;
import com.appleyk.service.BService;

@Service
@Primary
public class BServiceImpl implements BService {

	@Autowired
	private BMapper bMapper;

	@Override
	@DataSource(name = "slave") //切换数据源
	@Transactional(value="slaveTransactionManager")
	public boolean SaveB(B b) {

		return bMapper.insert(b) > 0;
	}

}
