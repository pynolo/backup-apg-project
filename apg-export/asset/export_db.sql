DROP TABLE if exists crm_export_config;
CREATE TABLE `crm_export_config` (
	id varchar(32) NOT NULL,
	val varchar(64) NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists crm_export;
CREATE TABLE `crm_export` (
	uid varchar(16) NOT NULL,
	identity_uid varchar(32) DEFAULT NULL,
	deleted bit(1) NOT NULL DEFAULT false,
	merged_into_uid varchar(16) DEFAULT NULL,
	update_timestamp timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	address_title varchar(32) DEFAULT NULL,
	address_first_name varchar(32) DEFAULT NULL,
	address_last_name_company varchar(64) DEFAULT NULL,
	address_co varchar(64) DEFAULT NULL,
	address_address varchar(128) DEFAULT NULL,
	address_locality varchar(64) DEFAULT NULL,
	address_province varchar(4) DEFAULT NULL,
	address_zip varchar(8) DEFAULT NULL,
	address_country_code varchar(2) DEFAULT NULL,
	sex varchar(1) DEFAULT NULL,
	cod_fisc varchar(16) DEFAULT NULL,
	piva varchar(16) DEFAULT NULL,
	phone_mobile varchar(32) DEFAULT NULL,
	phone_landline varchar(32) DEFAULT NULL,
	email_primary varchar(128) DEFAULT NULL,
	id_job int(11) DEFAULT NULL,
	id_qualification int(11) DEFAULT NULL,
	id_tipo_anagrafica varchar(8) DEFAULT NULL,
	birth_date date DEFAULT NULL,
	consent_tos bit(1) DEFAULT NULL,
	consent_marketing bit(1) DEFAULT NULL,
	consent_profiling bit(1) DEFAULT NULL,
	consent_update_date date DEFAULT NULL,
	
	own_subscription_identifier_0 varchar(16) DEFAULT NULL,
	own_subscription_media_0 varchar(2) DEFAULT NULL,
	own_subscription_status_0 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_0 date DEFAULT NULL,
	own_subscription_end_date_0 date DEFAULT NULL,
	gift_subscription_end_date_0 date DEFAULT NULL,
	own_subscription_identifier_1 varchar(16) DEFAULT NULL,
	own_subscription_media_1 varchar(2) DEFAULT NULL,
	own_subscription_status_1 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_1 date DEFAULT NULL,
	own_subscription_end_date_1 date DEFAULT NULL,
	gift_subscription_end_date_1 date DEFAULT NULL,
	own_subscription_identifier_2 varchar(16) DEFAULT NULL,
	own_subscription_media_2 varchar(2) DEFAULT NULL,
	own_subscription_status_2 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_2 date DEFAULT NULL,
	own_subscription_end_date_2 date DEFAULT NULL,
	gift_subscription_end_date_2 date DEFAULT NULL,
	own_subscription_identifier_3 varchar(16) DEFAULT NULL,
	own_subscription_media_3 varchar(2) DEFAULT NULL,
	own_subscription_status_3 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_3 date DEFAULT NULL,
	own_subscription_end_date_3 date DEFAULT NULL,
	gift_subscription_end_date_3 date DEFAULT NULL,
	own_subscription_identifier_4 varchar(16) DEFAULT NULL,
	own_subscription_media_4 varchar(2) DEFAULT NULL,
	own_subscription_status_4 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_4 date DEFAULT NULL,
	own_subscription_end_date_4 date DEFAULT NULL,
	gift_subscription_end_date_4 date DEFAULT NULL,
	own_subscription_identifier_5 varchar(16) DEFAULT NULL,
	own_subscription_media_5 varchar(2) DEFAULT NULL,
	own_subscription_status_5 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_5 date DEFAULT NULL,
	own_subscription_end_date_5 date DEFAULT NULL,
	gift_subscription_end_date_5 date DEFAULT NULL,
	own_subscription_identifier_6 varchar(16) DEFAULT NULL,
	own_subscription_media_6 varchar(2) DEFAULT NULL,
	own_subscription_status_6 varchar(16) DEFAULT NULL,
	own_subscription_creation_date_6 date DEFAULT NULL,
	own_subscription_end_date_6 date DEFAULT NULL,
	gift_subscription_end_date_6 date DEFAULT NULL,
	PRIMARY KEY (`uid`),
	INDEX (`update_timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE USER 'apgcrm'@'%' IDENTIFIED BY 'd9T42l35';
GRANT SELECT ON apg.crm_export TO 'apgcrm'@'%';

update istanze_abbonamenti set update_timestamp = CURRENT_TIMESTAMP;
update anagrafiche set update_timestamp = CURRENT_TIMESTAMP ;

ALTER TABLE `anagrafiche` DROP COLUMN `uid_merge_list`; 
