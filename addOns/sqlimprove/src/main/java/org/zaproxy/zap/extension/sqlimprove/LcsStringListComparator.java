package org.zaproxy.zap.extension.sqlimprove;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class LcsStringListComparator extends LcsOnp<String>{
	
	int maxcharlength = 100000;
	int minrowlength = 3000;
	int extrowlength = 10;
	
	LcsStringListComparator(Logger _log){
		super(_log);
	}
	
	int extractLCS(String a , String b, LcsCharacterList charLCSresult) {
		if(a==null) {
			a = "";
		}
		
		if(b==null) {
			b = "";
		}
		
		List<String> alist = Arrays.asList(a.split("\n"));
		List<String> blist = Arrays.asList(b.split("\n"));
		
		int asiz = alist.size();
		int bsiz = blist.size();
		int maxsiz = asiz>bsiz?asiz:bsiz;
		
		if(maxsiz<extrowlength) {
			//CharacterList
			CharacterList achar = new CharacterList(a);
			CharacterList bchar = new CharacterList(b);
			return calcPercentChar(achar, bchar, charLCSresult);
		}
		//List<String>
		
		LcsStringList listLCSresult = new LcsStringList();
		
		int lpercent =  calcPercent(alist, blist, listLCSresult);
		List<String> lcslist = listLCSresult.getLCS();
		String lcsStr = String.join("\n", lcslist);
		
		charLCSresult.setLCS(lcsStr);
		
		return lpercent;
		
		
	}
	
	int calcPercentChar(CharacterList ca, CharacterList cb, LcsCharacterList clcs) {
		LcsOnp<Character> onp = new LcsOnp<Character>(getLogger());
		LcsCharacterList cresult = new LcsCharacterList();
		
		int cpercent = onp.calcPercent(ca, cb, clcs);
		{
			String diffCA = cresult.getDiffAString();
			int sa = diffCA.length();
			String diffCB = cresult.getDiffBString();
			int sb = diffCB.length();
			//System.out.println("diffCA[" + diffCA.substring(0, sa>50?50:sa) + "...]");
			//System.out.println("diffCB[" + diffCB.substring(0, sb>50?50:sb) + "...]");
			String lcsstr = cresult.getLCS().getString();
			int lcssiz = lcsstr.length();
			String samelcs = lcsstr.substring(0, lcssiz>50?50:lcssiz);
			//System.out.println("lcs[" + samelcs + "...]");
		}
		return cpercent;
	}
	
	int compare(String a, String b, LcsStringList result) {
		
		if(result==null) {
			result = new LcsStringList();
		}
		
		if(a==null) {
			a = "";
		}
		
		if(b==null) {
			b = "";
		}
		
		
		

		
		ListStringFactory lsfct = new ListStringFactory();
		
		int rowsiza = lsfct.calcRowSize(a);
		int origAsiz = lsfct.getOrigRowSize();
		int rowsizb = lsfct.calcRowSize(b);
		int origBsiz = lsfct.getOrigRowSize();
		int origMaxsiz = origAsiz>origBsiz?origAsiz:origBsiz;
		
		if(origMaxsiz<minrowlength) {
			//calcPercentChar
			LcsCharacterList cresult = new LcsCharacterList();
			CharacterList charA = new CharacterList(a);
			CharacterList charB = new CharacterList(b);
			return calcPercentChar(charA, charB, cresult);
		}
		
		if(rowsiza>rowsizb) {
			lsfct.setRowSize(rowsiza);
		}
		List<String> alist = lsfct.getLFSplittedStringList(a);
		
		List<String> blist = lsfct.getLFSplittedStringList(b);
		
		int lpercent =  calcPercent(alist, blist, result);
		if(log!=null) {
			log.debug("listpercent:" + lpercent);
		}
		//System.out.println("listpercent:" + lpercent);
		
		double dlpercent = 1000 - lpercent;
		
		if(lpercent<750) return lpercent;
		
		List<String> dalist = result.getDiffA();
		List<String> dblist = result.getDiffB();
		Collections.reverse(dalist);
		Collections.reverse(dblist);
		
		String joinedA = String.join("\n", dalist);
		String joinedB = String.join("\n", dblist);
		int alen = joinedA.length();
		int blen = joinedB.length();
		
		int clen = alen>blen?alen:blen;
		int bytelen = clen>maxcharlength?maxcharlength:clen;
		
		int aend = bytelen>alen?alen:bytelen;
		int bend = bytelen>blen?blen:bytelen;
		CharacterList charA = new CharacterList(joinedA.substring(0, aend));
		CharacterList charB = new CharacterList(joinedB.substring(0, bend));
		LcsCharacterList cresult = new LcsCharacterList();
		int cpercent = calcPercentChar(charA, charB, cresult);
		result.setLcsChars(cresult.getLCS().getString());
		
		//System.out.println("cpercent:" + cpercent + " alen,blen:" + alen + "," + blen);
		double lcsbytes = bytelen * (double)cpercent/ 1000;
		double daddpercent = dlpercent * lcsbytes / clen;
		int addpercent = (int)Math.floor(daddpercent);
		if(log!=null) {
			log.debug("addprecent:" + addpercent + " lcsbytes:" + lcsbytes);
		}
		//System.out.println("addprecent:" + addpercent + " lcsbytes:" + lcsbytes);
		return lpercent+addpercent;
		
	}
}
