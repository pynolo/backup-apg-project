# Operazioni di rimozione dati per OS
#SET FOREIGN_KEY_CHECKS=0;
truncate table log_editing;
truncate table log_ws;
truncate table rapporti;
truncate table avvisi;
truncate table rinnovi_massivi;
delete from fatture_stampe WHERE data_creazione <= '2017-01-01 00:01:01';
delete fatture_articoli.* FROM fatture_articoli INNER JOIN fatture WHERE fatture_articoli.id=fatture.id_fattura_stampa and fatture.id_periodico != 2;
delete from fatture where id_periodico != 2;
update periodici set data_fine = '2016-12-31' where data_fine is null and uid not like '%B%';
delete evasioni_articoli.* FROM evasioni_articoli INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE 
	evasioni_articoli.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete evasioni_comunicazioni.* FROM evasioni_comunicazioni INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE 
	evasioni_comunicazioni.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete evasioni_fascicoli.* FROM evasioni_fascicoli INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE 
	evasioni_fascicoli.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico = 1;
delete evasioni_fascicoli.* FROM evasioni_fascicoli INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE 
	evasioni_fascicoli.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico = 7;
delete evasioni_fascicoli.* FROM evasioni_fascicoli INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE 
	evasioni_fascicoli.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete pagamenti.* from pagamenti INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE
	pagamenti.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
update anagrafiche set giunti_card = false;
update anagrafiche ana, istanze_abbonamenti ia, abbonamenti abb set ana.giunti_card = true where 
	abb.id=ia.id_abbonamento and
	(ia.id_abbonato=ana.id or ia.id_pagante=ana.id or ia.id_promotore=ana.id) and
	abb.id_periodico = 2;
delete pagamenti.* from pagamenti INNER JOIN anagrafiche WHERE
	pagamenti.id_anagrafica=anagrafiche.id and
	anagrafiche.giunti_card = false;
delete pagamenti_crediti.* from pagamenti_crediti INNER JOIN anagrafiche WHERE
	pagamenti_crediti.id_anagrafica=anagrafiche.id and
	anagrafiche.giunti_card = false;
delete opzioni_istanze_abbonamenti.* FROM opzioni_istanze_abbonamenti INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE
	opzioni_istanze_abbonamenti.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete istanze_abbonamenti.* FROM istanze_abbonamenti INNER JOIN abbonamenti WHERE
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete abbonamenti.* FROM abbonamenti WHERE
	abbonamenti.id_periodico != 2;
delete evasioni_fascicoli.* FROM evasioni_fascicoli where 
	id_istanza_abbonamento not in (select id from istanze_abbonamenti);
delete evasioni_articoli.* FROM evasioni_articoli where
	id_anagrafica in (select id from anagrafiche where giunti_card = false);
delete ordini_logistica.* from ordini_logistica where
	id_anagrafica in (select id from anagrafiche where giunti_card = false);
delete anagrafiche.* from anagrafiche where giunti_card = false;
delete indirizzi.* from indirizzi INNER JOIN anagrafiche WHERE
	indirizzi.id=anagrafiche.id_indirizzo_principale and
	anagrafiche.giunti_card = false;
delete stat_abbonati.* from stat_abbonati where id_periodico != 2;
delete stat_invio.* from stat_invio where id_periodico != 2;
delete fascicoli.* from fascicoli where id_periodico != 2;
delete comunicazioni.* from comunicazioni where id_periodico != 2;
delete articoli_opzioni.* FROM articoli_opzioni INNER JOIN opzioni WHERE 
	articoli_opzioni.id_opzione=opzioni.id and
	opzioni.id_periodico != 2;
delete articoli_listini.* FROM articoli_listini INNER JOIN listini INNER JOIN tipi_abbonamento WHERE 
	articoli_listini.id_listino=listini.id and
	listini.id_tipo_abbonamento=tipi_abbonamento.id and
	tipi_abbonamento.id_periodico != 2;
delete opzioni_listini.* FROM opzioni_listini INNER JOIN opzioni WHERE 
	opzioni_listini.id_opzione=opzioni.id and
	opzioni.id_periodico != 2;
delete opzioni.* from opzioni where id_periodico != 2;
delete listini.* FROM listini INNER JOIN tipi_abbonamento WHERE 
	listini.id_tipo_abbonamento=tipi_abbonamento.id and
	tipi_abbonamento.id_periodico != 2;
delete tipi_abbonamento_rinnovo.* from tipi_abbonamento_rinnovo INNER JOIN tipi_abbonamento WHERE
	tipi_abbonamento_rinnovo.id=tipi_abbonamento.id and
	tipi_abbonamento.id_periodico != 2;
delete tipi_abbonamento.* from tipi_abbonamento where id_periodico != 2;
delete modelli_bollettini.* from modelli_bollettini where id_periodico != 2;
update utenti set id_ruolo = 0 where (periodici_uid_restriction is null or periodici_uid_restriction not like '%B%') and id not like 'admin';
#update utenti set periodici_uid_restriction = null where periodici_uid_restriction like '%B%';
update articoli set data_fine='2016-12-31' where data_fine is null and codice_meccanografico not like '%B';
delete periodici.* from periodici where id != 2;
update config set valore = 'P' where id = 'orderPrefix';
#SET FOREIGN_KEY_CHECKS=1;
CREATE TABLE `log_editing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entity_name` varchar(64) NOT NULL,
  `entity_id` int(11) NOT NULL,
  `id_utente` varchar(32) NOT NULL,
  `log_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `logEntityIdx` (`entity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;
CREATE TABLE `log_ws` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `service` varchar(32) NOT NULL,
  `operation` varchar(64) NOT NULL,
  `parameters` varchar(1024) NOT NULL,
  `result` varchar(256) NOT NULL,
  `log_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;
