#Update istanze esistenti
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `proposta_acquisto` bit(1) NOT NULL DEFAULT false;
update istanze_abbonamenti set proposta_acquisto=true where 
	id_listino in (select lst.id from listini lst inner join tipi_abbonamento ta on (ta.id=lst.id_tipo_abbonamento) where
		ta.codice like 'POP');

###

insert into localita (cap,nome,id_provincia, modifica_propagata) values ('22023','Centro Valle Intelvi','CO', false);

###

#Modifiche a 'localita'
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('28827','Valle Cannobina','VB', false);
update localita set cap='28827' where (nome like 'Cavaglio Spoccia' and cap='28825');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('10039','Val di Chy','TO', false);
update localita set cap='10039' where (nome like 'Alice Superiore' and cap='10010');
update localita set cap='10039' where (nome like 'Lugnacco' and cap='10080');
update localita set cap='10039' where (nome like 'Pecco' and cap='10080');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('10089','Valchiusa','TO', false);
update localita set cap='10089' where (nome like 'Vico Canavese' and cap='10080');
update localita set cap='10089' where (nome like 'Trausella' and cap='10080');
update localita set cap='10089' where (nome like 'Meugliano' and cap='10080');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('13854','Quaregna Cerreto','BI', false);
update localita set cap='13854' where (nome like 'Cerreto Castello' and cap='13852');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('13835','Valdilana','BI', false);
update localita set cap='13835' where (nome like 'Mosso' and cap='13822');
update localita set cap='13835' where (nome like 'Soprana' and cap='13834');
update localita set cap='13835' where (nome like 'Valle Mosso' and cap='13825');
update localita set cap='12058' where (nome like 'Camo' and cap='12050');
update localita set cap='12022' where (nome like 'Valmala' and cap='12020');
update localita set cap='12037' where (nome like 'Castellar' and cap='12030');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('28013','Gattico-Veruno','NO', false);
update localita set cap='28013' where (nome like 'Veruno' and cap='28010');
update localita set cap='13021' where (nome like 'Riva Valdobbia' and cap='13020');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('15037','Lu e Cuccaro Monferrato','AL', false);
update localita set cap='15037' where (nome like 'Lu' and cap='15040');
update localita set cap='15037' where (nome like 'Cuccaro Monferrato' and cap='15040');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('38097','Terre d\'Adige','TN', false);
update localita set cap='38097' where (nome like 'Zambana' and cap='38010');
update localita set cap='38097' where (nome like 'Nave San Rocco' and cap='38010');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('44033','Riva del Po','FE', false);
update localita set cap='44033' where (nome like 'Ro' and cap='44030');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('44039','Tresignana','FE', false);
update localita set cap='44039' where (nome like 'Formignana' and cap='44035');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('43058','Sorbolo Mezzani','PR', false);
update localita set cap='43058' where (nome like 'Mezzani' and cap='43055');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('50028','Barberino Tavarnelle','FI', false);
update localita set cap='50028' where (nome like 'Barberino Val d\'Elsa' and cap='50021');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('46021','Borgocarbonara','MN', false);
update localita set cap='46021' where (nome like 'Borgofranco sul Po' and cap='46020');
update localita set cap='46021' where (nome like 'Carbonara sul Po' and cap='46020');
update localita set nome='San Giorgio Bigarello', cap='46051' where (nome like 'San Giorgio di Mantova' and cap='46030');
update localita set cap='46051' where (nome like 'Bigarello' and cap='46030');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('22043','Solbiate con Cagno','CO', false);
update localita set cap='22043' where (nome like 'Solbiate' and cap='22070');
update localita set cap='22043' where (nome like 'Cagno' and cap='22070');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('26034','Piadena Drizzona','CR', false);
update localita set cap='26038' where (nome like 'Ca\' d\'Andrea' and cap='26030');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('27061','Colli Verdi','PV', false);
update localita set cap='27061' where (nome like 'Canevino' and cap='27040');
update localita set cap='27061' where (nome like 'Ruino' and cap='27040');
update localita set cap='27061' where (nome like 'Valverde' and cap='27050');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('20071','Vermezzo con Zelo','MI', false);
update localita set cap='20071' where (nome like 'Vermezzo' and cap='20080');
update localita set cap='20071' where (nome like 'Zelo Surrigone' and cap='20080');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('21062','Cadrezzate con Osmate','VA', false);
update localita set cap='21062' where (nome like 'Cadrezzate' and cap='21020');
update localita set cap='21062' where (nome like 'Osmate' and cap='21018');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('61028','Sassocorvaro Auditore','PU', false);
update localita set cap='61028' where (nome like 'Auditore' and cap='61020');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('32026','Borgo Valbelluna','BL', false);
update localita set cap='32026' where (nome like 'Lentiai' and cap='32020');
update localita set cap='32026' where (nome like 'Trichiana' and cap='32028');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('31017','Pieve del Grappa','TV', false);
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('36029','Valbrenta','VI', false);
update localita set cap='36029' where (nome like 'Campolongo sul Brenta' and cap='36020');
update localita set cap='36029' where (nome like 'Cismon del Grappa' and cap='36020');
update localita set cap='36029' where (nome like 'San Nazario' and cap='36020');
update localita set cap='36029' where (nome like 'Valstagna' and cap='36020');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('36046','Lusiana Conco','VI', false);
update localita set cap='36046' where (nome like 'Conco' and cap='36062');
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('36064','Colceresa','VI', false);
update localita set cap='36064' where (nome like 'Molvena' and cap='36060');
#Modifiche a 'indirizzi'
update indirizzi set cap='28827' where (localita like 'Cavaglio Spoccia' and cap='28825');
update indirizzi set cap='10039' where (localita like 'Alice Superiore' and cap='10010');
update indirizzi set cap='10039' where (localita like 'Lugnacco' and cap='10080');
update indirizzi set cap='10039' where (localita like 'Pecco' and cap='10080');
update indirizzi set cap='10089' where (localita like 'Vico Canavese' and cap='10080');
update indirizzi set cap='10089' where (localita like 'Trausella' and cap='10080');
update indirizzi set cap='10089' where (localita like 'Meugliano' and cap='10080');
update indirizzi set cap='13854' where (localita like 'Cerreto Castello' and cap='13852');
update indirizzi set cap='13835' where (localita like 'Mosso' and cap='13822');
update indirizzi set cap='13835' where (localita like 'Soprana' and cap='13834');
update indirizzi set cap='13835' where (localita like 'Valle Mosso' and cap='13825');
update indirizzi set cap='12058' where (localita like 'Camo' and cap='12050');
update indirizzi set cap='12022' where (localita like 'Valmala' and cap='12020');
update indirizzi set cap='12037' where (localita like 'Castellar' and cap='12030');
update indirizzi set cap='28013' where (localita like 'Veruno' and cap='28010');
update indirizzi set cap='13021' where (localita like 'Riva Valdobbia' and cap='13020');
update indirizzi set cap='15037' where (localita like 'Lu' and cap='15040');
update indirizzi set cap='15037' where (localita like 'Cuccaro Monferrato' and cap='15040');
update indirizzi set cap='38097' where (localita like 'Zambana' and cap='38010');
update indirizzi set cap='38097' where (localita like 'Nave San Rocco' and cap='38010');
update indirizzi set cap='44033' where (localita like 'Ro' and cap='44030');
update indirizzi set cap='44039' where (localita like 'Formignana' and cap='44035');
update indirizzi set cap='43058' where (localita like 'Mezzani' and cap='43055');
update indirizzi set cap='50028' where (localita like 'Barberino Val d\'Elsa' and cap='50021');
update indirizzi set cap='46021' where (localita like 'Borgofranco sul Po' and cap='46020');
update indirizzi set cap='46021' where (localita like 'Carbonara sul Po' and cap='46020');
update indirizzi set localita='San Giorgio Bigarello', cap='46051' where (localita like 'San Giorgio di Mantova' and cap='46030');
update indirizzi set cap='46051' where (localita like 'Bigarello' and cap='46030');
update indirizzi set cap='22043' where (localita like 'Solbiate' and cap='22070');
update indirizzi set cap='22043' where (localita like 'Cagno' and cap='22070');
update indirizzi set cap='26038' where (localita like 'Ca\' d\'Andrea' and cap='26030');
update indirizzi set cap='27061' where (localita like 'Canevino' and cap='27040');
update indirizzi set cap='27061' where (localita like 'Ruino' and cap='27040');
update indirizzi set cap='27061' where (localita like 'Valverde' and cap='27050');
update indirizzi set cap='20071' where (localita like 'Vermezzo' and cap='20080');
update indirizzi set cap='20071' where (localita like 'Zelo Surrigone' and cap='20080');
update indirizzi set cap='21062' where (localita like 'Cadrezzate' and cap='21020');
update indirizzi set cap='21062' where (localita like 'Osmate' and cap='21018');
update indirizzi set cap='61028' where (localita like 'Auditore' and cap='61020');
update indirizzi set cap='32026' where (localita like 'Lentiai' and cap='32020');
update indirizzi set cap='32026' where (localita like 'Trichiana' and cap='32028');
update indirizzi set cap='36029' where (localita like 'Campolongo sul Brenta' and cap='36020');
update indirizzi set cap='36029' where (localita like 'Cismon del Grappa' and cap='36020');
update indirizzi set cap='36029' where (localita like 'San Nazario' and cap='36020');
update indirizzi set cap='36029' where (localita like 'Valstagna' and cap='36020');
update indirizzi set cap='36046' where (localita like 'Conco' and cap='36062');
update indirizzi set cap='36064' where (localita like 'Molvena' and cap='36060');

