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

#Esportazione cache_crm
select ana.uid as 'id_customer', ind.titolo as 'address_title', ind.nome as 'address_first_name', 
		ind.cognome_ragione_sociale as 'address_last_name_company', ind.presso as 'address_co',
		ind.indirizzo as 'address_address', ind.localita as 'address_locality', 
		ind.id_provincia as 'address_province', ind.cap as 'address_zip',
		naz.sigla_nazione as 'address_country_code', ana.sesso as 'sex',
		ana.codice_fiscale as 'cod_fisc', ana.partita_iva as 'piva', ana.tel_mobile as 'phone_mobile',
		ana.tel_casa as 'phone_landline', ana.email_primaria as 'email_primary',
		ana.id_professione as 'id_job', ana.id_titolo_studio as 'id_qualification', 
		ana.id_tipo_anagrafica as 'id_tipo_anagrafica', ana.data_nascita as 'birth_date',
		ana.consenso_tos as 'consent_tos', ana.consenso_marketing as 'consent_marketing',
		ana.consenso_profilazione as 'consent_profiling', ana.data_aggiornamento_consenso as 'consent_update_date',
		ana.data_creazione as 'creation_date', cc.modified_date, cc.customer_type,
		 cc.own_subscription_identifier_0, cc.own_subscription_blocked_0, cc.own_subscription_begin_0,
		cc.own_subscription_end_0, cc.gift_subscription_end_0, cc.subscription_creation_date_0,
		 cc.own_subscription_identifier_1, cc.own_subscription_blocked_1, cc.own_subscription_begin_1,
		cc.own_subscription_end_1, cc.gift_subscription_end_1, cc.subscription_creation_date_1,
		 cc.own_subscription_identifier_2, cc.own_subscription_blocked_2, cc.own_subscription_begin_2,
		cc.own_subscription_end_2, cc.gift_subscription_end_2, cc.subscription_creation_date_2,
		 cc.own_subscription_identifier_3, cc.own_subscription_blocked_3, cc.own_subscription_begin_3,
		cc.own_subscription_end_3, cc.gift_subscription_end_3, cc.subscription_creation_date_3,
		 cc.own_subscription_identifier_4, cc.own_subscription_blocked_4, cc.own_subscription_begin_4,
		cc.own_subscription_end_4, cc.gift_subscription_end_4, cc.subscription_creation_date_4,
		 cc.own_subscription_identifier_5, cc.own_subscription_blocked_5, cc.own_subscription_begin_5,
		cc.own_subscription_end_5, cc.gift_subscription_end_5, cc.subscription_creation_date_5,
		 cc.own_subscription_identifier_6, cc.own_subscription_blocked_6, cc.own_subscription_begin_6,
		cc.own_subscription_end_6, cc.gift_subscription_end_6, cc.subscription_creation_date_6,
		 cc.own_subscription_identifier_7, cc.own_subscription_blocked_7, cc.own_subscription_begin_7,
		cc.own_subscription_end_7, cc.gift_subscription_end_7, cc.subscription_creation_date_7
	from anagrafiche ana, indirizzi ind, nazioni naz, cache_crm cc
	where ana.id_indirizzo_principale=ind.id and ind.id_nazione=naz.id and cc.id_anagrafica=ana.id
	order by ana.id;

###

ALTER TABLE `anagrafiche` ADD COLUMN `pa` bit(1) NOT NULL DEFAULT false;

DELETE FROM config where id = 'magnewsAccessToken';
INSERT INTO config (id,valore) VALUES ("magnewsAccessToken","4921A3485E366E8535AF4F7520B8777DF2310884A7BB00B96CAC5ACED8F3C235C84D3B0B1652DCD078CB194AA16D3C70F5530985C7988265599DD858A5987B74550067F78913C6DB68207B350C8B47913C8F23F52AD180E9E8364466C7D936485822475E415C5A7F875BF52EEF6B60FCCCC8D2E585472E29288517172C4D8CF69A783");
DELETE FROM config where id = 'magnewsRefreshToken';
INSERT INTO config (id,valore) VALUES ("magnewsRefreshToken","4921A2ED4BB33D11C93D1758104CEDBE48FC3B666BB99B1CC01C5038FBBADCD75AEF97EFA8A356800BE9AD1BF1CCB6F06592FFCDDD69E8AA592D3A09505F863D4E444148ED50D1511C49CD27D3BE08312633191C88CF690C02E2AC430666C74AB685C6CCF1F14C32BF75B21937102C55D47FD644925A4C2B12EFF29AE6C0FC213B06C");
ALTER TABLE `comunicazioni` CHANGE COLUMN `id_bandella` `id_bandella` varchar(16) DEFAULT NULL;
ALTER TABLE `comunicazioni` ADD COLUMN `oggetto_messaggio` varchar(64) DEFAULT NULL;

