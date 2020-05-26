package it.giunti.apg.updater;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import it.giunti.apg.core.persistence.MaterialiDao;
import it.giunti.apg.core.persistence.MaterialiProgrammazioneDao;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;
import it.giunti.apg.shared.model.Articoli;
import it.giunti.apg.shared.model.Fascicoli;
import it.giunti.apg.shared.model.Materiali;
import it.giunti.apg.shared.model.MaterialiProgrammazione;

public class MigrationTo7 {

	public static void run() {
		Session ses = SessionFactory.getSession();
		Transaction trn = ses.beginTransaction();
		MaterialiDao matDao = new MaterialiDao();
		MaterialiProgrammazioneDao matProgDao = new MaterialiProgrammazioneDao();
		Integer offset = 0;
		try {
			// 1: fascicoli -> materiali
			//    articoli -> materiali
			// 2: fascicoli -> materiali_programmazione
			//    evasioni_comunicazioni.id_fascicolo -> .id_materiale_programmazione
			// 3: evasioni_fascicoli -> materiali_spedizione
			//    evasioni_articoli -> materiali_spedizione
			// 4: istanze_abbonamenti date based
			
			// FASE 1.1 - i fascicoli diventano materiali e materiali_programmazione
			Map<Integer,Materiali> fasMatMap = new HashMap<Integer, Materiali>();
			Map<Integer,MaterialiProgrammazione> fasMatProgMap = new HashMap<Integer, MaterialiProgrammazione>();
			String hql = "from Fascicoli f order by f.id";
			Query q = ses.createQuery(hql);
			List<Fascicoli> fasList = q.list();
			for (Fascicoli f:fasList) {
				Materiali mat = toMateriale(f);
				matDao.save(ses, mat);
				fasMatMap.put(f.getId(), mat);
				MaterialiProgrammazione matProg = toMaterialeProgrammazione(f, mat);
				matProgDao.save(ses, matProg);
				fasMatProgMap.put(f.getId(), matProg);
			}
			// FASE 1.2 - gli articoli diventano materiali
			Map<Integer,Materiali> artMatMap = new HashMap<Integer, Materiali>();
			hql = "from Articoli a order by a.id";
			q = ses.createQuery(hql);
			List<Articoli> artList = q.list();
			for (Articoli a:artList) {
				Materiali mat = toMateriale(a);
				matDao.save(ses, mat);
				artMatMap.put(a.getId(), mat);
			}
						
		
			// FASE 2.1 - i fascicoli diventano materiali_programmazione
			
			hql = "from Fascicoli f order by f.id";
			q = ses.createQuery(hql);
			List<Fascicoli> fasList = q.list();
			for (Fascicoli f:fasList) {
				Materiali mat = toMateriale(f);
				matDao.save(ses, mat);
				fasMatProgMap.put(f.getId(), mat);
			}
			
				offset += aList.size();
				Double perc = 100*(offset.doubleValue()/totalAnag.doubleValue());
				System.out.println("Aggiornate "+offset+" anagrafiche ("+df.format(perc)+"%) "+
						"fine stimata "+stimaFine(dtStart, offset, totalAnag));
				ses.flush();
				ses.clear();
				trn.commit();
				trn = ses.beginTransaction();
			} while (aList.size() == PAGE_SIZE);
			rw.close();
		} catch (HibernateException e) {
			trn.rollback();
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}

	private static Materiali toMateriale(Fascicoli item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(item.getNote());
		mat.setSottotitolo(item.getDataCop());
		mat.setTitolo(item.getTitoloNumero());
		return mat;
	}
	
	private static Materiali toMateriale(Articoli item) {
		Materiali mat = new Materiali();
		mat.setCodiceMeccanografico(item.getCodiceMeccanografico());
		mat.setIdTipoAnagraficaSap(item.getIdTipoAnagraficaSap());
		mat.setInAttesa(item.getInAttesa());
		mat.setNote(null);
		mat.setSottotitolo(item.getCodiceInterno());
		mat.setTitolo(item.getTitoloNumero());
		return mat;
	}
	
	private static MaterialiProgrammazione toMaterialeProgrammazione(Fascicoli item, Materiali mat) {
		MaterialiProgrammazione matProg = new MaterialiProgrammazione();
		matProg.setComunicazioniInviate(item.getComunicazioniInviate());
		matProg.setDataEstrazione(item.getDataEstrazione());
		matProg.setDataNominale(item.getDataInizio());
		matProg.setMateriale(mat);
		matProg.setOpzione(item.getOpzione());
		matProg.setPeriodico(item.getPeriodico());
		return matProg;
	}
}
	
