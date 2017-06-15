package it.giunti.apg.server;

import it.giunti.apg.server.persistence.OpzioniDao;
import it.giunti.apg.server.persistence.OpzioniIstanzeAbbonamentiDao;
import it.giunti.apg.server.persistence.OpzioniListiniDao;
import it.giunti.apg.shared.AbstractOpzioniUtil;
import it.giunti.apg.shared.AppConstants;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.ValidationException;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.Opzioni;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

public class OpzioniUtil extends AbstractOpzioniUtil {
	//private static final long serialVersionUID = 3675026682668787432L;
	//private static final Logger LOG = LoggerFactory.getLogger(AvvisiBusiness.class);
	
	private static final DecimalFormat DF = new DecimalFormat(AppConstants.SUPPL_ID_FORMAT);
	
	public OpzioniUtil(String s) {
		super(s);
	}
	
	public OpzioniUtil(List<Opzioni> opzList) {
		super(opzList);
	}
	
	public OpzioniUtil(Set<Integer> opzIdArray) {
		super(opzIdArray);
	}
	
	@Override
	public String opzListToString(List<Opzioni> opzList) {
		String result = "";
		if (opzList != null) {
			for (Opzioni opz:opzList) {
				result += DF.format(opz.getId())+AppConstants.SUPPL_SEPARATOR;
			}
			//Se esiste un elenco opzioni rimuove l'ultimo SEPARATOR
			if (result.length()>0) {
				result = result.substring(0, result.length()-1);
			}
		}
		return result;
	}
	
	public static boolean hasOpzione(Session ses, IstanzeAbbonamenti ia, Opzioni opz) {
		return OpzioniDao.hasOpzione(ses, ia, opz);
	}
	
	public static void addOpzioniObbligatorie(Session ses, IstanzeAbbonamenti ia, boolean isTransientIstance)
			throws BusinessException {
		Listini lst = ia.getListino();
		if (lst.getOpzioniListiniSet() != null) {
			if (lst.getOpzioniListiniSet().size() > 0) {
				Set<Opzioni> opzSet = new HashSet<Opzioni>();
				for(OpzioniListini ol:lst.getOpzioniListiniSet()) opzSet.add(ol.getOpzione());
				if (opzSet != null) {
					if (opzSet.size() > 0) {
						replaceOpzioni(ses, ia, opzSet, isTransientIstance);
					}
				}
			}
		}
	}
	
//	public static void addOpzione(Session ses, IstanzeAbbonamenti ia, Opzioni opz, boolean isTransientIstance)
//			throws BusinessException {
//		if (opz == null) throw new BusinessException("Impossibile aggiungere un'Opzione null");
//		//Verifica se già esiste
//		if (!hasOpzione(ses, ia, opz)) {
//			//Se non esiste la aggiunge
//			OpzioniIstanzeAbbonamenti oia = new OpzioniIstanzeAbbonamenti();
//			oia.setOpzione(opz);
//			oia.setIstanza(ia);
//			oia.setIdFattura(null);
//			if (!isTransientIstance) {
//				Set<OpzioniIstanzeAbbonamenti> set = ia.getOpzioniIstanzeAbbonamentiSet();
//				if (set == null) set = new HashSet<OpzioniIstanzeAbbonamenti>();
//				set.add(oia);
//				new OpzioniIstanzeAbbonamentiDao().save(ses, oia);
//			}
//		}
//	}
	
//	public static void addOpzioniSet(Session ses, IstanzeAbbonamenti ia, Set<Opzioni> opzSet, boolean transientIstanza)
//			throws BusinessException {
//		for (Opzioni opz:opzSet) {
//			addOpzione(ses, ia, opz, transientIstanza);
//		}
//	}
	
//	public static void removeAllOpzioni(Session ses, IstanzeAbbonamenti ia, boolean isTransientIstance)
//			throws BusinessException {
//		if (ia.getOpzioniIstanzeAbbonamentiSet() == null) ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
//		Set<OpzioniIstanzeAbbonamenti> oldSet = new HashSet<OpzioniIstanzeAbbonamenti>();
//		oldSet.addAll(ia.getOpzioniIstanzeAbbonamentiSet());
//		for (OpzioniIstanzeAbbonamenti oia:oldSet) {
//			//if (oia.getIdFattura() == null) {
//				ia.getOpzioniIstanzeAbbonamentiSet().remove(oia);
//				new OpzioniIstanzeAbbonamentiDao().delete(ses, oia);
//			//}
//		}
//		LOG.debug("old opzioni: "+oldSet.size()+" new: "+ia.getOpzioniIstanzeAbbonamentiSet().size());
//	}
	
//	public static void removeOpzione(Session ses, IstanzeAbbonamenti ia, Opzioni opz, boolean isTransientIstance)
//			throws BusinessException {
//		if (opz == null) throw new BusinessException("Impossibile rimuovere un'Opzione null");
//		if (ia.getOpzioniIstanzeAbbonamentiSet() == null) ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
//		Set<OpzioniIstanzeAbbonamenti> tempSet = new HashSet<OpzioniIstanzeAbbonamenti>();
//		tempSet.addAll(ia.getOpzioniIstanzeAbbonamentiSet());
//		for (OpzioniIstanzeAbbonamenti oia:tempSet) {
//			if (oia.getOpzione().getId() == opz.getId()) {
//				if (oia.getIdFattura() == null) {
//					if (!isTransientIstance) {
//						ia.getOpzioniIstanzeAbbonamentiSet().remove(oia);
//						new OpzioniIstanzeAbbonamentiDao().delete(ses, oia);
//					}
//				} else {
//					//throw new ValidationException("Impossibile rimuovere un'opzione gia' fatturata");
//				}
//			}
//		}
//	}
	
