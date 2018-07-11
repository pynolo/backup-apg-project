ALTER TABLE fatture ADD UNIQUE (numero_fattura);
update pagamenti p, istanze_abbonamenti ia set p.id_anagrafica=ia.id_pagante where
	p.id_istanza_abbonamento = ia.id and
	p.id_anagrafica is null and ia.id_pagante is not null;
update pagamenti p, istanze_abbonamenti ia set p.id_anagrafica=ia.id_abbonato where
	p.id_istanza_abbonamento = ia.id and
	p.id_anagrafica is null and ia.id_pagante is null;
	
***

ALTER TABLE `articoli` ADD COLUMN `id_tipo_anagrafica_sap` varchar(4) NOT NULL;
ALTER TABLE `fascicoli` ADD COLUMN `id_tipo_anagrafica_sap` varchar(4) NOT NULL;
update articoli set id_tipo_anagrafica_sap = 'LI' where id_societa = 'GE' and id_tipo_articolo = 'LIB';
update articoli set id_tipo_anagrafica_sap = 'RG' where id_societa = 'GE' and id_tipo_articolo = 'FAS';
update articoli set id_tipo_anagrafica_sap = 'RS' where id_societa = 'GS' and id_tipo_articolo = 'FAS';
update fascicoli f, periodici p set f.id_tipo_anagrafica_sap = 'RS' where 
	f.id_periodico=p.id and p.id_societa='GS' and f.id_tipo_articolo = 'FAS';
update fascicoli f, periodici p set f.id_tipo_anagrafica_sap = 'RG' where 
	f.id_periodico=p.id and p.id_societa='GE' and f.id_tipo_articolo = 'FAS';
update fascicoli set id_tipo_anagrafica_sap = 'LI' where id_tipo_articolo = 'LIB';
#ALTER TABLE `articoli` DROP COLUMN `id_tipo_articolo`;
#ALTER TABLE `articoli` DROP COLUMN `id_societa`;
#ALTER TABLE `fascicoli` DROP COLUMN `id_tipo_articolo`;
#ALTER TABLE `istanza_abbonamento` DROP COLUMN `id_stampa_fattura`;

***

select * from fatture f1 where f1.id_nota_credito_storno_resto is not null and f1.id_nota_credito_storno_resto not in (select f2.id from fatture f2);
select * from fatture f1 where f1.id_nota_credito_storno is not null and f1.id_nota_credito_storno not in (select f2.id from fatture f2);
select * from fatture f1 where f1.id_nota_credito_rimborso is not null and f1.id_nota_credito_rimborso not in (select f2.id from fatture f2);
select * from fatture f1 where f1.id_nota_credito_rimborso_resto is not null and f1.id_nota_credito_rimborso_resto not in (select f2.id from fatture f2);
ALTER TABLE fatture ADD FOREIGN KEY (id_nota_credito_storno_resto) REFERENCES fatture(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_nota_credito_storno) REFERENCES fatture(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_nota_credito_rimborso) REFERENCES fatture(id);
ALTER TABLE fatture ADD FOREIGN KEY (id_nota_credito_rimborso_resto) REFERENCES fatture(id);

***

CREATE TABLE `file_uploads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` mediumblob NOT NULL,
  `mime_type` varchar(128) NOT NULL,
  `file_name` varchar(256) NOT NULL,
  `data_creazione` datetime DEFAULT NULL,
  `id_tipo_file` varchar(4) NOT NULL,
  `id_utente` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

***

update listini l, tipi_abbonamento ta set l.digitale = true
	where l.id_tipo_abbonamento=ta.id and
	ta.id_periodico = 1 and (l.data_fine >= '2017-05-01' or l.data_fine is null);
update listini l, tipi_abbonamento ta set l.digitale = true
	where l.id_tipo_abbonamento=ta.id and
	ta.id_periodico = 4 and (l.data_fine >= '2017-05-01' or l.data_fine is null);