###

insert into professioni(id, nome) values ('56', 'Formatore');

###

ALTER TABLE `cache_crm` CHANGE COLUMN `modified_date` `modified_date` datetime default NULL;

update anagrafiche, indirizzi, istanze_abbonamenti, fascicoli set anagrafiche.pa=true where
		(istanze_abbonamenti.id_abbonato=anagrafiche.id or istanze_abbonamenti.id_pagante=anagrafiche.id) and
		anagrafiche.id_indirizzo_principale=indirizzi.id and istanze_abbonamenti.id_fascicolo_fine=fascicoli.id and
    fascicoli.data_fine >= '2018-09-01 00:00:00' and
		(indirizzi.cognome_ragione_sociale like 'ist.%' or indirizzi.cognome_ragione_sociale like 'istituto%' or indirizzi.cognome_ragione_sociale like 'scuola%' or
		indirizzi.cognome_ragione_sociale like 'biblio%' or indirizzi.cognome_ragione_sociale like 'bib.%' or indirizzi.cognome_ragione_sociale like 'comune%' or
		indirizzi.cognome_ragione_sociale like '%didattica%' or indirizzi.cognome_ragione_sociale like '%asilo%' or indirizzi.cognome_ragione_sociale like '%nido%' or
		indirizzi.cognome_ragione_sociale like 'regione%' or indirizzi.cognome_ragione_sociale like '%facolta%' or indirizzi.cognome_ragione_sociale like '%dipart%' or
		indirizzi.cognome_ragione_sociale like '%civic%');

select distinct anagrafiche.uid, indirizzi.cognome_ragione_sociale, indirizzi.nome, anagrafiche.codice_fiscale, anagrafiche.partita_iva, anagrafiche.cuf, anagrafiche.codice_sap 
	from anagrafiche, indirizzi, istanze_abbonamenti, fascicoli where
		(istanze_abbonamenti.id_abbonato=anagrafiche.id or istanze_abbonamenti.id_pagante=anagrafiche.id) and
		anagrafiche.id_indirizzo_principale=indirizzi.id and istanze_abbonamenti.id_fascicolo_fine=fascicoli.id and
	fascicoli.data_fine >= '2018-09-01 00:00:00' and
		(indirizzi.cognome_ragione_sociale like 'ist.%' or indirizzi.cognome_ragione_sociale like 'istituto%' or indirizzi.cognome_ragione_sociale like 'scuola%' or
		indirizzi.cognome_ragione_sociale like 'biblio%' or indirizzi.cognome_ragione_sociale like 'bib.%' or indirizzi.cognome_ragione_sociale like 'comune%' or
		indirizzi.cognome_ragione_sociale like '%didattica%' or indirizzi.cognome_ragione_sociale like '%asilo%' or indirizzi.cognome_ragione_sociale like '%nido%' or
		indirizzi.cognome_ragione_sociale like 'regione%' or indirizzi.cognome_ragione_sociale like '%facolta%' or indirizzi.cognome_ragione_sociale like '%dipart%' or
		indirizzi.cognome_ragione_sociale like '%civic%');

###

//Esportazione Psychometrics
//Anagrafiche Beneficiari
select distinct ana.uid as uid_anagrafica, ind.nome, ind.cognome_ragione_sociale, ana.codice_fiscale, pro.nome, ind.localita, ind.cap,
		ind.id_provincia, ana.email_primaria, ind.presso, naz.nome_nazione, ind.indirizzo, ana.tel_casa, ana.tel_mobile,
		ana.consenso_tos as privacy1, ana.consenso_marketing as privacy2, ana.consenso_profilazione as privacy3
	from anagrafiche ana left outer join professioni pro on (ana.id_professione=pro.id), indirizzi ind, istanze_abbonamenti ia, abbonamenti abb, fascicoli ff, nazioni naz
	where ana.id_indirizzo_principale=ind.id and ia.id_abbonamento=abb.id and ia.id_fascicolo_fine=ff.id and ia.id_abbonato=ana.id and ind.id_nazione=naz.id and
	abb.codice_abbonamento like 'B%' and ff.data_fine >= '2018-12-01 00:00:01'
	order by uid
//Anagrafiche Paganti
select distinct ana.uid as uid_anagrafica, ind.nome, ind.cognome_ragione_sociale, ana.codice_fiscale, pro.nome, ind.localita, ind.cap,
		ind.id_provincia, ana.email_primaria, ind.presso, naz.nome_nazione, ind.indirizzo, ana.tel_casa, ana.tel_mobile,
		ana.consenso_tos as privacy1, ana.consenso_marketing as privacy2, ana.consenso_profilazione as privacy3
	from anagrafiche ana left outer join professioni pro on (ana.id_professione=pro.id), indirizzi ind, istanze_abbonamenti ia, abbonamenti abb, fascicoli ff, nazioni naz
	where ana.id_indirizzo_principale=ind.id and ia.id_abbonamento=abb.id and ia.id_fascicolo_fine=ff.id and ia.id_pagante=ana.id and ind.id_nazione=naz.id and
	abb.codice_abbonamento like 'B%' and ff.data_fine >= '2018-12-01 00:00:01'
	order by uid
