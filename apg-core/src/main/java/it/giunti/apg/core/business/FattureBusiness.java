package it.giunti.apg.core.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.mail.EmailException;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import it.giunti.apg.core.Mailer;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.AliquoteIvaDao;
import it.giunti.apg.core.persistence.ContatoriDao;
import it.giunti.apg.core.persistence.FattureArticoliDao;
import it.giunti.apg.core.persistence.FattureDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.IndirizziUtil;
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

public class FattureBusiness {

	private static int MAX_FATTURE_ERROR_COUNT = 10;
	
	private static final String NOTA_FATTURA_PAGATA = "FATTURA PAGATA";
	private static final String NOTA_CARTA_DOCENTE = "PAGATA CON CARTA DEL DOCENTE";
	private static final String NOTA_RIMBORSO = "DOCUMENTO IN CORSO DI RIMBORSO";
	
	//static private Logger LOG = LoggerFactory.getLogger(FattureBusiness.class);
	public static void initNumFatture(Session ses, List<IstanzeAbbonamenti> iaList, Date ultimoGiornoMese) {
		ContatoriDao contDao = new ContatoriDao();
		//Crea l'elenco società per cui inizializzare il generatore di numeri fatture
		List<String> idSocietaList = new ArrayList<String>();
		for (IstanzeAbbonamenti ia:iaList) {
			String id = ia.getAbbonamento().getPeriodico().getIdSocieta();
			if (!idSocietaList.contains(id)) {
				idSocietaList.add(id);
				Societa societa = GenericDao.findById(ses, Societa.class, id);
				String prefix = societa.getPrefissoFatture();
				if (ia.getListino().getFatturaInibita()) prefix = AppConstants.FATTURE_PREFISSO_FITTIZIO;
  				contDao.initNumFattura(ses, prefix, ultimoGiornoMese);
			}
		}
	}
	
	public static void initNumFatture(Session ses, Date data, String idSocieta) {
		ContatoriDao contDao = new ContatoriDao();
		Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
		String prefix = societa.getPrefissoFatture();
		contDao.initNumFattura(ses, prefix, data);
		contDao.initNumFattura(ses, AppConstants.FATTURE_PREFISSO_FITTIZIO, data);
	}
	
	public static void commitNumFatture(Session ses, List<Fatture> fattureList, String idSocieta) 
			throws HibernateException {
		Date lastDate;
		try {
			lastDate = ServerConstants.FORMAT_DAY.parse("01/01/1900");
		} catch (ParseException e) { throw new HibernateException(e.getMessage(), e);}
		for (Fatture fatt:fattureList) {
			if (fatt.getDataFattura().after(lastDate))
				lastDate=fatt.getDataFattura();
		}
		ContatoriDao contDao = new ContatoriDao();
		Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
		String prefix = societa.getPrefissoFatture();
		contDao.commitNumFattura(ses, prefix, lastDate);
		contDao.commitNumFattura(ses, AppConstants.FATTURE_PREFISSO_FITTIZIO, lastDate);
	}
	public static Fatture setupEmptyFattura(Session ses, Anagrafiche pagante, String idSocieta,
			Date dataPagamento, boolean isFittizia, String idUtente)
			throws BusinessException {
		Date dataFattura = pickDataFattura(dataPagamento);
		
		//** INIT ** dei numeri fattura creati
		initNumFatture(ses, dataFattura, idSocieta);
			List<Fatture> fattureList = new ArrayList<Fatture>();
			//Creazione oggetti Fatture senza produrre i byte[] di stampa
			
			//Persist fatture
			Fatture fattura = null;
			try {
				fattura = createEmptyFatturaConNumero(ses, 
						pagante, idSocieta, dataFattura, isFittizia, idUtente);
				fattureList.add(fattura);
			} catch (HibernateException e) {
				e.printStackTrace();
				throw new BusinessException(e.getMessage(), e);
			} finally {
		//** COMMIT ** dei numeri fattura creati
		commitNumFatture(ses, fattureList, idSocieta);
			}
		return fattura;
	}
	
