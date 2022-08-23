package com.jy.medusa.gaze.stuff;

import com.jy.medusa.gaze.stuff.param.lambda.HolyGetPropertyNameLambda;
import com.jy.medusa.gaze.stuff.param.lambda.HolyGetter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author neo 2016.09.23
 * Bean类 - 分页
 */

public class Pager<T> implements Serializable {

	public final Integer MAX_PAGE_SIZE = 1000;// 每页最大记录数限制


	private Integer pageNumber = 1;// 当前页码
	private Integer pageSize = 10;// 每页记录数
	private Integer totalCount = 0;// 总记录数
	private Integer pageCount = 0;// 总页数
	private List<String> orderByList;// 排序字段
	private List<String> orderTypeList;// 排序方式
	private List<T> list;//  数据List


	public static Pager getPager() {
        return new Pager();
    }

	public Integer getPageNumber() {
		return pageNumber;
	}

	public Pager<T> setPageNumber(Integer pageNumber) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		this.pageNumber = pageNumber;
		return this;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Pager<T> setPageSize(Integer pageSize) {
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

	public Integer getStartRecord() {
		return (pageNumber - 1) * pageSize;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public List<String> getOrderByList() {
		return orderByList;
	}

//	public void setOrderByList(List<String> orderByList) {
//		this.orderByList = orderByList;
//	}

	public List<String> getOrderTypeList() {
		return orderTypeList;
	}

//	public void setOrderTypeList(List<String> orderTypeList) {
//		this.orderTypeList = orderTypeList;
//	}


	public Pager<T> setSortColumn(HolyGetter<T> fns) {

		orderByList = orderByList == null ? new ArrayList<>() : orderByList;

		orderByList.add(MedusaSqlHelper.convertEntityName2SqlName(HolyGetPropertyNameLambda.convertToFieldName(fns)));

		return this;
	}

	public Pager<T> setSortType(SortTypeEnum sortTypeEnum) {

		orderTypeList = orderTypeList == null ? new ArrayList<>() : orderTypeList;

		orderTypeList.add(sortTypeEnum.getCode());

		return this;
	}

	public enum SortTypeEnum {

		SORT_DESC("desc"),

		SORT_ASC("asc");

		String code;

		private SortTypeEnum(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}
	}
}