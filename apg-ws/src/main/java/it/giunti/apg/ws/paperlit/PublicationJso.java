package it.giunti.apg.ws.paperlit;

import java.util.Date;

public class PublicationJso {

	private String publicationId = null;
	private Boolean issubscribed = null;
	private String message = null;
	private Date startedon = null; //Datetime in ISO 8601 format
	private Date expireson = null; //Datetime in ISO 8601 format
	
	
	public String getPublicationId() {
		return publicationId;
	}
	public void setPublicationId(String publicationId) {
		this.publicationId = publicationId;
	}
	public Boolean getIssubscribed() {
		return issubscribed;
	}
	public void setIssubscribed(Boolean issubscribed) {
		this.issubscribed = issubscribed;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Date getStartedon() {
		return startedon;
	}
	public void setStartedon(Date startedon) {
		this.startedon = startedon;
	}
	public Date getExpireson() {
		return expireson;
	}
	public void setExpireson(Date expireson) {
		this.expireson = expireson;
	}
	
}
