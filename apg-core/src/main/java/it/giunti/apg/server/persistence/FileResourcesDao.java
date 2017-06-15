package it.giunti.apg.server.persistence;

import it.giunti.apg.shared.model.FileResources;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class FileResourcesDao implements BaseDao<FileResources> {

	@Override
	public void update(Session ses, FileResources instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, FileResources transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, FileResources instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}
		
	@SuppressWarnings("unchecked")
	public List<FileResources> findByType(Session ses, String fileType)
			throws HibernateException {
		String qs = "from FileResources as fr where " +
				"fr.fileType = :s1 " +
				"order by fr.path ";
		Query q = ses.createQuery(qs);
		q.setString("s1", fileType);
		List<FileResources> frList = (List<FileResources>) q.list();
		return frList;
	}

}
