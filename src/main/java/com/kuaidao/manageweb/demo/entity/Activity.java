package com.kuaidao.manageweb.demo.entity;

import java.util.Date;
import java.util.List;

public class Activity {
	private String id;
	private String name;
	private String region;
	private Date  date1;
	private Date date2;
	private boolean delivery;
	private List type;
	private String resource;
	private String desc;
	public String getName() {
		return name;
	}
	public String getRegion() {
		return region;
	}

	public boolean isDelivery() {
		return delivery;
	}
	public List getType() {
		return type;
	}
	public String getResource() {
		return resource;
	}
	public String getDesc() {
		return desc;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setRegion(String region) {
		this.region = region;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}
	public void setType(List type) {
		this.type = type;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Date getDate1() {
		return date1;
	}
	public Date getDate2() {
		return date2;
	}
	public void setDate1(Date date1) {
		this.date1 = date1;
	}
	public void setDate2(Date date2) {
		this.date2 = date2;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Activity [id=" + id + ", name=" + name + ", region=" + region + ", date1=" + date1 + ", date2=" + date2
				+ ", delivery=" + delivery + ", type=" + type + ", resource=" + resource + ", desc=" + desc + "]";
	}
	

}
