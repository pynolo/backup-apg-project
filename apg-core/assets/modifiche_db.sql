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

#Postponed query
update anagrafiche set data_creazione = data_modifica where data_creazione is null;
