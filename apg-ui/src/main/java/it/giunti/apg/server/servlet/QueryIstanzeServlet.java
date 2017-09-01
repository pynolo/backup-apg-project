package it.giunti.apg.server.servlet;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.QueryResultFormatBusiness;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryIstanzeServlet extends HttpServlet {
	private static final long serialVersionUID = -7081928582376306472L;
	
	private static final Logger LOG = LoggerFactory.getLogger(OutputInvioServlet.class);
	private static final String SEPARATOR = ";";
	private static final int MIN_CONDITIONS = 2;
	private static final String FILENAME_PREFIX = "query_istanze_";
	private static final String FILENAME_EXT = ".csv";
	private static final int PAGE_SIZE = 500;
	private static final long TIME_FRAME_MAX = AppConstants.YEAR * 3;
			
	public QueryIstanzeServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Integer idPagante = select2int(req.getParameter("idPagante"));
		Integer idPromotore = select2int(req.getParameter("idPromotore")); 
		Integer idPeriodico = select2int(req.getParameter("idPeriodico")); 
		if (idPeriodico < 1) idPeriodico = null;
		String tipiAbbonamentoString = req.getParameter("tipiAbbonamento");
		String opzioniString = req.getParameter("opzioni");
		Integer idFascicolo = select2int("idFascicolo");
		Date dniGe = txt2date(req.getParameter("dniGe"));
		Date dniLe = txt2date(req.getParameter("dniLe"));
		Date dnfGe = txt2date(req.getParameter("dnfGe"));
		Date dnfLe = txt2date(req.getParameter("dnfLe"));
		Date creGe = txt2date(req.getParameter("creGe"));
		Date creLe = txt2date(req.getParameter("creLe"));
		Date inactiveAtDt = txt2date(req.getParameter("inactiveAtDt"));
		Integer idAdesione = select2int(req.getParameter("idAdesione"));
		Boolean pagato = select2bool(req.getParameter("pagato"));
		Boolean fatturato = select2bool(req.getParameter("fatturato"));
		Boolean disdetta = select2bool(req.getParameter("disdetta"));
		Boolean bloccato = select2bool(req.getParameter("bloccato"));
		Integer idTipoDisdetta = select2int(req.getParameter("idTipoDisdetta"));
		
		ArrayList<String> tipiAbbonamento = new ArrayList<String>();
		if (tipiAbbonamentoString != null) {
			String[] taArray = tipiAbbonamentoString.split(SEPARATOR);
			for (String ta:taArray) tipiAbbonamento.add(ta.toUpperCase().trim());
		}
		ArrayList<String> opzioni = new ArrayList<String>();
		if (opzioniString != null) {
			String[] oArray = opzioniString.split(SEPARATOR);
			for (String o:oArray) opzioni.add(o.toUpperCase().trim());
		}
		//Verifica date
		if (dniGe != null && dniLe != null) {
			if (dniGe.getTime()-dniLe.getTime() > TIME_FRAME_MAX) throw new ServletException("Time frame too long");
		}
		if (dnfGe != null && dnfLe != null) {
			if (dnfGe.getTime()-dnfLe.getTime() > TIME_FRAME_MAX) throw new ServletException("Time frame too long");
		}
		if (creGe != null && creLe != null) {
			if (creGe.getTime()-creLe.getTime() > TIME_FRAME_MAX) throw new ServletException("Time frame too long");
		}
		
		//Query
		List<IstanzeAbbonamenti> iaList = queryData(idPagante, idPromotore, idPeriodico,
					tipiAbbonamento, opzioni, idFascicolo,
					dniGe, dniLe, dnfGe, dnfLe, creGe, creLe, inactiveAtDt,
					idAdesione, pagato, fatturato,
					disdetta, bloccato, idTipoDisdetta);
		String fileContent = QueryResultFormatBusiness.format(iaList);
		File f;
		try {
			f = File.createTempFile(FILENAME_PREFIX, FILENAME_EXT);
			PrintWriter pw = new PrintWriter(f);
			pw.print(fileContent);
			pw.close();
		} catch (Exception e) {
			LOG.error("File creation error: "+e.getMessage(), e);
			throw new ServletException(e);
		}
		
		//Durante i test il file è inviato in HTTP
		try {
			int BUFSIZE = 2048;
			//Try to send via http
			int length = 0;
			ServletOutputStream op = resp.getOutputStream();
			resp.setContentType("application/octet-stream");
			String header = "attachment; filename=\""+FILENAME_PREFIX+
					ServerConstants.FORMAT_FILE_NAME_TIMESTAMP.format(new Date())+
					FILENAME_EXT+"\"";
			resp.setHeader("Content-Disposition", header);
			// Stream to the requester
			byte[] bbuf = new byte[BUFSIZE];
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			    op.write(bbuf,0,length);
			}
			in.close();
			op.flush();
			op.close();
			resp.setContentLength( (int)f.length() );
		} catch (Exception e) { 
			LOG.error("Http file transfer error: "+e.getMessage(), e);
			throw new ServletException(e);
		}
	}
	
	private List<IstanzeAbbonamenti> queryData(Integer idPagante, Integer idPromotore, Integer idPeriodico,
			List<String> tipiAbbonamento, List<String> opzioni, Integer idFascicolo,
			Date dniGe, Date dniLe, Date dnfGe, Date dnfLe, Date creGe, Date creLe,
			Date inactiveAtDt,
			Integer idAdesione, Boolean pagato, Boolean fatturato,
			Boolean disdetta, Boolean bloccato, Integer idTipoDisdetta) throws ServletException {
		List<IstanzeAbbonamenti> result = null;
		Session ses = SessionFactory.getSession();
		try {
			// Query string
			String qs = "from IstanzeAbbonamenti ia where ";
			//		"ia.ultimaDellaSerie = :x1 and ";
			List<String> cond = new ArrayList<String>();
			if (idPagante != null) cond.add("ia.pagante.id = :id1 ");
			if (idPromotore != null) cond.add("ia.promotore.id = :id2 ");
			if (idPeriodico != null) cond.add("ia.listino.tipoAbbonamento.periodico.id = :id3 ");
			if (tipiAbbonamento.size() > 0) {
				String taCond = "( ";
				for (int i=0; i<tipiAbbonamento.size(); i++) {
					if (i > 0) taCond += "or ";
					taCond += "(ia.listino.tipoAbbonamento.codice = :ta"+i+") ";
				}
				taCond += ") ";
				cond.add(taCond);
			}
			if (opzioni.size() > 0) {
				String oCond = "(select count(oia.id) from OpzioniIstanzeAbbonamenti oia "+
					"where oia.istanza.id = ia.id and (";
				for (int i=0; i<opzioni.size(); i++) {
					if (i > 0) oCond += "or ";
					oCond += "(oia.opzione.uid = :opz"+i+") ";
				}
				oCond += ")) > 0 ";
				cond.add(oCond);
			}
			if (idFascicolo != null) cond.add("(select count(ef.id) from EvasioniFascicoli ef "+
					"where ef.idIstanzaAbbonamento = ia.id and ef.fascicolo.id = :id4) > 0 ");
			if (dniGe != null) cond.add("ia.fascicoloInizio.dataInizio >= :dt1 ");
			if (dniLe != null) cond.add("ia.fascicoloInizio.dataInizio <= :dt2 ");
			if (dnfGe != null) cond.add("ia.fascicoloFine.dataFine >= :dt3 ");
			if (dnfLe != null) cond.add("ia.fascicoloFine.dataFine <= :dt4 ");
			if (creGe != null) cond.add("ia.abbonamento.dataCreazione >= :dt5 ");
			if (creLe != null) cond.add("ia.abbonamento.dataCreazione <= :dt6 ");
			if (inactiveAtDt != null) cond.add("(select count(ia2.id) from IstanzeAbbonamenti ia2 "+
					"where ia2.abbonamento.id = ia.abbonamento.id and "+
					"ia2.invioBloccato = :inactb1 and "+//false
					"ia2.fascicoloInizio.dataInizio <= :inactdt1 and "+
					"ia2.fascicoloFine.dataFine >= :inactdt2) < 1");
			if (idAdesione != null) cond.add("ia.adesione.id = :id5 ");
			if (pagato != null) cond.add("ia.pagato = :b1 ");
			if (fatturato != null) cond.add("(ia.inFatturazione = :b21 or ia.listino.fatturaDifferita = :b22)");
			if (disdetta != null) {
				if (disdetta) {
					cond.add("ia.dataDisdetta is not null ");
				} else {
					cond.add("ia.dataDisdetta is null ");
				}
			}
			if (bloccato != null) cond.add("ia.invioBloccato = :b3 ");
			if (idTipoDisdetta != null) cond.add("ia.idTipoDisdetta = :id6 ");

			if (cond.size() >= MIN_CONDITIONS) {
				for (int i=0; i < cond.size(); i++) {
					if (i > 0) qs += "and ";
					qs += cond.get(i);
				}
				Query q = ses.createQuery(qs);
				
				// Parameter values
				//q.setBoolean("x1", Boolean.TRUE);
				if (idPagante != null) q.setInteger("id1", idPagante);
				if (idPromotore != null) q.setInteger("id2", idPromotore);
				if (idPeriodico != null) q.setInteger("id3", idPeriodico);
				if (tipiAbbonamento.size() > 0) {
					for (int i=0; i<tipiAbbonamento.size(); i++) {
						q.setString("ta"+i, tipiAbbonamento.get(i));
					}
				}
				if (opzioni.size() > 0) {
					for (int i=0; i<opzioni.size(); i++) {
						q.setString("opz"+i, opzioni.get(i));
					}
				}
				if (idFascicolo != null) q.setInteger("id4", idFascicolo);
				if (dniGe != null) q.setDate("dt1", dniGe);
				if (dniLe != null) q.setDate("dt2", dniLe);
				if (dnfGe != null) q.setDate("dt3", dnfGe);
				if (dnfLe != null) q.setDate("dt4", dnfLe);
				if (creGe != null) q.setDate("dt5", creGe);
				if (creLe != null) q.setDate("dt6", creLe);
				if (inactiveAtDt != null) {
					q.setBoolean("inactb1", Boolean.FALSE);
					q.setDate("inactdt1", inactiveAtDt);
					q.setDate("inactdt2", inactiveAtDt);
				}
				if (idAdesione != null) q.setInteger("id5", idAdesione);
				if (pagato != null) q.setBoolean("b1", pagato);
				if (fatturato != null) {
					q.setBoolean("b21", fatturato);
					q.setBoolean("b22", fatturato);
				}
				if (bloccato != null) q.setBoolean("b3", bloccato);
				if (idTipoDisdetta != null) q.setInteger("id6", idTipoDisdetta);
				
				result = paginateQuery(ses, q);
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new ServletException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<IstanzeAbbonamenti> paginateQuery(Session ses, Query q) throws HibernateException {
		List<IstanzeAbbonamenti> result = new ArrayList<IstanzeAbbonamenti>();
		List<IstanzeAbbonamenti> stepResult = new ArrayList<IstanzeAbbonamenti>();
		int offset = 0;
		do {
			if (offset > 0) LOG.debug("Estratti: "+offset);
			q.setMaxResults(PAGE_SIZE);
			q.setFirstResult(offset);
			stepResult = q.list();
			offset += stepResult.size();
			result.addAll(stepResult);
			ses.flush();
			ses.clear();
		} while (stepResult.size() > 0);
		return result;
	}
	
	
	public static Date txt2date(String s) {
		Date dt = null;
		if (s != null) {
			try {
				dt = ServerConstants.FORMAT_DAY_SQL.parse(s);
			} catch (ParseException e) { }
		}
		return dt;
	}
	
	public static Integer select2int(String s) {
		if (s == null) return null;
		Integer result;
		try {
			result = Integer.valueOf(s);
			if (result < 0) result = null;
		} catch (NumberFormatException e) {
			result = null;
		}
		return result;
	}
	
	public static Boolean select2bool(String s) {
		if (s != null) {
			if (s.equalsIgnoreCase(AppConstants.BOOLEAN_TRUE)) {
				return true;
			}
			if (s.equalsIgnoreCase(AppConstants.BOOLEAN_FALSE)) {
				return false;
			}
		}
		return null;
	}
}