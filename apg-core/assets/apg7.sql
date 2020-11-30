#NOTE DI RILASCIO
#Le istanze di abbonamento riporteranno DATA inizio/fine e non più fascicolo inizio/fine
#Le vecchie statistiche di invio saranno perdute
#Fascicoli e articoli convergono in 'materiali'
#le comunicazioni non riporteranno fascicolo_fine/inizio ma data_fine/inizio
#nei file invio è presente la data fine abb invece che il fascicolo fine
#tutti i tipi abbonamenti saranno resettati a 12 mesi, biennali o ridotti andranno modificati manualmente
#rimosse tutte le API precedenti a 4.x

# verso fascicoli
ALTER TABLE `evasioni_fascicoli` drop foreign key evasioni_fascicoli_ibfk_1; #SLOW
ALTER TABLE `evasioni_comunicazioni` drop foreign key evasioni_comunicazioni_ibfk_3;
# verso articoli
ALTER TABLE `articoli_listini` drop foreign key articoli_listini_ibfk_2;
ALTER TABLE `articoli_opzioni` drop foreign key articoli_opzioni_ibfk_2;

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
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE `materiali` ADD INDEX `id_materiale_idx` (`id_materiale`);

DROP TABLE IF EXISTS `materiali`;
CREATE TABLE `materiali` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `codice_meccanografico` varchar(32) NOT NULL,
  `titolo` varchar(64) NOT NULL,
  `sottotitolo` varchar(64) DEFAULT NULL,
  `in_attesa` bit(1) NOT NULL DEFAULT true,
  `id_tipo_anagrafica_sap` varchar(4) NOT NULL,
  `id_tipo_materiale` varchar(4) NOT NULL,
  `data_limite_visibilita` date DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `id_fascicolo` int(11) DEFAULT NULL,
  `id_articolo` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE `materiali` ADD INDEX `materiali_cm_idx` (`codice_meccanografico`);
ALTER TABLE `materiali` ADD INDEX `materiali_cm_titolo_sottotitolo_idx` (`codice_meccanografico`, `titolo`, `sottotitolo`);
ALTER TABLE `materiali` ADD INDEX `materiali_id_articolo` (`id_articolo`);
ALTER TABLE `materiali` ADD INDEX `materiali_id_fascicolo` (`id_fascicolo`);

