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
	
	public static Currency fromString(String text){
		if (text != null){
			if (text.toLowerCase().equals("chineseyuan")){
				return Currency.CHINESEYUAN;
			}
			else if (text.toLowerCase().equals("pound")){
				return Currency.POUND;
			}
			else if (text.toLowerCase().equals("dollar")){
				return Currency.DOLLAR;
			}
			else if (text.toLowerCase().equals("euro")){
				return Currency.EURO;
			}
			
		}
		return null;
	}
	
}
