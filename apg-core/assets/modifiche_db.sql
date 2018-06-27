ALTER TABLE `fatture` ADD COLUMN `pubblica` bit(1) NOT NULL DEFAULT true;
update fatture set pubblica = false where numero_fattura like 'ZZZ%';