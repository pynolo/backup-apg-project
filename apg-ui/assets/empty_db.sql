#Elimina istanze abbonamenti e dati abbinati
truncate stat_abbonati;
truncate stat_invio;
truncate `ordini_logistica`;
truncate evasioni_doni;
truncate evasioni_fascicoli;
truncate evasioni_comunicazioni;
truncate pagamenti;
truncate opzioni_istanze_abbonamenti;
#Elimina anagrafiche e indirizzi
delete from istanze_abbonamenti;
delete from abbonamenti;
truncate avvisi;
delete from anagrafiche;
delete from indirizzi;
truncate rapporti;
truncate log_editing;
truncate log_ws;