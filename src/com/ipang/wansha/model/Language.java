package com.ipang.wansha.model;

public enum Language {
	
	CHINESE("chinese", 0), ENGLISH("english", 1), JAPANESE("japanese", 2); 
	
	private int index;
	private String name;
	
	private Language(String name, int index){
		this.index = index;
		this.name = name;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public static String getName(int index){
		for (Language lan : Language.values()) {  
            if (lan.getIndex() == index) {  
                return lan.name;  
            }  
        }  
        return null;   
	}
	
	@Override  
    public String toString() {  
        return this.name;  
    } 
	
	public static Language fromString(String text){
		if (text != null){
			if (text.toLowerCase().equals("chinese")){
				return Language.CHINESE;
			}
			else if (text.toLowerCase().equals("english")){
				return Language.ENGLISH;
			}
			else if (text.toLowerCase().equals("japanese")){
				return Language.JAPANESE;
			}
		}
		return null;
	}
	
}
