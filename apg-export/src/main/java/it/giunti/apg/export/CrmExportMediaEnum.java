package it.giunti.apg.export;

public enum CrmExportMediaEnum {
	DIGITAL("D"),
	PAPER("P"),
	BOTH("DP");
	
	private final String media;
	
	CrmExportMediaEnum(String media) {
		this.media=media;
	}
	
	public String getMedia() {
		return media;
	}
}
