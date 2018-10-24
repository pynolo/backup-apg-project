package it.giunti.apg.shared;

import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Nazioni;

import java.util.Arrays;
import java.util.Date;

/**
 * <p>
 * ValueUtil validates that the name the user enters is valid.
 * </p>
 * <p>
 * This class is in the <code>shared</code> packing because we use it in both
 * the client code and on the server. On the client, we verify that the name is
 * valid before sending an RPC request so the user doesn't have to wait for a
 * network round trip to get feedback. On the server, we verify that the name is
 * correct to ensure that the input is correct regardless of where the RPC
 * originates.
 * </p>
 * <p>
 * When creating a class that is used on both the client and the server, be sure
 * that all code is translatable and does not use native JavaScript. Code that
 * is note translatable (such as code that interacts with a database or the file
 * system) cannot be compiled into client side JavaScript. Code that uses native
 * JavaScript (such as Widgets) cannot be run on the server.
 * </p>
 */
public class ValueUtil {
	
	private static final long DAY = 1000 * 60 * 60 * 24;
	private final static char[] elencoPari = {'0','1','2','3','4','5','6','7','8','9','A','B',
			'C','D','E','F','G','H','I','J','K','L','M','N',
			'O','P','Q','R','S','T','U','V','W','X','Y','Z'
	};
             
	private final static int[] elencoDispari= {1, 0, 5, 7, 9, 13, 15, 17, 19, 21, 1, 0, 5, 7, 9, 13,
			15, 17, 19, 21, 2, 4, 18, 20, 11, 3, 6, 8, 12, 14, 16,
			10, 22, 25, 24, 23
	};
	
	/**
	 * String to integer
	 * @param s
	 * @return
	 */
	public static Integer stoi(String s) {
		if (s == null) return null;
		Integer result;
		try {
			result = Integer.valueOf(s);
		} catch (NumberFormatException e) {
			result = null;
		}
		return result;
	}
	
	/**
	 * buffer to String
	 * @param s
	 * @return
	 */
	public static String btos(byte[] b) {
		String result = new String(b);
		return result.trim();
	}
	
	/**
	 * String to integer
	 * @param s
	 * @return
	 */
	public static boolean stobool(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase("true")) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifies that the specified name is valid for our service.
	 * 
	 * In this example, we only require that the name is at least four
	 * characters. In your application, you can use more complex checks to ensure
	 * that usernames, passwords, email addresses, URLs, and other fields have the
	 * proper syntax.
	 * 
	 * @param name the name to validate
	 * @return true if valid, false if invalid
	 */
	public static boolean isValidName(String name) {
		if (name == null) {
			return false;
		}
		return name.length() > 2;
	}
	
	public static boolean isValidEmail(String email) {
		if (email == null) return true;
		if (email.equals("")) return true;
		return email.matches(AppConstants.REGEX_EMAIL);
	}
	
	public static boolean isValidPIva(String pi, String idNazione) {
		if (idNazione == null) idNazione = AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
		if (idNazione.equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			if (pi == null) return false;
			boolean pIvaOk = pi.matches(AppConstants.REGEX_PARTITA_IVA);
			if (pIvaOk) pIvaOk = verifyCinPIva(pi);
			return pIvaOk;
		}
		return true;
	}
	
	public static boolean isValidCodFisc(String codFisc, String idNazione) {
		if (idNazione == null) idNazione = AppConstants.DEFAULT_ID_NAZIONE_ITALIA;
		if (idNazione.equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			if (codFisc == null) return false;
			codFisc = codFisc.toUpperCase();
			boolean codFiscOk = codFisc.matches(AppConstants.REGEX_CODFISC);
			if (codFiscOk) codFiscOk = verifyCinCodFisc(codFisc);
			//Can contain a PIva
			boolean pIvaOk = isValidPIva(codFisc, idNazione);
			return codFiscOk || pIvaOk;
		}
		return true;
	}
	
