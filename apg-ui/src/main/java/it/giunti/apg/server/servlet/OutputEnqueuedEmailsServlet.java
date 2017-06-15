package it.giunti.apg.server.servlet;

import it.giunti.apg.server.Mailer;
import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.server.VisualLogger;
import it.giunti.apg.server.business.EmailBusiness;
import it.giunti.apg.server.persistence.EvasioniComunicazioniDao;
import it.giunti.apg.server.persistence.GenericDao;
import it.giunti.apg.server.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.EmailConstants;
import it.giunti.apg.shared.ValueUtil;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Comunicazioni;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.ModelliEmail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.mail.EmailException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutputEnqueuedEmailsServlet extends HttpServlet {
	private static final long serialVersionUID = -4890415376184450847L;
	
	private static final Logger LOG = LoggerFactory.getLogger(OutputEnqueuedEmailsServlet.class);
	
	private static final int PAGE_SIZE = 250;
	
	public OutputEnqueuedEmailsServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Date now = new Date();
		Integer idRapporto = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_RAPPORTO));
		String idTipoMedia = req.getParameter(AppConstants.PARAM_ID_TIPO_MEDIA);
		Integer idFas = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_FASCICOLO));
		Integer idCom = ValueUtil.stoi(req.getParameter(AppConstants.PARAM_ID_COMUNICAZIONE));
		String idUtente = req.getParameter(AppConstants.PARAM_ID_UTENTE);
		String testParam = req.getParameter(AppConstants.PARAM_TEST);
		boolean test = true;
		if (testParam != null) {
			if (testParam.equals("false")) test = false;
		}
		if (test) {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Modalit&agrave; TEST: le email non verranno effettivamente spedite");
		} else {
			VisualLogger.get().addHtmlInfoLine(idRapporto, "Preparazione in corso");
		}
		if ((idUtente != null) && (idTipoMedia != null) && (idRapporto != null) ) {
			if (idUtente.length()>0) {
				if (idFas != null) {
					prepareResponseByFascicolo(resp, now,
							idFas, idTipoMedia, test, idRapporto, idUtente);
				} else {
					if (idCom != null) {
						prepareResponseByComunicazione(resp, now,
								idCom, test, idRapporto, idUtente);
					}
				}
			}
		}
		try {
			VisualLogger.get().closeAndSaveRapporto(idRapporto);
		} catch (BusinessException e) {
			throw new ServletException(e);
		}
	}
	
	private void prepareResponseByFascicolo(HttpServletResponse resp, Date now,
			Integer idFascicolo, String idTipoMedia, boolean test, int idRapporto,
			String idUtente) 
					throws ServletException {
		Session ses = SessionFactory.getSession();
		Fascicoli fas;
		try {
			fas = GenericDao.findById(ses, Fascicoli.class, idFascicolo);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} finally {
			ses.close();
		}
		// SPEDIZIONE
		String avviso = "Spedizione email per il fascicolo "+
				fas.getTitoloNumero()+" di "+fas.getPeriodico().getNome();
		if (test) avviso = "TEST "+avviso;
		try {
			//Cerca tutte le comunicazioni per i nuovi attivati e genera gli EvasioniComunicazioni
			VisualLogger.get().addHtmlInfoLine(idRapporto, avviso);
			
			List<EvasioniComunicazioni> ecList = findEnqueuedComunicazioniByFascicolo(
						idFascicolo, idTipoMedia, idRapporto);
			
			//Creazione report
			if (ecList.size() == 0) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna email da spedire");
			} else {
				//Spedizione e scrittura su DB
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Tentativo di spedizione di "+ecList.size()+" email, con scrittura su DB");
				int successCount = 0;
				String descrizioneInvio = fas.getTitoloNumero()+
						" "+fas.getPeriodico().getNome();
				successCount = sendEmailAndSave(ecList, descrizioneInvio, test, idRapporto);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Spedizione e scrittura su DB terminata: "+
						successCount+" email inviate su "+ecList.size()+" previste</b>");
			}

			//Avviso
			//if (avviso.length() > 0) {
			//	AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			//}
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} catch (NumberFormatException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} finally {
			VisualLogger.get().setLogTitle(idRapporto, avviso);
		}
	}
	
	private void prepareResponseByComunicazione(HttpServletResponse resp, Date now,
			Integer idCom, boolean test, int idRapporto,
			String idUtente) 
					throws ServletException {
		Session ses = SessionFactory.getSession();
		Comunicazioni com;
		try {
			com = GenericDao.findById(ses, Comunicazioni.class, idCom);
		} catch (HibernateException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} finally {
			ses.close();
		}
		// SPEDIZIONE
		String avviso = "Spedizione email '"+
				com.getTitolo()+"' per "+com.getPeriodico().getNome();
		try {
			//Cerca tutte le comunicazioni per i nuovi attivati e genera gli EvasioniComunicazioni
			VisualLogger.get().addHtmlInfoLine(idRapporto, avviso);
			
			List<EvasioniComunicazioni> ecList = findEnqueuedComunicazioniByComunicazione(
						idCom, idRapporto);
			
			//Creazione report
			if (ecList.size() == 0) {
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Nessuna email da spedire");
			} else {
				//Spedizione e scrittura su DB
				VisualLogger.get().addHtmlInfoLine(idRapporto, "Tentativo di spedizione di "+ecList.size()+" email, con scrittura su DB");
				int successCount = 0;
				String descrizioneInvio = com.getTitolo()+" "+com.getPeriodico().getNome();
				successCount = sendEmailAndSave(ecList, descrizioneInvio, test, idRapporto);
				VisualLogger.get().addHtmlInfoLine(idRapporto, "<b>Spedizione e scrittura su DB terminata: "+
						successCount+" email inviate su "+ecList.size()+" previste</b>");
			}

			//Avviso
			//if (avviso.length() > 0) {
			//	AvvisiBusiness.writeAvviso(avviso, false, ServerConstants.DEFAULT_SYSTEM_USER);
			//}
		} catch (BusinessException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} catch (NumberFormatException e) {
			VisualLogger.get().addHtmlErrorLine(idRapporto, "ERROR: "+e.getMessage(), e);
			throw new ServletException(e);
		} finally {
			VisualLogger.get().setLogTitle(idRapporto, avviso);
		}
	}
	
	private int sendEmailAndSave(List<EvasioniComunicazioni> ecList,
			String descrizioneInvio, boolean test, int idRapporto) throws BusinessException {
		int count = 0;
		int successCount = 0;
		String emailReport = "";
		//Hibernate Session
		Session ses = SessionFactory.getSession();
		Transaction trn = null;
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		if (test) {
			ecList = replaceEvasioniListWithTest(ecList);
		}
		try {
			for (EvasioniComunicazioni ec:ecList) {
				trn = ses.beginTransaction();
				Anagrafiche destinatario = ec.getIstanzaAbbonamento().getAbbonato();
				if (ec.getComunicazione().getIdTipoDestinatario().equals(AppConstants.DEST_PAGANTE)) {
					destinatario = ec.getIstanzaAbbonamento().getPagante();
				}
				if (ec.getComunicazione().getIdTipoDestinatario().equals(AppConstants.DEST_PROMOTORE)) {
					destinatario = ec.getIstanzaAbbonamento().getPromotore();
				}
				String errorMessage = "Il destinatario non e' definito";
				if (destinatario != null) {
					errorMessage = "Nessun indirizzo email";
					if (destinatario.getEmailPrimaria() != null) {
						if (destinatario.getEmailPrimaria().length()>2) {
							String[] recArray = destinatario.getEmailPrimaria().split(AppConstants.STRING_SEPARATOR);
							if (recArray.length > 0) {
								//Ha un indirizzo email
								String[] recipient = {recArray[0]};
								errorMessage = "L'email non ha nessun testo o modello";
								String senderName = EmailConstants.DEFAULT_FROM_NAME;
								String subject = EmailConstants.DEFAULT_SUBJECT;
								//String textBody = null;
								String htmlBody = null;
								if (ec.getComunicazione().getModelloEmail() != null) {
									//Acquisisce il modello e lo customizza
									errorMessage = null;
									ModelliEmail me = ec.getComunicazione().getModelloEmail();
									senderName = me.getNomeMittente();
									subject = me.getOggetto();
									//textBody = EmailBusiness.replaceValues(ses,
									//		me.getTestoSemplice(),
									//		ec.getIstanzaAbbonamento());
									htmlBody = EmailBusiness.replaceValues(ses,
											me.getTestoHtml(),
											ec.getIstanzaAbbonamento());
									
								} else {
									//Se non è definito un modello assegna il corpo del
									//messaggio inserito manualmente (se presente)
									if (ec.getMessaggio() != null) {
										if (ec.getMessaggio().length() > 2) {
											errorMessage = null;
											//textBody = ec.getMessaggio();
											htmlBody = ec.getMessaggio();
										}
									}
								}
								//Invia se non ci sono errori
								if (errorMessage == null) {
									try {
										EmailBusiness.postCommonsHtmlMail(ServerConstants.SMTP_HOST,
												//ServerConstants.SMTP_USER,
												//ServerConstants.SMTP_PASSWORD,
												ServerConstants.SMTP_FROM,
												senderName, recipient,
												subject, htmlBody, null);
										successCount++;
									} catch (EmailException e) {
										String rec = "";
										for (String r:recipient) rec += r+" ";
										errorMessage = "ERRORE "+rec+": "+e.getMessage()+" " +
												ServerConstants.FORMAT_TIMESTAMP.format(new Date());
									}
								}
							}
						}
					}
				}
				if (!test) {
					//Controlla errori e marca come errato o inviato
					if (errorMessage != null) {
						//Con errore
						ec.setNote(errorMessage);
						ec.setEliminato(true);
						emailReport += ec.getIstanzaAbbonamento().getAbbonamento().getCodiceAbbonamento()+
								" " + errorMessage + "\r\n";
					} else {
						//Senza errore
						ec.setDataEstrazione(new Date());
					}
					ecDao.update(ses, ec);
				}
				trn.commit();
				trn = null;
				count++;
				if (count%PAGE_SIZE == 0) {
					VisualLogger.get().addHtmlInfoLine(idRapporto, "Elaborate "+count+" email su "+ecList.size()+" previste");
				}
			}
		} catch (HibernateException e) {
			if (trn != null) {
				if (trn.isActive()) {
					trn.rollback();
				}
			}
			emailReport += "\r\nERROR: " + e.getMessage();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		//Report email sull'invio
		if (ServerConstants.SMTP_DEFAULT_RECIPIENTS.length > 0) {
			try {
				String subject = "[APG] ";
				if (test) subject += "TEST ";
				subject += "Spedizione "+successCount+" email '"+descrizioneInvio+"'";
				emailReport = subject + "\r\n\r\n" + emailReport;
				//Mailer.postMail(ServerConstants.SMTP_HOST,
				//		ServerConstants.SMTP_USER,
				//		ServerConstants.SMTP_PASSWORD,
				//		ServerConstants.SMTP_FROM,
				//		ServerConstants.SMTP_DEFAULT_RECIPIENTS,
				//		subject, emailReport);
				Mailer.postMail(ServerConstants.SMTP_HOST,
						ServerConstants.SMTP_FROM,
						ServerConstants.SMTP_DEFAULT_RECIPIENTS,
						subject, emailReport, false);
			} catch (MessagingException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		
		return successCount;
	}
	
	private List<EvasioniComunicazioni> findEnqueuedComunicazioniByFascicolo(
			Integer idFascicolo, String idTipoMedia, int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		List<EvasioniComunicazioni> ecList = new ArrayList<EvasioniComunicazioni>();
		try {
			int offset = 0;
			int size = 0;
			do {
				List<EvasioniComunicazioni> list = ecDao
						.findEnqueuedComunicazioniByFascicolo(ses,
						idFascicolo, idTipoMedia, offset, PAGE_SIZE, idRapporto);
				size = list.size();
				offset += size;
				ecList.addAll(list);
			} while (size > 0);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return ecList;
	}
	
	private List<EvasioniComunicazioni> findEnqueuedComunicazioniByComunicazione(
			Integer idComunicazione, int idRapporto) throws BusinessException {
		Session ses = SessionFactory.getSession();
		EvasioniComunicazioniDao ecDao = new EvasioniComunicazioniDao();
		List<EvasioniComunicazioni> ecList = new ArrayList<EvasioniComunicazioni>();
		try {
			int offset = 0;
			int size = 0;
			do {
				List<EvasioniComunicazioni> list = ecDao
						.findEnqueuedComunicazioniByComunicazione(ses,
						idComunicazione, offset, PAGE_SIZE, idRapporto);
				size = list.size();
				offset += size;
				ecList.addAll(list);
			} while (size > 0);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		}
		return ecList;
	}
	
	private List<EvasioniComunicazioni> replaceEvasioniListWithTest(List<EvasioniComunicazioni> ecList) {
		//If it's a test then the list is replaced with a single recipient with test email
		if(ecList != null) {
			if (ecList.size() > 0) {
				EvasioniComunicazioni testEc = ecList.get(0);
				ecList.clear();
				for (String rec:EmailConstants.TEST_RECIPIENTS) {
					try {
						EvasioniComunicazioni ec = (EvasioniComunicazioni)BeanUtils.cloneBean(testEc);
						IstanzeAbbonamenti ia = (IstanzeAbbonamenti)BeanUtils.cloneBean(testEc.getIstanzaAbbonamento());
						ec.setIstanzaAbbonamento(ia);
						Anagrafiche abbonato = (Anagrafiche)BeanUtils.cloneBean(testEc.getIstanzaAbbonamento().getAbbonato());
						abbonato.setEmailPrimaria(rec);
						ia.setAbbonato(abbonato);
						if (testEc.getIstanzaAbbonamento().getPagante() != null) {
								Anagrafiche pagante = (Anagrafiche)BeanUtils.cloneBean(testEc.getIstanzaAbbonamento().getPagante());
								pagante.setEmailPrimaria(rec);
								ia.setPagante(pagante);
						}
						ecList.add(ec);
					} catch (IllegalAccessException e) {
						LOG.error(e.getMessage(), e);
					} catch (InstantiationException e) {
						LOG.error(e.getMessage(), e);
					} catch (InvocationTargetException e) {
						LOG.error(e.getMessage(), e);
					} catch (NoSuchMethodException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}
		}
		return ecList;
	}
}