RENAME TABLE doni TO articoli;
RENAME TABLE evasioni_doni TO evasioni_articoli;
ALTER TABLE `evasioni_articoli` CHANGE COLUMN `id_dono` `id_articolo` int(11) NOT NULL;
ALTER TABLE `tipi_abbonamento_listino` CHANGE COLUMN `id_dono` `id_articolo` int(11) DEFAULT NULL;
ALTER TABLE `tipi_abbonamento_listino` CHANGE COLUMN `id_tipo_destinatario_dono` `id_tipo_destinatario_articolo` varchar(4) DEFAULT NULL;
DROP TABLE IF EXISTS `societa`;
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
DROP TABLE IF EXISTS `stampe_fatture`;
CREATE TABLE `stampe_fatture` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`id_anagrafica` int(11) NOT NULL,
	`content` mediumblob NOT NULL,
	`mime_type` varchar(128) NOT NULL,
	`importo` decimal(9,2) NOT NULL,
	`data_modifica` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `articoli` ADD COLUMN `id_societa` varchar(4) NOT NULL;

ALTER TABLE `pagamenti` drop COLUMN `id_stampa_fattura`;
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `id_stampa_fattura` int(11) DEFAULT NULL;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP FOREIGN KEY `supplementi_istanze_ibfk_2`;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP FOREIGN KEY `opzioni_istanze_ibfk_1`;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP PRIMARY KEY;
ALTER TABLE `opzioni_istanze_abbonamenti` ADD `id` int(11) NOT NULL AUTO_INCREMENT, ADD PRIMARY KEY(id);
ALTER TABLE `opzioni_istanze_abbonamenti` ADD CONSTRAINT `opzioni_istanze_ibfk_1` FOREIGN KEY (`id_opzione`) REFERENCES `opzioni` (`id`);
ALTER TABLE `opzioni_istanze_abbonamenti` ADD CONSTRAINT `opzioni_istanze_ibfk_2` FOREIGN KEY (`id_istanza`) REFERENCES `istanze_abbonamenti` (`id`);
ALTER TABLE `opzioni_istanze_abbonamenti` ADD COLUMN `id_stampa_fattura` int(11) DEFAULT NULL;

***

ALTER TABLE `rapporti` ADD COLUMN `errore` bit(1) NOT NULL DEFAULT FALSE;

***

RENAME TABLE tipi_abbonamento_listino TO listini;
ALTER TABLE istanze_abbonamenti DROP FOREIGN KEY `istanze_abbonamenti_ibfk_4`;
ALTER TABLE istanze_abbonamenti CHANGE COLUMN id_tipo_abbonamento_listino id_listino int(11) NOT NULL;
DROP TABLE IF EXISTS `articoli_listino`;
CREATE TABLE `articoli_listino` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`id_listino` int(11) NOT NULL,
`id_articolo` int(11) NOT NULL,
`id_tipo_destinatario` varchar(4) NOT NULL,
`giorno_limite_pagamento` int(11) DEFAULT NULL,
`mese_limite_pagamento` int(11) DEFAULT NULL,
`id_utente` varchar(32) NOT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `articoli_listino_ibfk_1` FOREIGN KEY (`id_listino`) REFERENCES `listini` (`id`),
CONSTRAINT `articoli_listino_ibfk_2` FOREIGN KEY (`id_articolo`) REFERENCES `articoli` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `opzioni_listino`;
CREATE TABLE `opzioni_listino` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`id_listino` int(11) NOT NULL,
`id_opzione` int(11) NOT NULL,
`id_utente` varchar(32) NOT NULL,
PRIMARY KEY (`id`),
CONSTRAINT `opzioni_listino_ibfk_1` FOREIGN KEY (`id_listino`) REFERENCES `listini` (`id`),
CONSTRAINT `opzioni_listino_ibfk_2` FOREIGN KEY (`id_opzione`) REFERENCES `opzioni` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE istanze_abbonamenti ADD CONSTRAINT `istanze_abbonamenti_ibfk_4` FOREIGN KEY (`id_listino`) REFERENCES `listini` (`id`);

***