	private static boolean hasValidInvoiceData(String codFisc, String partitaIva, String idNazione) {
		if (codFisc == null) codFisc = "";
		if (partitaIva == null) partitaIva = "";
		boolean cfValid = false;
		if (codFisc.length() > 0) cfValid = ValueUtil.isValidCodFisc(codFisc, idNazione);
		boolean piValid = false;
		if (partitaIva.length() > 0) piValid = ValueUtil.isValidPartitaIva(partitaIva, idNazione);
		return cfValid || piValid;
	}
	
	/** Nei primi giorni di gennaio, se il pagamento è dell'anno precedente, viene assegnata
	 * come data fattura il 31 dicembre dell'anno precedente.
	 * @param dataPagamento
	 * @param dataAccredito
	 * @return
	 */
	private static Date pickDataFattura(Date dataPagamento) {
		Date dataFattura = DateUtil.now();
		Calendar calAcc = new GregorianCalendar();
		calAcc.setTime(dataFattura);
		int monthAcc = calAcc.get(Calendar.MONTH);
		int dayAcc = calAcc.get(Calendar.DAY_OF_MONTH);
		if ((monthAcc == 0) &&
				(dayAcc <= AppConstants.FATTURE_NEW_YEAR_DELAY_DAYS)) {
			//Accredito è nei primi X giorni di gennaio
			Calendar calPag = new GregorianCalendar();
			calPag.setTime(dataPagamento);
			int yearPag = calPag.get(Calendar.YEAR);
			int yearAcc = calAcc.get(Calendar.YEAR);
			if (yearPag < yearAcc) {
				//il pagamento è dell'anno precedente
				//=> dataFattura = 31 dicembre ore 12:00
				Calendar calFat = new GregorianCalendar(yearPag,11,31,12,00);
				dataFattura = calFat.getTime();
			}
		}
		return dataFattura;
	}
	
	
	
	
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
	
	private static Fatture createEmptyFatturaConNumero(Session ses,
			Anagrafiche pagante, String idSocieta, Date dataFattura, boolean isFittizia,
			String idUtente) 
			throws HibernateException, BusinessException {
		FattureDao fattureDao = new FattureDao();
		Fatture fattura = new Fatture();
		fattura.setDataCreazione(DateUtil.now());
		fattura.setDataFattura(dataFattura);
		fattura.setDataEmail(null);
		fattura.setIdAnagrafica(pagante.getId());
		fattura.setIdIstanzaAbbonamento(null);
		fattura.setIdPeriodico(null);
		fattura.setIdSocieta(idSocieta);
		fattura.setIdTipoDocumento(AppConstants.DOCUMENTO_FATTURA);
		fattura.setPubblica(true);
		fattura.setFittizia(isFittizia);
		
		IndirizziUtil.denormalizeFromAnagraficaToFattura(pagante, fattura);
		
		boolean isSocieta = false;
		if (pagante.getPartitaIva() != null) {
			if (pagante.getPartitaIva().length() > 1) isSocieta = true;
		}
		String tipoIva = ValueUtil.getTipoIva(fattura.getNazione(), isSocieta);
		fattura.setTipoIva(tipoIva);
		//Double totaleFinale = sumTotaleFinale(ia, oiaList, ivaScorporata);
		fattura.setTotaleFinale(-1D);
		//Double totaleImponibile = sumTotaleImponibile(ia, oiaList);
		fattura.setTotaleImponibile(-1D);
		//Double totaleIva = sumTotaleIva(ia, oiaList, ivaScorporata);
		fattura.setTotaleIva(-1D);
		//NUMERO FATTURA
		Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
		String prefisso = pickFatturaPrefix(societa, fattura.getNazione().getId(), fattura.getCodiceFiscale(),
				fattura.getPartitaIva(), isFittizia);
		boolean pubblica = !prefisso.equals(AppConstants.FATTURE_PREFISSO_FITTIZIO);
		fattura.setPubblica(pubblica);
		boolean numFatVerified = false;
		String numeroFattura = null;
		int counter = 0;
		do {
			//INCREMENTO
			Integer numero = new ContatoriDao().nextTempNumFattura(ses, prefisso, dataFattura);
			numeroFattura = FattureBusiness
					.buildNumeroFattura(prefisso, dataFattura, numero);
			//VERIFICA UNICITA'
			List<Fatture> anomalie = fattureDao.findByNumeroFattura(ses, numeroFattura);
			if (anomalie.size() == 0) numFatVerified = true;
			counter++;
			if (counter >= MAX_FATTURE_ERROR_COUNT) 
				throw new BusinessException("NumeroFattura could not be created after "+MAX_FATTURE_ERROR_COUNT+" attempts");
		} while (!numFatVerified);
		//SALVATAGGIO
		fattura.setNumeroFattura(numeroFattura);
		fattura.setIdUtente(idUtente);
		fattureDao.save(ses, fattura);
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
		if (IndirizziUtil.isFilledUp(pagante.getIndirizzoFatturazione()))
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
			result.setImportoIvaUnit(result.getImportoTotUnit()-result.getImportoImpUnit());
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
			result.setImportoIvaUnit(result.getImportoTotUnit()-result.getImportoImpUnit());
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
		result.setImportoImpUnit(ValueUtil.roundToCents(resto));
		result.setImportoTotUnit(ValueUtil.roundToCents(resto));
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
				ia.setDataSaldo(DateUtil.now());
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
		
		//Remove mandatory options from idOpzList
		List<Integer> cleanIdOpzList = new ArrayList<Integer>();
		for (Integer idOpz:idOpzList) {
			boolean obbligatoria = false;
			for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
				if (ol.getOpzione().getId() == idOpz) obbligatoria = true;
			}
			if (!obbligatoria) cleanIdOpzList.add(idOpz);
		}
		
