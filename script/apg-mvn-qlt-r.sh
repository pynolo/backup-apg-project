# Install APG Core in Maven repository
cd apg-core
mvn clean install -Pqlt

# Package jobs module
cd ../apg-automation
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apgautomation.war ~/workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package web services module
cd ../apg-ws
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apgws.war ~/workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package GWT user interfare
cd ../apg-ui
rm -R src/main/webapp/apg
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apg.war ~/workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean install -Pdev

cd ~
