package com.appleyk.entity;

import javax.persistence.Table;

@Table(name = "b")
public class B {

	private Integer id;
	private String name;
	private String sex;
	private Integer age;

	
	public B(){
		
	}
	
	public B(A a){
		this.id = a.getId();
		this.name = a.getName();
		this.age = a.getAge();
		this.sex = a.getSex();
	}
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

}
