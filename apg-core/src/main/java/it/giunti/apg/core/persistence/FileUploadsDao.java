package it.giunti.apg.core.persistence;

import it.giunti.apg.shared.model.FileUploads;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FileUploadsDao implements BaseDao<FileUploads> {

	@Override
	public void update(Session ses, FileUploads instance) throws HibernateException {
		GenericDao.updateGeneric(ses, instance.getId(), instance);
	}

	@Override
	public Serializable save(Session ses, FileUploads transientInstance)
			throws HibernateException {
		return GenericDao.saveGeneric(ses, transientInstance);
	}

	@Override
	public void delete(Session ses, FileUploads instance)
			throws HibernateException {
		GenericDao.deleteGeneric(ses, instance.getId(), instance);
	}

}
