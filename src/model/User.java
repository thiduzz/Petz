package model;

import java.io.Serializable;

public class User implements Serializable{

	public int user_source;
	public String user_name;
	public String user_city;
	public String user_lastlocation;
	
	public User(int user_source, String user_name, String user_city,
			String user_lastlocation) {
		super();
		this.user_source = user_source;
		this.user_name = user_name;
		this.user_city = user_city;
		this.user_lastlocation = user_lastlocation;
	}
	
	public int getUser_source() {
		return user_source;
	}
	public String getUser_name() {
		return user_name;
	}
	public String getUser_city() {
		return user_city;
	}
	public String getUser_lastlocation() {
		return user_lastlocation;
	}
	public void setUser_source(int user_source) {
		this.user_source = user_source;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public void setUser_city(String user_city) {
		this.user_city = user_city;
	}
	public void setUser_lastlocation(String user_lastlocation) {
		this.user_lastlocation = user_lastlocation;
	}
}
