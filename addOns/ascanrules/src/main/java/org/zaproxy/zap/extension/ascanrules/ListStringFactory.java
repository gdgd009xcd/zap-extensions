package org.zaproxy.zap.extension.ascanrules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListStringFactory {
	private int n;
	private int rowunit;
	private int origrowsiz ;
	List<String> records;
	
	ListStringFactory() {
		n= 1;
		rowunit = 10000;
	}
	
	ListStringFactory(int ru){
		n = 1;
		rowunit = ru;
	}
	
	public int calcRowSize(String lines) {
		
		records = Arrays.asList(lines.split("\n"));
		origrowsiz = records.size();
		n = origrowsiz / rowunit + 1;
		return n;
	}
	
	public int getOrigRowSize() {
		return origrowsiz;
	}
	
	public void setRowSize(int _n) {
		n = _n;
	}
	
	public int getRowSize() {
		return n;
	}
	
	List<String> getLFSplittedStringList(String b){
		List<String> splinesB = new ArrayList<String>();
		int x = 0;
		
		List<String> blist = records;
		if(b!=null) {
			blist = Arrays.asList(b.split("\n"));
		}
		
		int reclen = blist.size();
		
		for(int stp = 0; stp < reclen ; stp+=n) {
			int etp = stp + n;
			if(etp>reclen) {
				etp = reclen;
			}
			String joinedstring = String.join("\n", blist.subList(stp, etp));
			splinesB.add(joinedstring);
		}
		
		return splinesB;
	}
	
	
}