#ALTER TABLE `istanze_abbonamenti` ADD COLUMN `id_stampa_fattura` int(11) DEFAULT NULL;
ALTER TABLE `contatori` ADD COLUMN `temp_progressivo` int(11) DEFAULT NULL;
ALTER TABLE `societa` ADD COLUMN `prefisso_fatture` varchar(8) DEFAULT NULL;
UPDATE societa set prefisso_fatture='FPG' where id='GE';
UPDATE societa set prefisso_fatture='FPS' where id='GS';
ALTER TABLE `nazioni` ADD COLUMN `ue` bit(1) NOT NULL DEFAULT FALSE;
update nazioni set ue=true where id='ITA';
update nazioni set ue=true where id='AUT';
update nazioni set ue=true where id='BEL';
update nazioni set ue=true where id='BGR';
update nazioni set ue=true where id='CYP';
update nazioni set ue=true where id='CZE';
update nazioni set ue=true where id='DEU';
update nazioni set ue=true where id='DNK';
update nazioni set ue=true where id='ESP';
update nazioni set ue=true where id='EST';
update nazioni set ue=true where id='FIN';
update nazioni set ue=true where id='FRA';
update nazioni set ue=true where id='GBR';
update nazioni set ue=true where id='GIB';
update nazioni set ue=true where id='GRC';
update nazioni set ue=true where id='HRV';
update nazioni set ue=true where id='HUN';
update nazioni set ue=true where id='IRL';
update nazioni set ue=true where id='LIE';
update nazioni set ue=true where id='LTU';
update nazioni set ue=true where id='LUX';
update nazioni set ue=true where id='LVA';
update nazioni set ue=true where id='MLT';
update nazioni set ue=true where id='NLD';
update nazioni set ue=true where id='POL';
update nazioni set ue=true where id='PRT';
update nazioni set ue=true where id='ROU';
update nazioni set ue=true where id='SVK';
update nazioni set ue=true where id='SVN';
update nazioni set ue=true where id='SWE';
ALTER TABLE `pagamenti` ADD COLUMN `data_accredito` date DEFAULT NULL;
update pagamenti set data_accredito = data_pagamento where data_accredito is null;
ALTER TABLE `pagamenti` CHANGE COLUMN `data_accredito` `data_accredito` date NOT NULL;
ALTER TABLE `pagamenti` CHANGE COLUMN `data_pagamento` `data_pagamento` date NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `numero_fattura` varchar(64) NOT NULL;
ALTER TABLE `stampe_fatture` CHANGE COLUMN `data_modifica` `data_fattura` date NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `file_name` varchar(256) NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `data_email` datetime DEFAULT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `data_archiviazione` datetime DEFAULT NULL;
ALTER TABLE `stampe_fatture` CHANGE COLUMN `importo` `totale_finale` decimal(9,2) NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `totale_iva` decimal(9,2) NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `totale_imponibile` decimal(9,2) NOT NULL;
ALTER TABLE `stampe_fatture` ADD COLUMN `id_societa` varchar(4) NOT NULL;
CREATE INDEX stampeFattureNumeroIdx ON stampe_fatture (numero_fattura);
ALTER TABLE `societa` ADD COLUMN `codice_societa` varchar(32) NOT NULL;
update societa set codice_societa='001' where id='GE';
update societa set codice_societa='027' where id='GS';
ALTER TABLE `articoli` CHANGE COLUMN `codice_interno` `codice_interno` varchar(4) DEFAULT NULL;
ALTER TABLE `articoli` ADD COLUMN `cartaceo` bit(1) NOT NULL;
ALTER TABLE `articoli` ADD COLUMN `digitale` bit(1) NOT NULL;
ALTER TABLE `articoli` ADD COLUMN `data_estrazione` datetime DEFAULT NULL;
ALTER TABLE `fascicoli` CHANGE COLUMN `data_estrazione_effettiva` `data_estrazione` datetime DEFAULT NULL;

***

update articoli SET cartaceo=true;

ALTER TABLE `stampe_fatture` ADD COLUMN `id_istanza` int(11) DEFAULT NULL;
ALTER TABLE `periodici` DROP COLUMN `codice_sap`;

***

ALTER TABLE `fascicoli` drop COLUMN `id_societa`;
ALTER TABLE `fascicoli` drop COLUMN `id_fascicolo_abbinato`;
ALTER TABLE `opzioni` CHANGE COLUMN `codice_num` `codice_interno` varchar(16) NOT NULL;
DROP TABLE IF EXISTS `articoli_opzioni`;
CREATE TABLE `articoli_opzioni` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`id_opzione` int(11) NOT NULL,
	`id_articolo` int(11) NOT NULL,
	`id_utente` varchar(32) NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `articoli_opzioni_ibfk_1` FOREIGN KEY (`id_opzione`) REFERENCES `opzioni` (`id`),
	CONSTRAINT `articoli_opzioni_ibfk_2` FOREIGN KEY (`id_articolo`) REFERENCES `articoli` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE `articoli` DROP COLUMN `data_estrazione`;
