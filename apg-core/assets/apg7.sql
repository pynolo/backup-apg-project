ALTER TABLE `evasioni_comunicazioni` drop foreign key evasioni_comunicazioni_ibfk_3;
ALTER TABLE `evasioni_fascicoli` drop foreign key evasioni_fascicoli_ibfk_1;

# NUOVE TABELLE

DROP TABLE IF EXISTS `materiali_programmazione`;
CREATE TABLE `materiali_programmazione` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_materiale` int(11) NOT NULL,
  `id_periodico` int(11) NOT NULL,
  `id_opzione` int(11) DEFAULT NULL,
  `data_nominale` date NOT NULL,
  `data_estrazione` date DEFAULT NULL,
  `comunicazioni_inviate` bit(1) NOT NULL DEFAULT false,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;
INSERT INTO materiali_programmazione SELECT 
	id,id,id_periodico,id_opzione,data_inizio,data_estrazione,comunicazioni_inviate FROM fascicoli;

DROP TABLE IF EXISTS `materiali`;
CREATE TABLE `materiali` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codice_meccanografico` varchar(32) NOT NULL,
  `titolo` varchar(64) NOT NULL,
  `sottotitolo` varchar(64) DEFAULT NULL,
  `in_attesa` bit(1) NOT NULL DEFAULT true,
  `id_tipo_anagrafica_sap` varchar(4) NOT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;
INSERT INTO materiali SELECT 
	id,codice_meccanografico,titolo_numero,data_cop,in_attesa,id_tipo_anagrafica_sap,note FROM fascicoli;

DROP TABLE IF EXISTS `materiali_spedizione`;
CREATE TABLE `materiali_spedizione` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_materiale` int(11) NOT NULL, //id_fascicolo
  `id_abbonamento` int(11) DEFAULT NULL,
  `id_articolo_listino` int(11) DEFAULT NULL,
  `id_articolo_opzione` int(11) DEFAULT NULL,
  `id_anagrafica` int(11) NOT NULL,
  `data_creazione` date NOT NULL,
  `data_invio` datetime DEFAULT NULL,
  `copie` int(11) NOT NULL,
  `prenotazione_istanza_futura` bit(1) NOT NULL DEFAULT b'0',
  `data_limite` date DEFAULT NULL,
  `data_annullamento` datetime DEFAULT NULL,
  `id_ordine_logistica` int(11) DEFAULT NULL,
  `data_ordine` datetime DEFAULT NULL,
  `data_conferma_evasione` datetime DEFAULT NULL,
  `note` tinytext DEFAULT NULL,
  `update_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;
INSERT INTO materiali_spedizione (id_materiale,id_abbonamento,id_articolo_listino,id_articolo_opzione,id_anagrafica,data_creazione,data_invio,copie,prenotazione_istanza_futura,data_limite,id_ordine_logistica,data_conferma_evasione,data_annullamento,note) SELECT 
	id_articolo,id_abbonamento,id_articolo_listino,id_articolo_opzione,id_anagrafica,data_creazione,data_invio,copie,prenotazione_istanza_futura,data_limite,id_ordine_logistica,data_conferma_evasione,data_annullamento,note
	FROM evasioni_articoli;
INSERT INTO materiali_spedizione (id_materiale,id_abbonamento,id_anagrafica,data_creazione,data_invio,copie,id_ordine_logistica,data_ordine,data_conferma_evasione,note) SELECT 
	id_articolo,id_abbonamento,id_anagrafica,data_creazione,data_invio,copie,id_ordine_logistica,data_ordine,data_conferma_evasione,note
	FROM evasioni_fascicoli;

# ID DA MIGRARE

ALTER TABLE `istanze_abbonamenti` 
	ADD COLUMN `data_inizio` date NOT NULL,
	ADD COLUMN `data_fine` date NOT NULL,
	CHANGE COLUMN `id_fascicolo_inizio` int(11) DEFAULT NULL,
	CHANGE COLUMN `id_fascicolo_fine` int(11) DEFAULT NULL;
ALTER TABLE `evasioni_comunicazioni` ADD COLUMN `id_materiale_programmazione` int(11) DEFAULT NULL;

#ALTER TABLE `istanze_abbonamenti` DROP COLUMN `fascicoli_spediti`,
#	DROP COLUMN `fascicoli_totali`;
#ALTER TABLE `evasioni_comunicazioni` DROP COLUMN `id_fascicolo`; 
#DROP TABLE `fascicoli`;
#DROP TABLE `articoli`;
#DROP TABLE `evasioni_fascicoli`;
#DROP TABLE `evasioni_articoli`;
