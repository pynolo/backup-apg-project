@echo  Install APG Core in Maven repository
cd \Users\USER\eclipse-workspace\apg-project\apg-core
@echo mvn generate-sources
start /wait mvn clean install -Pqlt
@echo  Package jobs module
cd ..\apg-automation
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
start /wait mvn clean compile war:inplace package -Pqlt
move target\apgautomation.war ..\..
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
@echo  Package web services module
cd ..\apg-ws
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
start /wait mvn clean compile war:inplace package -Pqlt
move target\apgws.war ..\..
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
@echo  Package GWT user interfare
cd ..\apg-ui
del -R src\main\webapp\apg\*.*
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
start /wait mvn clean compile war:inplace package -Pqlt
move target\apg.war ..\..
del -R src\main\webapp\WEB-INF\lib\*.*
del -R src\main\webapp\WEB-INF\classes\*.*
@echo  Leaves in repository apg-core jar with dev profile
cd ..\apg-core
@echo mvn build-helper:remove-project-artifact
start /wait mvn clean install -Pdev
cd ..
