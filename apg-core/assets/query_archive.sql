#ESTRAZIONE PAGAMENTI PER OS
select pag.id_tipo_pagamento, pag.data_pagamento, abb.codice_abbonamento, ia.id as uid, ff.data_fine from pagamenti pag, istanze_abbonamenti ia, abbonamenti abb, fascicoli ff where
	pag.id_istanza_abbonamento = ia.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonamento = abb.id and
	ff.data_fine > '2018-03-15' and
	pag.id_tipo_pagamento is not null and
	abb.codice_abbonamento like 'B%' and
	ia.invio_bloccato = false
	order by pag.data_pagamento desc


//Fascicolo iniziale settembre
select * from fascicoli where codice_meccanografico like "X1401A";
select * from fascicoli where codice_meccanografico like "X1401M";

//Abbonati A da settembre con email
//no bloccati, no omaggi, no fatturati
select a.codice_abbonamento, ana.cognome_ragione_sociale, ana.nome, i.presso, ana.email_primaria, ia.pagato
	from abbonamenti a, istanze_abbonamenti ia, listini l, tipi_abbonamento ta, anagrafiche ana, indirizzi i where
	ia.id_abbonamento=a.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and ia.id_abbonato=ana.id and ana.id_indirizzo_principale=i.id and
	ia.id_fascicolo_inizio=574 and
	ia.invio_bloccato=false and
	ta.codice not like '%9' and
	ia.in_fatturazione = false and
	l.fattura_differita = false and
	ana.email_primaria is not null and
	ana.email_primaria <> '' order by a.codice_abbonamento;

//Abbonati M da settembre con email
//no bloccati, no omaggi, no fatturati
select a.codice_abbonamento, ana.cognome_ragione_sociale, ana.nome, i.presso, ana.email_primaria, ia.pagato
	from abbonamenti a, istanze_abbonamenti ia, listini l, tipi_abbonamento ta, anagrafiche ana, indirizzi i where
	ia.id_abbonamento=a.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and ia.id_abbonato=ana.id and ana.id_indirizzo_principale=i.id and
	ia.id_fascicolo_inizio=579 and
	ia.invio_bloccato=false and
	ta.codice not like '%9' and
	ia.in_fatturazione = false and
	l.fattura_differita = false and
	ana.email_primaria is not null and
	ana.email_primaria <> '' order by a.codice_abbonamento;

//Abbonamenti attivi B PAGATI O FATTURATI comprensivi di gracing finale
select ana.nome, ana.cognome_ragione_sociale, ind.presso, ind.id_provincia, ana.id_tipo_anagrafica,
		a.codice_abbonamento, ta.codice, ia.copie, f1.data_nominale, f2.data_nominale_fine,
		ia.adesione, ana.email_primaria, pro.nome,
		ana.codice_fiscale, ana.partita_iva
	from
	istanze_abbonamenti ia, abbonamenti a, listini l, tipi_abbonamento ta, fascicoli f1, fascicoli f2, indirizzi ind,
	anagrafiche ana left outer join professioni pro on pro.id=ana.id_professione
	where
	ia.id_abbonamento=a.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and ia.id_fascicolo_inizio=f1.id and ia.id_fascicolo_fine=f2.id and ia.id_abbonato=ana.id and ana.id_indirizzo_principale=ind.id and
	a.codice_abbonamento like 'B%' and
	ia.invio_bloccato=false and
	ia.ultima_della_serie=true and
	(l.fattura_differita=true or ia.pagato=true) and
	f1.data_nominale <= '2014-09-01' and
	f2.data_nominale_fine >= '2014-05-01'
