ALTER TABLE `fatture` ADD COLUMN `pubblica` bit(1) NOT NULL DEFAULT true;
update fatture set pubblica = false where numero_fattura like 'ZZZ%';

###

#Aggiornamento località - giugno 2018
update localita set cap='29031' where (nome like 'CAMINATA' and cap='29010');
update localita set cap='29031' where (nome like 'NIBBIANO' and cap='29010');
update localita set cap='29031' where (nome like 'PECORARA' and cap='29010');
update localita set cap='15047' where (nome like 'ALLUVIONI CAMBIÒ' and cap='15040');
update localita set cap='15047' where (nome like 'PIOVERA' and cap='15040');
update localita set cap='13029' where (nome like 'RIMA SAN GIUSEPPE' and cap='13026');
update localita set cap='13029' where (nome like 'RIMASCO' and cap='13026');
update localita set cap='13024' where (nome like 'BREIA');
update localita set cap='13019' where (nome like 'SABBIA' and cap='13020');
update localita set cap='13024' where (nome like 'BREIA' and cap='13020');
update localita set cap='10079' where (nome like 'MAPPANO' and cap='10072');
update localita set cap='18028' where (nome like 'CARPASIO' and cap='18010');
update localita set cap='18028' where (nome like 'MONTALTO LIGURE' and cap='18010');
update localita set cap='38036' where (nome like 'VIGO DI FASSA' and cap='38039');
update localita set cap='23836' where (nome like 'INTROZZO' and cap='23835');
update localita set cap='23836' where (nome like 'VESTRENO' and cap='23822');
update localita set cap='46036' where (nome like 'PIEVE DI CORIANO' and cap='46020');
update localita set cap='46036' where (nome like 'VILLA POMA' and cap='46020');
update localita set cap='46028' where (nome like 'FELONICA' and cap='46022');
update localita set cap='22023' where (nome like 'CASASCO D%INTELVI' and cap='22022');
update localita set cap='22023' where (nome like 'SAN FEDELE INTELVI' and cap='22028');
update localita set cap='26844' where (nome like 'CAMAIRAGO' and cap='26823');
update localita set cap='87059' where (nome like 'CASOLE BRUZIO' and cap='87050');
update localita set cap='87059' where (nome like 'PEDACE' and cap='87050');
update localita set cap='87059' where (nome like 'SERRA PEDACE' and cap='87050');
update localita set cap='87059' where (nome like 'SPEZZANO PICCOLO' and cap='87050');
update localita set cap='87059' where (nome like 'TRENTA' and cap='87050');
update localita set cap='52019' where (nome like 'LATERINA' and cap='52020');
update localita set cap='52019' where (nome like 'PERGINE VALDARNO' and cap='52020');
update localita set cap='57038' where (nome like 'RIO NELL%ELBA' and cap='57039');
update localita set cap='36044' where (nome like 'GRANCONA' and cap='36040');
update localita set cap='36044' where (nome like 'SAN GERMANO DEI BERICI' and cap='36040');
update localita set cap='33012', id_provincia='UD' where (nome like 'SAPPADA');
update localita set cap='36044' where (nome like 'PEDERIVA' and cap='36040');
update localita set cap='87059' where (nome like 'PERITO' and cap='87050');
update localita set cap='87059' where (nome like 'SILVANA MANSIO' and cap='87050');
update localita set cap='87059' where (nome like 'MAGLI' and cap='87050');
update localita set cap='87059' where (nome like 'MORELLI' and cap='87050');
update localita set cap='29031' where (nome like 'STRÀ' and cap='29010');
update localita set cap='29031' where (nome like 'TASSARA' and cap='29010');
update localita set cap='29031' where (nome like 'TREVOZZO' and cap='29010');
update localita set cap='15047' where (nome like 'GRAVA' and cap='15040');
update localita set cap='38036' where (nome like 'COSTALUNGA' and cap='38039');
update localita set cap='52019' where (nome like 'PONTICINO' and cap='52020');
update localita set cap='52019' where (nome like 'CASALONE' and cap='52020');
update localita set cap='52019' where (nome like 'CAVI' and cap='52020');
update localita set cap='52019' where (nome like 'LATERINA STAZIONE' and cap='52020');
update localita set cap='52019' where (nome like 'MONTALTO' and cap='52020');
update localita set cap='52019' where (nome like 'PIEVE A PRESCIANO' and cap='52020');
update localita set cap='09089' where (nome like 'BOSA' and cap='08013');
update localita set cap='09089' where (nome like 'BOSA MARINA' and cap='08013');
update localita set cap='09089' where (nome like 'TURAS' and cap='08013');
update localita set cap='09090' where (nome like 'LACONI' and cap='08034');
update localita set cap='09090' where (nome like 'CRASTU' and cap='08034');
update localita set cap='09090' where (nome like 'SANTA SOFIA' and cap='08034');
update localita set cap='09090' where (nome like 'SU LAU' and cap='08034');
update localita set cap='09090' where (nome like 'TRAIDODINI' and cap='08034');
update localita set cap='09090' where (nome like 'MAGOMADAS');
update localita set cap='09090' where (nome like 'SA LUMENERA' and cap='08010');
update localita set cap='09090' where (nome like 'MONTRESTA' and cap='08010');
update localita set cap='09090' where (nome like 'SANTA MARIA' and cap='08010');
update localita set cap='09090' where (nome like 'FLUSSIO' and cap='08010');
update localita set cap='09090' where (nome like 'MODOLO' and cap='08019');
update localita set cap='09090' where (nome like 'SAGAMA' and cap='08010');
update localita set cap='09090' where (nome like 'SUNI' and cap='08010');
update localita set cap='09090' where (nome like 'TINNURA' and cap='08010');
update localita set cap='22061' where (nome like 'CAMPIONE D%ITALIA' and cap='22060');
update localita set cap='23041' where (nome like 'LIVIGNO');
update localita set cap='23041' where (nome like 'TREPALLE');
#Riordino Sardegna
insert into province (id,nome_provincia,id_regione,istat) values ('SU','Sud Sardegna','SAR',111);
update localita set id_provincia='SU' where id_provincia='VS';#Sardegna: Medio Campidano -> SU
update localita set id_provincia='SU' where id_provincia='CI';#Sardegna: Carbonia Iglesias -> SU
update localita set id_provincia='SS' where id_provincia='OT';#Sardegna: Olbia Tempio -> Sassari
update localita set id_provincia='NU' where id_provincia='OG';#Sardegna: Ogliastra -> Nuoro
#Comuni -> Sud Sardegna
update localita set id_provincia='SU', cap='09057' where (nome like 'NURAGUS' and cap='08030');
update localita set id_provincia='SU', cap='09057' where (nome like 'LIXIUS' and cap='08030');
update localita set id_provincia='SU', cap='09051' where (nome like 'ESCALAPLANO' and cap='08043');
update localita set id_provincia='SU', cap='09052' where (nome like 'ESCOLCA' and cap='08030');
update localita set id_provincia='SU', cap='09053' where (nome like 'ESTERZILI' and cap='08030');
update localita set id_provincia='SU', cap='09054' where (nome like 'GENONI' and cap='08030');
update localita set id_provincia='SU', cap='09055' where (nome like 'GERGEI' and cap='08030');
update localita set id_provincia='SU', cap='09056' where (nome like 'ISILI' and cap='08033');
update localita set id_provincia='SU', cap='09058' where (nome like 'NURALLAO' and cap='08030');
update localita set id_provincia='SU', cap='09059' where (nome like 'NURRI' and cap='08035');
update localita set id_provincia='SU', cap='09061' where (nome like 'ORROLI' and cap='08030');
update localita set id_provincia='SU', cap='09062' where (nome like 'SADALI' and cap='08030');
update localita set id_provincia='SU', cap='09063' where (nome like 'SERRI' and cap='08030');
update localita set id_provincia='SU', cap='09064' where (nome like 'SEUI' and cap='08037');
update localita set id_provincia='SU', cap='09065' where (nome like 'SEULO' and cap='08030');
update localita set id_provincia='SU', cap='09066' where (nome like 'VILLANOVA TULO' and cap='08030');
#Comuni -> Sassari
update localita set id_provincia='SS', cap='07051' where (nome like 'BUDONI' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'AGRUSTOS' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'BERRUILES' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'BIRGALAVÒ' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'LIMPIDDU' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'LOTTURAI' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'LUDDUI' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'LUTTUNI' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'MAIORCA' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'MALAMORÌ' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'MURISCUVÒ' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'NUDITTA' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'OTTIOLU' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'PEDRA E CUPA' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SAN GAVINO' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SAN LORENZO' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SAN PIETRO' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SAN SILVESTRO' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'S%ISCALA' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SOLITÀ' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'STRUGAS' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'SU LINALVU' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'TAMARISPA' and cap='08020');
update localita set id_provincia='SS', cap='07051' where (nome like 'TANAUNELLA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'SAN TEODORO' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'BADUALGA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'BUDDITOGLIU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'CAPO CODA CAVALLO' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'FRANCULACCIU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LA RUNCINA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'L%ALZONI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LI MORI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LI TEGGI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU CUPONEDDI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU FRAILI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU IMPOSTU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU LIONI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU MUVRUNEDDU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU RICCIU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LU TITIMBARU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'LUTTURAI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'MONTE PETROSU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'NURAGHEDDU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'PATTIMEDDA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'PIRA MASEDA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'PUNTALDIA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'RINAGGIU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'SCHIFONI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'SILIMINI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'SITAGLIACCIU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'STAZZU BRUCIATU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'STAZZU MESU' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'STRAULAS' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'SUAREDDA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'TERRAPADEDDA' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'TIRIDDULI' and cap='08020');
update localita set id_provincia='SS', cap='07052' where (nome like 'TRAVERSA' and cap='08020');
#Aggiornamento indirizzi
update indirizzi set cap='29031' where (localita like 'CAMINATA' and cap='29010');
update indirizzi set cap='29031' where (localita like 'NIBBIANO' and cap='29010');
update indirizzi set cap='29031' where (localita like 'PECORARA' and cap='29010');
update indirizzi set cap='15047' where (localita like 'ALLUVIONI CAMBIÒ' and cap='15040');
update indirizzi set cap='15047' where (localita like 'PIOVERA' and cap='15040');
update indirizzi set cap='13029' where (localita like 'RIMA SAN GIUSEPPE' and cap='13026');
update indirizzi set cap='13029' where (localita like 'RIMASCO' and cap='13026');
update indirizzi set cap='13024' where (localita like 'BREIA');
update indirizzi set cap='13019' where (localita like 'SABBIA' and cap='13020');
update indirizzi set cap='13024' where (localita like 'BREIA' and cap='13020');
update indirizzi set cap='10079' where (localita like 'MAPPANO' and cap='10072');
update indirizzi set cap='18028' where (localita like 'CARPASIO' and cap='18010');
update indirizzi set cap='18028' where (localita like 'MONTALTO LIGURE' and cap='18010');
update indirizzi set cap='38036' where (localita like 'VIGO DI FASSA' and cap='38039');
update indirizzi set cap='23836' where (localita like 'INTROZZO' and cap='23835');
update indirizzi set cap='23836' where (localita like 'VESTRENO' and cap='23822');
update indirizzi set cap='46036' where (localita like 'PIEVE DI CORIANO' and cap='46020');
update indirizzi set cap='46036' where (localita like 'VILLA POMA' and cap='46020');
update indirizzi set cap='46028' where (localita like 'FELONICA' and cap='46022');
update indirizzi set cap='22023' where (localita like 'CASASCO D%INTELVI' and cap='22022');
update indirizzi set cap='22023' where (localita like 'SAN FEDELE INTELVI' and cap='22028');
update indirizzi set cap='26844' where (localita like 'CAMAIRAGO' and cap='26823');
update indirizzi set cap='87059' where (localita like 'CASOLE BRUZIO' and cap='87050');
update indirizzi set cap='87059' where (localita like 'PEDACE' and cap='87050');
update indirizzi set cap='87059' where (localita like 'SERRA PEDACE' and cap='87050');
update indirizzi set cap='87059' where (localita like 'SPEZZANO PICCOLO' and cap='87050');
update indirizzi set cap='87059' where (localita like 'TRENTA' and cap='87050');
update indirizzi set cap='52019' where (localita like 'LATERINA' and cap='52020');
update indirizzi set cap='52019' where (localita like 'PERGINE VALDARNO' and cap='52020');
update indirizzi set cap='57038' where (localita like 'RIO NELL%ELBA' and cap='57039');
update indirizzi set cap='36044' where (localita like 'GRANCONA' and cap='36040');
update indirizzi set cap='36044' where (localita like 'SAN GERMANO DEI BERICI' and cap='36040');
update indirizzi set cap='33012', id_provincia='UD' where (localita like 'SAPPADA');
update indirizzi set cap='36044' where (localita like 'PEDERIVA' and cap='36040');
update indirizzi set cap='87059' where (localita like 'PERITO' and cap='87050');
update indirizzi set cap='87059' where (localita like 'SILVANA MANSIO' and cap='87050');
update indirizzi set cap='87059' where (localita like 'MAGLI' and cap='87050');
update indirizzi set cap='87059' where (localita like 'MORELLI' and cap='87050');
update indirizzi set cap='29031' where (localita like 'STRÀ' and cap='29010');
update indirizzi set cap='29031' where (localita like 'TASSARA' and cap='29010');
update indirizzi set cap='29031' where (localita like 'TREVOZZO' and cap='29010');
update indirizzi set cap='15047' where (localita like 'GRAVA' and cap='15040');
update indirizzi set cap='38036' where (localita like 'COSTALUNGA' and cap='38039');
update indirizzi set cap='52019' where (localita like 'PONTICINO' and cap='52020');
update indirizzi set cap='52019' where (localita like 'CASALONE' and cap='52020');
update indirizzi set cap='52019' where (localita like 'CAVI' and cap='52020');
update indirizzi set cap='52019' where (localita like 'LATERINA STAZIONE' and cap='52020');
update indirizzi set cap='52019' where (localita like 'MONTALTO' and cap='52020');
update indirizzi set cap='52019' where (localita like 'PIEVE A PRESCIANO' and cap='52020');
update indirizzi set cap='09089' where (localita like 'BOSA' and cap='08013');
update indirizzi set cap='09089' where (localita like 'BOSA MARINA' and cap='08013');
update indirizzi set cap='09089' where (localita like 'TURAS' and cap='08013');
update indirizzi set cap='09090' where (localita like 'LACONI' and cap='08034');
update indirizzi set cap='09090' where (localita like 'CRASTU' and cap='08034');
update indirizzi set cap='09090' where (localita like 'SANTA SOFIA' and cap='08034');
update indirizzi set cap='09090' where (localita like 'SU LAU' and cap='08034');
update indirizzi set cap='09090' where (localita like 'TRAIDODINI' and cap='08034');
update indirizzi set cap='09090' where (localita like 'MAGOMADAS');
update indirizzi set cap='09090' where (localita like 'SA LUMENERA' and cap='08010');
update indirizzi set cap='09090' where (localita like 'MONTRESTA' and cap='08010');
update indirizzi set cap='09090' where (localita like 'SANTA MARIA' and cap='08010');
update indirizzi set cap='09090' where (localita like 'FLUSSIO' and cap='08010');
update indirizzi set cap='09090' where (localita like 'MODOLO' and cap='08019');
update indirizzi set cap='09090' where (localita like 'SAGAMA' and cap='08010');
update indirizzi set cap='09090' where (localita like 'SUNI' and cap='08010');
update indirizzi set cap='09090' where (localita like 'TINNURA' and cap='08010');
update indirizzi set cap='22061' where (localita like 'CAMPIONE D%ITALIA' and cap='22060');
update indirizzi set cap='23041' where (localita like 'LIVIGNO');
update indirizzi set cap='23041' where (localita like 'TREPALLE');
update indirizzi set id_provincia='SU' where id_provincia='VS';#Sardegna: Medio Campidano -> SU
update indirizzi set id_provincia='SU' where id_provincia='CI';#Sardegna: Carbonia Iglesias -> SU
update indirizzi set id_provincia='SS' where id_provincia='OT';#Sardegna: Olbia Tempio -> Sassari
update indirizzi set id_provincia='NU' where id_provincia='OG';#Sardegna: Ogliastra -> Nuoro
update indirizzi set id_provincia='SU', cap='09057' where (localita like 'NURAGUS' and cap='08030');
update indirizzi set id_provincia='SU', cap='09057' where (localita like 'LIXIUS' and cap='08030');
update indirizzi set id_provincia='SU', cap='09051' where (localita like 'ESCALAPLANO' and cap='08043');
update indirizzi set id_provincia='SU', cap='09052' where (localita like 'ESCOLCA' and cap='08030');
update indirizzi set id_provincia='SU', cap='09053' where (localita like 'ESTERZILI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09054' where (localita like 'GENONI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09055' where (localita like 'GERGEI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09056' where (localita like 'ISILI' and cap='08033');
update indirizzi set id_provincia='SU', cap='09058' where (localita like 'NURALLAO' and cap='08030');
update indirizzi set id_provincia='SU', cap='09059' where (localita like 'NURRI' and cap='08035');
update indirizzi set id_provincia='SU', cap='09061' where (localita like 'ORROLI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09062' where (localita like 'SADALI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09063' where (localita like 'SERRI' and cap='08030');
update indirizzi set id_provincia='SU', cap='09064' where (localita like 'SEUI' and cap='08037');
update indirizzi set id_provincia='SU', cap='09065' where (localita like 'SEULO' and cap='08030');
update indirizzi set id_provincia='SU', cap='09066' where (localita like 'VILLANOVA TULO' and cap='08030');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'BUDONI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'AGRUSTOS' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'BERRUILES' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'BIRGALAVÒ' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'LIMPIDDU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'LOTTURAI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'LUDDUI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'LUTTUNI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'MAIORCA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'MALAMORÌ' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'MURISCUVÒ' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'NUDITTA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'OTTIOLU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'PEDRA E CUPA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SAN GAVINO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SAN LORENZO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SAN PIETRO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SAN SILVESTRO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'S%ISCALA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SOLITÀ' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'STRUGAS' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'SU LINALVU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'TAMARISPA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07051' where (localita like 'TANAUNELLA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'SAN TEODORO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'BADUALGA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'BUDDITOGLIU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'CAPO CODA CAVALLO' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'FRANCULACCIU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LA RUNCINA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'L%ALZONI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LI MORI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LI TEGGI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU CUPONEDDI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU FRAILI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU IMPOSTU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU LIONI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU MUVRUNEDDU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU RICCIU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LU TITIMBARU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'LUTTURAI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'MONTE PETROSU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'NURAGHEDDU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'PATTIMEDDA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'PIRA MASEDA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'PUNTALDIA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'RINAGGIU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'SCHIFONI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'SILIMINI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'SITAGLIACCIU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'STAZZU BRUCIATU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'STAZZU MESU' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'STRAULAS' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'SUAREDDA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'TERRAPADEDDA' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'TIRIDDULI' and cap='08020');
update indirizzi set id_provincia='SS', cap='07052' where (localita like 'TRAVERSA' and cap='08020');
#Vecchie provincie sarde
delete from province where id='VS';#Medio campidano
delete from province where id='OT';#Olbia Tempio
delete from province where id='OG';#Ogliastra
delete from province where id='CI';#Carbonia Iglesias

###

ALTER TABLE `utenti` ADD COLUMN `aziendale` bit(1) NOT NULL DEFAULT true;
ALTER TABLE `utenti` ADD COLUMN `password_reset` bit(1) NOT NULL DEFAULT false;
ALTER TABLE `utenti` CHANGE COLUMN `password` `password` varchar(32) DEFAULT NULL;
DROP TABLE IF EXISTS `utenti_password`;
CREATE TABLE `utenti_password` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_utente` varchar(32) NOT NULL,
  `password_md5` varchar(128) NOT NULL DEFAULT '25D55AD283AA400AF464C76D713C07AD',
  `data_creazione` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
ALTER TABLE `utenti_password` ADD INDEX `id_utente` (`id_utente`);
UPDATE utenti set aziendale=false, password_reset = true;
UPDATE utenti set aziendale=true, password_reset = false where password is null;
UPDATE utenti set aziendale=true, password_reset = false where password like '';
#INSERT INTO `utenti_password` (id_utente, password_md5, data_creazione)  
#	SELECT id, '25D55AD283AA400AF464C76D713C07AD', data_modifica FROM `utenti`;
#UPDATE utenti_password set password_md5='' where id_utente like 'api';

###

ALTER TABLE `anagrafiche` CHANGE COLUMN `note` `note` varchar(2048) DEFAULT NULL;
ALTER TABLE `anagrafiche` CHANGE COLUMN `uid_merge_list` `uid_merge_list` varchar(1024) DEFAULT NULL;
ALTER TABLE `istanze_abbonamenti` CHANGE COLUMN `note` `note` varchar(2048) DEFAULT NULL;

###

update anagrafiche a1 join anagrafiche a2 on a1.id=a2.id_anagrafica_da_aggiornare
	set a1.necessita_verifica = true
	where a1.necessita_verifica = false;

