ALTER TABLE `fascicoli` ADD COLUMN `comunicazioni_inviate` bit NOT NULL DEFAULT FALSE;
update fascicoli set comunicazioni_inviate=true where data_nominale<='2012-08-13';
ALTER TABLE `evasioni_comunicazioni` MODIFY COLUMN `data_estrazione` datetime NULL;
ALTER TABLE `evasioni_comunicazioni` MODIFY COLUMN `data_creazione` datetime NULL;

***

ALTER TABLE `opzioni` ADD COLUMN `tag` varchar(16) NULL;
ALTER TABLE `opzioni` MODIFY COLUMN `codice_opzione` int NOT NULL;
ALTER TABLE `opzioni` CHANGE COLUMN `codice_opzione` `codice_num` int NOT NULL;
ALTER TABLE `comunicazioni` ADD COLUMN `id_opzione` int(11) NULL;
- !!!!!!!!!!*ATTENZIONE* MODIFICARE I VALORI 'CODICE_INTERNO' NELLA TABELLA SUPPLEMENTI!!!!!!!!!!!!!!!!
- Tabella Comunicazioni: Impostare a true "solo più istanze" se "prima di giugno" e "solo una istanza" se "dopo giugno".

***

ALTER TABLE `comunicazioni` MODIFY COLUMN `id_opzione` varchar(16) NULL;
ALTER TABLE `comunicazioni` CHANGE COLUMN `id_opzione` `tag_opzione` varchar(16) NULL;

***

