#Elimina ogni traccia delle fatture
update opzioni_istanze_abbonamenti set id_stampa_fattura = null where id_stampa_fattura is not null;
update istanze_abbonamenti set id_stampa_fattura = null where id_stampa_fattura is not null;
delete from contatori where ckey like 'FP%';
delete from contatori where ckey like 'REGMENS%';
truncate stampe_fatture;
