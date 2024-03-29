/**
 * Copyright 2015-2015 the original author or authors.
 * HomePage: http://www.kayura.org
 */
package org.kayura.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author liangxia@live.com
 *
 */
public class PageList<E> extends ArrayList<E>implements Serializable {

	private static final long serialVersionUID = 1611112691285620907L;

	private Paginator paginator;

	public PageList() {
	}

	public PageList(Collection<? extends E> data) {
		this();
		this.addAll(data);
	}

	public PageList(Collection<? extends E> data, Paginator paginator) {
		this(data);
		this.paginator = paginator;
	}

	public PageList(Paginator paginator) {
		this.paginator = paginator;
	}

	public Paginator getPaginator() {
		return paginator;
	}

	public int getTotalPages() {
		return paginator.getTotalPages();
	}

	public int getTotalCount() {
		return paginator.getTotalCount();
	}

	public int getPageIndex() {
		return paginator.getPageIndex();
	}

	public List<E> getRows() {
		return this;
	}

	public void setRows(List<E> rows) {
		this.clear();
		this.addAll(rows);
	}
}
