package com.taotao.manage.bean;

public interface Function<T,E> {
	//提供一个方法
	public T callback(E e);
}
