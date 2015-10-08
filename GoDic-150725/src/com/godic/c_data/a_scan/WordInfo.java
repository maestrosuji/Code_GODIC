package com.godic.c_data.a_scan;

import java.io.Serializable;

public class WordInfo implements Serializable{
	public String engword;
	public String korword;
	public Boolean bmkstate;
	
	public WordInfo(String eng, String kor) {
		engword = eng;
		korword = kor;
		bmkstate = false;
	}
}
