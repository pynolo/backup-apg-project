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

ALTER TABLE `anagrafiche` ADD COLUMN `merged_into_uid` varchar(16) DEFAULT NULL,
	ADD COLUMN `deleted` bit(1) NOT NULL DEFAULT false,
	ADD COLUMN `identity_uid` varchar(16) DEFAULT NULL,
	ADD COLUMN `adottatario` bit(1) NOT NULL DEFAULT false;
CREATE INDEX `anagrafiche_merged_idx` on `anagrafiche` (merged_into_uid(16));
CREATE INDEX `anagrafiche_identity_idx` on `anagrafiche` (identity_uid(16));
#ALTER TABLE `anagrafiche` DROP COLUMN `uid_merge_list`; 