update listini l, tipi_abbonamento ta set l.digitale = true
	where l.id_tipo_abbonamento=ta.id and
	ta.id_periodico = 7 and (l.data_fine >= '2017-05-01' or l.data_fine is null);
update listini l, tipi_abbonamento ta set l.digitale = true
	where l.id_tipo_abbonamento=ta.id and
	ta.id_periodico = 11 and (l.data_fine >= '2017-05-01' or l.data_fine is null);

***

update localita set cap='51024' where (nome='ABETONE' and cap='51021');
update localita set cap='51024' where (nome='LE REGINE' and cap='51021');
update localita set cap='51028' where (nome='PITEGLIO' and cap='51020');
update localita set cap='51028' where (nome='CALAMECCA' and cap='51020');
update localita set cap='51028' where (nome='CRESPOLE' and cap='51020');
update localita set cap='51028' where (nome='LA LIMA' and cap='51020');
update localita set cap='51028' where (nome='POPIGLIO' and cap='51020');
update localita set cap='51028' where (nome='PRATACCIO' and cap='51020');
update localita set cap='51028' where (nome='PRUNETTA' and cap='51020');
update localita set cap='53024' where (nome='SAN GIOVANNI D\'ASSO' and cap='53020');
update localita set cap='53024' where (nome='MONTISI' and cap='53020');
insert into localita (cap,nome,id_provincia,modifica_propagata,data_modifica) values ('62031','VALFORNACE','MC',false,'2017-05-23');
update localita set cap='62031' where (nome='FIORDIMONTE' and cap='62035');
update localita set cap='62031' where (nome='PIEVEBOVIGLIANA' and cap='62035');
insert into localita (cap,nome,id_provincia,modifica_propagata,data_modifica) values ('61038','TERRE ROVERESCHE','PU',false,'2017-05-23');
update localita set cap='61038' where (nome='BARCHI' and cap='61040');
update localita set cap='61038' where (nome='PIAGGE' and cap='61030');
update localita set cap='61038' where (nome='SAN GIORGIO DI PESARO' and cap='61030');
insert into localita (cap,nome,id_provincia,modifica_propagata,data_modifica) values ('61036','COLLI AL METAURO','PU',false,'2017-05-23');
update localita set cap='61036' where (nome='MONTEMAGGIORE AL METAURO' and cap='61030');
update localita set cap='61036' where (nome='SALTARA' and cap='61030');
update localita set cap='61036' where (nome='SERRUNGARINA' and cap='61030');
update localita set cap='61036' where (nome='VILLANOVA' and cap='61030');
update localita set cap='61036' where (nome='CALCINELLI' and cap='61030');
update localita set cap='61036' where (nome='TAVERNELLE' and cap='61030');
insert into localita (cap,nome,id_provincia,modifica_propagata,data_modifica) values ('44047','TERRE DEL RENO','FE',false,'2017-05-23');
update localita set cap='44047' where (nome='MIRABELLO' and cap='44043');
update localita set cap='22042' where (nome='SAN FERMO DELLA BATTAGLIA' and cap='22020');
update localita set cap='22042' where (nome='CAVALLASCA' and cap='22020');
insert into localita (cap,nome,id_provincia,modifica_propagata,data_modifica) values ('22024','ALTA VALLE INTELVI','CO',false,'2017-05-23');
update localita set cap='22024' where (nome='PELLIO INTELVI' and cap='22020');
update localita set cap='22024' where (nome='RAMPONIO VERNA' and cap='22020');
update localita set cap='22024' where (nome='PELLIO' and cap='22020');
update localita set cap='13843' where (nome='SELVE MARCONE' and cap='13841');
#
update indirizzi set cap='51024' where (localita='ABETONE' and cap='51021');
update indirizzi set cap='51024' where (localita='LE REGINE' and cap='51021');
update indirizzi set cap='51028' where (localita='PITEGLIO' and cap='51020');
update indirizzi set cap='51028' where (localita='CALAMECCA' and cap='51020');
update indirizzi set cap='51028' where (localita='CRESPOLE' and cap='51020');
update indirizzi set cap='51028' where (localita='LA LIMA' and cap='51020');
update indirizzi set cap='51028' where (localita='POPIGLIO' and cap='51020');
update indirizzi set cap='51028' where (localita='PRATACCIO' and cap='51020');
update indirizzi set cap='51028' where (localita='PRUNETTA' and cap='51020');
update indirizzi set cap='53024' where (localita='SAN GIOVANNI D\'ASSO' and cap='53020');
update indirizzi set cap='53024' where (localita='MONTISI' and cap='53020');
update indirizzi set cap='62031' where (localita='FIORDIMONTE' and cap='62035');
update indirizzi set cap='62031' where (localita='PIEVEBOVIGLIANA' and cap='62035');
update indirizzi set cap='61038' where (localita='BARCHI' and cap='61040');
update indirizzi set cap='61038' where (localita='ORCIANO DI PESARO' and cap='61038');
update indirizzi set cap='61038' where (localita='PIAGGE' and cap='61030');
update indirizzi set cap='61038' where (localita='SAN GIORGIO DI PESARO' and cap='61030');
update indirizzi set cap='61036' where (localita='MONTEMAGGIORE AL METAURO' and cap='61030');
update indirizzi set cap='61036' where (localita='SALTARA' and cap='61030');
update indirizzi set cap='61036' where (localita='SERRUNGARINA' and cap='61030');
update indirizzi set cap='61036' where (localita='VILLANOVA' and cap='61030');
update indirizzi set cap='61036' where (localita='CALCINELLI' and cap='61030');
update indirizzi set cap='61036' where (localita='TAVERNELLE' and cap='61030');
update indirizzi set cap='44047' where (localita='MIRABELLO' and cap='44043');
update indirizzi set cap='22042' where (localita='SAN FERMO DELLA BATTAGLIA' and cap='22020');
update indirizzi set cap='22042' where (localita='CAVALLASCA' and cap='22020');
update indirizzi set cap='22024' where (localita='PELLIO INTELVI' and cap='22020');
update indirizzi set cap='22024' where (localita='RAMPONIO VERNA' and cap='22020');
update indirizzi set cap='22024' where (localita='PELLIO' and cap='22020');
update indirizzi set cap='13843' where (localita='SELVE MARCONE' and cap='13841');

