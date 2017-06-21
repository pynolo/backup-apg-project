package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.TipiAbbonamento;
import it.giunti.apg.shared.model.TipiAbbonamentoRinnovo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class TipiAbbonamentoRinnovoDao implements BaseDao<TipiAbbonamentoRinnovo> {

	@Override
	public void update(Session ses, TipiAbbonamentoRinnovo instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, TipiAbbonamentoRinnovo transientInstance)
			throws HibernateException {
		Integer id = (Integer)GenericDao.saveGeneric(ses, transientInstance);
		return id;
	}

	@Override
	public void delete(Session ses, TipiAbbonamentoRinnovo instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
	
	@SuppressWarnings("unchecked")
	public List<TipiAbbonamentoRinnovo> findByIdListinoOrdine(Session ses,
			Integer idListino) throws HibernateException {
		//Cerca il listino senza data di scadenza
		String queryString = "from TipiAbbonamentoRinnovo tar where " +
				"tar.idListino = :id1 order by ordine asc";
		Query q = ses.createQuery(queryString);
		q.setInteger("id1", idListino);
		List<TipiAbbonamentoRinnovo> list = (List<TipiAbbonamentoRinnovo>) q.list();
		return list;
	}
	
	public TipiAbbonamento findFirstTipoRinnovoByIdListino(Session ses,
			Integer idListino)  throws HibernateException {
		TipiAbbonamento result = null;
		List<TipiAbbonamentoRinnovo> tarList = findByIdListinoOrdine(ses, idListino);
		if (tarList != null) {
			if (tarList.size() > 0) result = tarList.get(0).getTipoAbbonamento();
		}
		return result;
	}
	
	public TipiAbbonamento findSecondTipoRinnovoByIdListino(Session ses,
			Integer idListino)  throws HibernateException {
		TipiAbbonamento result = null;
		List<TipiAbbonamentoRinnovo> tarList = findByIdListinoOrdine(ses, idListino);
		if (tarList != null) {
			if (tarList.size() > 1) result = tarList.get(1).getTipoAbbonamento();
		}
		return result;
	}
		
	public List<TipiAbbonamentoRinnovo> replaceTipiRinnovoByListino(Session ses, Integer idListino,
			List<Integer> idTipiAbbList)
			throws HibernateException {
		//Rimuove tutti i vecchi abbinamenti
		String delHql = "delete from TipiAbbonamentoRinnovo where "+
				" idListino = :id1";
		Query delQ = ses.createQuery(delHql);
		delQ.setParameter("id1", idListino);
		delQ.executeUpdate();
		//Abbina i nuovi tipi al rinnovo
		List<TipiAbbonamentoRinnovo> tarList = new ArrayList<TipiAbbonamentoRinnovo>();
		for (int i=0; i < idTipiAbbList.size(); i++) {
			TipiAbbonamentoRinnovo tar = new TipiAbbonamentoRinnovo();
			tar.setIdListino(idListino);
			TipiAbbonamento ta = GenericDao.findById(ses, TipiAbbonamento.class, idTipiAbbList.get(i));
			tar.setTipoAbbonamento(ta);
			tar.setOrdine(i);
			GenericDao.saveGeneric(ses, tar);
			tarList.add(tar);
		}
		return tarList;
	}
}