DROP TABLE IF EXISTS `ws_log`;
CREATE TABLE `ws_log` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`service` varchar(32) NOT NULL,
	`operation` varchar(64) NOT NULL,
	`parameters` varchar(1024) NOT NULL,
	`result` varchar(256) NOT NULL,
	`log_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

***

ALTER TABLE `tipi_abbonamento` ADD COLUMN `tag` varchar(256) NULL;
ALTER TABLE `opzioni` MODIFY COLUMN `tag` varchar(256) NULL;
ALTER TABLE `periodici` ADD COLUMN `tag` varchar(256) NULL;
ALTER TABLE `evasioni_doni` ADD COLUMN `id_tipo_destinatario` varchar(4) NULL;

***

ALTER TABLE `tipi_abbonamento_listino` ADD COLUMN `id_dono` int(11) NULL;
ALTER TABLE `tipi_abbonamento_listino` ADD COLUMN `id_tipo_destinatario_dono` varchar(4) NULL;
UPDATE evasioni_doni set id_tipo_destinatario='BEN' where per_promotore=false;
UPDATE evasioni_doni set id_tipo_destinatario='PRM' where per_promotore=true;
UPDATE tipi_abbonamento_listino set id_tipo_destinatario_dono='BEN' where id_dono_abbonato is not null;
UPDATE tipi_abbonamento_listino set id_tipo_destinatario_dono='PRM' where id_dono_promotore is not null;
UPDATE tipi_abbonamento_listino set id_dono=id_dono_abbonato where id_dono_abbonato is not null;
UPDATE tipi_abbonamento_listino set id_dono=id_dono_promotore where id_dono_abbonato is not null;

***

ALTER TABLE `istanze_abbonamenti` ADD COLUMN `data_cambio_tipo` date DEFAULT NULL;
*
UPDATE istanze_abbonamenti set data_cambio_tipo=data_creazione where data_cambio_tipo is null;
ALTER TABLE `evasioni_doni` DROP COLUMN `per_promotore`;
ALTER TABLE `tipi_abbonamento_listino` DROP COLUMN `id_dono_abbonato`;
ALTER TABLE `tipi_abbonamento_listino` DROP COLUMN `id_dono_promotore`;

***

update fascicoli set fascicoli_accorpati=0 where id_opzione is not null;
ALTER TABLE `evasioni_doni` ADD COLUMN `copie` int(11) NOT NULL;
ALTER TABLE `evasioni_doni` ADD COLUMN `data_ordine` datetime NULL;
ALTER TABLE `evasioni_doni` CHANGE COLUMN `data_estrazione` `data_invio` datetime NULL;
ALTER TABLE `evasioni_doni` ADD COLUMN `id_abbonamento` int(11) NULL, 
	ADD COLUMN `id_anagrafica` int(11) NOT NULL;
ALTER TABLE `evasioni_doni` MODIFY COLUMN `id_istanza_abbonamento` int(11) NULL;
ALTER TABLE `evasioni_fascicoli` CHANGE COLUMN `quantita` `copie` int(11) NOT NULL;
ALTER TABLE `evasioni_fascicoli` ADD COLUMN `data_ordine` datetime NULL;
ALTER TABLE `evasioni_fascicoli` CHANGE COLUMN `data_stampa` `data_invio` datetime NULL;
ALTER TABLE `evasioni_fascicoli` ADD COLUMN `id_anagrafica` int(11) NOT NULL;
ALTER TABLE `evasioni_fascicoli` MODIFY COLUMN `id_abbonamento` int(11) NULL;

DROP TABLE IF EXISTS `edit_log`;
CREATE TABLE `edit_log` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`entity_name` varchar(64) NOT NULL,
	`entity_id` int(11) NOT NULL,
	`id_utente` varchar(32) NOT NULL,
	`log_datetime` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'IstanzeAbbonamenti', ia.id, ia.id_utente, ia.data_modifica from istanze_abbonamenti ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'Abbonamenti', ia.id, ia.id_utente, ia.data_modifica from abbonamenti ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'Anagrafiche', ia.id, ia.id_utente, ia.data_modifica from anagrafiche ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'Comunicazioni', ia.id, ia.id_utente, ia.data_modifica from comunicazioni ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'Doni', ia.id, ia.id_utente, ia.data_modifica from doni ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'Pagamenti', ia.id, ia.id_utente, ia.data_modifica from pagamenti ia
	where ia.data_modifica is not null;
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'TipiAbbonamento', ia.id, ia.id_utente, ia.data_modifica from tipi_abbonamento ia
	where ia.data_modifica is not null;	
insert into edit_log (entity_name, entity_id, id_utente, log_datetime)
	select 'TipiAbbonamentoListino', ia.id, ia.id_utente, ia.data_modifica from tipi_abbonamento_listino ia
	where ia.data_modifica is not null;

update evasioni_fascicoli as ef 
	inner join istanze_abbonamenti as ia on ia.id = ef.id_istanza_abbonamento
	set ef.copie = ia.copie where (ef.copie is null or ef.copie < 1);
update evasioni_fascicoli as ef 
	inner join istanze_abbonamenti as ia on ia.id = ef.id_istanza_abbonamento
	set ef.id_anagrafica = ia.id_abbonato;
update evasioni_fascicoli as ef 
	inner join istanze_abbonamenti as ia on ia.id = ef.id_istanza_abbonamento
	set ef.id_abbonamento = ia.id_abbonamento;
update evasioni_doni as ed 
	inner join istanze_abbonamenti as ia on ia.id = ed.id_istanza_abbonamento
	set ed.copie = ia.copie;
update evasioni_doni as ed 
	inner join istanze_abbonamenti as ia on ia.id = ed.id_istanza_abbonamento
	set ed.id_abbonamento = ia.id_abbonamento;
update evasioni_doni as ed 
	inner join istanze_abbonamenti as ia on ia.id = ed.id_istanza_abbonamento
	set ed.id_anagrafica = ia.id_abbonato where ed.id_tipo_destinatario like 'BEN';
update evasioni_doni as ed 
	inner join istanze_abbonamenti as ia on ia.id = ed.id_istanza_abbonamento
	set ed.id_anagrafica = ia.id_promotore where ed.id_tipo_destinatario like 'PRM' and ia.id_promotore is not null;
update evasioni_doni as ed 
	inner join istanze_abbonamenti as ia on ia.id = ed.id_istanza_abbonamento
	set ed.id_anagrafica = ia.id_pagante where ed.id_tipo_destinatario like 'PAG' and ia.id_pagante is not null;

***

DROP TABLE IF EXISTS `ordini_logistica`;
CREATE TABLE `ordini_logistica` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`numero_ordine` varchar(32) NOT NULL,
	`id_anagrafica` int(11) NOT NULL,
	`data_inserimento` datetime NULL,
	`data_conferma_invio` datetime NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `doni` ADD COLUMN `id_tipo_articolo` varchar(4) NOT NULL default 'LIB'; 
ALTER TABLE `evasioni_doni` ADD COLUMN `id_ordine_logistica` int(11) NULL;
ALTER TABLE `fascicoli` ADD COLUMN `id_tipo_articolo` varchar(4) NOT NULL default 'FAS'; 
ALTER TABLE `evasioni_fascicoli` ADD COLUMN `id_ordine_logistica` int(11) NULL;
ALTER TABLE `cursori` CHANGE COLUMN `id_periodico` `ckey` varchar(64) NOT NULL;
UPDATE `cursori` SET ckey='NDD' where ckey = '-1';
UPDATE `cursori` SET ckey='PERIODICO_1' where ckey = '1';
UPDATE `cursori` SET ckey='PERIODICO_2' where ckey = '2';
UPDATE `cursori` SET ckey='PERIODICO_3' where ckey = '3';
UPDATE `cursori` SET ckey='PERIODICO_4' where ckey = '4';
UPDATE `cursori` SET ckey='PERIODICO_6' where ckey = '6';
UPDATE `cursori` SET ckey='PERIODICO_7' where ckey = '7';
UPDATE `cursori` SET ckey='PERIODICO_8' where ckey = '8';
UPDATE `cursori` SET ckey='PERIODICO_9' where ckey = '9';
UPDATE `cursori` SET ckey='PERIODICO_10' where ckey = '10';

***

ALTER TABLE ordini_logistica ADD CONSTRAINT codiceUnico UNIQUE (numero_ordine);
CREATE UNIQUE INDEX codiceUnicoIdx ON ordini_logistica (numero_ordine);
ALTER TABLE evasioni_doni ADD FOREIGN KEY (id_ordine_logistica) REFERENCES ordini_logistica(id);
ALTER TABLE evasioni_fascicoli ADD FOREIGN KEY (id_ordine_logistica) REFERENCES ordini_logistica(id);
CREATE INDEX logEntityIdx ON edit_log (entity_id);
ALTER TABLE `periodici` ADD COLUMN `id_societa` varchar(4) NOT NULL default 'GE';
UPDATE `periodici` SET id_societa='GS' where id = 1;
UPDATE `periodici` SET id_societa='GS' where id = 4;
UPDATE `periodici` SET id_societa='GS' where id = 7;
UPDATE `periodici` SET id_societa='GS' where id = 10;
ALTER TABLE `ordini_logistica` ADD COLUMN `note` varchar(256) NULL;
ALTER TABLE `ordini_logistica` ADD COLUMN `annullato` bit NOT NULL DEFAULT FALSE;

***
#suddividere il fascicolo 1+L2 di Sesamo (id=543) in fascicolo+dono e assegnare manualmente i doni
***

CREATE TABLE `contatori` ( 
    `id` int(11) AUTO_INCREMENT NOT NULL,
    `ckey` varchar(64) NOT NULL,
    `progressivo` int(11) NOT NULL,
    PRIMARY KEY(id)
);
CREATE UNIQUE INDEX contatoriCkeyIdx ON contatori (ckey);
INSERT contatori (ckey, progressivo) SELECT ckey, progressivo FROM cursori;
DROP TABLE cursori;
ALTER TABLE `doni` DROP COLUMN `id_tipo_articolo`;
ALTER TABLE `fascicoli` DROP COLUMN `data_consegna_poste`;
ALTER TABLE `fascicoli` CHANGE COLUMN `id_meccanografico` `codice_meccanografico` varchar(32) NOT NULL;

***

ALTER TABLE `fascicoli` ADD COLUMN `id_societa` varchar(4) NOT NULL default 'GE';
ALTER TABLE `doni` ADD COLUMN `id_societa` varchar(4) NOT NULL default 'GE';
ALTER TABLE `doni` ADD COLUMN `id_tipo_articolo` varchar(4) NOT NULL default 'LIB';
update fascicoli as f inner join periodici as p on p.id = f.id_periodico
	set f.id_societa = p.id_societa;

***

ALTER TABLE `fascicoli` CHANGE COLUMN `etichetta_separata` `in_attesa` bit NOT NULL DEFAULT FALSE;
ALTER TABLE `doni` ADD COLUMN `in_attesa` bit NOT NULL DEFAULT FALSE;

***

ALTER TABLE `anagrafiche` CHANGE COLUMN `email` `email_primaria` varchar(256) DEFAULT NULL;
ALTER TABLE `anagrafiche` ADD COLUMN `email_secondaria` varchar(256) DEFAULT NULL;
DROP TABLE IF EXISTS `aliquote_iva`;
CREATE TABLE `aliquote_iva` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`codice_aliquota` varchar(4) NOT NULL,
	`descr` varchar(128) NOT NULL,
	`valore` decimal(9,2) NOT NULL,
	`data_inizio` date NOT NULL,
	`data_fine` date DEFAULT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `aliquote_iva`(codice_aliquota,descr,valore,data_inizio) VALUES('ASS','Assolta',0,'1970-01-01');
INSERT INTO `aliquote_iva`(codice_aliquota,descr,valore,data_inizio) VALUES('ESN','Esente',0,'1970-01-01');
INSERT INTO `aliquote_iva`(codice_aliquota,descr,valore,data_inizio) VALUES('AI5','22%',22,'1970-01-01');
ALTER TABLE `tipi_abbonamento_listino` ADD COLUMN `id_aliquota_iva` int(11) NOT NULL DEFAULT 1;
ALTER TABLE `opzioni` ADD COLUMN `id_aliquota_iva` int(11) NOT NULL DEFAULT 1;
ALTER TABLE `tipi_abbonamento_listino` CHANGE COLUMN `id_aliquota_iva` `id_aliquota_iva` int(11) NOT NULL;
ALTER TABLE `opzioni` CHANGE COLUMN `id_aliquota_iva` `id_aliquota_iva` int(11) NOT NULL;

***

RENAME TABLE edit_log TO log_editing;
RENAME TABLE ws_log TO log_ws;
/*CREATE UNIQUE INDEX `log_editing_name_id_idx1` ON log_editing (entity_name,entity_id);*/
ALTER TABLE ordini_logistica ADD CONSTRAINT `ordini_logistica_anagrafica_fk1` FOREIGN KEY (`id_anagrafica`) REFERENCES `anagrafiche` (`id`);
ALTER TABLE `contatori` ADD COLUMN `locked` bit NOT NULL DEFAULT FALSE;
/*DROP TABLE IF EXISTS `tipi_abbonamento_listino_articoli`;
DROP TABLE IF EXISTS `evasioni_articoli`;
DROP TABLE IF EXISTS `articoli`;*/
DROP TABLE IF EXISTS `societa`;
DROP TABLE IF EXISTS `stampa_fatture`;
/*CREATE TABLE `articoli` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`codice_meccanografico` varchar(32) NOT NULL,
	`codice_per_web` int(11) DEFAULT NULL,
	`codice_veloce` varchar(16) DEFAULT NULL,
	`titolo` varchar(256) NOT NULL,
	`autore` varchar(256) DEFAULT NULL,
	`cartaceo` bit NOT NULL DEFAULT FALSE,
	`digitale` bit NOT NULL DEFAULT FALSE,
	`data_inizio` date NOT NULL,
	`data_fine` date DEFAULT NULL,
	`data_modifica` datetime NOT NULL,
	`data_uscita` date DEFAULT NULL,
	`prezzo` decimal(9,2) NOT NULL,
	`id_aliquota_iva` int(11) NOT NULL,
	`tag` varchar(256) NULL,
	`id_periodico` int(11) DEFAULT NULL,
	`id_societa` varchar(4) NOT NULL,
	`id_tipo_articolo` varchar(4) NOT NULL,
	`id_utente` varchar(32) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `articoli_periodico_fk1` FOREIGN KEY (`id_periodico`) REFERENCES `periodici` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `evasioni_articoli` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`copie` int(11) NOT NULL,
	`data_creazione` datetime NOT NULL,
	`data_invio` datetime DEFAULT NULL,
	`data_modifica` datetime NOT NULL,
	`annullato` bit NOT NULL DEFAULT FALSE,
	`id_articolo` int(11) NOT NULL,
	`id_istanza_abbonamento` int(11) DEFAULT NULL,
	`id_anagrafica` int(11) NOT NULL,
	`id_ordine_logistica` int(11) DEFAULT NULL,
	`opzione` bit NOT NULL DEFAULT FALSE,
	`dono` bit NOT NULL DEFAULT FALSE,
	`prenotazione_istanza_futura` bit NOT NULL DEFAULT FALSE,
	`note` varchar(256) NULL,
	`id_utente` varchar(32) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `evasioni_articoli_articolo_fk1` FOREIGN KEY (`id_articolo`) REFERENCES `articoli` (`id`),
	CONSTRAINT `evasioni_articoli_istanza_fk2` FOREIGN KEY (`id_istanza_abbonamento`) REFERENCES `istanze_abbonamenti` (`id`),
	CONSTRAINT `evasioni_articoli_anagrafica_fk3` FOREIGN KEY (`id_anagrafica`) REFERENCES `anagrafiche` (`id`),
	CONSTRAINT `evasioni_articoli_ordine_fk4` FOREIGN KEY (`id_ordine_logistica`) REFERENCES `ordini_logistica` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `tipi_abbonamento_listino_articoli` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`id_tipo_abbonamento_listino` int(11) DEFAULT NULL,
	`id_articolo` int(11) NOT NULL,
	`id_tipo_destinatario` varchar(4) NULL,
	`opzione` bit NOT NULL DEFAULT FALSE,
	`dono` bit NOT NULL DEFAULT FALSE,
	`giorno_limite_pagamento` int(11) DEFAULT NULL,
	`mese_limite_pagamento` int(11) DEFAULT NULL,
	`id_utente` varchar(32) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `tipi_abbonamento_listino_articoli_fk1` FOREIGN KEY (`id_tipo_abbonamento_listino`) REFERENCES `tipi_abbonamento_listino` (`id`),
	CONSTRAINT `tipi_abbonamento_listino_articoli_fk2` FOREIGN KEY (`id_articolo`) REFERENCES `articoli` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;*/
CREATE TABLE `societa` (
	`id` varchar(4) NOT NULL,
	`nome` varchar(256) NOT NULL,
	`testo_fattura_1` varchar(1024) NOT NULL,
	`testo_fattura_2` varchar(1024) NOT NULL,
	`codice_fiscale` varchar(32) NOT NULL,
	`partita_iva` varchar(32) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `societa`(id,nome,testo_fattura_1, testo_fattura_2,codice_fiscale,partita_iva) 
VALUES('GE','Giunti Editore S.P.A.','50139 Firenze\nVia Bolognese, 165\nSede legale\n20122 Milano\nVia Borgogna, 5\nTel.(055) 5062.1\nFax (055) 5062.299\nR.A.E.E. N. AEETEL-MI-000762\ne-mail: clienti@giunti.it\nPrivacy: Informativa ai sensi del D.L. 196/03 su www.giunti.it',
'R.E.A. Firenze 264655\nR.E.A. Milano 1327444\nCasella postale FI 4072\nC/C Postale N. 307504\nCapitale sociale 8.000.000,00 I.V.\nhttp://www.giunti.it\nRegistro Pile N. IT11040P00002545','80009810484','IT03314600481');
INSERT INTO `societa`(id,nome,testo_fattura_1, testo_fattura_2,codice_fiscale,partita_iva) 
VALUES('GS','Giunti Scuola S.P.A.','50139 Firenze\nVia Bolognese, 165\nSede legale\n20122 Milano\nVia Borgogna, 5\nTel.(055) 5062.1\nFax (055) 5062.299\nR.A.E.E. N. AEETEL-MI-000762\ne-mail: clienti@giunti.it\nPrivacy: Informativa ai sensi del D.L. 196/03 su www.giunti.it',
'R.E.A. Firenze 264655\nR.E.A. Milano 1327444\nCasella postale FI 4072\nC/C Postale N. 307504\nCapitale sociale 8.000.000,00 I.V.\nhttp://www.giunti.it\nRegistro Pile N. IT11040P00002545','05492160485','05492160485');
CREATE TABLE `stampe_fatture` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`id_anagrafica` int(11) NOT NULL,
	`content` mediumblob NOT NULL,
	`mime_type` varchar(128) NOT NULL,
	`importo` decimal(9,2) NOT NULL,
	`data_modifica` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `pagamenti` ADD COLUMN `id_stampa_fattura` int(11) DEFAULT NULL;

***

RENAME TABLE supplementi TO opzioni;
ALTER TABLE `fascicoli` CHANGE COLUMN `id_supplemento` `id_opzione` int(11) DEFAULT NULL;
ALTER TABLE `tipi_abbonamento_listino` CHANGE COLUMN `prezzo_suppl_obbligatori` `prezzo_opz_obbligatori` double DEFAULT NULL;
RENAME TABLE supplementi_istanze_abbonamenti TO opzioni_istanze_abbonamenti;
ALTER TABLE `bollettini_modelli` CHANGE COLUMN `testo_supplementi` `testo_opzioni` varchar(4096) DEFAULT NULL;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP FOREIGN KEY `supplementi_istanze_ibfk_1`;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP PRIMARY KEY;
ALTER TABLE `opzioni_istanze_abbonamenti` CHANGE COLUMN `id_supplemento` `id_opzione` int(11) NOT NULL;
ALTER TABLE `opzioni_istanze_abbonamenti` ADD PRIMARY KEY (`id_opzione`,`id_istanza`);
ALTER TABLE `opzioni_istanze_abbonamenti` ADD CONSTRAINT `opzioni_istanze_ibfk_1` FOREIGN KEY (`id_opzione`) REFERENCES `opzioni` (`id`);
ALTER TABLE `comunicazioni` CHANGE COLUMN `tag_supplemento` `tag_opzione` varchar(16) NULL;


