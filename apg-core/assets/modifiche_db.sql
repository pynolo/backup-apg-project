#Update istanze esistenti
ALTER TABLE `istanze_abbonamenti` ADD COLUMN `proposta_acquisto` bit(1) NOT NULL DEFAULT false;
update istanze_abbonamenti set proposta_acquisto=true where 
	id_listino in (select lst.id from listini lst inner join tipi_abbonamento ta on (ta.id=lst.id_tipo_abbonamento) where
		ta.codice like 'POP');

###

insert into localita (cap,nome,id_provincia, modifica_propagata) values ('22023','Centro Valle Intelvi','CO', false);