//Abbonamenti
select distinct ana.uid as uid_anagrafica_BEN, ta.codice as tipo_abb, ta.nome as tipo_abb_descr, abb.codice_abbonamento, fi.data_inizio as dt_inizio_istanza,
		ff.data_fine as dt_fine_istanza, ia.id as uid_istanza, abb.data_creazione as dt_creazione_abb, ia.data_creazione as dt_creazione_istanza,
		fi.titolo_numero as numero_inizio_istanza, ff.titolo_numero as numero_fine_istanza, ia.invio_bloccato, ia.data_disdetta as dt_disdetta,
		(lst.prezzo < 0.02) as omaggio, ia.fattura_differita as fatturazione, ia.pagato, ade.codice, ia.copie, pag.uid as uid_anagrafica_PAG
	from anagrafiche ana, abbonamenti abb, istanze_abbonamenti ia left outer join anagrafiche pag on (ia.id_pagante=pag.id) left outer join adesioni ade on (ia.id_adesione=ade.id),
		listini lst, tipi_abbonamento ta, fascicoli fi, fascicoli ff
	where ia.id_abbonato=ana.id and ia.id_abbonamento=abb.id and  ia.id_listino=lst.id and
	lst.id_tipo_abbonamento=ta.id and ia.id_fascicolo_fine=ff.id and ia.id_fascicolo_inizio=fi.id and
	abb.codice_abbonamento like 'B%' and ff.data_fine >= '2018-12-01 00:00:01'
	order by ana.uid

###

ALTER TABLE `istanze_abbonamenti` ADD COLUMN `adesione` varchar(32) DEFAULT NULL;
update istanze_abbonamenti set adesione = 
		(select codice from adesioni where adesioni.id=istanze_abbonamenti.id_adesione);
#ALTER TABLE `istanze_abbonamenti` DROP COLUMN `id_adesione`;

###

#Trova duplicati di opzioni-abbonamenti
select oia.id,oia.id_istanza_abbonamento,oia.id_opzione, oia.id_fattura from opzioni_istanze_abbonamenti oia 
	inner join (select * from opzioni_istanze_abbonamenti group by id_istanza_abbonamento, id_opzione 
		having count(id) > 1) temp on (oia.id_istanza_abbonamento=temp.id_istanza_abbonamento and oia.id_opzione=temp.id_opzione);

ALTER TABLE `opzioni_istanze_abbonamenti` ADD UNIQUE `unique_opzione_istanza_index`(id_opzione, id_istanza_abbonamento);

###

ALTER TABLE `opzioni_istanze_abbonamenti` ADD COLUMN `inclusa` bit(1) NOT NULL DEFAULT false;
#fissato un oia, seleziona la relativa istanza e le relative opzioni obbligatorie da listino,
#le conta, e mette il valore in "inclusa"
update opzioni_istanze_abbonamenti set inclusa = (select count(opzioni_listini.id)
	from opzioni_listini inner join istanze_abbonamenti on (istanze_abbonamenti.id_listino = opzioni_listini.id_listino)
	where opzioni_istanze_abbonamenti.id_istanza_abbonamento = istanze_abbonamenti.id and
	opzioni_istanze_abbonamenti.id_opzione = opzioni_listini.id_opzione);

#VERIFICA
select fas.data_inizio, ia.id, ta.codice, opz.nome  from opzioni_istanze_abbonamenti oia, opzioni opz, istanze_abbonamenti ia, fascicoli fas, listini lst, tipi_abbonamento ta where
	oia.id_istanza_abbonamento=ia.id and ia.id_fascicolo_inizio=fas.id and oia.id_opzione=opz.id and ia.id_listino=lst.id and lst.id_tipo_abbonamento=ta.id and
	oia.inclusa = true 
	order by fas.data_inizio, ia.id, ta.codice;
select ia.id from istanze_abbonamenti as ia 
	left outer join opzioni_istanze_abbonamenti as oia on oia.id_istanza_abbonamento=ia.id 
	join fascicoli ff on ia.id_fascicolo_fine = ff.id where
	ff.data_inizio < '2019-01-01 00:00:01' and 
	ia.id_listino = 768 and
	ia.data_disdetta is null and
	ia.invio_bloccato = false
	group by ia.id having count(oia.id) = 0 order by ia.id;
	
###

