package it.giunti.apg.shared;

public class NumberBaseConverter {

	// private static char symbols[] = new char[] {
	//	 '0','1','2','3','4','5','6','7','8','9',
	//	 'A','B','C','D','E','F','G','H','L','M',
	//	 'N','P','Q','R','T','U','V','W','X','Z' };
	public static int BASE = 30;
	private static String symbols = 
			"0123456789"+
			"ABCDEFGHLM"+
			"NPQRTUVWXZ";

	public static String toBase30(int num) {
		return convert(num, BASE);
	}

	public static String getChecksum30(String number) {
		return checksum(number, BASE);
	}


	//Metodo privato di conversione
	private static String convert ( int number, int base ) {
		return convert(number, base, 0, "" );
	}

	//Metodo interno per le chiamate ricorsive
	private static String convert ( int number, int base, int position, String result ) {
		if ( number < Math.pow(base, position + 1) ) {
			int i = (number / (int)Math.pow(base, position));
			return symbols.substring(i, i+1) + result;
		} else {
			int remainder = (number % (int)Math.pow(base, position + 1));
			int i = remainder / (int)(Math.pow(base, position));
			return convert(number - remainder, base, position + 1, symbols.substring(i, i+1) + result );
		}
	}

	private static String checksum(String number, int base) {
		int sum = 1;
		for (int i=0; i<number.length(); i++) {
			sum += symbols.indexOf(number.substring(i, i+1));//Trasforma il carattere in intero 0-29 e somma
		}
		String longChecksum = convert(sum, BASE);
		String checksum = longChecksum.substring(longChecksum.length()-1);//Ultimo carattere
		return checksum;
	}
}
