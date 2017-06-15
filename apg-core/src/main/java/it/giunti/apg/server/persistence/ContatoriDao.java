package it.giunti.apg.server.persistence;

import it.giunti.apg.server.ServerConstants;
import it.giunti.apg.shared.NumberBaseConverter;
import it.giunti.apg.shared.model.Contatori;
import it.giunti.apg.shared.model.Periodici;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContatoriDao implements BaseDao<Contatori> {

	static private Logger LOG = LoggerFactory.getLogger(ContatoriDao.class);
	public static final DecimalFormat FORMAT_CODICE = new DecimalFormat("000000");
	
	@Override
	public void update(Session ses, Contatori instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Contatori transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Contatori instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public String createCodiceAbbonamento(Session ses, Integer idPeriodico) throws HibernateException {
		String queryString = "from Contatori ac where ac.ckey = :s1 ";
		Query q = ses.createQuery(queryString);
		q.setString("s1", ServerConstants.CONTATORE_PERIODICO_PREFIX+idPeriodico);
		Contatori cont = (Contatori)q.uniqueResult();
		if (cont == null) {
			//se non esiste un contatore per la rivista allora lo crea
			Contatori ac = new Contatori();
			ac.setCkey(ServerConstants.CONTATORE_PERIODICO_PREFIX+idPeriodico);
			ac.setProgressivo(ServerConstants.DEFAULT_ABBONAMENTI_START_VALUE);
			Integer idAc = (Integer) save(ses, ac);
			cont = GenericDao.findById(ses, Contatori.class, idAc);
		}
		Periodici p = GenericDao.findById(ses, Periodici.class, idPeriodico);
		String code;
		Integer progressivo = cont.getProgressivo();
		boolean usedCode = true;
		String testQuery = "select a.id from Abbonamenti as a where a.codiceAbbonamento = :p1";
		do {
			progressivo++;
			code = p.getUid() + FORMAT_CODICE.format(progressivo);
			Query testQ = ses.createQuery(testQuery);
			testQ.setString("p1", code);
			List<Object> idList = (List<Object>) testQ.list();
			//Se la ricerca del codice dà 1 o più risultati allora ripete
			usedCode = false;
			if (idList != null) {
				if (idList.size() > 0) {
					usedCode = true;
				}
			}
		} while (usedCode);
		//A questo punto progressivo contiene il primo valore libero
		//Su DB viene scritto il precedente (cioè progressivo-1) in modo da non
		//bruciare un codice finché non abbinato ad un abbonamento realmente esistente
		cont.setProgressivo(progressivo-1);
		update(ses, cont);
		return code;
	}
	
	public String generateUidCliente(Session ses) {
		String queryString = "from Contatori ac where ac.ckey = :s1 ";
		Query q = ses.createQuery(queryString);
		q.setString("s1", ServerConstants.CONTATORE_ANAGRAFICHE);
		Contatori cont = (Contatori)q.uniqueResult();
		if (cont == null) {
			//se non esiste un cursore per le anagrafiche allora lo crea
			Contatori ac = new Contatori();
			ac.setCkey(ServerConstants.CONTATORE_ANAGRAFICHE);
			ac.setProgressivo(ServerConstants.DEFAULT_ANAGRAFICHE_START_VALUE);
			Integer idAc = (Integer) save(ses, ac);
			cont = GenericDao.findById(ses, Contatori.class, idAc);
		}
		Integer progressivo = cont.getProgressivo();
		String code = NumberBaseConverter.toBase30(progressivo);
		if (code.length() < ServerConstants.DEFAULT_ANAGRAFICHE_CODE_LENGTH) {
			code = "0000000000"+code;
			code = code.substring(code.length()-ServerConstants.DEFAULT_ANAGRAFICHE_CODE_LENGTH);
		}
		code = NumberBaseConverter.getChecksum30(code) + code;
		cont.setProgressivo(progressivo+1);
		update(ses, cont);
		return code;
	}
	
	public String createCodiceOrdine(Session ses) throws HibernateException {
		String queryString = "from Contatori ac where ac.ckey = :s1 ";
		Query q = ses.createQuery(queryString);
		q.setString("s1", ServerConstants.CONTATORE_ORDINI);
		Contatori cont = (Contatori)q.uniqueResult();
		if (cont == null) {
			//se non esiste un cursore per gli ordini
			Contatori ac = new Contatori();
			ac.setCkey(ServerConstants.CONTATORE_ORDINI);
			ac.setProgressivo(1);
			Integer idAc = (Integer) save(ses, ac);
			cont = GenericDao.findById(ses, Contatori.class, idAc);
		}
		Integer progressivo = cont.getProgressivo();//Incrementa e assegna
		String code = NumberBaseConverter.toBase30(progressivo);
		if (code.length() < ServerConstants.DEFAULT_ORDINI_CODE_LENGTH) {
			code = "0000000000"+code;
			code = code.substring(code.length()-ServerConstants.DEFAULT_ORDINI_CODE_LENGTH);
		}
		cont.setProgressivo(progressivo+1);
		update(ses, cont);
		return code;
	}

	public Integer loadProgressivo(Session ses, String key) throws HibernateException {
		Integer result = null;
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", key);
		if (cList != null) {
			if (cList.size() > 0) {
				result = cList.get(0).getProgressivo();
			}
		}
		return result;
	}
	
	public void updateProgressivo(Session ses, Integer progressivo, String key) throws HibernateException {
		Contatori cur = null;
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", key);
		if (cList != null) {
			if (cList.size() > 0) {
				cur = cList.get(0);
			}
		}
		cur.setProgressivo(progressivo);
		update(ses, cur);
	}
	
	
	
	// ** PAGINAZIONE REGISTRO MENSILE FATTURE **
	
	
	
	/** Restituisce un nuovo numero di pagina
	 * Memorizza il valore in una colonna temporanea e marca la riga come LOCK
	 */
	public void initPagRegMensile(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		Integer pageNum = null;
		String ckey = ServerConstants.CONTATORE_PAGINE_REG_MENS_FATTURE+
				companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				if (c.getLocked())
					LOG.error("PagRegMensile has been already inited for "+ckey+
							". PagRegMensile has not been interrupted though.");
				c.setTempProgressivo(c.getProgressivo());
				c.setLocked(true);
				update(ses, c);
				pageNum = c.getProgressivo();
			}
		}
		if (pageNum == null) {
			//La riga sul DB non esiste, quindi va creata
			pageNum = 0;
			Contatori c = new Contatori();
			c.setCkey(ckey);
			c.setProgressivo(pageNum);
			c.setLocked(true);
			c.setTempProgressivo(pageNum);
			save(ses, c);
		}
	}
	
	/** Restituisce un nuovo numero di pagina in base all'anno
	 * Memorizza il valore in una colonna temporanea e marca la riga come LOCK
	 */
	public Integer nextTempPagRegMensile(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		Integer pageNum = null;
		String ckey = ServerConstants.CONTATORE_PAGINE_REG_MENS_FATTURE+
				companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				//La riga esiste, ma dà precedenza al valore in temp, se presente
				Contatori c = cList.get(0);
				if (!c.getLocked()) new HibernateException("Uninitialized PagRegMensile generator");
				Integer progressivo = c.getTempProgressivo();
				if (progressivo == null) new HibernateException("Uninitialized PagRegMensile generator");
				pageNum = progressivo+1;
				c.setTempProgressivo(pageNum);
				update(ses, c);
			}
		}
		return pageNum;
	}
	
	/** La riga del contatore pagine viene portata a unlock
	 * e viene annullato l'eventuale valore temporaneo
	 */
	public void rollbackPagRegMensile(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		String ckey = ServerConstants.CONTATORE_PAGINE_REG_MENS_FATTURE+
				companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				c.setTempProgressivo(null);
				c.setLocked(false);
				update(ses, c);
			}
		}
	}
	
	/** Scrive nel contatore il valore temporaneo in base a società e data e rimuove il lock
	 */
	public void commitPagRegMensile(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		String ckey = ServerConstants.CONTATORE_PAGINE_REG_MENS_FATTURE+
				companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				if (c.getTempProgressivo() == null || !c.getLocked()) {
					throw new HibernateException("Contatori with ckey="+ckey+" has no temp value nor lock");
				}
				c.setProgressivo(c.getTempProgressivo());
				c.setTempProgressivo(null);
				c.setLocked(false);
				update(ses, c);
				return;
			}
		}
		throw new HibernateException("Contatori with ckey="+ckey+" not found");
	}
	
	
	
	// ** FATTURE **
	
	
	
	/** Restituisce un nuovo numero fattura in base al
	 * prefisso della società e all'anno
	 * Memorizza il valore in una colonna temporanea e marca la riga come LOCK
	 */
	public void initNumFattura(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		Integer numFattura = null;
		String ckey = companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				if (c.getLocked())
					LOG.error("Fatture number has been already inited for "+ckey+
							". Fatture creation has not been interrupted though.");
				c.setTempProgressivo(c.getProgressivo());
				c.setLocked(true);
				update(ses, c);
				numFattura = c.getProgressivo();
			}
		}
		if (numFattura == null) {
			//La riga sul DB non esiste, quindi va creata
			numFattura = 0;
			Contatori c = new Contatori();
			c.setCkey(ckey);
			c.setProgressivo(numFattura);
			c.setLocked(true);
			c.setTempProgressivo(numFattura);
			save(ses, c);
		}
	}
	
	/** Restituisce un nuovo numero fattura in base al
	 * prefisso della società e all'anno
	 * Memorizza il valore in una colonna temporanea e marca la riga come LOCK
	 */
	public Integer nextTempNumFattura(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		Integer numFattura = null;
		String ckey = companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				//La riga esiste, ma dà precedenza al valore in temp, se presente
				Contatori c = cList.get(0);
				if (!c.getLocked()) new HibernateException("Uninitialized NumFattura generator");
				Integer progressivo = c.getTempProgressivo();
				if (progressivo == null) new HibernateException("Uninitialized NumFattura generator");
				numFattura = progressivo+1;
				c.setTempProgressivo(numFattura);
				update(ses, c);
			}
		}
		return numFattura;
	}
	
	/** La riga del contatore viene portata a unlock
	 * e viene annullato l'eventuale valore temporaneo
	 */
	public void rollbackNumFattura(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		String ckey = companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				c.setTempProgressivo(null);
				c.setLocked(false);
				update(ses, c);
			}
		}
	}
	
	/** Scrive nel contatore il valore temporaneo in base a società e data e rimuove il lock
	 */
	public void commitNumFattura(Session ses, String companyPrefix, Date date)
			throws HibernateException {
		String ckey = companyPrefix+ServerConstants.FORMAT_YEAR.format(date);
		List<Contatori> cList = GenericDao.findByProperty(ses, Contatori.class, "ckey", ckey);
		if (cList != null) {
			if (cList.size() > 0) {
				Contatori c = cList.get(0);
				if (c.getTempProgressivo() == null || !c.getLocked()) {
					throw new HibernateException("Contatori with ckey="+ckey+" has no temp value nor lock");
				}
				c.setProgressivo(c.getTempProgressivo());
				c.setTempProgressivo(null);
				c.setLocked(false);
				update(ses, c);
				return;
			}
		}
		throw new HibernateException("Contatori with ckey="+ckey+" not found");
	}
}