	public static boolean isValidTelephone(String phone) {
		if (phone == null) return true;
		if (phone.equals("")) return true;
		return phone.matches(AppConstants.REGEX_TELEPHONE);
	}
	
	/** Compare two dates
	 * @return 0 if dt1-dt2 <= DAY
	 * @return 0 if dt2-dt1 <= DAY
	 * @return 1 if dt1 > dt2
	 * @return -1 if dt1 < dt2
	 */
	public static int fuzzyCompare(Date date1, Date date2) {
		long dt1 = date1.getTime();
		long dt2 = date2.getTime();
		if ((dt1-dt2 <= DAY) || (dt2-dt1 <= DAY)) return 0;
		if (dt1 > dt2) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public static String rot13(String s) {
		String result = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if       (c >= 'a' && c <= 'm') c += 13;
            else if  (c >= 'n' && c <= 'z') c -= 13;
            else if  (c >= 'A' && c <= 'M') c += 13;
            else if  (c >= 'A' && c <= 'Z') c -= 13;
            result += c;
        }
        return result;
    }
	
	public static String newLineToBr(String s) {
		String replaced = s.replaceAll("\\r\\n", "<br />");
		return replaced;
	}
		
	//public static String spaceToNbsp(String html) {
	//	return StringUtils.replace(html," ", "&nbsp;");
	//}
	//
	//public static String spaceToUnderscore(String s) {
	//	return StringUtils.replace(s," ", "_");
	//}

	//public static String capitalizeFirstLetter(String s) {
	//	if (s == null) return null;
	//	String result = "";
	//	boolean nextCapital = true;
	//	for (int i = 0; i<s.length(); i++) {
	//		String letter = s.substring(i, i+1);
	//		if (nextCapital) {
	//			result += letter.toUpperCase();
	//		} else {
	//			result += letter.toLowerCase();
	//		}
	//		if (" -.,:;/()'#*_<>\"\\".contains(letter)) {
	//			nextCapital=true;
	//		} else {
	//			nextCapital=false;
	//		}
	//	}
	//	return result;
	//}
	
//	public static String capitalize(String input) {
//	boolean capital = true;
//	String result = "";
//	for (int i=0;i<input.length();i++) {
//		String c = input.substring(i, i+1);
//		if (!capital) {
//			c = c.toLowerCase();
//		}
//		result += c;
//		if (c.equals(" ") || c.equals(".") || c.equals("-") ||
//				c.equals("'") || c.equals(",") || c.equals("(")) {
//			capital = true;
//		} else {
//			capital = false;
//		}
//	}
//	return result;
//}
	
	public static Double roundToCents(Double value) {
		return (double)Math.round(value * 100D) / 100D;
	}
	
	public static Double getImponibile(Double prezzo, Double valoreAliquotaIva) {
		double coefficiente = 1D + valoreAliquotaIva;
		double imponibile = prezzo / coefficiente;
		imponibile = roundToCents(imponibile);
		return imponibile;
	}
	
	public static String addLeftPaddingSpaces(String s, int finalLength) {
		while (s.length() < finalLength) {
			s = " "+s;
		}
		return s;
	}
	
	//public static String getCodiceIva(AliquoteIva aliquota, Nazioni nazione, boolean isSocieta) {
	//	if (nazione.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
	//		//ITALIA
	//		if (isSocieta) {
	//			return aliquota.getCodiceItaSoc();
	//		} else {
	//			return aliquota.getCodiceItaPvt();
	//		}
	//	}
	//	if (nazione.getUe()) {
	//		//UE
	//		if (isSocieta) {
	//			return aliquota.getCodiceUeSoc();
	//		} else {
	//			return aliquota.getCodiceUePvt();
	//		}
	//	}
	//	return aliquota.getCodiceExtraUe();
	//}
	//
	//public static Double getValoreIva(AliquoteIva aliquota, Nazioni nazione, boolean isSocieta) {
	//	if (nazione.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
	//		//ITALIA
	//		return aliquota.getValore();
	//	}
	//	if (nazione.getUe()) {
	//		//UE
	//		if (isSocieta) {
	//			return 0D;
	//		} else {
	//			return aliquota.getValore();
	//		}
	//	}
	//	return 0D;
	//}
	
