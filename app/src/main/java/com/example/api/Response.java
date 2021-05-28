package com.example.api;

import com.google.gson.annotations.SerializedName;

public class Response{

	@SerializedName("base64")
	private String base64;

	@SerializedName("title")
	private String title;

	public String getBase64(){
		return base64;
	}

	public String getTitle(){
		return title;
	}
}