//Abbonamenti attivi B NON PAGATI in gracing iniziale
select ana.nome, ana.cognome_ragione_sociale, ind.presso, ind.id_provincia, ana.id_tipo_anagrafica,
		a.codice_abbonamento, ta.codice, ia.copie, f1.data_nominale,
		ia.adesione, ana.email_primaria, pro.nome,
		ana.codice_fiscale, ana.partita_iva
	from
	istanze_abbonamenti ia, abbonamenti a, listini l, tipi_abbonamento ta, fascicoli f1, indirizzi ind,
	anagrafiche ana left outer join professioni pro on pro.id=ana.id_professione
	where
	ia.id_abbonamento=a.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and ia.id_fascicolo_inizio=f1.id and ia.id_abbonato=ana.id and ana.id_indirizzo_principale=ind.id and
	a.codice_abbonamento like 'B%' and
	ia.invio_bloccato=false and
	ia.ultima_della_serie=true and
	(l.fattura_differita=false or ia.pagato=false) and
	f1.data_nominale <= '2014-09-01' and
	f1.data_nominale >= '2014-05-01'

//Totali INFOCLIP annuali
select count(ia.id), p.nome from istanze_abbonamenti ia, abbonamenti a, periodici p, adesioni ade where 
	ia.id_abbonamento=a.id and a.id_periodico=p.id and
	ia.data_creazione >= '2013-01-01' and ia.data_creazione <= '2013-12-31' and
	ade.codice like 'INFOCLIP' and ia.id_adesione=ade.id
	group by p.nome;
	
//Istanze W (e D) di tipo TV che hanno ricevuto almeno un fascicolo nel 2014
select a.codice_abbonamento, ia.copie, abb.cognome_ragione_sociale, abb.nome, pag.cognome_ragione_sociale, pag.nome, fi.data_nominale, ff.data_nominale
	from istanze_abbonamenti ia, abbonamenti a, fascicoli fi, fascicoli ff,
	anagrafiche abb, anagrafiche pag, listini l, tipi_abbonamento ta where
	ia.id_abbonamento=a.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine=ff.id and ia.id_abbonato=abb.id and
	ia.id_pagante=pag.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and
	ia.invio_bloccato=false and ia.data_disdetta is null and
	ta.id_periodico=9 and ta.codice='TV' and
	fi.data_nominale <= '2014-12-31' and ff.data_nominale >= '2014-01-01'
	order by a.codice_abbonamento asc
	
//Istanze Q di tipo PE con promotore 102PFQ che abbiano ricevuto almeno un fascicolo nel 2014
select a.codice_abbonamento, ia.copie, abb.cognome_ragione_sociale, abb.nome, pag.cognome_ragione_sociale, pag.nome, fi.data_nominale, ff.data_nominale
	from istanze_abbonamenti ia, abbonamenti a, fascicoli fi, fascicoli ff,
	anagrafiche abb, anagrafiche pag, listini l, tipi_abbonamento ta where
	ia.id_abbonamento=a.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine=ff.id and ia.id_abbonato=abb.id and
	ia.id_pagante=pag.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and
	ia.invio_bloccato=false and ia.data_disdetta is null and
	ta.id_periodico=8 and ta.codice='PE' and
	pag.codice_cliente like '102PFQ' and
	fi.data_nominale <= '2014-12-31' and ff.data_nominale >= '2014-01-01'
	order by a.codice_abbonamento asc
	
// indirizzi email degli abbonati attivi (ovvero non disdetti o bloccati) alla riviste scolastiche VS e SI dei tipi 01, d1, px, 09, OR
select abb.codice_abbonamento, ta.codice, a.cognome_ragione_sociale, a.email_primaria
		from anagrafiche a, istanze_abbonamenti ia, abbonamenti abb, fascicoli fi, listini l, tipi_abbonamento ta where
	ia.id_abbonato=a.id and ia.id_abbonamento=abb.id and ia.id_fascicolo_inizio=fi.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and
	fi.data_nominale = '2014-09-01' and
	(abb.id_periodico = 1 or abb.id_periodico = 7) and
	ia.invio_bloccato = false and
	ia.data_disdetta is null and
	(ta.codice='01' or ta.codice='D1' or ta.codice='PX' or ta.codice='09' or ta.codice='0R') and
	a.email_primaria is not null and a.email_primaria <> ''

