package it.giunti.apg.core.persistence;

import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.business.FattureBusiness;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Anagrafiche;
import it.giunti.apg.shared.model.Fatture;
import it.giunti.apg.shared.model.FattureStampe;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.Societa;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

public class FattureDao implements BaseDao<Fatture> {

	@Override
	public void update(Session ses, Fatture instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, Fatture transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, Fatture instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	
	
	// Metodi di RICERCA
	
	
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findByAnagrafica(Session ses, Integer idAnagrafica, boolean includeFittizie) throws HibernateException {
		//Query
		String qs = "from Fatture f where " +
				"f.idAnagrafica = :i1 ";
		if (!includeFittizie) qs += "and f.numeroFattura not like :s1 ";
		qs += "order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("i1", idAnagrafica, IntegerType.INSTANCE);
		if (!includeFittizie) q.setParameter("s1", AppConstants.FATTURE_PREFISSO_FITTIZIO+"%", StringType.INSTANCE);
		List<Fatture> sfList = (List<Fatture>) q.list();
		return sfList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findByIstanza(Session ses, Integer idIstanza) throws HibernateException {
		Set<Fatture> set = new HashSet<Fatture>();
		IstanzeAbbonamenti ia = GenericDao.findById(ses, IstanzeAbbonamenti.class, idIstanza);
		if (ia.getIdFattura() != null) {
			Fatture fattIa = GenericDao.findById(ses, Fatture.class, ia.getIdFattura());
			set.add(fattIa);
		}
		if (ia.getOpzioniIstanzeAbbonamentiSet() != null) {
			for (OpzioniIstanzeAbbonamenti oia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (oia.getIdFattura() != null) {
					Fatture fattOia = GenericDao.findById(ses, Fatture.class, oia.getIdFattura());
					set.add(fattOia);
				}
			}
		}
		//By Istanza
		String qsia = "from Fatture f where " +
				"f.idIstanzaAbbonamento = :id1 "+
				"order by f.id desc ";
		Query qia = ses.createQuery(qsia);
		qia.setParameter("id1", idIstanza, IntegerType.INSTANCE);
		List<Fatture> fattList = (List<Fatture>) qia.list();
		set.addAll(fattList);
		//Note di credito correlate
		List<Integer> idNdcList = new ArrayList<Integer>();
		for (Fatture fatt:set) {
			if (fatt.getIdNotaCreditoStornoResto() != null) idNdcList.add(fatt.getIdNotaCreditoStornoResto());
			if (fatt.getIdNotaCreditoRimborsoResto() != null) idNdcList.add(fatt.getIdNotaCreditoRimborsoResto());
			if (fatt.getIdNotaCreditoStorno() != null) idNdcList.add(fatt.getIdNotaCreditoStorno());
			if (fatt.getIdNotaCreditoRimborso() != null) idNdcList.add(fatt.getIdNotaCreditoRimborso());
		}
		for (Integer idNdc:idNdcList) {
			Fatture ndcIa = GenericDao.findById(ses, Fatture.class, idNdc);
			set.add(ndcIa);
		}
		
		List<Fatture> result = new ArrayList<Fatture>();
		result.addAll(set);
		//Sort
		Collections.sort(result, new Comparator<Fatture>() {
			@Override
			public int compare(Fatture f1, Fatture f2) {
				int compare = f1.getNumeroFattura().compareToIgnoreCase(f2.getNumeroFattura());
				return -1*compare;
			}
		});
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findByAnagraficaRemovingMissingPrints(Session ses, Integer idAnagrafica, boolean includeFittizie) throws HibernateException {
		//Query
		String qs = "from Fatture f where " +
				"f.idAnagrafica = :i1 ";
		if (!includeFittizie) qs += "and f.numeroFattura not like :s1 ";
		qs += "order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("i1", idAnagrafica, IntegerType.INSTANCE);
		if (!includeFittizie) q.setParameter("s1", AppConstants.FATTURE_PREFISSO_FITTIZIO+"%", StringType.INSTANCE);
		List<Fatture> fList = (List<Fatture>) q.list();
		for (Fatture f:fList) {
			if (f.getIdFatturaStampa() != null) {
				FattureStampe sf = GenericDao.findById(ses, FattureStampe.class, f.getIdFatturaStampa());
				if (sf == null) f.setIdFatturaStampa(null);
			}
		}
		return fList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findByPeriodicoData(Session ses,
			Integer idPeriodico, Date fromDate, Date toDate, boolean includeFittizie) throws HibernateException {
		String qs = "from Fatture f where " +
				"f.idPeriodico = :id1 and " + //societa
				"f.dataFattura >= :dt1 and " +
				"f.dataFattura <= :dt2 ";
		if (!includeFittizie) qs += "and f.numeroFattura not like :s1 ";
		qs += "order by f.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idPeriodico, IntegerType.INSTANCE);
		q.setParameter("dt1", fromDate, DateType.INSTANCE);
		q.setParameter("dt2", toDate, DateType.INSTANCE);
		if (!includeFittizie) q.setParameter("s1", AppConstants.FATTURE_PREFISSO_FITTIZIO+"%", StringType.INSTANCE);
		List<Fatture> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findBySocietaData(Session ses,
			String idSocieta, Date fromDate, Date toDate, boolean includeFittizie,
			int offset, int pageSize) throws HibernateException {
		String qs = "from Fatture f where " +
				"f.idSocieta = :id1 and " + //societa
				"f.dataFattura >= :dt1 and " +
				"f.dataFattura <= :dt2 ";
		if (!includeFittizie) qs += "and f.numeroFattura not like :s1 ";
		qs += "order by f.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idSocieta, StringType.INSTANCE);
		q.setParameter("dt1", fromDate, DateType.INSTANCE);
		q.setParameter("dt2", toDate, DateType.INSTANCE);
		if (!includeFittizie) q.setParameter("s1", AppConstants.FATTURE_PREFISSO_FITTIZIO+"%", StringType.INSTANCE);
		q.setMaxResults(pageSize);
		q.setFirstResult(offset);
		List<Fatture> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findNotYetPrintedBySocietaData(Session ses,
			String idSocieta, Date fromDate, Date toDate, boolean includeFittizie) throws HibernateException {
		String qs = "from Fatture f where " +
				"f.idSocieta = :id1 and " + //societa
				"f.idFatturaStampa is null and "+
				"f.dataFattura >= :dt1 and " +
				"f.dataFattura <= :dt2 ";
		if (!includeFittizie) qs += "and f.numeroFattura not like :s1 ";
		qs += "order by f.id ";
		Query q = ses.createQuery(qs);
		q.setParameter("id1", idSocieta, StringType.INSTANCE);
		q.setParameter("dt1", fromDate, DateType.INSTANCE);
		q.setParameter("dt2", toDate, DateType.INSTANCE);
		if (!includeFittizie) q.setParameter("s1", AppConstants.FATTURE_PREFISSO_FITTIZIO+"%", StringType.INSTANCE);
		List<Fatture> result = q.list();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<Fatture> findByNumeroFattura(Session ses, String numeroFattura) throws HibernateException {
		//Query
		String qs = "from Fatture f where " +
				"f.numeroFattura like :s1 " +
				"order by f.id desc ";
		Query q = ses.createQuery(qs);
		q.setParameter("s1", numeroFattura, StringType.INSTANCE);
		List<Fatture> fList = (List<Fatture>) q.list();
		return fList;
	}
	
	//@SuppressWarnings("unchecked")
	//public Fatture getRimborsoByNumFattura(Session ses, String numFatturaOrig) throws HibernateException {
	//	String qs = "from Fatture f where " +
	//			"f.numeroRimborsoCollegato like :s1 " +
	//			"order by f.dataFattura desc ";
	//	Query q = ses.createQuery(qs);
	//	q.setParameter("s1", numFatturaOrig, StringType.INSTANCE);
	//	List<Fatture> fList = (List<Fatture>) q.list();
	//	if (fList != null) {
	//		if (fList.size() > 0) {
	//			return fList.get(0);
	//		}
	//	}
	//	return null;
	//}
	
	
	
	// Metodi BUSINESS
	
	
	
	public void initNumFatture(Session ses, List<IstanzeAbbonamenti> iaList, Date ultimoGiornoMese) {
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
	
	public void initNumFatture(Session ses, Date data, String idSocieta) {
		ContatoriDao contDao = new ContatoriDao();
		Societa societa = GenericDao.findById(ses, Societa.class, idSocieta);
		String prefix = societa.getPrefissoFatture();
		contDao.initNumFattura(ses, prefix, data);
		contDao.initNumFattura(ses, AppConstants.FATTURE_PREFISSO_FITTIZIO, data);
	}
	
	public void commitNumFatture(Session ses, List<Fatture> fattureList, String idSocieta) 
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
	
	//public void commitNumFatture(Session ses, List<Fatture> fattureList) 
	//		throws HibernateException {
	//	Map<String, Object[]> fattNumMap = new HashMap<String, Object[]>();
	//	for (Fatture fatt:fattureList) {
	//		String companyPrefix = fatt.getNumeroFattura().substring(0, 3);
	//		String year = ServerConstants.FORMAT_YEAR.format(fatt.getDataFattura());
	//		Object[] obj = {fatt.getNumeroFattura().substring(0, 3), fatt.getDataFattura()};
	//		fattNumMap.put(companyPrefix+year, obj);
	//	}
	//	ContatoriDao contDao = new ContatoriDao();
	//	Date lastDate = new Date();
	//	for (String key:fattNumMap.keySet()) {
	//		Object[] obj = fattNumMap.get(key);
	//		String prefix = (String)obj[0];
	//		if (!prefix.equals(AppConstants.FATTURE_PREFISSO_FITTIZIO))
	//				contDao.commitNumFattura(ses, prefix, (Date)obj[1]);
	//		lastDate = (Date)obj[1];
	//	}
	//	contDao.commitNumFattura(ses, AppConstants.FATTURE_PREFISSO_FITTIZIO, lastDate);
	//}

	public Fatture createFattura(Session ses, Anagrafiche pagante, String idSocieta,  Date data, boolean isFittizia)
			throws BusinessException {
		//** INIT ** dei numeri fattura creati
		initNumFatture(ses, data, idSocieta);
			List<Fatture> fattureList = new ArrayList<Fatture>();
			//Creazione oggetti Fatture senza produrre i byte[] di stampa
			
			//Persist fatture
			Fatture fattura = null;
			try {
				fattura = FattureBusiness.saveFatturaConNumero(ses, pagante, idSocieta, data, isFittizia);
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
	
}