###

ALTER TABLE `indirizzi` CHANGE COLUMN `id_nazione` `id_nazione` varchar(4) DEFAULT NULL;
ALTER TABLE `anagrafiche` ADD COLUMN `merged_into_uid` varchar(16) DEFAULT NULL,
	ADD COLUMN `deleted` bit(1) NOT NULL DEFAULT false,
	ADD COLUMN `identity_uid` varchar(32) DEFAULT NULL,
	ADD COLUMN `adottatario` bit(1) NOT NULL DEFAULT false,
	ADD COLUMN `update_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `update_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
CREATE INDEX `anagrafiche_merged_idx` on `anagrafiche` (merged_into_uid(16));
CREATE INDEX `anagrafiche_identity_idx` on `anagrafiche` (identity_uid(16));
INSERT INTO `api_services` (nome,access_key) VALUES ('giuntiscuola.it(webranking)','gswr432fx8');
#ALTER TABLE `anagrafiche` DROP COLUMN `uid_merge_list`; 

###

update istanze_abbonamenti set update_timestamp = CURRENT_TIMESTAMP;
update anagrafiche set update_timestamp = CURRENT_TIMESTAMP ;

###

ALTER TABLE `anagrafiche` DROP COLUMN `uid_merge_list`; 
#DROP TABLE `cache_crm`;

