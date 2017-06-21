package it.giunti.apg.core;

import it.giunti.apg.shared.model.ArticoliListini;
import it.giunti.apg.shared.model.EvasioniComunicazioni;
import it.giunti.apg.shared.model.IstanzeAbbonamenti;
import it.giunti.apg.shared.model.Listini;
import it.giunti.apg.shared.model.OpzioniIstanzeAbbonamenti;
import it.giunti.apg.shared.model.OpzioniListini;
import it.giunti.apg.shared.model.Pagamenti;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SerializationUtil {

	public static <T> List<T> makeSerializable(List<T> entityList) {
		if (entityList != null) {
			for (int i = 0; i<entityList.size(); i++) {
				T be = entityList.get(i);
				if (be instanceof ArticoliListini) {
					makeSerializable((ArticoliListini)be);
				}
				if (be instanceof EvasioniComunicazioni) {
					makeSerializable((EvasioniComunicazioni)be);
				}
				if (be instanceof IstanzeAbbonamenti) {
					makeSerializable((IstanzeAbbonamenti)be);
				}
				if (be instanceof Listini) {
					makeSerializable((Listini)be);
				}
				if (be instanceof Pagamenti) {
					makeSerializable((Pagamenti)be);
				}
			}
		}
		return entityList;
	}
	
	public static EvasioniComunicazioni makeSerializable(EvasioniComunicazioni entity) {
		if (entity != null) {
			makeSerializable(entity.getIstanzaAbbonamento());
		}
		return entity;
	}
	
	public static IstanzeAbbonamenti makeSerializable(IstanzeAbbonamenti entity) {
		if (entity != null) {
			if (entity.getOpzioniIstanzeAbbonamentiSet() != null) {
				Set<OpzioniIstanzeAbbonamenti> persistedSet = entity.getOpzioniIstanzeAbbonamentiSet();
				Set<OpzioniIstanzeAbbonamenti> serializableSet = new HashSet<OpzioniIstanzeAbbonamenti>();
				serializableSet.addAll(persistedSet);
				entity.setOpzioniIstanzeAbbonamentiSet(serializableSet);
			}
			makeSerializable(entity.getListino());
		}
		return entity;
	}
	
	public static Listini makeSerializable(Listini entity) {
		if (entity != null) {
			if (entity.getArticoliListiniSet() != null) {
				Set<ArticoliListini> persistedSet = entity.getArticoliListiniSet();
				Set<ArticoliListini> serializableSet = new HashSet<ArticoliListini>();
				serializableSet.addAll(persistedSet);
				entity.setArticoliListinoSet(serializableSet);
			}
			if (entity.getOpzioniListiniSet() != null) {
				Set<OpzioniListini> persistedSet = entity.getOpzioniListiniSet();
				Set<OpzioniListini> serializableSet = new HashSet<OpzioniListini>();
				serializableSet.addAll(persistedSet);
				entity.setOpzioniListiniSet(serializableSet);
			}
		}
		return entity;
	}
	
	public static Pagamenti makeSerializable(Pagamenti entity) {
		if (entity != null) {
			makeSerializable(entity.getIstanzaAbbonamento());
		}
		return entity;
	}
	
	public static ArticoliListini makeSerializable(ArticoliListini entity) {
		if (entity != null) {
			makeSerializable(entity.getListino());
		}
		return entity;
	}


}
