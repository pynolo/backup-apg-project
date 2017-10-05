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

***

ALTER TABLE `anagrafiche` ADD COLUMN `data_nascita` date DEFAULT NULL;
