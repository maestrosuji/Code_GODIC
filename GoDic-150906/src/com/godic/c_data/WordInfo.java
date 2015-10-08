package com.godic.c_data;

import java.io.Serializable;

public class WordInfo implements Serializable{
	public String engword;
	public String korword;
	public boolean bmkstate;
	
	public WordInfo() {
	}
	
	public WordInfo(String eng, String kor) {
		engword = eng;
		korword = kor;
		bmkstate = false;
	}

	public WordInfo(String eng, String kor, boolean state) {
		engword = eng;
		korword = kor;
		bmkstate = state;
	}
}
