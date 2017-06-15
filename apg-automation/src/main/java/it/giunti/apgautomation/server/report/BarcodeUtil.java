package it.giunti.apgautomation.server.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.datamatrix.encoder.SymbolShapeHint;
import com.google.zxing.oned.Code128Writer;

public class BarcodeUtil {
	
	public static int MATRIX_WIDTH=1000;
	public static int MATRIX_HEIGHT=250;
	public static int CODE128_WIDTH=1000;
	public static int CODE128_HEIGHT=250;
	
	public static byte[] getDataMatrixFile(String message) 
			throws IOException {
		Map<EncodeHintType,SymbolShapeHint> hints = new HashMap<EncodeHintType, SymbolShapeHint>();
		hints.put(EncodeHintType.DATA_MATRIX_SHAPE, SymbolShapeHint.FORCE_RECTANGLE);
		BitMatrix bitMatrix = new DataMatrixWriter().encode(message,
				BarcodeFormat.DATA_MATRIX, MATRIX_WIDTH, MATRIX_HEIGHT, hints);

		String imageFormat = "jpg"; // could be "gif", "tiff", "jpeg"
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, baos); 
		return baos.toByteArray();
	}
	
	public static byte[] getBarcode128cFile(String message) 
			throws IOException {
		BitMatrix bitMatrix;
		try {
			bitMatrix = new Code128Writer().encode(message,
					BarcodeFormat.CODE_128, CODE128_WIDTH, CODE128_HEIGHT);
		} catch (WriterException e) {
			throw new IOException(e.getMessage());
		}

		String imageFormat = "jpg"; // could be "gif", "tiff", "jpeg"
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, baos); 
		return baos.toByteArray();
	}
	
}
