package com.ipang.wansha.model;

public enum Currency {

	CHINESEYUAN("¥", 0), POUND("£", 1), DOLLAR("$", 2), EURO("€", 3);
	
	private String name;
	private int index;

	private Currency(String name, int index){
		this.name = name;
		this.index = index;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
	public String getSymbol(){
		return this.name;
	}
}