###

insert into localita (cap,nome,id_provincia, modifica_propagata) values ('38013','Borgo D\'Anaunia','TN', false);
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('38028','Novella','TN', false);
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('38099','Ville di Fiemme','TN', false);
insert into localita (cap,nome,id_provincia, modifica_propagata) values ('20038','Busto Garofalo','MI', false);
update localita set cap='38013' where (nome like 'Castelfondo' and cap='38020');
update localita set cap='38028' where (nome like 'Brez' and cap='38021');
update localita set cap='38028' where (nome like 'Cloz' and cap='38020');
update localita set cap='38098' where (nome like 'San Michele all\'Adige' and cap='38010');
update localita set cap='38098' where (nome like 'Faedo' and cap='38010');
update localita set cap='38099' where (nome like 'Carano' and cap='38033');
update localita set cap='38099' where (nome like 'Daiano' and cap='38030');
update localita set cap='38099' where (nome like 'Varena' and cap='38030');
update localita set cap='23822' where (nome like 'Vendrogno' and cap='23838');
update localita set cap='20031' where (nome like 'Cesate' and cap='20020');
update localita set cap='20033' where (nome like 'Solaro' and cap='20020');
update localita set cap='20034' where (nome like 'San Giorgio su Legnano' and cap='20010');
update localita set cap='20035' where (nome like 'Villa Cortese' and cap='20020');
update localita set cap='20036' where (nome like 'Dairago' and cap='20020');
update localita set cap='20039' where (nome like 'Canegrate' and cap='20010');
update localita set cap='20042' where (nome like 'Pessano con Bornago' and cap='20060');
update localita set cap='20043' where (nome like 'Vanzago' and cap='20010');
update localita set cap='20044' where (nome like 'Arese' and cap='20020');
update localita set cap='20045' where (nome like 'Lainate' and cap='20020');
update localita set cap='20046' where (nome like 'Cisliano' and cap='20080');
update localita set cap='20047' where (nome like 'Cusago' and cap='20090');
update localita set cap='20048' where (nome like 'Pantigliate' and cap='20090');
update localita set cap='20049' where (nome like 'Settala' and cap='20090');
update localita set cap='20050' where (nome like 'Liscate' and cap='20060');
update localita set cap='20051' where (nome like 'Cassina De\' Pecchi' and cap='20060');
update localita set cap='20052' where (nome like 'Vignate' and cap='20060');
update localita set cap='20053' where (nome like 'Rodano' and cap='20090');
update localita set cap='20054' where (nome like 'Segrate' and cap='20090');
update localita set cap='20055' where (nome like 'Vimodrone' and cap='20090');
update localita set cap='20057' where (nome like 'Assago' and cap='20090');
update localita set cap='20058' where (nome like 'Zibido San Giacomo' and cap='20080');
update localita set cap='20059' where (nome like 'Casarile' and cap='20080');
update localita set cap='20041' where (nome like 'Bussero' and cap='20060');
update localita set cap='20072' where (nome like 'Pieve Emanuele' and cap='20090');
update localita set cap='20073' where (nome like 'Opera' and cap='20090');
update localita set cap='20074' where (nome like 'Carpiano' and cap='20080');
update localita set cap='20075' where (nome like 'Colturano' and cap='20060');
update localita set cap='20076' where (nome like 'Mediglia' and cap='20060');
update localita set cap='20079' where (nome like 'Basiglio' and cap='20080');
update localita set cap='20001' where (nome like 'Inveruno' and cap='20010');
update localita set cap='20008' where (nome like 'Bareggio' and cap='20010');
update localita set cap='20007' where (nome like 'Cornaredo' and cap='20010');
update localita set cap='20004' where (nome like 'Arluno' and cap='20010');
update localita set cap='20005' where (nome like 'Pogliano Milanese' and cap='20010');
update localita set cap='20003' where (nome like 'Casorezzo' and cap='20010');
update localita set cap='20002' where (nome like 'Ossona' and cap='20010');
update localita set cap='20006' where (nome like 'Pregnana Milanese' and cap='20010');
update localita set cap='20009' where (nome like 'Vittuone' and cap='20010');
update localita set cap='04026' where (nome like 'Marina di Minturno' and cap='04028');
update localita set cap='04026' where (nome like 'Scauri' and cap='04028');
update localita set cap='04016' where (nome like 'Borgo San Donato' and cap='04010');
update localita set cap='02012' where (nome like 'Scai' and cap='02010');
update localita set cap='02012' where (nome like 'Torrita' and cap='02010');
update localita set cap='00019' where (nome like 'Tivoli Terme' and cap='00011');
update localita set cap='00019' where (nome like 'Villa Adriana' and cap='00010');
update localita set cap='01100' where (nome like 'Fastello' and cap='01020');
update localita set cap='01100' where (nome like 'Grotte Santo Stefano' and cap='01026');
update localita set cap='01100' where (nome like 'Magugnano' and cap='01026');
update localita set cap='01100' where (nome like 'Roccalvecce' and cap='01020');

