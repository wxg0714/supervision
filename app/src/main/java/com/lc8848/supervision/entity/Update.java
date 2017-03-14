package com.lc8848.supervision.entity;

import java.io.Serializable;

/**
 * 更新实体类
 * @author wxg
 * @version 1.0
 * 
 */
public class Update implements Serializable {

	private String version;
	private String url;
	private String name;

	public Update(String version, String url, String name) {
		this.version = version;
		this.url = url;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