	public static void replaceOpzioni(Session ses, IstanzeAbbonamenti ia, Set<Opzioni> opzSet, boolean isTransientIstance)
			throws BusinessException {
		if (ia.getOpzioniIstanzeAbbonamentiSet() == null) ia.setOpzioniIstanzeAbbonamentiSet(new HashSet<OpzioniIstanzeAbbonamenti>());
		Set<OpzioniIstanzeAbbonamenti> newSet = new HashSet<OpzioniIstanzeAbbonamenti>();
		//Copy all old oia to the new set
		for (OpzioniIstanzeAbbonamenti iaOia:ia.getOpzioniIstanzeAbbonamentiSet()) {
			for (Opzioni opz:opzSet) {
				if (iaOia.getOpzione().getId().equals(opz.getId())) newSet.add(iaOia);
			}
		}
		//Add all missing options to the new set
		for (Opzioni opz:opzSet) {
			boolean found = false;
			for (OpzioniIstanzeAbbonamenti iaOia:ia.getOpzioniIstanzeAbbonamentiSet()) {
				if (iaOia.getOpzione().getId().equals(opz.getId())) found = true;
			}
			if (!found) {
				OpzioniIstanzeAbbonamenti oia = new OpzioniIstanzeAbbonamenti();
				oia.setOpzione(opz);
				oia.setIstanza(ia);
				oia.setIdFattura(null);
				newSet.add(oia);
				if (!isTransientIstance) {
					new OpzioniIstanzeAbbonamentiDao().save(ses, oia);
				}
			}
		}
		//Delete options not present in opzSet
		Set<OpzioniIstanzeAbbonamenti> tempSet = new HashSet<OpzioniIstanzeAbbonamenti>();
		tempSet.addAll(ia.getOpzioniIstanzeAbbonamentiSet());
		for (OpzioniIstanzeAbbonamenti iaOia:tempSet) {
			boolean found = false;
			for (Opzioni opz:opzSet) {
				if (iaOia.getOpzione().getId().equals(opz.getId())) found = true;
			}
			if (!found) {
				new OpzioniIstanzeAbbonamentiDao().delete(ses, iaOia);
			}
		}
		ia.getOpzioniIstanzeAbbonamentiSet().clear();
		ia.getOpzioniIstanzeAbbonamentiSet().addAll(newSet);
	}
	
	
	
	// Opzioni Listini
	
	
	
	public static void replaceOpzioni(Session ses, Listini lsn, Set<Opzioni> opzSet, boolean isTransientListino)
			throws BusinessException, ValidationException {
		List<Opzioni> allOpz = new ArrayList<Opzioni>();
		if (lsn.getOpzioniListiniSet() != null) {
			for (OpzioniListini ol:lsn.getOpzioniListiniSet()) {
				allOpz.add(ol.getOpzione());
			}
		}
		if (opzSet != null)	allOpz.addAll(opzSet);
		for (Opzioni opz:allOpz) {
			if (opzSet.contains(opz)) {
				addOpzione(ses, lsn, opz, isTransientListino);
			} else {
				removeOpzione(ses, lsn, opz, isTransientListino);
			}
		}
	}
	
	public static void addOpzione(Session ses, Listini lsn, Opzioni opz, boolean isTransientListino)
			throws BusinessException {
		if (opz == null) throw new BusinessException("Impossibile aggiungere un'Opzione null");
		//Verifica se già esiste
		if (!hasOpzione(ses, lsn, opz)) {
			//Se non esiste la aggiunge
			OpzioniListini ol = new OpzioniListini();
			ol.setOpzione(opz);
			ol.setListino(lsn);
			if (isTransientListino) {
				if (lsn.getOpzioniListiniSet() == null) lsn.setOpzioniListiniSet(new HashSet<OpzioniListini>());
				lsn.getOpzioniListiniSet().add(ol);
			} else {
				new OpzioniListiniDao().save(ses, ol);
			}
		}
	}
	
	public static void removeOpzione(Session ses, Listini lsn, Opzioni opz, boolean isTransientListino)
			throws BusinessException, ValidationException {
		if (opz == null) throw new BusinessException("Impossibile rimuovere un'Opzione null");
		if (lsn.getOpzioniListiniSet() == null) lsn.setOpzioniListiniSet(new HashSet<OpzioniListini>());
		Set<OpzioniListini> tempSet = new HashSet<OpzioniListini>();
		tempSet.addAll(lsn.getOpzioniListiniSet());
		for (OpzioniListini ol:tempSet) {
			if (ol.getOpzione().getId() == opz.getId()) {
				if (lsn.getOpzioniListiniSet() == null) lsn.setOpzioniListiniSet(new HashSet<OpzioniListini>());
				lsn.getOpzioniListiniSet().remove(ol);
				if (!isTransientListino) {
					new OpzioniListiniDao().delete(ses, ol);
				}
			}
		}
	}
	
	public static boolean hasOpzione(Session ses, Listini lsn, Opzioni opz) {
		return OpzioniDao.hasOpzione(ses, lsn, opz);
	}
}
