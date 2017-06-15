ALTER TABLE `listini` CHANGE COLUMN `pagato_con_fattura` `fattura_differita` bit NOT NULL DEFAULT false;
ALTER TABLE `listini` ADD COLUMN `fattura_inibita` bit NOT NULL DEFAULT false;

***

DROP TABLE IF EXISTS `api_services`;
CREATE TABLE `api_services` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nome` varchar(64) NOT NULL,
  `access_key` varchar(64) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into api_services(id,nome,access_key) values (1,"testing","1234");
insert into api_services(id,nome,access_key) values (2,"www.giuntiabbonamenti.it","rg54gnt21");
ALTER TABLE `anagrafiche` ADD COLUMN `id_anagrafica_da_aggiornare` int(11) DEFAULT null;
ALTER TABLE `anagrafiche` ADD COLUMN `necessita_verifica` bit NOT NULL DEFAULT false;

***

ALTER TABLE `anagrafiche` DROP COLUMN `id_import`;
ALTER TABLE `anagrafiche` ADD COLUMN `codici_cliente_merge` varchar(64) DEFAULT NULL;

***

ALTER TABLE `fascicoli` CHANGE COLUMN `numero_fascicolo` `titolo_numero` varchar(64) NOT NULL;
ALTER TABLE `fascicoli` CHANGE COLUMN `data_nominale` `data_inizio` date NOT NULL;
ALTER TABLE `fascicoli` CHANGE COLUMN `data_nominale_fine` `data_fine` date DEFAULT NULL;
ALTER TABLE `articoli` CHANGE COLUMN `titolo` `titolo_numero` varchar(64) NOT NULL;
insert into utenti(id,password,descrizione,id_ruolo) values ("api","pinguino","API",3);

***

ALTER TABLE `istanze_abbonamenti` ADD COLUMN `necessita_verifica` bit NOT NULL DEFAULT false;
update utenti set descrizione="Internet" where id="api";

/*ALTER TABLE `anagrafiche` DROP COLUMN `richiede_fattura`;*/
/*ALTER TABLE `anagrafiche` DROP COLUMN `centro_di_costo`;*/

***

ALTER TABLE `societa` ADD COLUMN `prefisso_rimborso` varchar(8) DEFAULT NULL;
update societa set prefisso_rimborso = 'RPG' where id='GE';
update societa set prefisso_rimborso = 'RPS' where id='GS';
ALTER TABLE `fatture` ADD COLUMN `data_modifica` datetime DEFAULT null;
ALTER TABLE `fatture` ADD COLUMN `id_fattura_rimborsata` int DEFAULT null;
DROP TABLE IF EXISTS `config`;
CREATE TABLE `config` (
  `id` varchar(64) NOT NULL,
  `valore` varchar(1024) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
insert into config(id,valore) values ("ftpFatRegHost","posap01.sap.intranet.giunti.it");
insert into config(id,valore) values ("ftpFatRegPort","8099");
insert into config(id,valore) values ("ftpFatRegUsername","rivistegge");
insert into config(id,valore) values ("ftpFatRegPassword","rivistegge");
insert into config(id,valore) values ("ftpFatRegDir","GGE_GSC_FILEREGISTRAZIONI");
insert into config(id,valore) values ("ftpFpgHost","posap01.sap.intranet.giunti.it");
insert into config(id,valore) values ("ftpFpgPort","8099");
insert into config(id,valore) values ("ftpFpgUsername","rivistegge");
insert into config(id,valore) values ("ftpFpgPassword","rivistegge");
insert into config(id,valore) values ("ftpFpgDir","GGE_RIVISTE_PDF_ARCHIDOC");
insert into config(id,valore) values ("ftpFpsHost","posap01.sap.intranet.giunti.it");
insert into config(id,valore) values ("ftpFpsPort","8099");
insert into config(id,valore) values ("ftpFpsUsername","rivistegge");
insert into config(id,valore) values ("ftpFpsPassword","rivistegge");
insert into config(id,valore) values ("ftpFpsDir","GSC_RIVISTE_PDF_ARCHIDOC");
insert into config(id,valore) values ("ftpFxeHost","posap01.sap.intranet.giunti.it");
insert into config(id,valore) values ("ftpFxePort","8099");
insert into config(id,valore) values ("ftpFxeUsername","rivistegge");
insert into config(id,valore) values ("ftpFxePassword","rivistegge");
insert into config(id,valore) values ("ftpFxeDir","GE_RIVISTE_ABBOCARTA");
insert into config(id,valore) values ("ftpFxsHost","posap01.sap.intranet.giunti.it");
insert into config(id,valore) values ("ftpFxsPort","8099");
insert into config(id,valore) values ("ftpFxsUsername","rivistegge");
insert into config(id,valore) values ("ftpFxsPassword","rivistegge");
insert into config(id,valore) values ("ftpFxsDir","GSC_RIVISTE_ABBOCARTA");
insert into config(id,valore) values ("ftpFatDbgHost","ftp.giunti.it");
insert into config(id,valore) values ("ftpFatDbgPort","21");
insert into config(id,valore) values ("ftpFatDbgUsername","apg-rw");
insert into config(id,valore) values ("ftpFatDbgPassword","Rwapg2014");
insert into config(id,valore) values ("ftpFatDbgDir","amministrazione");
insert into config(id,valore) values ("FattureJob_debug","true");
insert into config(id,valore) values ("FattureFxeFxsJob_debug","true");
insert into config(id,valore) values ("FattureRegistriCorrispettiviJob_debug","true");
insert into config(id,valore) values ("FattureSpesometroJob_debug","true");
insert into config(id,valore) values ("RebuildFatturaServlet_debug","true");
insert into config(id,valore) values ("CreateRimborsoServlet_debug","true");

***

update ruoli set descrizione = 'Supervisore / Servizio abbonati' where id=2;
ALTER TABLE fatture ADD CONSTRAINT `id_fattura_rimborsata_unique` UNIQUE (`id_fattura_rimborsata`);
ALTER TABLE `fatture` ADD COLUMN `id_tipo_documento` varchar(4) DEFAULT 'FAT' not null;
ALTER TABLE `fatture` ADD COLUMN `numero_rimborso_collegato` varchar(64) DEFAULT null;
ALTER TABLE `societa` DROP COLUMN `prefisso_rimborso`;

***

ALTER TABLE nazioni ADD CONSTRAINT `Iso3166Unique` UNIQUE (`sigla_nazione`);
CREATE INDEX Iso3166Idx ON nazioni (sigla_nazione);
ALTER TABLE `indirizzi` ADD COLUMN `ragione_sociale_fattura` varchar(64) DEFAULT null;

***

ALTER TABLE `indirizzi` DROP COLUMN `ragione_sociale_fattura`;
ALTER TABLE `indirizzi` ADD COLUMN `cognome_ragione_sociale` varchar(64) DEFAULT null;
ALTER TABLE `indirizzi` ADD COLUMN `nome` varchar(32) DEFAULT null;
ALTER TABLE `indirizzi` ADD COLUMN `titolo` varchar(32) DEFAULT null;
UPDATE indirizzi, anagrafiche
SET indirizzi.cognome_ragione_sociale = anagrafiche.cognome_ragione_sociale,
	indirizzi.nome = anagrafiche.nome,
	indirizzi.titolo = anagrafiche.titolo
WHERE indirizzi.id = anagrafiche.id_indirizzo_principale;
UPDATE indirizzi, anagrafiche
SET indirizzi.cognome_ragione_sociale = anagrafiche.cognome_ragione_sociale,
	indirizzi.nome = anagrafiche.nome,
	indirizzi.titolo = anagrafiche.titolo
WHERE indirizzi.id = anagrafiche.id_indirizzo_fatturazione and CHAR_LENGTH(indirizzi.indirizzo) > 1;
ALTER TABLE `anagrafiche` CHANGE COLUMN `cognome_ragione_sociale` `cognome_ragione_sociale_bak` varchar(64) DEFAULT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `nome` `nome_bak` varchar(32) DEFAULT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `titolo` `titolo_bak` varchar(32) DEFAULT NULL;

***

ALTER TABLE `modelli_bollettini` CHANGE COLUMN `aut_bol` `autorizzazione` varchar(256) NOT NULL;
ALTER TABLE `modelli_bollettini` CHANGE COLUMN `path_report` `report_file_path` varchar(256) NOT NULL;
ALTER TABLE `modelli_bollettini` CHANGE COLUMN `path_logo_img` `logo_vertical_path` varchar(256) NOT NULL;
ALTER TABLE `modelli_bollettini` ADD COLUMN `logo_small_path` varchar(256) NOT NULL DEFAULT "/icon.jpg";
ALTER TABLE `modelli_bollettini` ADD COLUMN `predefinito_periodico` bit NOT NULL DEFAULT false;
CREATE INDEX ModelloPredefinitoIdx ON modelli_bollettini (predefinito_periodico, id_periodico);
#UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. ', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_giunti_V.jpg', `logo_small_path`='/report/logo/logo_giunti.jpg' WHERE `id`=0;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_B_V.jpg', `logo_small_path`='/report/logo/logo_B.jpg' WHERE `id`=2;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SSIC/E 6801 DEL 01.03.2002', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_D_V.jpg', `logo_small_path`='/report/logo/logo_D.jpg' WHERE `id`=3;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43518 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_M_V.jpg', `logo_small_path`='/report/logo/logo_M.jpg' WHERE `id`=4;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43523 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_Q_V.jpg', `logo_small_path`='/report/logo/logo_Q.jpg' WHERE `id`=5;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_W_V.jpg', `logo_small_path`='/report/logo/logo_W.jpg' WHERE `id`=6;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_W_V.jpg', `logo_small_path`='/report/logo/logo_W.jpg' WHERE `id`=8;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_W_V.jpg', `logo_small_path`='/report/logo/logo_W.jpg' WHERE `id`=9;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43523 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_Q_V.jpg', `logo_small_path`='/report/logo/logo_Q.jpg' WHERE `id`=10;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43523 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_Q_V.jpg', `logo_small_path`='/report/logo/logo_Q.jpg' WHERE `id`=11;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_B_V.jpg', `logo_small_path`='/report/logo/logo_B.jpg' WHERE `id`=14;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_B_V.jpg', `logo_small_path`='/report/logo/logo_B.jpg' WHERE `id`=15;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SSIC/E 6801 DEL 01.03.2002', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_D_V.jpg', `logo_small_path`='/report/logo/logo_D.jpg' WHERE `id`=16;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SSIC/E 6801 DEL 01.03.2002', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_D_V.jpg', `logo_small_path`='/report/logo/logo_D.jpg' WHERE `id`=17;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_W_V.jpg', `logo_small_path`='/report/logo/logo_W.jpg' WHERE `id`=18;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_B_V.jpg', `logo_small_path`='/report/logo/logo_B.jpg' WHERE `id`=19;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SSIC/E 6801 DEL 01.03.2002', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_D_V.jpg', `logo_small_path`='/report/logo/logo_D.jpg' WHERE `id`=20;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43516 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_A_V.jpg', `logo_small_path`='/report/logo/logo_A.jpg' WHERE `id`=22;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_B_V.jpg', `logo_small_path`='/report/logo/logo_B.jpg' WHERE `id`=25;
UPDATE `modelli_bollettini` SET `autorizzazione`='AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016', `report_file_path`='/report/bollettino896_2016.jasper', `logo_vertical_path`='/report/logo/logo_W_V.jpg', `logo_small_path`='/report/logo/logo_W.jpg' WHERE `id`=30;
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 La Vita Scolastica','896','AUT. N. DB/SISB/PDF 43516 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_A_V.jpg','/report/logo/logo_A.jpg','',1);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Psicologia Contemporanea','896','AUT. N. DB/SISB/PDF 43522 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_B_V.jpg','/report/logo/logo_B.jpg','',2);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Psicologia e scuola','896','AUT. DB/SSIC/E 6801 DEL 01.03.2002','/report/bollettino896_2016.jasper','/report/logo/logo_D_V.jpg','/report/logo/logo_D.jpg','',4);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Scuola dell infanzia','896','AUT. N. DB/SISB/PDF 43518 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_M_V.jpg','/report/logo/logo_M.jpg','',7);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Nidi d Infanzia','896','AUT. N. DB/SISB/PDF 43520 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_N_V.jpg','/report/logo/logo_N.jpg','',11);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Art e Dossier','896','AUT. N. DB/SISB/PDF 43523 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_Q_V.jpg','/report/logo/logo_Q.jpg','',8);
INSERT INTO modelli_bollettini (predefinito_periodico,descr,codice_modello,autorizzazione,report_file_path,logo_vertical_path,logo_small_path,testo_bandella,id_periodico) VALUES
	(true,'896 Archeologia Viva','896','AUT. N. DB/SISB/PDF 43524 DEL 01.06.2016','/report/bollettino896_2016.jasper','/report/logo/logo_W_V.jpg','/report/logo/logo_W.jpg','',9);
ALTER TABLE `listini` CHANGE COLUMN `codice` `uid` varchar(16) NOT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `codice_cliente` `uid` varchar(16) NOT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `codici_cliente_merge` `uid_merge_list` varchar(64) DEFAULT NULL;
ALTER TABLE `opzioni` CHANGE COLUMN `codice` `uid` varchar(16) NOT NULL;
ALTER TABLE `periodici` CHANGE COLUMN `lettera` `uid` varchar(4) NOT NULL;
delete from `modelli_bollettini` where id >=0 and id <=1;
delete from `modelli_bollettini` where id >=25 and id <=29;

***

alter table `periodici` drop COLUMN `autorizzazione`;
ALTER TABLE `anagrafiche` CHANGE COLUMN `uid_merge_list` `uid_merge_list` varchar(256) DEFAULT NULL;

***

ALTER TABLE periodici ADD FOREIGN KEY (id_societa) REFERENCES societa(id);
ALTER TABLE tipi_abbonamento_rinnovo ADD FOREIGN KEY (id_listino) REFERENCES listini(id);
ALTER TABLE comunicazioni ADD FOREIGN KEY (id_modello_bollettino) REFERENCES modelli_bollettini(id);
ALTER TABLE comunicazioni ADD FOREIGN KEY (id_modello_email) REFERENCES modelli_email(id);
ALTER TABLE evasioni_articoli ADD FOREIGN KEY (id_articolo) REFERENCES articoli(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_societa) REFERENCES societa(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_periodico) REFERENCES periodici(id);
# ALTER TABLE evasioni_fascicoli ADD FOREIGN KEY (id_istanza_abbonamento) REFERENCES istanze_abbonamenti(id);
# ALTER TABLE evasioni_articoli ADD FOREIGN KEY (id_istanza_abbonamento) REFERENCES istanze_abbonamenti(id);
# ALTER TABLE evasioni_articoli ADD FOREIGN KEY (id_anagrafica) REFERENCES anagrafiche(id);
# ALTER TABLE istanze_abbonamenti ADD FOREIGN KEY (id_fascicolo_inizio) REFERENCES fascicoli(id);
# ALTER TABLE istanze_abbonamenti ADD FOREIGN KEY (id_fascicolo_fine) REFERENCES fascicoli(id);
# ALTER TABLE fatture ADD FOREIGN KEY (id_istanza) REFERENCES istanze_abbonamenti(id);
# ALTER TABLE fatture ADD FOREIGN KEY (id_fattura_stampa) REFERENCES fatture_stampe(id);
# ALTER TABLE fatture_articoli ADD FOREIGN KEY (id_fattura) REFERENCES fatture(id);

ALTER TABLE `utenti` ADD COLUMN `periodici_uid_restriction` varchar(256) DEFAULT NULL;

