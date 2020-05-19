# Install APG Core in Maven repository
cd ~/eclipse-workspace/apg-project/apg-core
#mvn generate-sources
mvn clean install -Pprod

# Package jobs module
cd ../apg-automation
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pprod
mv target/apgautomation.war ~/eclipse-workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package web services module
cd ../apg-ws
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pprod
mv target/apgws.war ~/eclipse-workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package GWT user interfare
cd ../apg-ui
rm -R src/main/webapp/apg
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pprod
mv target/apg.war ~/eclipse-workspace/
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean install -Pdev

cd ~
