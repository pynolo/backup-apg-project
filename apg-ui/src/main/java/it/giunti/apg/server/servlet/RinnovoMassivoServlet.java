package it.giunti.apg.server.servlet;

import it.giunti.apg.core.OpzioniUtil;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.VisualLogger;
import it.giunti.apg.core.business.FtpUtil;
import it.giunti.apg.core.business.PagamentiMatchBusiness;
import it.giunti.apg.core.persistence.EvasioniArticoliDao;
import it.giunti.apg.core.persistence.EvasioniFascicoliDao;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.IstanzeAbbonamentiDao;
import it.giunti.apg.core.persistence.ListiniDao;
import it.giunti.apg.core.persistence.PagamentiCreditiDao;
import it.giunti.apg.core.persistence.RinnoviMassiviDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.EvasioniFascicoli;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.PagamentiCrediti;
import it.giunti.apg.shared.model.Periodici;
import it.giunti.apg.shared.model.RinnoviMassivi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RinnovoMassivoServlet extends HttpServlet {
	private static final long serialVersionUID = -2696976322018161787L;

	private static final Logger LOG = LoggerFactory.getLogger(RinnovoMassivoServlet.class);
	
	private static final SimpleDateFormat EXPORT_SDF = new SimpleDateFormat("dd/MM/yyyy");
	private static final int RENEWAL_PAGESIZE = 50;
	private static final int LOG_INTERVAL = 500;
	
	private IstanzeAbbonamentiDao iaDao = new IstanzeAbbonamentiDao();
	private ListiniDao lstDao = new ListiniDao();
	private PagamentiCreditiDao credDao = new PagamentiCreditiDao();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer idRapporto = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_RAPPORTO));
		Integer idPeriodico = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_PERIODICO));
		if (idPeriodico == null) throw new ServletException("No "+AppConstants.PARAM_ID_PERIODICO+" has been provided");
		//String idUtente = req.getParameter(AppConstants.PARAM_ID_UTENTE);
		String idUtente = ServerConstants.DEFAULT_SYSTEM_USER;
		FileWriter fw = null;
		Date today = DateUtil.now();
		VisualLogger.get().addHtmlInfoLine(idRapporto, "Rinnovo massivo in corso");
		try {
			String fileName = ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(today)+
					" Rinnovo massivo.csv";
			File f = File.createTempFile("Rinnovo massivo ", ".csv");
			f.deleteOnExit();
			fw = new FileWriter(f);
			renewAbbonamenti(idPeriodico, today, fw, idUtente, idRapporto);
			fw.close();
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Trasferimento FTP del file "+fileName);
			String ftpHost = new FtpUtil("GS").fileTransfer(f, null, fileName);
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Il file "+fileName+" e' stato trasferito su "+ftpHost);
		} catch (IOException|BusinessException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new ServletException(e);
		}
	}
	
	public void renewAbbonamenti(Integer idPeriodico, Date renewalDate, Writer csvWriter, String idUtente, int idRapporto) {
		Session ses = SessionFactory.getSession();
		int count = 0;
		try {
			Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
			String title = "Rinnovo massivo di <b>"+p.getNome()+"</b>";
			VisualLogger.get().setLogTitle(idRapporto, title);
			csvWriter.write("codice;ragsoc;tipo1;inizio1;tipo2;inizio2;pagamento\r\n");//File header
			List<RinnoviMassivi> rmList = new RinnoviMassiviDao().findByPeriodico(ses, idPeriodico);
			//Ciclo su tutte le regole per il rinnovo
			for (RinnoviMassivi rm:rmList) {
				if (rm.getRegolaAttiva()) {
					Integer idTa = rm.getIdTipoAbbonamento();
					//Ricerca tutti i listini per il tipo indicato
					List<Listini> lstList = lstDao.findListiniByTipoAbb(ses,
							idTa, 0, Integer.MAX_VALUE);
					//Ciclo su tutti i listini per il tipo in esame
					for (Listini lst:lstList) {
						String descr = "Fase "+(count+1)+" <b>"+lst.getTipoAbbonamento().getPeriodico().getNome()+"</b>"+
								": tipi '<b>"+lst.getTipoAbbonamento().getCodice()+"</b> "+
								lst.getTipoAbbonamento().getNome()+"'";
						VisualLogger.get().addHtmlInfoLine(idRapporto, descr);
						renewIstanzeByListino(ses, lst, rm.getIdTipoAbbonamentoRinnovo(),
								rm.getIdFascicoloInizio(), rm.getSoloRegolari(), renewalDate, idUtente, csvWriter, idRapporto);
					}
				}
				count++;
			}
			LOG.warn("Rinnovo massivo terminato");
		} catch (Exception e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	
	private List<IstanzeAbbonamenti> renewIstanzeByListino(Session ses, Listini fromLst, Integer idTipoRinnovo,
					Integer idFascicoloInizio, boolean soloRegolari, Date renewalDate,
					String idUtente, Writer csvWriter, int idRapporto) 
			throws BusinessException, IOException {
		EvasioniFascicoliDao efDao = new EvasioniFascicoliDao();
		EvasioniArticoliDao edDao = new EvasioniArticoliDao();
		ListiniDao lDao = new ListiniDao();
		List<IstanzeAbbonamenti> iaList = new ArrayList<IstanzeAbbonamenti>();
		int lstCount = 0;
		Transaction trn = null;
		long total = RinnovoMassivoUtil.countIstanzeByFascicoloListinoPagato(ses, fromLst, idFascicoloInizio, soloRegolari);
		if (total > 0) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Totale istanze da rinnovare: "+total);
			try {
				String csvString = "";
				do {
					trn = ses.beginTransaction();
					iaList = RinnovoMassivoUtil.findIstanzeByFascicoloListinoPagato(ses,
							fromLst, idFascicoloInizio, soloRegolari, RENEWAL_PAGESIZE);
					for (IstanzeAbbonamenti ia:iaList) {
						//Definisce il listino destinazione
						Calendar cal = new GregorianCalendar();
						cal.setTime(ia.getFascicoloFine().getDataFine());
						cal.add(Calendar.DAY_OF_MONTH, 2);
						Date dtRinnovo = cal.getTime();
						Listini lstRinnovo = lDao.findListinoByTipoAbbDate(ses, idTipoRinnovo, dtRinnovo);
						//Il rinnovo include le opzioni obbligatorie
						IstanzeAbbonamenti transIa = RinnovoMassivoUtil.renewToTransientNoOpzioni(ses,
								ia, lstRinnovo, renewalDate, idUtente);//associa opzioni transient
						Integer idNewIa = (Integer)iaDao.save(ses, transIa);
						IstanzeAbbonamenti newIa = GenericDao.findById(ses, IstanzeAbbonamenti.class, idNewIa);
						OpzioniUtil.addOpzioniObbligatorie(ses, newIa, false);
						if (newIa.getOpzioniIstanzeAbbonamentiSet() == null) {
							newIa.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
						}
						//if (transIa.getOpzioniIstanzeAbbonamentiSet() != null) {
						//	for (OpzioniIstanzeAbbonamenti oia1:transIa.getOpzioniIstanzeAbbonamentiSet()) {
						//		boolean contains = false;
						//		for (OpzioniIstanzeAbbonamenti oia3:newIa.getOpzioniIstanzeAbbonamentiSet()) {
						//			if (oia3.getOpzione().equals(oia1.getOpzione())) contains=true;
						//		}
						//		if (!contains) {
						//			OpzioniIstanzeAbbonamenti oia2 = new OpzioniIstanzeAbbonamenti();
						//			oia2.setIstanza(newIa);
						//			oia2.setOpzione(oia1.getOpzione());
						//			newIa.getOpzioniIstanzeAbbonamentiSet().add(oia2);
						//			GenericDao.saveGeneric(ses, oia2);
						//		}
						//	}
						//}
						verifyOpzioni(newIa, idRapporto);
						String note = "";
						//Ultima della serie
						iaDao.markUltimaDellaSerie(ses, newIa.getAbbonamento());
						//Abbina pagamenti / o marca omaggio come pagato
						if (newIa.getListino().getPrezzo() < AppConstants.SOGLIA) {
							if (!newIa.getInFatturazione()) {
								//Omaggio
								newIa.setPagato(true);
								note = "saldato come omaggio";
							}
						} else {
							//NON omaggio
							note = saldaConCredito(ses, newIa, idUtente);
						}
						//Se risulta pagato o in fatturazione imposta data saldo
						if (newIa.getPagato() || newIa.getInFatturazione()) {
							newIa.setDataSaldo(DateUtil.now());
							GenericDao.updateGeneric(ses, newIa.getId(), newIa);
						}
						//verifyOpzioni(newIa, idRapporto);
						//Accoda eventuali ARRETRATI se almeno il fascicolo iniziale è stato evaso:
						if (newIa.getFascicoloInizio().getDataEstrazione() != null) {
							List<EvasioniFascicoli> arretrati =
									efDao.enqueueMissingArretratiByStatus(ses, newIa, idUtente);
							if (arretrati != null) {
								if (arretrati.size()>0) note += "Arretrati: "+arretrati.size();
							}
						}
						//Riassegna eventuali i fascicoli di gracing
						efDao.reattachEvasioniFascicoliToIstanza(ses, newIa);
						//Assegna eventuali articoli
						edDao.reattachEvasioniArticoliToInstanza(ses, newIa, idUtente);
						//verifyOpzioni(transIa, idRapporto);
						//Report line
						String nome = newIa.getAbbonato().getIndirizzoPrincipale().getCognomeRagioneSociale();
						if (newIa.getAbbonato().getIndirizzoPrincipale().getNome() != null) {
							nome += " " + newIa.getAbbonato().getIndirizzoPrincipale().getNome();
						}
						csvString = newIa.getAbbonamento().getCodiceAbbonamento() + ";" +
								nome + ";" +
								"\"" + ia.getListino().getTipoAbbonamento().getCodice() + "\";" +//tipo1
								EXPORT_SDF.format(ia.getFascicoloInizio().getDataInizio()) + ";" + //scadenza1
								"\"" + newIa.getListino().getTipoAbbonamento().getCodice() + "\";" +//tipo2
								EXPORT_SDF.format(newIa.getFascicoloInizio().getDataInizio()) + ";" +//scadenza2
								note+"\r\n";
						csvWriter.write(csvString);
					}
					trn.commit();
					ses.flush();
					ses.clear();
					lstCount += iaList.size();
					if (lstCount%LOG_INTERVAL == 0) VisualLogger.get().addHtmlInfoLine(idRapporto, "Rinnovati: "+lstCount+"/"+total);
				} while (iaList.size() > 0);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Rinnovati: "+lstCount);
			} catch (Exception e) {
				trn.rollback();
				throw new BusinessException(e.getMessage(), e);
			}
		}
		return iaList;
	}
	
	private String saldaConCredito(Session ses, IstanzeAbbonamenti ia, String idUtente) 
			throws IOException, BusinessException {
		Date today = DateUtil.now();
		Integer idPagante = ia.getAbbonato().getId();
		if (ia.getPagante() != null) idPagante = ia.getPagante().getId();
		List<PagamentiCrediti> pcList = credDao.findByAnagraficaSocieta(ses, idPagante,
				ia.getFascicoloInizio().getPeriodico().getIdSocieta(), true, false);
		if (pcList != null) {
			if (pcList.size() > 0) {
				//Calcola il pagato e il dovuto
				double pagato = PagamentiMatchBusiness.getTotalAmount(null, pcList);
				double dovuto = PagamentiMatchBusiness.getIstanzaTotalPrice(ses, ia.getId());
				//Se pagato == dovuto allora abbina e salda
				if (Math.abs(dovuto-pagato) < AppConstants.SOGLIA) {
					List<Integer> idCredList = new ArrayList<Integer>();
					for (PagamentiCrediti cred:pcList) idCredList.add(cred.getId());
					Fatture fatt = PagamentiMatchBusiness.processPayment(ses, today, today,
							null, idCredList, ia.getId(), null, idUtente);
					return "\"saldato con fattura "+fatt.getNumeroFattura()+"\"";
				} else {
					//Se pagato != dovuto allora mette il pagamento nelle correzioni
					return "\"credito non compatibile "+
							ServerConstants.FORMAT_CURRENCY.format(pagato)+"\"";
				}
			}
		}
		return "";
	}
	
	//private String updateAndSetupSaldo(Session ses, List<Pagamenti> pList, IstanzeAbbonamenti ia,
	//		boolean pagato, Utenti user) throws IOException {
	//	Date today = DateUtil.now();
	//	String importiString = "";
	//	//Associa pagamento
	//	for (Pagamenti pag:pList) {
	//		pag.setIdErrore(null);
	//		pag.setIstanzaAbbonamento(ia);
	//		pag.setDataModifica(today);
	//		pag.setUtente(user);
	//		pagDao.update(ses, pag);
	//		importiString += "EUR"+DF.format(pag.getImporto()-pag.getImportoResto());
	//		if (pag.getImportoResto() > 0d) importiString += " (in origine "+DF.format(pag.getImporto())+") ";
	//	}
	//	//Marca l'istanza pagata
	//	ia.setPagato(pagato);
	//	if (pagato && (ia.getDataSaldo() == null)) {
	//		ia.setDataSaldo(today);
	//	}
	//	ia.setDataModifica(today);
	//	ia.setUtente(user);
	//	iaDao.update(ses, ia);/*LOGGED*/
	//	return importiString;
	//}
	
	//private String markPaymentsAsError(Session ses, IstanzeAbbonamenti ia, List<Pagamenti> pList, Utenti user) throws IOException {
	//	Date today = DateUtil.now();
	//	String importiString = "";
	//	for (Pagamenti pag:pList) {
	//		pag.setIdErrore(AppConstants.PAGAMENTO_ERR_IMPORTO);
	//		pag.setIstanzaAbbonamento(null);
	//		if (pag.getCodiceAbbonamentoMatch() == null) pag.setCodiceAbbonamentoMatch(ia.getAbbonamento().getCodiceAbbonamento());
	//		if (pag.getCodiceAbbonamentoMatch().length() == 0) pag.setCodiceAbbonamentoMatch(ia.getAbbonamento().getCodiceAbbonamento());
	//		pag.setDataModifica(today);
	//		pag.setUtente(user);
	//		pDao.update(ses, pag);
	//		
	//		importiString += "EUR"+DF.format(pag.getImporto()-pag.getImportoResto());
	//		if (pag.getImportoResto() > 0d) importiString += " (in origine "+DF.format(pag.getImporto())+") ";
	//	}
	//	return importiString;
	//}
	
	private void verifyOpzioni(IstanzeAbbonamenti ia, int idRapporto) {
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			Date today = DateUtil.now();
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getOpzione().getDataFine().before(today)) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Anomalia: "+
							ia.getAbbonamento().getCodiceAbbonamento()+" ha opz. ["+
							oia.getOpzione().getUid()+"] "+oia.getOpzione().getNome());
				}
			}
		}
	}
}
