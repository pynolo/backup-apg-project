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

ALTER TABLE `fatture` ADD COLUMN `email_pec` varchar(256) DEFAULT NULL;
ALTER TABLE `fatture` ADD COLUMN `codice_destinatario` varchar(8) DEFAULT NULL;