	public static String getTipoIva(Nazioni nazione, boolean isSocieta) {
		if (nazione.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			//ITALIA
			if (isSocieta) {
				return AppConstants.IVA_ITALIA_SOCIETA;
			} else {
				return AppConstants.IVA_ITALIA_PRIVATO;
			}
		}
		if (nazione.getUe()) {
			//UE
			if (isSocieta) {
				return AppConstants.IVA_UE_SOCIETA;
			} else {
				return AppConstants.IVA_UE_PRIVATO;
			}
		}
		return AppConstants.IVA_EXTRA_UE;
	}
	
	public static String getCodiceIva(AliquoteIva aliquota, String tipoIva) throws BusinessException {
		if (tipoIva == null || aliquota == null) {
				throw new BusinessException("Null values! tipoIva ="+tipoIva+" aliquota"+aliquota);
		}
		if (tipoIva.equals(AppConstants.IVA_ITALIA_SOCIETA)) return aliquota.getCodiceItaSoc();
		if (tipoIva.equals(AppConstants.IVA_ITALIA_PRIVATO)) return aliquota.getCodiceItaPvt();
		if (tipoIva.equals(AppConstants.IVA_UE_SOCIETA)) return aliquota.getCodiceUeSoc();
		if (tipoIva.equals(AppConstants.IVA_UE_PRIVATO)) return aliquota.getCodiceUePvt();
		return aliquota.getCodiceExtraUe();
	}
	
	//public static Double getValoreIva(AliquoteIva aliquota, String tipoIva) {
	//	if (tipoIva.equals(AppConstants.IVA_ITALIA_SOCIETA)) return aliquota.getValore();
	//	if (tipoIva.equals(AppConstants.IVA_ITALIA_PRIVATO)) return aliquota.getValore();
	//	if (tipoIva.equals(AppConstants.IVA_UE_SOCIETA)) return 0D;
	//	if (tipoIva.equals(AppConstants.IVA_UE_PRIVATO)) return aliquota.getValore();
	//	return 0D;
	//}
	
	private static boolean verifyCinPIva(String pi) {
		boolean isValid = false;
		int zeroChar = "0".charAt(0);
		if( pi != null ) {
			if( pi.length() == 11 ) {
				int s = 0;
				for(int i = 0; i <= 9; i += 2 )
					s += pi.charAt(i) - zeroChar;
				for(int i = 1; i <= 9; i += 2 ){
					int c = 2*( pi.charAt(i) - zeroChar);
					if( c > 9 ) c = c - 9;
					s += c;
				}
				int modulo = (10 - s%10)%10;
				int num = pi.charAt(10) - zeroChar;
				if(modulo == num) isValid = true;
			}
		}
		return isValid;
	}
	
	private static boolean verifyCinCodFisc(String codFisc) {
		boolean isValid = false;
		if (codFisc != null) {
			if (codFisc.length() == 16) {
				//Calcolo CIN
				String str = codFisc.toUpperCase().substring(0, 15);
				int pari=0;
				int dispari=0;
		
				for(int i=0; i<str.length(); i++) {
					char ch = str.charAt(i);// i-esimo carattere della stringa
					// Il primo carattere e' il numero 1 non 0
					if((i+1) % 2 == 0) {
						int index = Arrays.binarySearch(elencoPari,ch);
						pari += (index >= 10) ? index-10 : index;
					} else {
						int index = Arrays.binarySearch(elencoPari,ch);
						dispari += elencoDispari[index];
					}
				}
		
				int controllo = (pari+dispari) % 26;
				controllo += 10;
				String cin = elencoPari[controllo]+"";
				if (codFisc.substring(15).equals(cin)) isValid = true;
			}
		}
		return isValid;
	}
}