update indirizzi set cap='38013' where (localita like 'Castelfondo' and cap='38020');
update indirizzi set cap='38028' where (localita like 'Brez' and cap='38021');
update indirizzi set cap='38028' where (localita like 'Cloz' and cap='38020');
update indirizzi set cap='38098' where (localita like 'San Michele all\'Adige' and cap='38010');
update indirizzi set cap='38098' where (localita like 'Faedo' and cap='38010');
update indirizzi set cap='38099' where (localita like 'Carano' and cap='38033');
update indirizzi set cap='38099' where (localita like 'Daiano' and cap='38030');
update indirizzi set cap='38099' where (localita like 'Varena' and cap='38030');
update indirizzi set cap='23822' where (localita like 'Vendrogno' and cap='23838');
update indirizzi set cap='20031' where (localita like 'Cesate' and cap='20020');
update indirizzi set cap='20033' where (localita like 'Solaro' and cap='20020');
update indirizzi set cap='20034' where (localita like 'San Giorgio su Legnano' and cap='20010');
update indirizzi set cap='20035' where (localita like 'Villa Cortese' and cap='20020');
update indirizzi set cap='20036' where (localita like 'Dairago' and cap='20020');
update indirizzi set cap='20038' where (localita like 'Busto Garofalo' and cap='20020');
update indirizzi set cap='20039' where (localita like 'Canegrate' and cap='20010');
update indirizzi set cap='20042' where (localita like 'Pessano con Bornago' and cap='20060');
update indirizzi set cap='20043' where (localita like 'Vanzago' and cap='20010');
update indirizzi set cap='20044' where (localita like 'Arese' and cap='20020');
update indirizzi set cap='20045' where (localita like 'Lainate' and cap='20020');
update indirizzi set cap='20046' where (localita like 'Cisliano' and cap='20080');
update indirizzi set cap='20047' where (localita like 'Cusago' and cap='20090');
update indirizzi set cap='20048' where (localita like 'Pantigliate' and cap='20090');
update indirizzi set cap='20049' where (localita like 'Settala' and cap='20090');
update indirizzi set cap='20050' where (localita like 'Liscate' and cap='20060');
update indirizzi set cap='20051' where (localita like 'Cassina De\' Pecchi' and cap='20060');
update indirizzi set cap='20052' where (localita like 'Vignate' and cap='20060');
update indirizzi set cap='20053' where (localita like 'Rodano' and cap='20090');
update indirizzi set cap='20054' where (localita like 'Segrate' and cap='20090');
update indirizzi set cap='20055' where (localita like 'Vimodrone' and cap='20090');
update indirizzi set cap='20057' where (localita like 'Assago' and cap='20090');
update indirizzi set cap='20058' where (localita like 'Zibido San Giacomo' and cap='20080');
update indirizzi set cap='20059' where (localita like 'Casarile' and cap='20080');
update indirizzi set cap='20041' where (localita like 'Bussero' and cap='20060');
update indirizzi set cap='20072' where (localita like 'Pieve Emanuele' and cap='20090');
update indirizzi set cap='20073' where (localita like 'Opera' and cap='20090');
update indirizzi set cap='20074' where (localita like 'Carpiano' and cap='20080');
update indirizzi set cap='20075' where (localita like 'Colturano' and cap='20060');
update indirizzi set cap='20076' where (localita like 'Mediglia' and cap='20060');
update indirizzi set cap='20079' where (localita like 'Basiglio' and cap='20080');
update indirizzi set cap='20001' where (localita like 'Inveruno' and cap='20010');
update indirizzi set cap='20008' where (localita like 'Bareggio' and cap='20010');
update indirizzi set cap='20007' where (localita like 'Cornaredo' and cap='20010');
update indirizzi set cap='20004' where (localita like 'Arluno' and cap='20010');
update indirizzi set cap='20005' where (localita like 'Pogliano Milanese' and cap='20010');
update indirizzi set cap='20003' where (localita like 'Casorezzo' and cap='20010');
update indirizzi set cap='20002' where (localita like 'Ossona' and cap='20010');
update indirizzi set cap='20006' where (localita like 'Pregnana Milanese' and cap='20010');
update indirizzi set cap='20009' where (localita like 'Vittuone' and cap='20010');
update indirizzi set cap='04026' where (localita like 'Marina di Minturno' and cap='04028');
update indirizzi set cap='04026' where (localita like 'Scauri' and cap='04028');
update indirizzi set cap='04016' where (localita like 'Borgo San Donato' and cap='04010');
update indirizzi set cap='02012' where (localita like 'Scai' and cap='02010');
update indirizzi set cap='02012' where (localita like 'Torrita' and cap='02010');
update indirizzi set cap='00019' where (localita like 'Tivoli Terme' and cap='00011');
update indirizzi set cap='00019' where (localita like 'Villa Adriana' and cap='00010');
update indirizzi set cap='01100' where (localita like 'Fastello' and cap='01020');
update indirizzi set cap='01100' where (localita like 'Grotte Santo Stefano' and cap='01026');
update indirizzi set cap='01100' where (localita like 'Magugnano' and cap='01026');
update indirizzi set cap='01100' where (localita like 'Roccalvecce' and cap='01020');


