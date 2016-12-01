package com.jy.medusa.stuff;

import com.jy.medusa.utils.SystemConfigs;

import java.io.Serializable;
import java.util.List;

/**
 * Bean类 - 分页
 */

public class Pager<T> implements Serializable {

	public final String[] legalColumn = { SystemConfigs.PRIMARY_KEY, "created_at"};
	public final String[] legalSort = { "desc", "asc" };

	public static final Integer MAX_PAGE_SIZE = 500;// 每页最大记录数限制
	
	
	private Integer pageNumber = 1;// 当前页码
	private Integer pageSize = 10;// 每页记录数
	private Integer totalCount = 0;// 总记录数
	private Integer pageCount = 0;// 总页数
	private String[] orderBy = {legalColumn[0]};// 排序字段
	private String[] orderType = {legalSort[0]};// 排序方式
	private List<T> list;//  数据List

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Pager setPageNumber(Integer pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Pager setPageSize(Integer pageSize) {
		if (pageSize < 1) {
			pageSize = 1;
		} else if (pageSize > MAX_PAGE_SIZE) {
			pageSize = MAX_PAGE_SIZE;
		}
		this.pageSize = pageSize;
		return this;
	}

	public Integer getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getPageCount() {
		pageCount = (totalCount+pageSize-1) / pageSize;
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public String[] getOrderBy() {
		return orderBy;
	}

	public Pager setOrderBy(String[] orderBy) {
		this.orderBy = orderBy;
		return this;
	}
	
	public String[] getOrderType() {
		return orderType;
	}

	public Pager setOrderType(String[] orderType) {
		this.orderType = orderType;
		return this;
	}

	public Long getStartRecord() {
		return (pageNumber - 1L) * pageSize;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}