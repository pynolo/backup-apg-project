#FattureInvioSap
DROP TABLE IF EXISTS `fatture_invio_sap`;
CREATE TABLE `fatture_invio_sap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_invio` int(11) NOT NULL,
  `id_fattura` int(11) DEFAULT NULL,
  `numero_fattura` varchar(16) DEFAULT NULL,
  `err_table` varchar(8) DEFAULT NULL,
  `err_field` varchar(16) DEFAULT NULL,
  `err_message` varchar(256) DEFAULT NULL,
  `data_creazione` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
#Aggiunta PEC e codice destinatario
ALTER TABLE `anagrafiche` ADD COLUMN `email_pec` varchar(256) DEFAULT NULL;
ALTER TABLE `anagrafiche` ADD COLUMN `codice_destinatario` varchar(8) DEFAULT NULL;
ALTER TABLE `anagrafiche` DROP COLUMN `richiede_fattura`;
#Fatture
ALTER TABLE `fatture` ADD COLUMN `fittizia` bit(1) NOT NULL DEFAULT false;
ALTER TABLE `fatture` ADD COLUMN `data_invio_sap` datetime DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `cognome_ragione_sociale` varchar(64) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `nome` varchar(32) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `indirizzo` varchar(128) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `cap` varchar(8) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `localita` varchar(64) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `presso` varchar(64) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_provincia` varchar(4) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_nazione` varchar(4) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `codice_fiscale` varchar(16) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `partita_iva` varchar(16) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `id_utente` varchar(32) DEFAULT NULL;
update fatture set id_utente = '' where id_utente is null;
update fatture set fittizia = true where numero_fattura like 'ZZZ%';
#istanze_abbonamenti
ALTER TABLE `istanze_abbonamenti` CHANGE COLUMN `in_fatturazione` `fattura_differita` bit(1) NOT NULL;
#pagamenti_crediti
ALTER TABLE `pagamenti_crediti` ADD COLUMN `id_utente` varchar(32) DEFAULT NULL;
update pagamenti_crediti set id_utente = '' where id_utente is null;
#ALTER TABLE `pagamenti_crediti` CHANGE COLUMN `id_utente` `id_utente` varchar(32) NOT NULL;

###

ALTER TABLE `anagrafiche` CHANGE COLUMN `note` `note` varchar(2048) DEFAULT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `uid_merge_list` `uid_merge_list` varchar(1024) DEFAULT NULL;
ALTER TABLE `istanze_abbonamenti` CHANGE COLUMN `note` `note` varchar(2048) DEFAULT NULL;

###

update anagrafiche a1 join anagrafiche a2 on a1.id=a2.id_anagrafica_da_aggiornare
	set a1.necessita_verifica = true
	where a1.necessita_verifica = false;

###

ALTER TABLE `fatture` ADD COLUMN `email_pec` varchar(256) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `codice_destinatario` varchar(8) DEFAULT NULL;

###

ALTER TABLE `anagrafiche` DROP COLUMN `giunti_card`;
ALTER TABLE `anagrafiche` ADD COLUMN `giunti_card_club` varchar(16) DEFAULT NULL;

###

ALTER TABLE `anagrafiche` ADD COLUMN `cuf` varchar(8) DEFAULT NULL;

###

#ALTER TABLE listini DROP COLUMN permetti_pagante;
ALTER TABLE comunicazioni ADD COLUMN solo_senza_pagante bit(1) NOT NULL default false;
ALTER TABLE comunicazioni ADD COLUMN solo_con_pagante bit(1) NOT NULL default false;
ALTER TABLE rinnovi_massivi ADD COLUMN solo_senza_pagante bit(1) NOT NULL default false;
ALTER TABLE rinnovi_massivi ADD COLUMN solo_con_pagante bit(1) NOT NULL default false;

###

DELETE FROM config where id = 'magnewsAccessToken';
INSERT INTO config (id,valore) VALUES ("magnewsAccessToken","4921A3485E366E8535AF4F7520B8777DF2310884A7BB00B96CAC5ACED8F3C235C84D3B0B1652DCD078CB194AA16D3C70F5530985C7988265599DD858A5987B74550067F78913C6DB68207B350C8B47913C8F23F52AD180E9E8364466C7D936485822475E415C5A7F875BF52EEF6B60FCCCC8D2E585472E29288517172C4D8CF69A783");
DELETE FROM config where id = 'magnewsRefreshToken';
INSERT INTO config (id,valore) VALUES ("magnewsRefreshToken","4921A2ED4BB33D11C93D1758104CEDBE48FC3B666BB99B1CC01C5038FBBADCD75AEF97EFA8A356800BE9AD1BF1CCB6F06592FFCDDD69E8AA592D3A09505F863D4E444148ED50D1511C49CD27D3BE08312633191C88CF690C02E2AC430666C74AB685C6CCF1F14C32BF75B21937102C55D47FD644925A4C2B12EFF29AE6C0FC213B06C");
ALTER TABLE `comunicazioni` CHANGE COLUMN `id_bandella` `id_bandella` varchar(16) DEFAULT NULL;
ALTER TABLE `comunicazioni` ADD COLUMN `oggetto_messaggio` varchar(64) DEFAULT NULL;
