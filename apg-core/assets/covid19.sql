# PER VEDERE GLI ABBONAMENTI DA BLOCCARE (non ancora bloccati)
select * from indirizzi i , anagrafiche a , istanze_abbonamenti ia, fascicoli ff, listini l where
	ia.id_abbonato = a.id and a.id_indirizzo_principale =i.id and 
	ia.id_fascicolo_fine = ff.id and ia.id_listino = l.id and
	i.localita in ('Bertonico','Brembio','Casalpusterlengo','Caselle Landi',
	'Castelgerundo','Castelnuovo Bocca d\'Adda','Castiglione d\'Adda','Codogno',
	'Corno Giovine','Cornovecchio','Fombio','Guardamiglio','Livraga','Maccastorna',
	'Maleo','Meleti','Orio Litta','Ospedaletto Lodigiano','San Fiorano',
	'San Rocco al Porto','Santo Stefano Lodigiano','Secugnago','Senna Lodigiana',
	'Somaglia','Terranova dei Passerini','Turano Lodigiano','Vo\'') and 
	ff.data_inizio >= '2019-12-31' and ia.invio_bloccato = false and
	l.cartaceo = 1;

# BLOCCO ABBONAMENTI
update istanze_abbonamenti ia inner join anagrafiche a on ia.id_abbonato = a.id
	inner join indirizzi i on a.id_indirizzo_principale =i.id
	inner join fascicoli ff on ia.id_fascicolo_fine = ff.id
	inner join listini l on ia.id_listino = l.id
	set ia.invio_bloccato = true, ia.adesione = 'EMERGENZA' where
	i.localita in ('Bertonico','Brembio','Casalpusterlengo','Caselle Landi',
	'Castelgerundo','Castelnuovo Bocca d\'Adda','Castiglione d\'Adda','Codogno',
	'Corno Giovine','Cornovecchio','Fombio','Guardamiglio','Livraga','Maccastorna',
	'Maleo','Meleti','Orio Litta','Ospedaletto Lodigiano','San Fiorano',
	'San Rocco al Porto','Santo Stefano Lodigiano','Secugnago','Senna Lodigiana',
	'Somaglia','Terranova dei Passerini','Turano Lodigiano','Vo\'') and 
	ff.data_inizio >= '2019-12-31' and ia.invio_bloccato = false and
	l.cartaceo = 1;

# PER VEDERE I RISULTATI DEL BLOCCO
select * from anagrafiche a, istanze_abbonamenti ia, abbonamenti abb where
	ia.id_abbonato = a.id and ia.id_abbonamento = abb.id and
	ia.adesione like 'EMERGENZA' and ia.invio_bloccato = true;

# SBLOCCO

# SBLOCCO CHE LASCIA 'EMERGENZE'
update istanze_abbonamenti ia set ia.invio_bloccato = false where
	ia.adesione like 'EMERGENZA' and ia.invio_bloccato = true;
# SBLOCCO CHE RIMUOVE L'ADESIONE
update istanze_abbonamenti ia set ia.invio_bloccato = false, ia.adesione = null where
	ia.adesione like 'EMERGENZA' and ia.invio_bloccato = true;
	