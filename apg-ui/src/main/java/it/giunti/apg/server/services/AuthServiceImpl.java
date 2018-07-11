package it.giunti.apg.server.services;

import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import it.giunti.apg.client.services.AuthService;
import it.giunti.apg.core.ServerConstants;
import it.giunti.apg.core.persistence.GenericDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.core.persistence.UtentiDao;
import it.giunti.apg.core.persistence.UtentiPasswordDao;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.DateUtil;
import it.giunti.apg.shared.EmptyResultException;
import it.giunti.apg.shared.model.Ruoli;
import it.giunti.apg.shared.model.Utenti;

public class AuthServiceImpl extends RemoteServiceServlet implements AuthService {
	private static final long serialVersionUID = 4756575502075034040L;
	private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);
	private static final UtentiDao utentiDao = new UtentiDao();
	
	private static final String ATTRIBUTE_FOR_USER = "sAMAccountName";
	private static final String ATTRIBUTE_FOR_NAME = "cn";
	private static final String ATTRIBUTE_FOR_MAIL = "mail";
	
	@Override
	public Utenti authenticate(String userName, String password) throws BusinessException, EmptyResultException {
		if (userName == null) userName = "";
		if (password == null) password = "";
		if (userName.length() == 0 || password.length() == 0) {
			throw new EmptyResultException(AppConstants.AUTH_EMPTY_CREDENTIALS);
		}
		boolean authenticated = false;
		
		Session ses = SessionFactory.getSession();
		Utenti utente = null;
		try {
			// You specify in the authenticate user the attributes that you want returned.
			// Some companies use standard attributes <like 'description' to hold an employee ID.
			Attributes att = null;
			try {
				att = authenticateLdapUser(userName, password,
						ServerConstants.LDAP_DOMAIN,
						ServerConstants.LDAP_HOST,
						ServerConstants.LDAP_BASE_DN);
			} catch (NamingException e) {
				LOG.debug(userName+" non presente in ldap");
				//throw new PagamentiException("Could not connect to the directory", e);
			}
			
			//Search on DB
			utente = utentiDao.findUtenteByUserName(ses, userName);
			if (utente == null) throw new EmptyResultException(AppConstants.AUTH_UNAUTHORIZED);
			//Se l'utente è nel DB verifica:
			//Password ldap corretta altrimenti password DB corretta
			if (att != null) {
				//è in ldap
				authenticated = true;
				String s = att.get(ATTRIBUTE_FOR_NAME).toString();
				s = s.substring(ATTRIBUTE_FOR_NAME.length()+2);
				utente.setDescrizione(s);
			} else {
				if (password != null) {
					boolean check = new UtentiPasswordDao().checkPassword(ses, utente.getId(), password);
					if (check) {
						//non è in ldap ma è su db con password corretta
						authenticated = true;
					}
				}
			}
			//Controllo sul ruolo
			if (utente.getRuolo().getId().intValue() == AppConstants.RUOLO_BLOCKED) {
				authenticated = false;
			}
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		
		//Risultato
		if (authenticated) {
			return utente;
		} else {
			throw new EmptyResultException(AppConstants.AUTH_UNAUTHORIZED);
		}
	}
	
	@Override
	public Utenti findUtenteByUserName(String userName) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		Utenti utente = null;
		try {
			utente = utentiDao.findUtenteByUserName(ses, userName);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (utente != null) {
			return utente;
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	public Attributes authenticateLdapUser(String userName, String password,
			String domain, String host, String baseDn) throws NamingException {
		String returnedAtts[] = { ATTRIBUTE_FOR_USER, ATTRIBUTE_FOR_NAME, ATTRIBUTE_FOR_MAIL };
		String searchFilter = "(&(objectClass=user)(" + ATTRIBUTE_FOR_USER
				+ "=" + userName + "))";
		Attributes result = null;
		// Create the search controls
		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningAttributes(returnedAtts);
		// Specify the search scope
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String searchBase = baseDn;
		Hashtable<String, String> environment = new Hashtable<String, String>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		// Using standard Port, check your instalation
		environment.put(Context.PROVIDER_URL, "ldap://" + host + ":389");
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, ServerConstants.LDAP_PRINCIPAL);//username + "@" + domain);
		environment.put(Context.SECURITY_CREDENTIALS, ServerConstants.LDAP_CREDENTIAL);//password);
		LdapContext ldapCtx = null;
		//Acquisisce il context
		ldapCtx = new InitialLdapContext(environment, null);
		//Ottiene il dn dell'utente cercato
		NamingEnumeration<SearchResult> answer = ldapCtx.search(searchBase,
				searchFilter, searchCtls);
		String dn = null;
		while (answer.hasMoreElements()) {
			SearchResult sr = answer.next();
			dn = sr.getName()+","+baseDn;
			result = sr.getAttributes();
			if (result == null) {
				throw new NamingException("Could not connect to directory server "+ host);
			}
		}
		ldapCtx.close();
		if (dn == null) {
			throw new NamingException("Could not connect to directory server "+ host);
		}
        // Authenticate
		environment.put(Context.SECURITY_AUTHENTICATION, "simple");
		environment.put(Context.SECURITY_PRINCIPAL, dn);
		environment.put(Context.SECURITY_CREDENTIALS, password);

        try {
			ldapCtx = new InitialLdapContext(environment, null);
			ldapCtx.close();
		} catch (NamingException e) {
			throw new NamingException("User "+ userName + " could not be found on the directory");
		}
        return result;
	}
	
	@Override
	public List<Utenti> findUtenti(boolean showBlocked, int offset, int size) throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Utenti> result = null;
		try {
			result = utentiDao.findUtenti(ses, showBlocked, offset, size);
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}
	
	@Override
	public String saveOrUpdate(Utenti item, String password) throws BusinessException {
		Session ses = SessionFactory.getSession();
		String idU = null;
		Transaction trx = ses.beginTransaction();
		try {
			Ruoli role = null;
			if (item.getIdRuoloT() == null) {
				if (item.getRuolo() != null) {
					item.setIdRuoloT(item.getRuolo().getId().toString());
				} else {
					item.setIdRuoloT("0");
				}
			}
			Integer id = Integer.valueOf(item.getIdRuoloT());
			role = GenericDao.findById(ses, Ruoli.class, id);
			Utenti oldItem = null;
			if (item.getId() != null) {
				oldItem = GenericDao.findById(ses, Utenti.class, item.getId());
			}
			if (oldItem != null) {
				//update
				oldItem.setRuolo(role);
				oldItem.setId(item.getNewId());
				oldItem.setDescrizione(item.getDescrizione());
				oldItem.setDataModifica(DateUtil.now());
				oldItem.setPeriodiciUidRestriction(item.getPeriodiciUidRestriction());
				utentiDao.update(ses, oldItem);
				idU = oldItem.getId();
			} else {
				//save
				item.setId(item.getNewId());
				item.setRuolo(role);
				idU = (String) utentiDao.save(ses, item);
			}
			new UtentiPasswordDao().addNewPassword(ses, idU, password);
			trx.commit();
		} catch (Exception e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return idU;
	}

	//@Override
	//public List<Utenti> delete(String id) throws PagamentiException, EmptyResultException {
	//	Session ses = SessionFactory.getSession();
	//	GenericDao baseDao = new GenericDao();
	//	Transaction trx = ses.beginTransaction();
	//	try {
	//		Utenti u = baseDao.findById(ses, Utenti.class, id);
	//		baseDao.delete(ses, id, u);
	//		trx.commit();
	//	} catch (HibernateException e) {
	//		trx.rollback();
	//		LOG.error(e.getMessage(), e);
	//		throw new PagamentiException(e.getMessage(), e);
	//	} finally {
	//		ses.close();
	//	}
	//	return findUtenti(0, ClientConstants.TABLE_ROWS_DEFAULT);
	//}
	
	@Override
	public List<Ruoli> findRuoli() throws BusinessException, EmptyResultException {
		Session ses = SessionFactory.getSession();
		List<Ruoli> result = null;
		try {
			result = GenericDao.findByClass(ses, Ruoli.class, "id");
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		if (result != null) {
			if (result.size() > 0) {
				return result;
			}
		}
		throw new EmptyResultException(AppConstants.MSG_EMPTY_RESULT);
	}

	@Override
	public Boolean sendHeartbeat(String idUtente) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			Utenti u = GenericDao.findById(ses, Utenti.class, idUtente);
			u.setHeartbeat(DateUtil.now());
			utentiDao.update(ses, u);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}

	@Override
	public Boolean addPassword(String idUtente, String password) throws BusinessException {
		Session ses = SessionFactory.getSession();
		Transaction trx = ses.beginTransaction();
		try {
			new UtentiPasswordDao().addNewPassword(ses, idUtente, password);
			trx.commit();
		} catch (HibernateException e) {
			trx.rollback();
			LOG.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
		return true;
	}
}
