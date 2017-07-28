package it.giunti.apg.core.business;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.PagamentiDao;
import it.giunti.apg.core.persistence.TipiAbbonamentoRinnovoDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.IstanzeStatusUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.TipiAbbonamento;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class ImportiBusiness {
	
	//private static final Logger LOG = LoggerFactory.getLogger(ImportiBusiness.class);
	
	private Map<Integer, Listini> lstMap = new HashMap<Integer, Listini>();
	
	private ListiniDao listiniDao = new ListiniDao();
	private static EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
	
	public static void persistImportiAndCausali(Session ses, List<EvasioniComunicazioni> ecList) 
			throws HibernateException, BusinessException {
		ImportiBusiness ib = new ImportiBusiness();
		for (EvasioniComunicazioni ec:ecList) {
			//Riempie tutte le causali, ma solo se sono bollettini!
			if (ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_BOLLETTINO) /*||
					ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD)*/) {
				Date dataListino = null;
				if (ec.getRichiestaRinnovo()) {
					Calendar cal = new GregorianCalendar();
					cal.setTime(ec.getIstanzaAbbonamento().getFascicoloFine().getDataFine());
					cal.add(Calendar.DAY_OF_MONTH, 2);
					dataListino = cal.getTime();
				} else {
					dataListino = ec.getIstanzaAbbonamento().getFascicoloInizio().getDataInizio();
				}
				ib.fillImportiCausaliBollettino(ses, ec, dataListino);
				if (ec.getId() == null) {
					ecDao.save(ses, ec);
				} else {
					ecDao.update(ses, ec);
				}
			}
		}
	}
	
	private void fillImportiCausaliBollettino(Session ses, EvasioniComunicazioni ec, Date dataListino) throws HibernateException,
			BusinessException {
		PagamentiDao pagamentiDao = new PagamentiDao();
		Double credito = 0D;
		Double dovuto = 0D;
		Double dovutoAlt = 0D;
		String causale = null;
		String causaleAlt = null;
		/*boolean isNdd = ec.getIdTipoMedia().equals(AppConstants.COMUN_MEDIA_NDD);*/
		boolean isPagato = ec.getIstanzaAbbonamento().getPagato();
		boolean isDisdettato = (ec.getIstanzaAbbonamento().getDataDisdetta() != null);
		boolean isScolastico = (ec.getIstanzaAbbonamento().getAbbonamento().getPeriodico().getIdTipoPeriodico().equals(AppConstants.PERIODICO_SCOLASTICO));
		boolean isBolManuale = (ec.getComunicazione() == null);
		TipiAbbonamento tipoRinnovo = new TipiAbbonamentoRinnovoDao()
				.findFirstTipoRinnovoByIdListino(ses, ec.getIstanzaAbbonamento().getListino().getId());
		if (tipoRinnovo == null) throw new BusinessException(
				ec.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento()+
				" non trovato primo tipo al rinnovo per "+
				ec.getIstanzaAbbonamento().getListino().getTipoAbbonamento().getCodice()+
				" "+ec.getIstanzaAbbonamento().getListino().getTipoAbbonamento().getNome());
		TipiAbbonamento tipoRinnovoAlt = new TipiAbbonamentoRinnovoDao()
				.findSecondTipoRinnovoByIdListino(ses, ec.getIstanzaAbbonamento().getListino().getId());
		if (ec.getRichiestaRinnovo() && !isDisdettato) {
			//Rinnovo - anche se è un bollettino (manuale) relativo a un abbonamento già pagato
			Listini lst = getListinoByTipiAbbonamentoDate(ses, tipoRinnovo.getId(), dataListino);
			if (lst == null) throw new BusinessException(
					ec.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento()+
					" non trovato listino per "+tipoRinnovo.getCodice()+
					" "+tipoRinnovo.getNome()+
					" in data "+ServerConstants.FORMAT_DAY.format(dataListino));
			dovuto = pagamentiDao.getStimaImportoTotale(ses, lst.getId(), 
					ec.getIstanzaAbbonamento().getCopie(), null);
			causale = causaleFromListino(lst);
		} else {
			//saldo
			Anagrafiche pagante = ec.getIstanzaAbbonamento().getPagante();
			if (pagante == null) pagante = ec.getIstanzaAbbonamento().getAbbonato();
			credito = new PagamentiCreditiDao().getCreditoByAnagraficaSocieta(ses, pagante.getId(), 
					ec.getIstanzaAbbonamento().getAbbonamento().getPeriodico().getIdSocieta(),null,false);
			dovuto = PagamentiMatchBusiness.getMissingAmount(ses, ec.getIstanzaAbbonamento().getId());
			causale = causaleFromListino(ec.getIstanzaAbbonamento().getListino());
		}
		//Alternativa
		if (!isBolManuale) {
			if (ec.getComunicazione().getMostraPrezzoAlternativo() && (tipoRinnovoAlt != null)) {
				Listini lstAlt = getListinoByTipiAbbonamentoDate(ses, tipoRinnovoAlt.getId(), dataListino);
				dovutoAlt = pagamentiDao.getStimaImportoTotale(ses, lstAlt.getId(), 
						ec.getIstanzaAbbonamento().getCopie(), null);
				causaleAlt = causaleFromListino(lstAlt);
			}
		}
		//Considerazioni sulle soglie
		boolean isGratis = (dovuto < AppConstants.SOGLIA);
		if (Math.abs(dovuto-dovutoAlt) < AppConstants.SOGLIA) dovutoAlt = 0D;//se sono uguali => non c'è listino Alt
		boolean isFatturato = IstanzeStatusUtil.isFatturato(ec.getIstanzaAbbonamento());
		double debito = dovuto-credito;
		boolean sottoSoglia = (debito <= AppConstants.SOGLIA);
		//Importi forzati a vuoto
		if (!isBolManuale) {
			if (ec.getComunicazione().getBollettinoSenzaImporto()) {
				dovuto = 0D;
				dovutoAlt = 0D;
			}
		}
		//Alla fine prepara gli importi
		ec.setImportoStampato(null);
		ec.setImportoAlternativoStampato(null);
		
		//E' eliminato?
		String codice = ec.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento();
		if (ec.getIstanzaAbbonamento().getInvioBloccato() || isGratis ||
				(isBolManuale && isPagato && isScolastico) ||
				(isBolManuale && isPagato && isDisdettato)) {
			ec.setEliminato(true);
			if (ec.getIstanzaAbbonamento().getInvioBloccato())
					ec.setNote(codice+" e' bloccato");
			if (isGratis)
					ec.setNote(codice+" e' gratuito");
			if (isPagato)
				ec.setNote(codice+" e' gia' pagato");
		} else {
			//Condizioni di stampa
			if (isFatturato || sottoSoglia){
				//Quando non deve essere versato nulla
				/*if (isNdd) {
					ec.setImportoStampato(listino);
					ec.setCausaleT(causale);
					ec.setEstrattoComeAnnullato(true);
				} else {*/
					ec.setEliminato(true);
					String nota = "";
					if (sottoSoglia) nota = codice + 
							" credito: "+ServerConstants.FORMAT_CURRENCY.format(credito)+
							" dovuto: "+ServerConstants.FORMAT_CURRENCY.format(dovuto)+
							" ("+ec.getIstanzaAbbonamento().getCopie()+" copie) ";
					if (Math.abs(debito) < AppConstants.SOGLIA) nota = codice + " e' stato pagato";
					ec.setNote(nota);
				/*}*/
				if (isFatturato) ec.setNote(codice + " emessa fattura a pagamento differito");
			} else {
				//Deve essere versato qualcosa!
				double saldo = dovuto-credito;
				double saldoAlt = dovutoAlt-credito;
				if (dovuto > AppConstants.SOGLIA) {
					ec.setImportoStampato(saldo);
					ec.setCausaleT(causale);
				}
				if (dovutoAlt > AppConstants.SOGLIA) {
					ec.setImportoAlternativoStampato(saldoAlt);
					ec.setCausaleAlternativaT(causaleAlt);
				}
			}
		}
	}
		
	private Listini getListinoByTipiAbbonamentoDate(Session ses, Integer idTipoAbb, Date dt) 
			throws HibernateException {
		Listini lst = lstMap.get(idTipoAbb);
		if (lst == null) {
			lst = listiniDao.findListinoByTipoAbbDate(ses, idTipoAbb, dt);
			lstMap.put(idTipoAbb, lst);
		}
		return lst;
	}
	
	private static String causaleFromListino(Listini lst) {
		String descr = "";
		if (lst != null) {
			int numLst = lst.getNumFascicoli();
			int numAnnuali = lst.getTipoAbbonamento().getPeriodico().getNumeriAnnuali();
			descr += "Saldo quota abbonamento ";
			if (numLst == numAnnuali) descr += "annuale ";
			if (numLst == (2*numAnnuali)) descr += "biennale ";
			descr += "rivista '" + lst.getTipoAbbonamento().getPeriodico().getNome()+"'";
		}
		return descr;
	}
}