***

ALTER TABLE opzioni ADD CONSTRAINT `UidUnique` UNIQUE (`uid`);

***

ALTER TABLE avvisi ADD COLUMN data_manutenzione DATE DEFAULT NULL;
ALTER TABLE avvisi ADD COLUMN ora_inizio time DEFAULT NULL;
ALTER TABLE avvisi ADD COLUMN ora_fine time DEFAULT NULL;
ALTER TABLE utenti ADD COLUMN heartbeat DATETIME DEFAULT NULL;

update ruoli set descrizione='Operatore' where id=1;
update ruoli set descrizione='Supervisore' where id=2;

***

ALTER TABLE `evasioni_comunicazioni` DROP COLUMN `estratto_come_annullato`;
ALTER TABLE avvisi CHANGE COLUMN messaggio messaggio varchar(1024) NOT NULL;

**

ALTER TABLE `societa` DROP COLUMN `prefisso_fatture_cartacee`;

***

ALTER TABLE `fatture` CHANGE COLUMN `id_istanza` `id_istanza_abbonamento` int(11) DEFAULT NULL;
ALTER TABLE `opzioni_istanze_abbonamenti` CHANGE COLUMN `id_istanza` `id_istanza_abbonamento` int(11) NOT NULL;

***

ALTER TABLE anagrafiche ADD COLUMN data_nascita DATE DEFAULT NULL;
ALTER TABLE anagrafiche ADD COLUMN data_creazione DATETIME DEFAULT NULL;
#update anagrafiche set data_creazione = 
#	(select min(ia.data_creazione) from istanze_abbonamenti as ia 
#		where anagrafiche.id = ia.id_abbonato or anagrafiche.id = ia.id_pagante);