ALTER TABLE `articoli_opzioni` ADD COLUMN `data_ultima_estrazione` datetime DEFAULT NULL;
DROP TABLE IF EXISTS articoli_listini;
RENAME TABLE articoli_listino TO articoli_listini;
ALTER TABLE `articoli_listini` ADD COLUMN `data_ultima_estrazione` datetime DEFAULT NULL;
CREATE INDEX evasioni_articoli_istanze_idx ON evasioni_articoli (id_istanza_abbonamento);
CREATE INDEX evasioni_articoli_articoli_idx ON evasioni_articoli (id_articolo);
CREATE INDEX evasioni_articoli_istanze_articoli_idx ON evasioni_articoli (id_istanza_abbonamento,id_articolo);
CREATE INDEX stampe_fatture_anagrafiche_idx ON stampe_fatture (id_anagrafica);
ALTER TABLE `articoli_opzioni` CHANGE COLUMN `data_ultima_estrazione` `data_estrazione` datetime DEFAULT NULL;
ALTER TABLE `articoli_listini` CHANGE COLUMN `data_ultima_estrazione` `data_estrazione` datetime DEFAULT NULL;
ALTER TABLE `evasioni_articoli` ADD COLUMN `id_articolo_listino` int(11) DEFAULT NULL;
ALTER TABLE `evasioni_articoli` ADD COLUMN `id_articolo_opzione` int(11) DEFAULT NULL;
RENAME TABLE opzioni_listino TO opzioni_listini;
ALTER TABLE `opzioni_listini` DROP COLUMN `id_utente`;
ALTER TABLE `articoli_listini` DROP COLUMN `id_utente`;
ALTER TABLE `articoli_opzioni` DROP COLUMN `id_utente`;

***

ALTER TABLE `listini` drop COLUMN `prezzo_opz_obbligatori`;
update aliquote_iva set descr='assolta art.74' where codice_aliquota='ASS';

***

DROP USER 'apgclienti'@'%';
FLUSH PRIVILEGES;
GRANT SELECT ON apg.* TO 'apgclienti'@'%' IDENTIFIED BY '28_apgclienti';
GRANT SELECT, INSERT, UPDATE, DELETE ON apg.feedback_anagrafiche TO 'apgclienti'@'%';

DROP TABLE IF EXISTS `feedback_anagrafiche`;
CREATE TABLE `feedback_anagrafiche` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_anagrafica` int(11) NOT NULL,
  `url_id` varchar(128) NOT NULL,
  `codice_cliente` varchar(10) NOT NULL,
  `cognome_ragione_sociale` varchar(64) NOT NULL,
  `sesso` varchar(1) DEFAULT 'M',
  `nome` varchar(32) DEFAULT NULL,
  `codice_fiscale` varchar(16) DEFAULT NULL,
  `partita_iva` varchar(16) DEFAULT NULL,
  `titolo` varchar(32) DEFAULT NULL,
  `id_professione` int(11) DEFAULT NULL,
  `indirizzo` varchar(128) DEFAULT NULL,
  `cap` varchar(8) DEFAULT NULL,
  `localita` varchar(64) DEFAULT NULL,
  `id_provincia` varchar(4) DEFAULT NULL,
  `id_nazione` varchar(4) NOT NULL,
  `presso` varchar(64) DEFAULT NULL,
  `tel_casa` varchar(32) DEFAULT NULL,
  `tel_mobile` varchar(32) DEFAULT NULL,
  `email_primaria` varchar(256) DEFAULT NULL,
  `email_secondaria` varchar(256) DEFAULT NULL,
  `data_creazione` datetime NOT NULL,
  `data_email` datetime NULL,
  `data_feedback` datetime NULL,
  `data_acquisizione` datetime NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE INDEX feedback_anagrafiche_anagrafiche_idx ON feedback_anagrafiche (id_anagrafica);
CREATE INDEX feedback_anagrafiche_url_idx ON feedback_anagrafiche (url_id);

***

ALTER TABLE `stampe_fatture` ADD COLUMN `id_periodico` int(11) DEFAULT NULL;
update aliquote_iva set codice_aliquota = "V7" where id=3;

***

GRANT SELECT, INSERT, UPDATE, DELETE ON apg.log_ws TO 'apgclienti'@'%';

***

DROP TABLE IF EXISTS `file_resources`;
CREATE TABLE `file_resources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `path` varchar(256) NOT NULL,
  `file_type` varchar(4) NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