		//Totale calcolato
		Double dovuto = PagamentiMatchBusiness.getMissingAmount(ses, ia.getId(), cleanIdOpzList);
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
		if (cleanIdOpzList != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				boolean create = false;
				if (oia.getIdFattura() == null) {
					create = true;
				} else {
					if (oia.getIdFattura().equals(fatt.getId())) create = true;
				}
				if (create) {
					boolean obbligatoria = false;
					for (OpzioniListini ol:ia.getListino().getOpzioniListiniSet()) {
						if (ol.getOpzione().getId() == oia.getOpzione().getId()) obbligatoria = true;
					}
					if (!obbligatoria) {
						FattureArticoli fatOia = FattureBusiness
								.createFatturaArticoloFromOpzione(fatt.getId(), oia, ivaScorporata, riduzione);
						faDao.save(ses, fatOia);
						faList.add(fatOia);
					}
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
						AppConstants.DEFAULT_ALIQUOTA_IVA, DateUtil.now());
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
		if (fatt.getIdIstanzaAbbonamento() != null) {
			OpzioniIstanzeAbbonamentiDao oiaDao = new OpzioniIstanzeAbbonamentiDao();
			IstanzeAbbonamenti ia = GenericDao
					.findById(ses, IstanzeAbbonamenti.class, fatt.getIdIstanzaAbbonamento());
			if (ia.getIdFattura() != null) {
				if (ia.getIdFattura().equals(fatt.getId())) {
					ia.setIdFattura(null);
					ia.setPagato(false);
				}
			}
			if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
				for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
					if (oia.getIdFattura() != null) {
						if (oia.getIdFattura().equals(fatt.getId())) {
							oia.setIdFattura(null);
							oiaDao.update(ses, oia);
							ia.setPagato(false);
						}
					}
				}
			}
			new IstanzeAbbonamentiDao().update(ses, ia);
		}
	}
	
	public static Fatture createRimborso(Session ses, Integer idFattura, boolean isRimborsoTotale,
			boolean isStornoTotale, boolean isRimborsoResto, boolean isStornoResto, 
			String idUtente)
			throws HibernateException, BusinessException {
		if (!isRimborsoTotale && !isStornoTotale && !isRimborsoResto && !isStornoResto) throw new BusinessException("Please define an action");
		Fatture ndc = null;
		Date now = DateUtil.now();
		
		Fatture fattura = GenericDao.findById(ses, Fatture.class, idFattura);
		if (fattura == null) throw new BusinessException("Incorrect idFattura");
		if (fattura.getIdNotaCreditoRimborso() != null || 
				fattura.getIdNotaCreditoStorno() != null) throw new BusinessException("Refund has already been made");
		if ((isRimborsoResto || isStornoResto) && 
				fattura.getIdNotaCreditoRimborsoResto() != null || fattura.getIdNotaCreditoStornoResto() != null) 
			throw new BusinessException("Refund has already been made");
		List<FattureArticoli> faList = new FattureArticoliDao().findByFattura(ses, fattura.getId());
		//Choose prefix
		Anagrafiche anag = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
		Indirizzi indirizzo = anag.getIndirizzoPrincipale();
		Societa societa = GenericDao.findById(ses, Societa.class, fattura.getIdSocieta());
		String prefisso = null;
		if (fattura.getNazione() != null) {
			prefisso = pickFatturaPrefix(societa, fattura.getNazione().getId(), fattura.getCodiceFiscale(),
					fattura.getPartitaIva(), fattura.getNumeroFattura());
		} else {
			prefisso = pickFatturaPrefix(societa, indirizzo.getNazione().getId(), anag.getCodiceFiscale(),
					anag.getPartitaIva(), fattura.getNumeroFattura());
		}
		boolean isFittizia = prefisso.equals(AppConstants.FATTURE_PREFISSO_FITTIZIO);
		boolean isPubblica = !isFittizia;
		//Initing fatture counter
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
			ndc.setIdIstanzaAbbonamento(fattura.getIdIstanzaAbbonamento());
			ndc.setIdPeriodico(fattura.getIdPeriodico());
			ndc.setIdSocieta(fattura.getIdSocieta());
			ndc.setIdTipoDocumento(AppConstants.DOCUMENTO_NOTA_CREDITO);
			ndc.setTipoIva(fattura.getTipoIva());
			ndc.setTotaleFinale(0D);
			ndc.setTotaleImponibile(0D);
			ndc.setTotaleIva(0D);
			ndc.setPubblica(isPubblica);
			ndc.setFittizia(isFittizia);
			
			IndirizziUtil.denormalizeFromAnagraficaToFattura(anag, ndc);
			
			//Numero rimborso (=numero fattura)
			Integer numero = new ContatoriDao().nextTempNumFattura(ses, prefisso, now);
			String numeroRimborso = FattureBusiness
					.buildNumeroFattura(prefisso, now, numero);
			ndc.setNumeroFattura(numeroRimborso);
			ndc.setIdUtente(idUtente);
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
						ra.setImportoImpUnit(ValueUtil.roundToCents(fa.getImportoImpUnit()));
						ra.setImportoIvaUnit(ValueUtil.roundToCents(fa.getImportoIvaUnit()));
						ra.setImportoTotUnit(ValueUtil.roundToCents(fa.getImportoTotUnit()));
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
						fattura, fattura.getTotaleFinale(), fattura.getIdSocieta(), 
						fattura.getIdAnagrafica(), true, idUtente);
				//Rimuove le opzioni legate a questa fattura
				FattureBusiness.unbindIstanzaOpzioni(ses, fattura);
			}
			if (isRimborsoResto) {
				FattureArticoli ra = new FattureArticoli();
				ra.setIdFattura(idNdc);
				AliquoteIva aliquotaIva = new AliquoteIvaDao()
						.findDefaultAliquotaIvaByDate(ses,
								AppConstants.DEFAULT_ALIQUOTA_IVA, DateUtil.now());
				ra.setAliquotaIva(aliquotaIva);
				ra.setDescrizione("Storno anticipo in fattura "+fattura.getNumeroFattura());
				ra.setImportoImpUnit(ValueUtil.roundToCents(fattura.getImportoResto()));
				ra.setImportoIvaUnit(0D);
				ra.setImportoTotUnit(ValueUtil.roundToCents(fattura.getImportoResto()));
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
				ra.setImportoImpUnit(ValueUtil.roundToCents(fattura.getImportoResto()));
				ra.setImportoIvaUnit(0D);
				ra.setImportoTotUnit(ValueUtil.roundToCents(fattura.getImportoResto()));
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
	
	public static Fatture createPagamentoFromFatturaRimborsata(Session ses, Integer idFattura, String idUtente)
			throws HibernateException, BusinessException {
		Fatture fattura = GenericDao.findById(ses, Fatture.class, idFattura);
		Fatture fatRimborso = FattureBusiness.createRimborso(ses, idFattura, true, false, false, false, idUtente);
		Date now = new Date();
		
		//Create a new payment from old fattura
		String codiceMatch = "";
		List<Pagamenti> oldPagList = new PagamentiDao().findPagamentiByIdFattura(ses, idFattura);
		for (Pagamenti oldPag:oldPagList) {
			if (oldPag.getCodiceAbbonamentoMatch() != null) codiceMatch = oldPag.getCodiceAbbonamentoMatch();
		}

		Pagamenti pag = new Pagamenti();
		Anagrafiche anagrafica = GenericDao.findById(ses, Anagrafiche.class, fattura.getIdAnagrafica());
		pag.setAnagrafica(anagrafica);
		pag.setCodiceAbbonamentoBollettino(null);
		pag.setCodiceAbbonamentoMatch(codiceMatch);
		pag.setDataAccredito(now);
		pag.setDataCreazione(now);
		pag.setDataModifica(now);
		pag.setDataPagamento(now);
		pag.setIdSocieta(fattura.getIdSocieta());
		pag.setIdTipoPagamento(AppConstants.PAGAMENTO_MANUALE);
		pag.setIdUtente(idUtente);
		pag.setImporto(fattura.getTotaleFinale());
		pag.setIstanzaAbbonamento(null);
		pag.setNote("Ri-creato da fattura "+fattura.getNumeroFattura());
		pag.setStringaBollettino("");
		pag.setTrn("");
		pag.setIdErrore(AppConstants.PAGAMENTO_ERR_NON_ABBINABILE);//So shows in error list
		new PagamentiDao().save(ses, pag);
		return fatRimborso;
	}

	public static String createNotaDocumento(Fatture fatt, String idTipoPagamento) {
		String notaDocumento = "";
		if (fatt.getIdTipoDocumento().equalsIgnoreCase(AppConstants.DOCUMENTO_FATTURA)) {
			notaDocumento = NOTA_FATTURA_PAGATA;
			if (idTipoPagamento != null) {
				if (idTipoPagamento.equals(AppConstants.PAGAMENTO_CARTA_DOCENTE)) {
					notaDocumento = NOTA_CARTA_DOCENTE;
				}
			}
		} else {
			notaDocumento = NOTA_RIMBORSO;
		}
		return notaDocumento;
	}
	
	
	public static String createNotaEstero(String partitaIva, Nazioni naz) {
		boolean hasIva = false;
		if (partitaIva != null) {
			if (partitaIva.length() > 0) hasIva = true;
		}
		if (naz.getId().equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA)) {
			return "";
		}
		if (naz.getUe()) {
			if (hasIva) {
				return "V.f.c.IVA art.7 ter (D) - Subject to reverse charge art. 196 Dir. 2006/112/EC";
			} else {
				return "";
			}
		}
		//ELSE (extra UE)
		return "V.f.c.IVA art.7 ter (F)";
	}
	
	public static String pickFatturaPrefix(Societa societa, String idNazione, String codFisc, String partIva, 
			Boolean isFatturaDifferita) {
		return pickFatturaPrefix(societa, idNazione, codFisc, partIva, null, isFatturaDifferita);
	}
	public static String pickFatturaPrefix(Societa societa, String idNazione, String codFisc, String partIva, 
			String parentNumeroFattura) {
		return pickFatturaPrefix(societa, idNazione, codFisc, partIva, parentNumeroFattura, null);
	}
	private static String pickFatturaPrefix(Societa societa, String idNazione, String codFisc, String partIva, 
			String parentNumeroFattura, Boolean isFatturaDifferita) {
		String prefisso = null;
		//Controllo nazione
		if (idNazione.equals(AppConstants.DEFAULT_ID_NAZIONE_ITALIA))
				prefisso = societa.getPrefissoFatture();
		//Dati fiscali validi
		Boolean hasValidData = hasValidInvoiceData(codFisc, partIva, idNazione);
		if (!hasValidData) prefisso = AppConstants.FATTURE_PREFISSO_FITTIZIO;
		//Controllo su fattura da cui deriva (se questa è derivata)
		if (parentNumeroFattura != null) {
			if (parentNumeroFattura.startsWith(AppConstants.FATTURE_PREFISSO_FITTIZIO))
				prefisso = AppConstants.FATTURE_PREFISSO_FITTIZIO;
		}
		//Fattura differita
		if (isFatturaDifferita != null) {
			if (isFatturaDifferita) prefisso = AppConstants.FATTURE_PREFISSO_FITTIZIO;
		}
		//Se non definito => fittizio
		if (prefisso == null) prefisso = AppConstants.FATTURE_PREFISSO_FITTIZIO;
		return prefisso;
	}
}