#Modifiche a 'localita'
update localita set cap='33059' where (nome like 'Fiumicello' and cap='33050');
update localita set cap='33059' where (nome like 'Villa Vicentina' and cap='33050');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('33059','Fiumicello Villa Vicentina','UD', false);
update localita set cap='33014' where (nome like 'Treppo Carnico' and cap='33020');
update localita set cap='33014' where (nome like 'Ligosullo' and cap='33020');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('33014','Treppo Ligosullo','UD', false);
update localita set cap='87064' where (nome like 'Rossano' and cap='87067');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('87064','Corigliano-Rossano','CS', false);
update localita set cap='36048' where (nome like 'Mossano' and cap='36024');
update localita set cap='36048' where (nome like 'Barbarano Vicentino' and cap='36021');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('36048','Barbarano Mossano','VI', false);
update localita set cap='35046', id_provincia='PD' where (nome like 'Saletto' and cap='31030');
update localita set cap='35046' where (nome like 'Saletto Di Vigodarzere' and cap='35010');
update localita set cap='35046' where (nome like 'Santa Margherita D%Adige' and cap='35040');
update localita set cap='35046' where (nome like 'Megliadino San Fidenzio' and cap='35040');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('35046','Borgo Veneto','PD', false);
update localita set cap='24032', id_provincia='BG' where (nome like 'Torre d% Busi' and cap='23806');
update localita set cap='80072' where (nome like 'Barano D%Ischia' and cap='80070');
update localita set cap='80081' where (nome like 'Serrara Fontana' and cap='80070');
update localita set cap='04031' where (nome like 'Ventotene' and cap='04020');
update localita set cap='71051' where (nome like 'Isole Tremiti' and cap='71040');
update localita set cap='92031' where (nome like 'Lampedusa e Linosa' and cap='92010');
update localita set cap='90051' where (nome like 'Ustica' and cap='90010');
update localita set cap='09050' where (nome like 'Pula' and cap='09010');
update localita set cap='09068' where (nome like 'Uta' and cap='09010');
update localita set cap='09050' where (nome like 'Villa San Pietro' and cap='09010');
update localita set cap='09067' where (nome like 'Elmas' and cap='09030');
update localita set cap='09069' where (nome like 'Maracalagonis' and cap='09040');
update localita set cap='09060' where (nome like 'Settimo San Pietro' and cap='09040');
update localita set cap='91023' where (nome like 'Marettimo' and cap='91010');
#Modifiche a 'indirizzi'
update indirizzi set cap='33059' where (localita like 'Fiumicello' and cap='33050');
update indirizzi set cap='33059' where (localita like 'Villa Vicentina' and cap='33050');
update indirizzi set cap='33014' where (localita like 'Treppo Carnico' and cap='33020');
update indirizzi set cap='33014' where (localita like 'Ligosullo' and cap='33020');
update indirizzi set cap='87064' where (localita like 'Rossano' and cap='87067');
update indirizzi set cap='36048' where (localita like 'Mossano' and cap='36024');
update indirizzi set cap='36048' where (localita like 'Barbarano Vicentino' and cap='36021');
update indirizzi set cap='35046', id_provincia='PD' where (nome like 'Saletto' and cap='31030');
delete from localita where id=6825;
update indirizzi set cap='35046' where (localita like 'Saletto Di Vigodarzere' and cap='35010');
update indirizzi set cap='35046' where (localita like 'Santa Margherita D%Adige' and cap='35040');
update indirizzi set cap='35046' where (localita like 'Megliadino San Fidenzio' and cap='35040');
update indirizzi set cap='24032', id_provincia='BG' where (localita like 'Torre d% Busi' and cap='23806');
update indirizzi set cap='80072' where (localita like 'Barano D%Ischia' and cap='80070');
update indirizzi set cap='80081' where (localita like 'Serrara Fontana' and cap='80070');
update indirizzi set cap='04031' where (localita like 'Ventotene' and cap='04020');
update indirizzi set cap='71051' where (localita like 'Isole Tremiti' and cap='71040');
update indirizzi set cap='92031' where (localita like 'Lampedusa e Linosa' and cap='92010');
update indirizzi set cap='90051' where (localita like 'Ustica' and cap='90010');
update indirizzi set cap='09050' where (localita like 'Pula' and cap='09010');
update indirizzi set cap='09068' where (localita like 'Uta' and cap='09010');
update indirizzi set cap='09050' where (localita like 'Villa San Pietro' and cap='09010');
update indirizzi set cap='09067' where (localita like 'Elmas' and cap='09030');
update indirizzi set cap='09069' where (localita like 'Maracalagonis' and cap='09040');
update indirizzi set cap='09060' where (localita like 'Settimo San Pietro' and cap='09040');
update indirizzi set cap='91023' where (localita like 'Marettimo' and cap='91010');


