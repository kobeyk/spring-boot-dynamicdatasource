package com.appleyk.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appleyk.entity.A;
import com.appleyk.result.ResponseMessage;
import com.appleyk.result.ResponseResult;
import com.appleyk.service.ObjectService;

@RestController
@RequestMapping("/rest/v1.0.1/object")
public class ObjectController {

	@Autowired
	private ObjectService objService;

	@PostMapping("/save")
	public ResponseResult SaveObject(@RequestBody A a) throws Exception {

		if (objService.Save(a)) {
			return new ResponseResult(ResponseMessage.OK);
		}
		return new ResponseResult(ResponseMessage.INTERNAL_SERVER_ERROR);
	}
}
