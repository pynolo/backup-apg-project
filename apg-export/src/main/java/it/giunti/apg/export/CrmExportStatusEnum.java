package it.giunti.apg.export;

public enum CrmExportStatusEnum {
	BLOCCATO("BLOCCATO"),
	//inizia: al blocco
	//cambia: solo in caso di modifica abbonamento o rinnovo
	DISDETTA("DISDETTA"),
	//inizia: da data di disdetta
	//cambia: solo in caso di modifica abbonamento o rinnovo
	OMAGGIO("OMAGGIO"),
	//inizia: da inserimento
	//cambia: solo in caso di modifica abbonamento o rinnovo
	BENEFICIARIO("BENEFICIARIO"),
	//inizia: da inserimento
	//cambia: solo per modifica o rinnovo
	PROSPECT("PROSPECT"),
	//inizia: da inserimento (flag "proposta d'acquisto")
	//cambia: al pagamento o per modifica abbonamento
	ATTESA_SALDO("ATTESA_SALDO"),
	//inizia: da inserimento
	//cambia: solo al pagamento o per modifica abbonamento
	PAGATO("PAGATO"); //(include i fatturati)
	//inizia: dal momento in cui risulta pagato
	//cambia: solo in caso di modifica abbonamento o rinnovo

	private final String status;
	
	CrmExportStatusEnum(String status) {
		this.status=status;
	}
	
	public String getStatus() {
		return status;
	}
}
