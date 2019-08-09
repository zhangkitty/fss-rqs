package com.znv.fssrqs.constant;

public enum Status {
	
	WAITING(1, "waiting"), 
	STARTING(4, "starting"), 
	STARTED(0, "started"), 
	PAUSING(5, "pausing"), 
	PAUSED(2, "paused"), 
	FINISHING(6, "finishing"),
	FINISHED(3, "finished");

	private int code;

	private String desc;

	private Status(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

	public int getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}
}