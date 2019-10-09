package com.kuaidao.manageweb.demo.DTO;

public class AddressReqDTO {
	private String area;
	private String address;
	public String getArea() {
		return area;
	}
	public String getAddress() {
		return address;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "AddressReqDTO [area=" + area + ", address=" + address + "]";
	}
	
	
}
