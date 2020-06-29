package it.giunti.apg.export;

public enum ApgExportModeEnum {
	FULL("FULL"),
	AUTO("AUTO"),
	NONE("NONE");
	
	private final String mode;
	
	ApgExportModeEnum(String mode) {
		this.mode=mode;
	}
	
	public String getMode() {
		return mode;
	}
}
