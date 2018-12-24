package com.kuaidao.manageweb.demo.entity;

import java.io.Serializable;

public class Address implements Serializable {
	
	/**   
	 * @Fields serialVersionUID ：
	 */   
	private static final long serialVersionUID = 1L;
	private String id;
	private String area;
	private String name;
	private String address;
	public String getArea() {
		return area;
	}
	public String getName() {
		return name;
	}
	public String getAddress() {
		return address;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Address [id=" + id + ", area=" + area + ", name=" + name + ", address=" + address + "]";
	}

	
}
