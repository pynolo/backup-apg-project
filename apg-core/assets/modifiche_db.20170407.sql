# APG 3.0
ALTER TABLE `pagamenti` ADD COLUMN `id_fattura` int(11) DEFAULT NULL;
#istanze_abbonamenti e opzioni_istanze_abbonamenti hanno già id_fattura
ALTER TABLE `pagamenti` ADD COLUMN `importo_resto` decimal(9,2) NOT NULL DEFAULT 0;
ALTER TABLE `pagamenti` ADD COLUMN `id_societa` varchar(4) NOT NULL;
update pagamenti p, istanze_abbonamenti ia set p.id_fattura = ia.id_fattura where ia.id = p.id_istanza_abbonamento;
update pagamenti p, fatture f set p.id_fattura = f.id where f.id_istanza = p.id_istanza_abbonamento;
update pagamenti p, istanze_abbonamenti ia set p.id_anagrafica = ia.id_abbonato where ia.id = p.id_istanza_abbonamento;
update pagamenti p, istanze_abbonamenti ia set p.id_anagrafica = ia.id_pagante where ia.id = p.id_istanza_abbonamento and ia.id_pagante is not null;
update `pagamenti` set id_societa = 'GS';
INSERT INTO societa (id,nome,testo_fattura_1,testo_fattura_2,codice_fiscale,partita_iva,prefisso_fatture,codice_societa,prefisso_fatture_cartacee) 
	VALUES ('OS','Giunti O.S. Psychometrics S.r.l.','Via Fra Paolo Sarpi, 7/A - 50136 Firenze\nC.C.I.A.A. 119169 - TRIB.FIRENZE 6444\nTEL. (+39) 055 6236501 - FAX (+39) 055 669446\nE-mail: amministrazione@giuntios.it','','00421250481','00421250481','FXY','035',null);
update societa set prefisso_fatture='FXS' where id='GS';
update societa set prefisso_fatture='FXE' where id='GE';
update `periodici` set id_societa = 'OS' where (uid like 'B');
update `pagamenti` set id_societa = 'GE' where 
	(codice_abbonamento_match like 'W%' or codice_abbonamento_match like 'Q%');
update `pagamenti` set id_societa = 'OS' where 
	(codice_abbonamento_match like 'B%');
INSERT INTO config (id,valore) VALUES ('ftpFxyDir','GOS_RIVISTE');
INSERT INTO config (id,valore) VALUES ('ftpFxyHost','posap01.sap.intranet.giunti.it');
INSERT INTO config (id,valore) VALUES ('ftpFxyPassword','rivistegge');
INSERT INTO config (id,valore) VALUES ('ftpFxyPort','8099');
INSERT INTO config (id,valore) VALUES ('ftpFxyUsername','rivistegge');
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `listino_cambiato` bit(1) NOT NULL DEFAULT false;
CREATE INDEX FattureNumeroRimborsoCollegatoIdx ON fatture (numero_rimborso_collegato);
CREATE INDEX PagamentiFattureIdx ON pagamenti (id_fattura);
alter table `anagrafiche` drop COLUMN `cognome_ragione_sociale_bak`;
alter table `anagrafiche` drop COLUMN `nome_bak`;
alter table `anagrafiche` drop COLUMN `titolo_bak`;
DROP TABLE IF EXISTS `feedback_anagrafiche`;
DROP TABLE IF EXISTS `keyword_istanze_abbonamenti`;
DROP TABLE IF EXISTS `keyword`;
DROP TABLE IF EXISTS `pubblicazioni_spedite`;
DROP TABLE IF EXISTS `pubblicazioni`;
#ALTER TABLE societa DROP COLUMN prefisso_fatture_cartacee;
# APG 4.0
DROP TABLE IF EXISTS `pagamenti_crediti`;
CREATE TABLE `pagamenti_crediti` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_anagrafica` int(11) NOT NULL,
  `id_istanza_abbonamento` int(11) DEFAULT NULL,
  `id_fattura_origine` int(11) NOT NULL,
  `id_fattura_impiego` int(11) DEFAULT NULL,
  `id_societa` varchar(4) NOT NULL,
  `importo` decimal(9,2) NOT NULL,
  `data_creazione` date NOT NULL,
  `data_modifica` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `note` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE pagamenti DROP COLUMN importo_resto;
ALTER TABLE `fatture` DROP COLUMN `numero_rimborso_collegato`;
ALTER TABLE `fatture` ADD COLUMN `importo_resto` decimal(9,2) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_nota_credito_resto` int(11) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_nota_credito_storno` int(11) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_nota_credito_rimborso` int(11) DEFAULT NULL;
update fatture ndc, fatture fatt set fatt.id_nota_credito_rimborso = ndc.id 
	where ndc.id_fattura_rimborsata = fatt.id;