//Abbonamenti D1 attivi in un qualsiasi momento del 2013 (incluso gracing) per la rivista B 
select abb.codice_abbonamento from abbonamenti abb, istanze_abbonamenti ia, fascicoli fi, fascicoli ff, listini l, tipi_abbonamento ta where
	ia.id_abbonamento=abb.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine=ff.id and ia.id_listino=l.id and l.id_tipo_abbonamento=ta.id and
	abb.codice_abbonamento like 'B%' and
	ff.data_nominale >= '2012-11-01' and fi.data_nominale <= '2013-12-31' and
	ta.codice like 'D1'
	group by abb.codice_abbonamento
	
//Abbonamenti ATTIVI W al 1/7/2015 considerando i gracing
select a.codice_abbonamento,ana.cognome_ragione_sociale,ana.nome,ana.email_primaria 
		from istanze_abbonamenti ia, abbonamenti a, listini l, fascicoli fi, fascicoli ff, anagrafiche ana where
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "W%" and
	l.gracing_finale = 2 and
	ff.data_nominale >= '2015-03-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "W%" and
	l.gracing_finale = 1 and
	ff.data_nominale >= '2015-05-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "W%" and
	l.gracing_finale = 0 and
	ff.data_nominale >= '2015-07-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = false and ia.in_fatturazione = false and l.fattura_differita = false and l.prezzo>1) and
	a.codice_abbonamento like "W%" and
	l.gracing_iniziale = 2 and
	fi.data_nominale >= '2015-05-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = false and ia.in_fatturazione = false and l.fattura_differita = false and l.prezzo>1) and
	a.codice_abbonamento like "W%" and
	l.gracing_iniziale = 1 and
	fi.data_nominale >= '2015-07-01')
	order by a.codice_abbonamento asc
	

//Abbonamenti ATTIVI Q al 1/7/2015 considerando i gracing
select a.codice_abbonamento,ana.cognome_ragione_sociale,ana.nome,ana.email_primaria 
		from istanze_abbonamenti ia, abbonamenti a, listini l, fascicoli fi, fascicoli ff, anagrafiche ana where
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_finale = 3 and
	ff.data_nominale >= '2015-04-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_finale = 2 and
	ff.data_nominale >= '2015-05-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_finale = 1 and
	ff.data_nominale >= '2015-06-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = true or ia.in_fatturazione = true or l.fattura_differita = true or l.prezzo<1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_finale = 0 and
	ff.data_nominale >= '2015-07-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = false and ia.in_fatturazione = false and l.fattura_differita = false and l.prezzo>1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_iniziale = 3 and
	fi.data_nominale >= '2015-05-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = false and ia.in_fatturazione = false and l.fattura_differita = false and l.prezzo>1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_iniziale = 2 and
	fi.data_nominale >= '2015-06-01')
	or
	(ia.id_abbonamento=a.id and ia.id_listino=l.id and ia.id_fascicolo_inizio=fi.id and ia.id_fascicolo_fine = ff.id and ia.id_abbonato=ana.id and
	ia.ultima_della_serie = true and
	ia.data_disdetta is null and ia.invio_bloccato = false and
	(ia.pagato = false and ia.in_fatturazione = false and l.fattura_differita = false and l.prezzo>1) and
	a.codice_abbonamento like "Q%" and
	l.gracing_iniziale = 1 and
	fi.data_nominale >= '2015-07-01')
	order by a.codice_abbonamento asc


	
	
	
	
	
#Non ha 'Dentro il testo' ma ha ricevuto il fascicolo
select a.codice_abbonamento from abbonamenti a, istanze_abbonamenti ia, listini l, evasioni_fascicoli ef where
	ia.id_abbonamento=a.id and ia.id_listino=l.id and ef.id_istanza_abbonamento=ia.id and
	ef.id_fascicolo=748 and
	(select count(oia.id) from opzioni_istanze_abbonamenti oia where
		oia.id_istanza=ia.id and oia.id_opzione=53) = 0 and
	(select count(ol.id) from opzioni_listini ol where
		ol.id_listino=ia.id_listino and ol.id_opzione=53) = 0
		