***

update anagrafiche set data_creazione = data_modifica where data_creazione is null;

***

alter table tipi_abbonamento ADD COLUMN delta_inizio_blocco_offerta int(11) DEFAULT NULL;
alter table tipi_abbonamento ADD COLUMN delta_inizio_avviso_pagamento int(11) DEFAULT NULL;
alter table tipi_abbonamento ADD COLUMN delta_inizio_pagamento_automatico int(11) DEFAULT NULL;
alter table tipi_abbonamento ADD COLUMN delta_fine_rinnovo_abilitato int(11) DEFAULT NULL;
alter table tipi_abbonamento ADD COLUMN delta_fine_avviso_rinnovo int(11) DEFAULT NULL;
alter table tipi_abbonamento ADD COLUMN delta_fine_rinnovo_automatico int(11) DEFAULT NULL;

#*** rimozione oggetti orfani ***
#update istanze_abbonamenti set id_pagante = null where
#	id_pagante is not null and
#	id_pagante not in (select ana.id from anagrafiche ana);
#update istanze_abbonamenti set note = 'REMOVE' where
#	id_abbonato is not null and
#	id_abbonato not in (select ana.id from anagrafiche ana);
#delete from pagamenti where id_anagrafica not in (select ana.id from anagrafiche ana);
#delete pagamenti from pagamenti 
#	inner join istanze_abbonamenti on pagamenti.id_istanza_abbonamento = istanze_abbonamenti.id where
#	istanze_abbonamenti.note = 'REMOVE';
#delete evasioni_comunicazioni from evasioni_comunicazioni 
#	inner join istanze_abbonamenti on evasioni_comunicazioni.id_istanza_abbonamento = istanze_abbonamenti.id where
#	istanze_abbonamenti.note = 'REMOVE';
#delete opzioni_istanze_abbonamenti from opzioni_istanze_abbonamenti 
#	inner join istanze_abbonamenti on opzioni_istanze_abbonamenti.id_istanza_abbonamento = istanze_abbonamenti.id where
#	istanze_abbonamenti.note = 'REMOVE';
#delete from istanze_abbonamenti where note = 'REMOVE';

***

alter table anagrafiche ADD COLUMN consenso_tos bit(1) NOT NULL DEFAULT true;
alter table anagrafiche ADD COLUMN consenso_marketing bit(1) NOT NULL DEFAULT false;
alter table anagrafiche ADD COLUMN consenso_profilazione bit(1) NOT NULL DEFAULT false;
alter table anagrafiche ADD COLUMN data_aggiornamento_consenso DATE NOT NULL;
update anagrafiche set data_aggiornamento_consenso = DATE(data_creazione) where 
	data_aggiornamento_consenso is null;
