# Install APG Core in Maven repository
cd ~/workspace/gwt-eclipse/apg-project/apg-core
mvn clean
mvn install -Pprod

# Package updater module
cd ../apg-updater
mvn package -Pbuild
mv target/apgupdater-jar-with-dependencies.jar ~/workspace/

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean
mvn install -Pdev

cd ~