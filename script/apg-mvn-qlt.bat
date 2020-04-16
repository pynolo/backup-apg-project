# Install APG Core in Maven repository
cd \Users\USER\eclipse-workspace\apg-project\apg-core
#mvn generate-sources
mvn clean install -Pqlt

# Package jobs module
cd ..\apg-automation
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*
mvn clean compile war:inplace package -Pqlt
move target/apgautomation.war ..
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*

# Package web services module
cd ../apg-ws
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*
mvn clean compile war:inplace package -Pqlt
move target/apgws.war ..
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*

# Package GWT user interfare
cd ../apg-ui
del -R src/main/webapp/apg/*.*
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*
mvn clean compile war:inplace package -Pqlt
move target/apg.war ..
del -R src/main/webapp/WEB-INF/lib/*.*
del -R src/main/webapp/WEB-INF/classes/*.*

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean install -Pdev

cd ..
