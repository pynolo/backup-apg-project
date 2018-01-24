package it.giunti.apg.core.business;

import java.text.Normalizer;

public class CharsetUtil {

	public static String toUppercaseAscii(String s) {
		String string = s.replaceAll("(è\\s)|(è$)", "E' ");
		string = string.replaceAll("(é\\s)|(é$)", "E' ");
		string = string.replaceAll("(à\\s)|(à$)", "A' ");
		string = string.replaceAll("(ò\\s)|(ò$)", "O' ");
		string = string.replaceAll("(ù\\s)|(ù$)", "U' ");
		string = string.toUpperCase().trim();
		string = Normalizer.normalize(string, Normalizer.Form.NFD);
		string = string.replaceAll("[^\\p{ASCII}]", "");//discard anything not-ASCII
		return string;
	}
	
	public static String toMixedCase(String s) {
		if (s == null) return null;
		//Phase 1: aggregate
		String result1 = "";
		for (int i=s.length()-1; i >= 0; i--) {
			String ch = s.substring(i, i+1).toLowerCase();
			String r1 = "";
			if (ch.equals("'") || ch.equals("`")) {
				//Controllo carattere precedente
				if (i > 0) {
					String prev = s.substring(i-1, i).toLowerCase();
					if (prev.contains("a")) r1 = "à";
					if (prev.contains("e")) r1 = "è";
					if (prev.contains("i")) r1 = "ì";
					if (prev.contains("o")) r1 = "ò";
					if (prev.contains("u")) r1 = "ù";
					if (r1.length()>0) i--;
				}
			}
			if (r1.equals("")) r1 = ch;
			result1 = r1+result1;
		}
		//Phase 2: change case
		String wordStart = " .-/\"&()#,_'";
		String result2 = "";
		for (int i = 0; i < result1.length(); i++) {
			String ch = "";
			if (i > 0) ch = result1.substring(i-1, i);
			if (i == 0 || wordStart.contains(ch)) {
				//First char of word
				result2 += result1.substring(i, i+1).toUpperCase();
			} else {
				result2 += result1.substring(i, i+1).toLowerCase();
			}
		}
		return result2;
	}
	
	public static String toSapAscii(String s, int maxLen) {
		if (s == null) return null;
		s = toUppercaseAscii(s);
		if (s.length() > maxLen) s = s.substring(0,maxLen);
		return s;
	}
}
