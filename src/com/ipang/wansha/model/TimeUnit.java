package com.ipang.wansha.model;

public enum TimeUnit {

	MINUTE("minute", 0), HOUR("hour", 1), DAY("day", 2);
	
	private String name;
	private int index;
	
	private TimeUnit(String name, int index){
		this.name = name;
		this.index = index;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public static String getName(int index){
		for (TimeUnit unit : TimeUnit.values()) {  
            if (unit.getIndex() == index) {  
                return unit.name;  
            }  
        }  
        return null;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
	
}