DROP TABLE IF EXISTS `materiali_spedizione`;
CREATE TABLE `materiali_spedizione` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_materiale` int(11) NOT NULL,
  `id_abbonamento` int(11) DEFAULT NULL,
  `id_materiale_listino` int(11) DEFAULT NULL,
  `id_materiale_opzione` int(11) DEFAULT NULL,
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
  `rispedizione` bit(1) NOT NULL DEFAULT b'0',
  `note` tinytext DEFAULT NULL,
  `id_utente` varchar(32) DEFAULT NULL,
  `update_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_fascicolo` int(11) DEFAULT NULL,
  `id_articolo` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE `materiali_spedizione` ADD INDEX `materiali_spedizione_abbonamento_idx` (`id_abbonamento`);
ALTER TABLE `materiali_spedizione` ADD INDEX `materiali_spedizione_anagrafica_abbonamento_idx` (`id_anagrafica`, `id_abbonamento`);

INSERT INTO materiali_spedizione 
	(id_materiale,id_abbonamento,id_materiale_listino,id_materiale_opzione,id_anagrafica,data_creazione,
	data_invio,copie,prenotazione_istanza_futura,data_limite,id_ordine_logistica,data_conferma_evasione,
	data_annullamento,note,id_utente,id_articolo) SELECT 
	0,id_abbonamento,id_articolo_listino,id_articolo_opzione,id_anagrafica,data_creazione,
	data_invio,copie,prenotazione_istanza_futura,data_limite,id_ordine_logistica,data_conferma_evasione,
	data_annullamento,note,id_utente,id_articolo
	FROM evasioni_articoli;
INSERT INTO materiali_spedizione
	(id_materiale,id_abbonamento,id_anagrafica,data_creazione,data_invio,copie,id_ordine_logistica,
	data_ordine,data_conferma_evasione,note,id_utente,id_fascicolo) SELECT 
	0,id_abbonamento,id_anagrafica,data_creazione,data_invio,copie,id_ordine_logistica,
	data_ordine,data_conferma_evasione,note,id_utente,id_fascicolo
	FROM evasioni_fascicoli; #SLOW

	
# ALTER STRUCTURES

ALTER TABLE `articoli_opzioni` RENAME `materiali_opzioni`;
ALTER TABLE `articoli_listini` RENAME `materiali_listini`;

ALTER TABLE `istanze_abbonamenti` 
	ADD COLUMN `data_inizio` date DEFAULT NULL,
	ADD COLUMN `data_fine` date DEFAULT NULL,
	CHANGE COLUMN `id_fascicolo_inizio` `id_fascicolo_inizio` int(11) DEFAULT NULL,
	CHANGE COLUMN `id_fascicolo_fine` `id_fascicolo_fine` int(11) DEFAULT NULL;
ALTER TABLE `evasioni_comunicazioni`
	ADD COLUMN `id_materiale_programmazione` int(11) DEFAULT NULL;
ALTER TABLE `comunicazioni`
	ADD COLUMN `solo_con_data_inizio` date DEFAULT NULL,
	ADD COLUMN `mesi_da_inizio_o_fine` int(11) NOT NULL,
	CHANGE COLUMN `numeri_da_inizio_o_fine` `numeri_da_inizio_o_fine` int(11) DEFAULT NULL;
ALTER TABLE `materiali_listini` ADD COLUMN `id_materiale` int(11) DEFAULT NULL;
ALTER TABLE `materiali_opzioni` ADD COLUMN `id_materiale` int(11) DEFAULT NULL;
ALTER TABLE `rinnovi_massivi` ADD COLUMN `data_inizio` date DEFAULT NULL;
ALTER TABLE `listini` ADD COLUMN `durata_mesi` int(11) NOT NULL DEFAULT 12,
	ADD COLUMN `gracing_iniziale_mesi` int(11) NOT NULL DEFAULT 0,
	ADD COLUMN `gracing_finale_mesi` int(11) NOT NULL DEFAULT 0,
	CHANGE COLUMN `gracing_iniziale` `gracing_iniziale` int(11) DEFAULT NULL,
	CHANGE COLUMN `gracing_finale` `gracing_finale` int(11) DEFAULT NULL;
UPDATE listini SET durata_mesi = 12 WHERE durata_mesi is null;
UPDATE listini SET gracing_iniziale_mesi = 0 WHERE gracing_iniziale_mesi is null;
UPDATE listini SET gracing_finale_mesi = 0 WHERE gracing_finale_mesi is null;

ALTER TABLE `materiali_listini` CHANGE COLUMN `id_articolo` `id_articolo` INT(11) NULL;
ALTER TABLE `materiali_opzioni` CHANGE COLUMN `id_articolo` `id_articolo` INT(11) NULL;

# Da lanciare dopo la migrazione dei dati in Migrationto7
ALTER TABLE `istanze_abbonamenti` 
	CHANGE COLUMN `data_inizio` `data_inizio` date NOT NULL,
	CHANGE COLUMN `data_fine` `data_inizio` date NOT NULL;
ALTER TABLE `materiali_listini` CHANGE COLUMN `id_materiale` `id_materiale` INT(11) NOT NULL ;
ALTER TABLE `materiali_opzioni` CHANGE COLUMN `id_materiale` `id_materiale` INT(11) NOT NULL ;


	
# PULIZIA FINALE



#ALTER TABLE `istanze_abbonamenti` DROP COLUMN `fascicoli_spediti`,
#	DROP COLUMN `fascicoli_totali`,
#	DROP COLUMN `id_fascicolo_inizio`,
#	DROP COLUMN `id_fascicolo_fine`,
#   DROP COLUMN `gracing_iniziale`,
#	DROP COLUMN `gracing_finale`;
#ALTER TABLE `evasioni_comunicazioni` DROP COLUMN `id_fascicolo`;
#ALTER TABLE `rinnovi_massivi` DROP COLUMN `id_fascicolo_inizio`;
#ALTER TABLE `materiali_spedizione` DROP COLUMN `id_fascicolo`,
#	DROP COLUMN `id_articolo`;
#ALTER TABLE `comunicazioni` DROP COLUMN `numeri_da_inizio_o_fine`;
#DROP TABLE `stat_invio`;
#DROP TABLE `stat_abbonati`;
#DROP TABLE `fascicoli`;
#DROP TABLE `articoli`;
#DROP TABLE `evasioni_fascicoli`;
#DROP TABLE `evasioni_articoli`;