ALTER TABLE `fatture` CHANGE COLUMN `id_fattura_rimborsata` `id_fattura_rimborsata_bak` int(11) DEFAULT NULL;
ALTER TABLE `fatture_articoli` ADD COLUMN `is_resto` bit(1) NOT NULL;
delete from contatori where ckey like '%2017';
delete from fatture where (numero_fattura like 'FXY7%' or numero_fattura like 'FXS7%' or numero_fattura like 'FPS7%');
update pagamenti as p set p.id_fattura = null where 
	p.data_creazione > '2017-01-01 00:00:00' and p.id_fattura is not null;
alter table `istanze_abbonamenti` drop COLUMN `listino_cambiato`;
ALTER TABLE `pagamenti_crediti` ADD COLUMN `stornato_da_origine` bit(1) NOT NULL;
ALTER TABLE `fatture_articoli` ADD COLUMN `is_iva_scorporata` bit(1) NOT NULL;
INSERT INTO config (id,valore) VALUES ('ftpGeDir','.');
INSERT INTO config (id,valore) VALUES ('ftpGeHost','ftp.giunti.it');
INSERT INTO config (id,valore) VALUES ('ftpGePassword','Rwapg2014');
INSERT INTO config (id,valore) VALUES ('ftpGePort','21');
INSERT INTO config (id,valore) VALUES ('ftpGeUsername','apg-rw');
INSERT INTO config (id,valore) VALUES ('ftpGsDir','.');
INSERT INTO config (id,valore) VALUES ('ftpGsHost','ftp.giunti.it');
INSERT INTO config (id,valore) VALUES ('ftpGsPassword','Rwapg2014');
INSERT INTO config (id,valore) VALUES ('ftpGsPort','21');
INSERT INTO config (id,valore) VALUES ('ftpGsUsername','apg-rw');
INSERT INTO config (id,valore) VALUES ('ftpOsDir','.');
INSERT INTO config (id,valore) VALUES ('ftpOsHost','ftp.giunti.it');
INSERT INTO config (id,valore) VALUES ('ftpOsPassword','apg95rw');
INSERT INTO config (id,valore) VALUES ('ftpOsPort','21');
INSERT INTO config (id,valore) VALUES ('ftpOsUsername','apgos-rw');
ALTER TABLE `pagamenti` ADD COLUMN `trn` varchar(128) DEFAULT NULL;
update `pagamenti` set id_fattura = -1 where id_fattura is null and data_accredito <= '2016-01-01';
ALTER TABLE opzioni drop column codice_interno;
update ruoli set descrizione='Assistenza tecnica' where id=4;
update utenti set descrizione='Sito web' where id='api';
ALTER TABLE `fatture` CHANGE COLUMN `id_nota_credito_resto` `id_nota_credito_storno_resto` int(11) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_nota_credito_rimborso_resto` int(11) DEFAULT NULL;
INSERT INTO config (id,valore) VALUES ('orderPrefix','');

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
	abbonamenti.id_periodico != 2;
delete pagamenti.* from pagamenti INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE
	pagamenti.id_istanza_abbonamento=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
update anagrafiche set giunti_card = false;
update anagrafiche ana, istanze_abbonamenti ia, abbonamenti abb set ana.giunti_card = true where 
	abb.id=ia.id_abbonamento and
	(ia.id_abbonato=ana.id or ia.id_pagante=ana.id or ia.id_abbonato=ana.id) and
	abb.id_periodico = 2;
delete pagamenti.* from pagamenti INNER JOIN anagrafiche WHERE
	pagamenti.id_anagrafica=anagrafiche.id and
	anagrafiche.giunti_card = false;
delete pagamenti_crediti.* from pagamenti_crediti INNER JOIN anagrafiche WHERE
	pagamenti_crediti.id_anagrafica=anagrafiche.id and
	anagrafiche.giunti_card = false;
delete opzioni_istanze_abbonamenti.* FROM opzioni_istanze_abbonamenti INNER JOIN istanze_abbonamenti INNER JOIN abbonamenti WHERE
	opzioni_istanze_abbonamenti.id_istanza=istanze_abbonamenti.id and
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete istanze_abbonamenti.* FROM istanze_abbonamenti INNER JOIN abbonamenti WHERE
	istanze_abbonamenti.id_abbonamento=abbonamenti.id and
	abbonamenti.id_periodico != 2;
delete abbonamenti.* FROM abbonamenti WHERE
	abbonamenti.id_periodico != 2;
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
update config set valore = 'O' where id = 'orderPrefix';
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