update modelli_bollettini set path_report = concat('/report', path_report), path_logo_img = concat('/report', path_logo_img)

***

ALTER TABLE `stampe_fatture` CHANGE COLUMN `data_archiviazione` `data_creazione` datetime DEFAULT NULL;

***

ALTER TABLE `istanze_abbonamenti` ADD COLUMN `id_adesione` int(11) DEFAULT NULL;
DROP TABLE IF EXISTS `adesioni`;
CREATE TABLE `adesioni` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`codice` varchar(64) NOT NULL,
	`descr` varchar(64) DEFAULT NULL,
	`data_modifica` timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
	PRIMARY KEY (`id`),
	UNIQUE KEY `descr_key` (`descr`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE INDEX adesioni_idx ON adesioni (codice);

***

DROP TABLE IF EXISTS `fatture_stampe`;
DROP TABLE IF EXISTS `fatture_articoli`;
DROP TABLE IF EXISTS `fatture`;
CREATE TABLE `fatture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_anagrafica` int(11) NOT NULL,
  `totale_finale` decimal(9,2) NOT NULL,
  `data_fattura` date NOT NULL,
  `numero_fattura` varchar(64) NOT NULL,
  `data_email` datetime DEFAULT NULL,
  `data_creazione` datetime DEFAULT NULL,
  `totale_iva` decimal(9,2) NOT NULL,
  `totale_imponibile` decimal(9,2) NOT NULL,
  `id_fattura_stampa` int(11) DEFAULT NULL,
  `id_societa` varchar(4) NOT NULL,
  `id_istanza` int(11) DEFAULT NULL,
  `id_periodico` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `stampeFattureNumeroIdx` (`numero_fattura`),
  KEY `stampe_fatture_anagrafiche_idx` (`id_anagrafica`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
CREATE TABLE `fatture_stampe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` mediumblob NOT NULL,
  `mime_type` varchar(128) NOT NULL,
  `file_name` varchar(256) NOT NULL,
  `data_creazione` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
CREATE TABLE `fatture_articoli` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_fattura` int(11) NOT NULL,
  `id_aliquota_iva` int(11) DEFAULT NULL,
  `importo_tot_unit` decimal(9,2) NOT NULL,
  `importo_iva_unit` decimal(9,2) NOT NULL,
  `importo_imp_unit` decimal(9,2) NOT NULL,
  `quantita` int(11) NOT NULL,
  `descrizione` varchar(256) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

ALTER TABLE `fatture` ADD COLUMN `tipo_iva` varchar(4) NOT NULL;
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `id_fattura` int(11) DEFAULT NULL;
ALTER TABLE `opzioni_istanze_abbonamenti` ADD COLUMN `id_fattura` int(11) DEFAULT NULL;
ALTER TABLE `aliquote_iva` ADD COLUMN `codice_ita_pvt` varchar(8) NOT NULL;
ALTER TABLE `aliquote_iva` ADD COLUMN `codice_ita_soc` varchar(8) NOT NULL;
ALTER TABLE `aliquote_iva` ADD COLUMN `codice_ue_pvt` varchar(8) NOT NULL;
ALTER TABLE `aliquote_iva` ADD COLUMN `codice_ue_soc` varchar(8) NOT NULL;
ALTER TABLE `aliquote_iva` ADD COLUMN `codice_extra_ue` varchar(8) NOT NULL;
ALTER TABLE `aliquote_iva` DROP COLUMN `codice_aliquota`;
update aliquote_iva set codice_ita_pvt='VA', codice_ita_soc='VA', codice_ue_pvt='VA', codice_ue_soc='X0', codice_extra_ue='N2' where id=1;
update aliquote_iva set codice_ita_pvt='V7', codice_ita_soc='V7', codice_ue_pvt='V7', codice_ue_soc='N5', codice_extra_ue='NN' where id=3;
delete from aliquote_iva where id=2;

update nazioni set ue=true where id='SMR';
update nazioni set ue=true where id='VAT';
update nazioni set ue=true where id='AND';
update nazioni set ue=true where id='MCO';

***

ALTER TABLE `istanze_abbonamenti` DROP COLUMN `id_stampe_fatture`;
ALTER TABLE `opzioni_istanze_abbonamenti` DROP COLUMN `id_stampe_fatture`;
DROP TABLE `stampe_fatture`;
ALTER TABLE `istanze_abbonamenti` DROP COLUMN `adesione`;
ALTER TABLE `evasioni_articoli` ADD COLUMN data_conferma_evasione datetime DEFAULT NULL;
ALTER TABLE `evasioni_articoli` ADD COLUMN data_annullamento datetime DEFAULT NULL;
ALTER TABLE `evasioni_fascicoli` ADD COLUMN data_conferma_evasione datetime DEFAULT NULL;
ALTER TABLE `evasioni_fascicoli` ADD COLUMN data_annullamento datetime DEFAULT NULL;
ALTER TABLE `ordini_logistica` ADD COLUMN data_rifiuto datetime DEFAULT NULL;
ALTER TABLE `ordini_logistica` ADD COLUMN data_chiusura datetime DEFAULT NULL;
UPDATE `ordini_logistica` set data_rifiuto=data_conferma_invio where annullato=true;
UPDATE `ordini_logistica` set data_rifiuto='1970-01-01' where annullato=true and data_rifiuto is null;
UPDATE `ordini_logistica` set data_chiusura=data_conferma_invio where annullato=false;
UPDATE `ordini_logistica` set data_chiusura='1970-01-01' where annullato=false and data_chiusura is null;

UPDATE `evasioni_articoli` set data_annullamento=null, data_conferma_evasione=(select data_chiusura from ordini_logistica where id=id_ordine_logistica) where id_ordine_logistica is not null;
UPDATE `evasioni_articoli` set data_annullamento=(select data_rifiuto from ordini_logistica where id=id_ordine_logistica), data_conferma_evasione=null where id_ordine_logistica is not null;
UPDATE `evasioni_articoli` set data_annullamento='1970-01-01' where eliminato = true and data_annullamento is null;
UPDATE `evasioni_fascicoli` set data_annullamento=null, data_conferma_evasione=(select data_chiusura from ordini_logistica where id=id_ordine_logistica) where id_ordine_logistica is not null;
UPDATE `evasioni_fascicoli` set data_annullamento=(select data_rifiuto from ordini_logistica where id=id_ordine_logistica), data_conferma_evasione=null where id_ordine_logistica is not null;

***

DROP INDEX adesioni_idx on adesioni;
ALTER TABLE adesioni ADD CONSTRAINT `adesioni_codice_key` UNIQUE (`codice`);
insert into adesioni(codice, descr) VALUES ("MEDIE","MEDIE");
insert into adesioni(codice, descr) VALUES ("SUP","SUP");

ALTER TABLE `ordini_logistica` DROP COLUMN `data_conferma_invio`; 
ALTER TABLE `ordini_logistica` DROP COLUMN `annullato`;
ALTER TABLE `evasioni_articoli` DROP COLUMN eliminato;
ALTER TABLE `evasioni_fascicoli` DROP COLUMN data_annullamento;

DROP TABLE IF EXISTS `tipi_abbonamento_rinnovo`;
CREATE TABLE `tipi_abbonamento_rinnovo` (
	`id` int(11) NOT NULL AUTO_INCREMENT,
	`id_listino` int(11) NOT NULL,
	`id_tipo_abbonamento` int(11) NOT NULL,
	`ordine` int(11) NOT NULL,
	PRIMARY KEY (`id`),
	UNIQUE KEY `listino_tipo_key` (`id_listino`,`id_tipo_abbonamento`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into tipi_abbonamento_rinnovo(id_listino, id_tipo_abbonamento, ordine)
	select lis.id, lis.id_tipo_abbonamento_rinnovo, 0 from listini as lis where
	lis.id_tipo_abbonamento_rinnovo is not null;
insert into tipi_abbonamento_rinnovo(id_listino, id_tipo_abbonamento, ordine)
	select lis.id, lis.id_tipo_abbonamento_rinnovo_alternativa, 1 from listini as lis where
	lis.id_tipo_abbonamento_rinnovo_alternativa is not null and
	lis.id_tipo_abbonamento_rinnovo != lis.id_tipo_abbonamento_rinnovo_alternativa;

