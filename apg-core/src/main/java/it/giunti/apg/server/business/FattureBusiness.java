package it.giunti.apg.server.business;

import it.giunti.apg.server.Mailer;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.persistence.AliquoteIvaDao;
import it.giunti.apg.server.persistence.ContatoriDao;
import it.giunti.apg.server.persistence.FattureArticoliDao;
import it.giunti.apg.server.persistence.FattureDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.PagamentiCreditiDao;
import it.giunti.apg.server.persistence.PagamentiDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.AliquoteIva;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureArticoli;
import it.giunti.apg.shared.model.Indirizzi;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Nazioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Pagamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.Societa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.mail.EmailException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class FattureBusiness {

	//static private Logger LOG = LoggerFactory.getLogger(FattureBusiness.class);

	
	public static Societa findSocieta(String idSocieta)
			throws BusinessException {
		Societa result = null;
		Session ses = SessionFactory.getSession();
		try {
			result = GenericDao.findById(ses, Societa.class, idSocieta);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result == null) throw new BusinessException("Nessuna societa trovata");
		return result;
	}
	
	public static Fatture saveFatturaConNumero(Session ses,
			Anagrafiche pagante, String idSocieta, Date dataFattura, boolean isFittizia) 
			throws HibernateException, BusinessException {
		Fatture fattura = new Fatture();
		fattura.setDataCreazione(new Date());
		fattura.setDataFattura(dataFattura);
		fattura.setDataEmail(null);
		fattura.setIdAnagrafica(pagante.getId());
		fattura.setIdIstanza(null);
		fattura.setIdPeriodico(null);
		fattura.setIdSocieta(idSocieta);
		fattura.setIdTipoDocumento(AppConstants.DOCUMENTO_FATTURA);
		Indirizzi indirizzo = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione()))
				indirizzo = pagante.getIndirizzoFatturazione();
		Nazioni nazione = indirizzo.getNazione();
		boolean isSocieta = false;
		if (pagante.getPartitaIva() != null) {
			if (pagante.getPartitaIva().length() > 1) isSocieta = true;
		}
		String tipoIva = ValueUtil.getTipoIva(nazione, isSocieta);
		fattura.setTipoIva(tipoIva);
		//Double totaleFinale = sumTotaleFinale(ia, oiaList, ivaScorporata);
		fattura.setTotaleFinale(-1D);
		//Double totaleImponibile = sumTotaleImponibile(ia, oiaList);
		fattura.setTotaleImponibile(-1D);
		//Double totaleIva = sumTotaleIva(ia, oiaList, ivaScorporata);
		fattura.setTotaleIva(-1D);
		//NUMERO FATTURA
		Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
		String prefisso = societa.getPrefissoFatture();
		if (isFittizia) prefisso = AppConstants.FATTURE_PREFISSO_FITTIZIO;
		Integer numero = new ContatoriDao().nextTempNumFattura(ses, prefisso, dataFattura);
		String numeroFattura = FattureBusiness
				.buildNumeroFattura(prefisso, dataFattura, numero);
		fattura.setNumeroFattura(numeroFattura);
		new FattureDao().save(ses, fattura);
		return fattura;
	}
	
	public static void sumIntoFattura(Fatture fattura, List<FattureArticoli> faList) {
		//Update fattura
		Double totFatt = 0D;
		Double impFatt = 0D;
		Double ivaFatt = 0D;
		for (FattureArticoli fa:faList) {
			totFatt += fa.getImportoTotUnit()*fa.getQuantita();
			impFatt += fa.getImportoImpUnit()*fa.getQuantita();
			ivaFatt += fa.getImportoIvaUnit()*fa.getQuantita();
		}
		fattura.setTotaleFinale(totFatt);
		fattura.setTotaleImponibile(impFatt);
		fattura.setTotaleIva(ivaFatt);
	}
	
	public static boolean hasIvaScorporata(Anagrafiche pagante) {
		boolean ivaScorporata = false;
		Indirizzi indirizzo = pagante.getIndirizzoPrincipale();
		if (IndirizziBusiness.isFilledUp(pagante.getIndirizzoFatturazione()))
				indirizzo = pagante.getIndirizzoFatturazione();
		if (!indirizzo.getNazione().getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			//NON Italia
			if (indirizzo.getNazione().getUe()) {
				//Inside UE
				boolean hasIva = false;
				if (pagante.getPartitaIva() != null) {
					if (pagante.getPartitaIva().length() > 1) hasIva = true;
				}
				if (hasIva) {
					ivaScorporata = true;//In UE le aziende hanno iva scorporata
				}
			} else {
				//Extra UE
				ivaScorporata = true;//ExtraUE sempre iva scorporata
			}
		}
		return ivaScorporata;
	}
	
	public static void filterListsByCodFisc(List<IstanzeAbbonamenti> iaList, List<IstanzeAbbonamenti> errorList) {
		for (IstanzeAbbonamenti ia:iaList) {
			Anagrafiche pagante = ia.getPagante();
			if (pagante == null) pagante = ia.getAbbonato();
			//Controllo codice fiscale
			if (pagante.getCodiceFiscale() == null) {
				errorList.add(ia);
			} else {
				if (pagante.getCodiceFiscale().length() < 16) {
					errorList.add(ia);
				}
			}
		}
		for (IstanzeAbbonamenti errorIa:errorList) {
			iaList.remove(errorIa);
		}
	}
	
	public static void filterListsByEmail(List<IstanzeAbbonamenti> iaList, List<IstanzeAbbonamenti> errorList) {
		for (IstanzeAbbonamenti ia:iaList) {
			Anagrafiche pagante = ia.getPagante();
			if (pagante == null) pagante = ia.getAbbonato();
			//Controllo email
			if (pagante.getEmailPrimaria() == null) {
				errorList.add(ia);
			} else {
				if (pagante.getEmailPrimaria().length() < 3) {
					errorList.add(ia);
				}
			}
		}
		for (IstanzeAbbonamenti errorIa:errorList) {
			iaList.remove(errorIa);
		}
	}
	
	public static void sendWarningEmailForMissingCodFisc(List<IstanzeAbbonamenti> errorList, 
			Periodici periodico, String[] emailRecipients) 
			throws BusinessException, MessagingException, EmailException {
		if (errorList.size() == 0) return;
		//String avviso = "Non e' stato possibile generare le fatture per i seguenti abbonamenti: ";
		String emailSubject = "[APG "+periodico.getNome()+"] " +
				"Errori di fatturazione: "+errorList.size();
		String emailMsg = "Per alcuni abbonamenti non e' stato possibile generare le fatture previste a norma di legge.\r\n"+
				"Per non incorrere in sanzioni e' necessario inserire codice fiscale ed email nelle seguenti anagrafiche:\r\n";
		for (IstanzeAbbonamenti errorIa:errorList) {
			Anagrafiche pagante = errorIa.getPagante();
			if (pagante == null) pagante = errorIa.getAbbonato();
			//avviso += errorIa.getAbbonamento().getCodiceAbbonamento()+" ";
			emailMsg += "["+pagante.getUid()+"] "+pagante.getIndirizzoPrincipale().getCognomeRagioneSociale()+" ";
			if (pagante.getIndirizzoPrincipale().getNome() != null)
					if (pagante.getIndirizzoPrincipale().getNome().length() > 0)
							emailMsg += pagante.getIndirizzoPrincipale().getNome()+" ";
			emailMsg += "abbonamento "+errorIa.getAbbonamento().getCodiceAbbonamento()+" tipo "+
					errorIa.getListino().getTipoAbbonamento().getCodice()+"\r\n";
		}
		//AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
		Mailer.postMail(ServerConstants.SMTP_HOST, ServerConstants.SMTP_FROM,
				emailRecipients, emailSubject, emailMsg, false);
	}
	
	public static String buildNumeroFattura(String companyPrefix, Date date, Integer numero) 
			throws BusinessException {
		//Prefisso società
		if (companyPrefix == null) throw new BusinessException("Il prefisso del numero fattura e' null");
		if (companyPrefix.length() != 3) throw new BusinessException("Il prefisso del numero fattura non e' lungo 3");
		String nf = companyPrefix;
		//Cifra dell'anno
		if (date == null) throw new BusinessException("La data e' necessaria per creare il numero fatturazione");
		String anno = ServerConstants.FORMAT_YEAR.format(date);
		nf += anno.substring(anno.length()-1);//ultima cifra
		//Numero fattura
		if (numero == null) throw new BusinessException("Il numero fatturazione e' null");
		if (numero < 1) throw new BusinessException("Il numero fatturazione e' < 1");
		String numTmp = "000000"+numero;
		nf += numTmp.substring(numTmp.length()-6);
		return nf;
	}
		
	public static FattureArticoli createFatturaArticoloFromIstanza(Integer idFattura,
			IstanzeAbbonamenti ia, boolean ivaScorporata, double riduzione) {
		FattureArticoli result = new FattureArticoli();
		result.setIdFattura(idFattura);
		result.setQuantita(ia.getCopie());
		result.setResto(false);
		result.setAliquotaIva(ia.getListino().getAliquotaIva());
		Double prezzo = ia.getListino().getPrezzo()*riduzione;
		Double impUnit = ValueUtil.getImponibile(prezzo,
				ia.getListino().getAliquotaIva().getValore());
		result.setIvaScorporata(ivaScorporata);
		if (ivaScorporata) {
			result.setImportoImpUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoTotUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoIvaUnit(0D);
		} else {
			result.setImportoImpUnit(ValueUtil.roundToCents(impUnit));
			result.setImportoTotUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoIvaUnit(ValueUtil.roundToCents(prezzo)-impUnit);
		}
		String descAbb = "Quota abbonamento a '"+
				ia.getListino().getTipoAbbonamento().getPeriodico().getNome()+"' ";
		if (ia.getListino().getDigitale() && !ia.getListino().getCartaceo()) descAbb += "digitale ";
		String descDurata = ServerConstants.FORMAT_MONTH.format(
				ia.getFascicoloInizio().getDataInizio())+" - "+
				ServerConstants.FORMAT_MONTH.format(
						ia.getFascicoloFine().getDataFine())+" ";
		descAbb += descDurata;
		if (ia.getOpzioniIstanzeAbbonamentiSet().size() > 0) {
			String descOpz = "";
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
					if (oia.getOpzione().getId() == ol.getOpzione().getId()) {
						if (descOpz.length() > 0) descOpz += ", ";
						descOpz += ol.getOpzione().getNome();
					}
				}
			}
			if (ia.getListino().getOpzioniListiniSet().size() > 1) descAbb += "con inclusi "+descOpz;
			if (ia.getListino().getOpzioniListiniSet().size() == 1) descAbb += "con incluso "+descOpz;
		}
		descAbb +="\r\nCodice abbonato "+ia.getAbbonamento().getCodiceAbbonamento();
		result.setDescrizione(descAbb);
		return result;
	}
	
	public static FattureArticoli createFatturaArticoloFromOpzione(Integer idFattura,
			OpzioniIstanzeAbbonamenti oia, boolean ivaScorporata, double riduzione) {
		FattureArticoli result = new FattureArticoli();
		result.setIdFattura(idFattura);
		result.setQuantita(oia.getIstanza().getCopie());
		result.setResto(false);
		result.setAliquotaIva(oia.getOpzione().getAliquotaIva());
		Double prezzo = oia.getOpzione().getPrezzo()*riduzione;
		Double impUnit = ValueUtil.getImponibile(prezzo,
				oia.getOpzione().getAliquotaIva().getValore());
		result.setIvaScorporata(ivaScorporata);
		if (ivaScorporata) {
			result.setImportoImpUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoTotUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoIvaUnit(0D);
		} else {
			result.setImportoImpUnit(ValueUtil.roundToCents(impUnit));
			result.setImportoTotUnit(ValueUtil.roundToCents(prezzo));
			result.setImportoIvaUnit(ValueUtil.roundToCents(prezzo)-impUnit);
		}
		result.setDescrizione(oia.getOpzione().getNome());
		return result;
	}
	
	public static FattureArticoli createFatturaArticoloFromResto(Integer idFattura,
			Double resto, AliquoteIva aliquotaIva) {//, boolean totalmenteAnticipo) {
		FattureArticoli result = new FattureArticoli();
		result.setIdFattura(idFattura);
		result.setQuantita(1);
		result.setResto(true);//!totalmenteAnticipo);
		result.setAliquotaIva(aliquotaIva);
		result.setImportoImpUnit(resto);
		result.setImportoTotUnit(resto);
		result.setImportoIvaUnit(0D);
		result.setDescrizione("Anticipo");
		result.setIvaScorporata(false);
		return result;
	}
	
	public static void bindIstanzeOpzioni(Session ses, Fatture fatt, 
			IstanzeAbbonamenti ia, Set<OpzioniIstanzeAbbonamenti> oiaSet) {
		OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
		if (ia != null) {
			if (ia.getIdFattura() == null) {
				ia.setIdFattura(fatt.getId());
				ia.setDataSaldo(new Date());
				new IstanzeAbbonamentiDao().updateUnlogged(ses, ia);
			}
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getIdFattura() == null) {
					oia.setIdFattura(fatt.getId());
					oiaDao.update(ses, oia);
				}
			}
		}
		for (OpzioniIstanzeAbbonamenti oia:oiaSet) {
			if (oia.getIdFattura() == null) {
				oia.setIdFattura(fatt.getId());
				oiaDao.update(ses, oia);
			}
		}
	}
	
	public static void bindPagamentiCrediti(Session ses, Fatture fatt, 
			IstanzeAbbonamenti ia,
			List<Pagamenti> pagList, List<PagamentiCrediti> credList){
		//Dati di base
		PagamentiDao pagDao = new PagamentiDao();
		PagamentiCreditiDao credDao = new PagamentiCreditiDao();
		
		//Abbina pagamenti e crediti a ia e pagante
		if (pagList != null) {
			for (Pagamenti pag:pagList) {
				pag.setIdFattura(fatt.getId());
				pag.setIdErrore(null);
				pag.setIstanzaAbbonamento(ia);
				if (ia != null) {
					pag.setIdUtente(ia.getIdUtente());
					if (pag.getAnagrafica() == null) {
						if (ia.getPagante() != null) {
							pag.setAnagrafica(ia.getPagante());
						} else {
							pag.setAnagrafica(ia.getAbbonato());
						}
					}
				}
				pagDao.update(ses, pag);
			}
		}
		if (credList != null) {
			for (PagamentiCrediti cred:credList) {
				cred.setFatturaImpiego(fatt);
				if (ia != null) {
					cred.setIdIstanzaAbbonamento(ia.getId());
				} else {
					cred.setIdIstanzaAbbonamento(null);
				}
				credDao.update(ses, cred);
			}
		}
	}
	
	/* assegna tutte le chiavi esterne in Pagamenti e IstanzeAbbonamento coinvolti nella fattura,
	 * alle Opzioni obbligatorie e quelle passate nei parametri
	 */
	public static List<FattureArticoli> bindFattureArticoli(Session ses, Fatture fatt,
			Double totalePagato, Double resto, Anagrafiche pagante,
			IstanzeAbbonamenti ia, List<Integer> idOpzList,
			List<PagamentiCrediti> credList){
		//Dati di base
		if (resto == null) resto = 0D;
		boolean ivaScorporata = FattureBusiness.hasIvaScorporata(pagante);
		List<FattureArticoli> faList = new ArrayList<FattureArticoli>();
		FattureArticoliDao faDao = new FattureArticoliDao();
		
		//Totale calcolato
		Double dovuto = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId(), idOpzList);
		Double riduzione;
		if (resto > AppConstants.SOGLIA) {
			riduzione = 1D; // 1 = Nessuna riduzione
		} else {
			riduzione = totalePagato / dovuto; //Importi saranno ridotti in proporzione
		}
		
		//Abbina idFattura a ia 
		//e crea FatturaArticolo contenente ia
		if (ia != null) {
			boolean create = false;
			if (ia.getIdFattura() == null) {
				create = true;
			} else {
				if (ia.getIdFattura().equals(fatt.getId())) create = true;
			}
			if (create) {
				new IstanzeAbbonamentiDao().update(ses, ia);
				FattureArticoli fatIa = FattureBusiness
						.createFatturaArticoloFromIstanza(fatt.getId(), ia, ivaScorporata, riduzione);
				faDao.save(ses, fatIa);
				faList.add(fatIa);
			}
		}
		
		//Crea voci per ciascuna opzione
		if (idOpzList != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				boolean create = false;
				if (oia.getIdFattura() == null) {
					create = true;
				} else {
					if (oia.getIdFattura().equals(fatt.getId())) create = true;
				}
				if (create) {
					FattureArticoli fatOia = FattureBusiness
							.createFatturaArticoloFromOpzione(fatt.getId(), oia, ivaScorporata, riduzione);
					faDao.save(ses, fatOia);
					faList.add(fatOia);
				}
			}
		}
				
		//Nuovo resto (agganciato alla fattura ma non a ia)
		if (resto > AppConstants.SOGLIA) {
			//cioè idPagamento ha un importo superiore al dovuto
			List<FattureArticoli> faResto = bindFattureArticoliResto(ses, fatt, resto);//, false);
			faList.addAll(faResto);
		}
		
		return faList;
	}
	
	/* Crea un oggetto FattureArticoli che riporta come anticipo un importo
	 * creando il relativo oggetto PagamentiCrediti
	 */
	public static List<FattureArticoli> bindFattureArticoliResto(Session ses, Fatture fatt,
			double importoAnticipo) {//, boolean totalmenteAnticipo){
		List<FattureArticoli> faList = new ArrayList<FattureArticoli>();
		AliquoteIva aliquotaIva = new AliquoteIvaDao()
				.findDefaultAliquotaIvaByDate(ses,
						AppConstants.DEFAULT_ALIQUOTA_IVA, new Date());
		//Anticipo
		FattureArticoli fatCred = FattureBusiness
				.createFatturaArticoloFromResto(fatt.getId(), importoAnticipo,
						aliquotaIva);//, totalmenteAnticipo);
		new FattureArticoliDao().save(ses, fatCred);
		faList.add(fatCred);
		return faList;
	}
		
	public static boolean isFittizia(Fatture fatt) {
		String prefix = fatt.getNumeroFattura().substring(0,3);
		return prefix.equals(AppConstants.FATTURE_PREFISSO_FITTIZIO);
	}
	
	public static void unbindIstanzaOpzioni(Session ses, Fatture fatt) {
		if (fatt.getIdIstanza() != null) {
			OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
			IstanzeAbbonamenti ia = GenericDao
					.findById(ses, IstanzeAbbonamenti.class, fatt.getIdIstanza());
			if (ia.getIdFattura().equals(fatt.getId())) {
				ia.setIdFattura(null);
				ia.setPagato(false);
			}
			if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					if (oia.getIdFattura().equals(fatt.getId())) {
						oia.setIdFattura(null);
						oiaDao.update(ses, oia);
						ia.setPagato(false);
					}
				}
			}
			new IstanzeAbbonamentiDao().update(ses, ia);
		}
	}
	
	public static Fatture createRimborso(Integer idFattura, boolean isRimborsoTotale,
			boolean isStornoTotale, boolean isRimborsoResto, boolean isStornoResto)
			throws BusinessException {
		if (!isRimborsoTotale && !isStornoTotale && !isRimborsoResto && !isStornoResto) throw new BusinessException("Please define an action");
		Fatture ndc = null;
		Date now = new Date();
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		try {
			Fatture fattura = GenericDao.findById(ses, Fatture.class, idFattura);
			if (fattura == null) throw new BusinessException("Incorrect idFattura");
			if (fattura.getIdNotaCreditoRimborso() != null || 
					fattura.getIdNotaCreditoStorno() != null) throw new BusinessException("Refund has already been made");
			if ((isRimborsoResto || isStornoResto) && 
					fattura.getIdNotaCreditoRimborsoResto() != null || fattura.getIdNotaCreditoStornoResto() != null) 
				throw new BusinessException("Refund has already been made");
			List<FattureArticoli> faList = new FattureArticoliDao().findByFattura(ses, fattura.getId());
			//Initing fatture counter
			String prefisso = fattura.getNumeroFattura().substring(0,3);
			ContatoriDao contDao = new ContatoriDao();
			FattureDao fatDao = new FattureDao();
			//Societa societa = GenericDao.findById(ses, Societa.class, fattura.getIdSocieta());
			contDao.initNumFattura(ses, prefisso, now);
				//Creating rimborso (=fattura)
				List<Fatture> rimborsiList = new ArrayList<Fatture>();
				ndc = new Fatture();
				ndc.setDataCreazione(now);
				ndc.setDataFattura(now);
				ndc.setDataModifica(now);
				ndc.setIdAnagrafica(fattura.getIdAnagrafica());
				ndc.setIdIstanza(fattura.getIdIstanza());
				ndc.setIdPeriodico(fattura.getIdPeriodico());
				ndc.setIdSocieta(fattura.getIdSocieta());
				ndc.setIdTipoDocumento(AppConstants.DOCUMENTO_NOTA_CREDITO);
				ndc.setTipoIva(fattura.getTipoIva());
				ndc.setTotaleFinale(0D);
				ndc.setTotaleImponibile(0D);
				ndc.setTotaleIva(0D);
				//Numero rimborso (=numero fattura)
				Integer numero = new ContatoriDao().nextTempNumFattura(ses, prefisso, now);
				String numeroRimborso = FattureBusiness
						.buildNumeroFattura(prefisso, now, numero);
				ndc.setNumeroFattura(numeroRimborso);
				Integer idNdc = (Integer) fatDao.save(ses, ndc);
				if (isRimborsoTotale) fattura.setIdNotaCreditoRimborso(idNdc);
				if (isStornoTotale) fattura.setIdNotaCreditoStorno(idNdc);
				if (isRimborsoResto) fattura.setIdNotaCreditoRimborsoResto(idNdc);
				if (isStornoResto) fattura.setIdNotaCreditoStornoResto(idNdc);
				fatDao.update(ses, fattura);
				rimborsiList.add(ndc);
				//Articoli
				List<FattureArticoli> raList = new ArrayList<FattureArticoli>();
				FattureArticoliDao faDao = new FattureArticoliDao();
				if (isRimborsoTotale || isStornoTotale) { 
					for (FattureArticoli fa:faList) {
						if ((fattura.getIdNotaCreditoRimborsoResto() == null && fattura.getIdNotaCreditoStornoResto() == null)
								|| !fa.getResto()) {
							FattureArticoli ra = new FattureArticoli();
							ra.setIdFattura(idNdc);
							ra.setAliquotaIva(fa.getAliquotaIva());
							ra.setDescrizione("Storno fattura "+fattura.getNumeroFattura()+
									" per errato addebito: "+fa.getDescrizione());
							ra.setImportoImpUnit(fa.getImportoImpUnit());
							ra.setImportoIvaUnit(fa.getImportoIvaUnit());
							ra.setImportoTotUnit(fa.getImportoTotUnit());
							ra.setQuantita(fa.getQuantita());
							ra.setResto(fa.getResto());
							ra.setIvaScorporata(fa.getIvaScorporata());
							faDao.save(ses, ra);
							raList.add(ra);
						}
					}
					//Elimina eventuali resti
					removeCrediti(ses, fattura);
					//Se è uno storno, crea credito
					if (isStornoTotale) PagamentiMatchBusiness.createCredito(ses,
							fattura, fattura.getTotaleFinale(), 
							fattura.getIdSocieta(), fattura.getIdAnagrafica(), true);
					//Rimuove le opzioni legate a questa fattura
					FattureBusiness.unbindIstanzaOpzioni(ses, fattura);
				}
				if (isRimborsoResto) {
					FattureArticoli ra = new FattureArticoli();
					ra.setIdFattura(idNdc);
					ra.setAliquotaIva(null);
					ra.setDescrizione("Storno anticipo in fattura "+fattura.getNumeroFattura());
					ra.setImportoImpUnit(fattura.getImportoResto());
					ra.setImportoIvaUnit(0D);
					ra.setImportoTotUnit(fattura.getImportoResto());
					ra.setQuantita(1);
					ra.setResto(true);
					ra.setIvaScorporata(false);
					faDao.save(ses, ra);
					raList.add(ra);
					//Elimina eventuali resti
					removeCrediti(ses, fattura);
				}
				if (isStornoResto) {
					FattureArticoli ra = new FattureArticoli();
					ra.setIdFattura(idNdc);
					ra.setAliquotaIva(null);
					ra.setDescrizione("Storno anticipo in fattura "+fattura.getNumeroFattura());
					ra.setImportoImpUnit(fattura.getImportoResto());
					ra.setImportoIvaUnit(0D);
					ra.setImportoTotUnit(fattura.getImportoResto());
					ra.setQuantita(1);
					ra.setResto(true);
					ra.setIvaScorporata(false);
					faDao.save(ses, ra);
					raList.add(ra);
					markCreditiStornati(ses, fattura);
				}
				FattureBusiness.sumIntoFattura(ndc, raList);
				fatDao.update(ses, ndc);
				
				//STAMPA
				//FattureStampe stampa = FatturePdfBusiness.createTransientStampaFattura(ses, ndc);
				//new FattureStampeDao().save(ses, stampa);

			//** COMMIT ** dei numeri fattura creati
			new ContatoriDao().commitNumFattura(ses, prefisso, now);
			//Return .pdf to HTTP outputstream
			//PrintWriter out = new PrintWriter(response.getOutputStream());
			//ServletOutputStream binout = response.getOutputStream();
			//response.setContentType(stampa.getMimeType());
			//response.setHeader("Content-Disposition", "attachment;filename="+stampa.getFileName());
			//byte[] sfBytes = stampa.getContent();
			//binout.write(sfBytes);
			//out.close();
			trn.commit();
		} catch (HibernateException | BusinessException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage());
		} finally {
			ses.close();
		}
		return ndc;
	}
	private static void markCreditiStornati(Session ses, Fatture fatturaOrigine) {
		PagamentiCreditiDao credDao = new PagamentiCreditiDao();
		List<PagamentiCrediti> credList = 
				credDao.findByFatturaOrigine(ses, fatturaOrigine.getId());
		for (PagamentiCrediti cred:credList) {
			cred.setStornatoDaOrigine(true);
			credDao.update(ses, cred);
		}
	}
	private static void removeCrediti(Session ses, Fatture fatturaOrigine) {
		PagamentiCreditiDao credDao = new PagamentiCreditiDao();
		List<PagamentiCrediti> credList = 
				credDao.findByFatturaOrigine(ses, fatturaOrigine.getId());
		for (PagamentiCrediti cred:credList) {
			credDao.delete(ses, cred);
		}
	}
}
