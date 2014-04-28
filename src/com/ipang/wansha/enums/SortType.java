package com.ipang.wansha.enums;

public enum SortType {
	
	DEFAULT(0), PRICE(1), RANKING(2), POPULARITY(3), TIMELONG(4), TIMESHORT(5), SPECIAL(6);
	
	private int index;
	
	private SortType(int index) {
		this.index = index;
	}
	
	public static SortType fromIndex(int index){
		for (SortType type : SortType.values()) {
            if (type.getIndex() == index) {
                return type;
            }
        }
        return null;
	}
	
	public int getIndex(){
		return this.index;
	}
}
