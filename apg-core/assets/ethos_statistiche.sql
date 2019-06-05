#AGGREGAZIONE PER CONTRATTO ETHOS

#A) Anagrafiche modificate o create
select count(id) from log_editing where log_datetime >= '2018-06-01 00:00:01' and log_datetime < '2019-04-01 00:00:01' and
	entity_name = 'Anagrafiche' and id_utente <> 'api';

#B1) Abbonamenti con fatturazione differita
select count(ia.id) from istanze_abbonamenti ia join listini l on ia.id_listino=l.id where 
	(ia.fattura_differita = true or l.fattura_differita = true) and 
	ia.data_creazione >= '2018-04-01 00:00:01' and ia.data_creazione < '2019-04-01 00:00:01';
	
#B2) Abbonamenti modificati o creati (A CUI SOTTRARRE B1)
select count(id) from log_editing where log_datetime >= '2018-06-01 00:00:01' and log_datetime < '2019-04-01 00:00:01' and
	entity_name = 'IstanzeAbbonamenti' and id_utente <> 'api';
	
#C) Fatture
select count(fat.id) from fatture fat where fat.id_utente <> 'api' and
	fat.data_creazione >= '2018-04-01 00:00:01' and fat.data_creazione < '2019-04-01 00:00:01';

#E) Arretrati spediti
select count(ef.id) from evasioni_fascicoli ef where 
	ef.id_utente <> 'api' and ef.id_tipo_evasione = 'ARR' and
	ef.data_creazione >= '2018-04-01 00:00:01' and ef.data_creazione < '2019-04-01 00:00:01';
	