update anagrafiche set consenso_tos = true, consenso_marketing = false, consenso_profilazione = false;
alter table log_editing ADD COLUMN `entity_uid` varchar(16) NOT NULL;
DROP TABLE IF EXISTS `log_deletion`;
CREATE TABLE `log_deletion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `entity_name` varchar(64) NOT NULL,
  `entity_id` int(11) NOT NULL,
  `entity_uid` varchar(16) NOT NULL,
  `log_datetime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_utente` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
update log_editing set entity_uid = entity_id where entity_uid is null;

***

alter table evasioni_comunicazioni ADD COLUMN credito_scalato decimal(9,2) DEFAULT NULL;

***

#ESTRAZIONE PAGAMENTI PER OS
select pag.id_tipo_pagamento, pag.data_pagamento, abb.codice_abbonamento, ia.id as uid, ff.data_fine from pagamenti pag, istanze_abbonamenti ia, abbonamenti abb, fascicoli ff where
	pag.id_istanza_abbonamento = ia.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonamento = abb.id and
	ff.data_fine > '2018-03-15' and
	pag.id_tipo_pagamento is not null and
	abb.codice_abbonamento like 'B%' and
	ia.invio_bloccato = false
	order by pag.data_pagamento desc
	
***

ALTER TABLE istanze_abbonamenti ADD COLUMN data_job DATETIME DEFAULT NULL;

***

CREATE TABLE `cache_crm` (
	id_anagrafica int(11) NOT NULL,
	modified_date date NOT NULL,
	customer_type varchar(4) DEFAULT NULL,
	own_subscription_identifier_0 varchar(16) DEFAULT NULL,
	own_subscription_blocked_0 bit(1) NOT NULL,
	own_subscription_begin_0 date DEFAULT NULL,
	own_subscription_end_0 date DEFAULT NULL,
	gift_subscription_end_0 date DEFAULT NULL,
	subscription_creation_date_0 date DEFAULT NULL,
	own_subscription_identifier_1 varchar(16) DEFAULT NULL,
	own_subscription_blocked_1 bit(1) NOT NULL,
	own_subscription_begin_1 date DEFAULT NULL,
	own_subscription_end_1 date DEFAULT NULL,
	gift_subscription_end_1 date DEFAULT NULL,
	subscription_creation_date_1 date DEFAULT NULL,
	own_subscription_identifier_2 varchar(16) DEFAULT NULL,
	own_subscription_blocked_2 bit(1) NOT NULL,
	own_subscription_begin_2 date DEFAULT NULL,
	own_subscription_end_2 date DEFAULT NULL,
	gift_subscription_end_2 date DEFAULT NULL,
	subscription_creation_date_2 date DEFAULT NULL,
	own_subscription_identifier_3 varchar(16) DEFAULT NULL,
	own_subscription_blocked_3 bit(1) NOT NULL,
	own_subscription_begin_3 date DEFAULT NULL,
	own_subscription_end_3 date DEFAULT NULL,
	gift_subscription_end_3 date DEFAULT NULL,
	subscription_creation_date_3 date DEFAULT NULL,
	own_subscription_identifier_4 varchar(16) DEFAULT NULL,
	own_subscription_blocked_4 bit(1) NOT NULL,
	own_subscription_begin_4 date DEFAULT NULL,
	own_subscription_end_4 date DEFAULT NULL,
	gift_subscription_end_4 date DEFAULT NULL,
	subscription_creation_date_4 date DEFAULT NULL,
	own_subscription_identifier_5 varchar(16) DEFAULT NULL,
	own_subscription_blocked_5 bit(1) NOT NULL,
	own_subscription_begin_5 date DEFAULT NULL,
	own_subscription_end_5 date DEFAULT NULL,
	gift_subscription_end_5 date DEFAULT NULL,
	subscription_creation_date_5 date DEFAULT NULL,
	own_subscription_identifier_6 varchar(16) DEFAULT NULL,
	own_subscription_blocked_6 bit(1) NOT NULL,
	own_subscription_begin_6 date DEFAULT NULL,
	own_subscription_end_6 date DEFAULT NULL,
	gift_subscription_end_6 date DEFAULT NULL,
	subscription_creation_date_6 date DEFAULT NULL,
	own_subscription_identifier_7 varchar(16) DEFAULT NULL,
	own_subscription_blocked_7 bit(1) NOT NULL,
	own_subscription_begin_7 date DEFAULT NULL,
	own_subscription_end_7 date DEFAULT NULL,
	gift_subscription_end_7 date DEFAULT NULL,
	subscription_creation_date_7 date DEFAULT NULL,
  PRIMARY KEY (`id_anagrafica`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table `abbonamenti` modify column `data_modifica` datetime NOT NULL DEFAULT '1970-01-01 00:00:00';
alter table `anagrafiche` modify column `data_modifica` datetime NOT NULL DEFAULT '1970-01-01 00:00:00';
alter table `istanze_abbonamenti` modify column `data_modifica` datetime NOT NULL DEFAULT '1970-01-01 00:00:00';

