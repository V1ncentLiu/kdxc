package com.kuaidao.manageweb.demo.DTO;

import java.io.Serializable;
import java.util.List;

/**
 * 分页table
 * @author: Chen Chengxue
 * @date: 2018年12月19日 下午2:15:38   
 * @version V1.0
 */
public class PageTable<T> implements Serializable{
	/**   
	 * @Fields serialVersionUID ：
	 */   
	private static final long serialVersionUID = 1L;
	/**
	 * 当前页数据
	 */
	List<T> data;
	/**
	 * 当前页
	 */
	int currentPage;
	/**
	 * 总记录数
	 */
	int total;
	/**
	 * 每页条数
	 */
	int pageSize = 100;
	/**
	 * 总页数
	 */
	int pageSizes;
	
	public PageTable() {
		
	}
    public PageTable(List<T> data) {
		this.data = data;
	}
	
	public List<T> getData() {
		return data;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public int getTotal() {
		return total;
	}
	public int getPageSize() {
		return pageSize;
	}
	public int getPageSizes() {
		return pageSizes;
	}
	public void setData(List<T> data) {
		this.data = data;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public void setPageSizes(int pageSizes) {
		this.pageSizes = pageSizes;
	}
	@Override
	public String toString() {
		return "PageTable [data=" + data + ", currentPage=" + currentPage + ", total=" + total + ", pageSize="
				+ pageSize + ", pageSizes=" + pageSizes + "]";
	}
	
	

}
