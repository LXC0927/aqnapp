package com.njaqn.itravel.aqnapp.service.bean;

public class MapResultBean {

	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MapResultBean(int type, String name) {
		super();
		this.type = type;
		this.name = name;
	}
	private int type;
	private String name;
}
