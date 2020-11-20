package edu.wpi.modula3.what2think.model;

import com.sun.istack.internal.Nullable;

public class User {
	String name;

	@Nullable
	String password;

	public User() {

	}

	public User(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
