# Install APG Core in Maven repository
cd ~/workspace/eclipse_gwt/apg-project/apg-core
mvn clean
mvn install -Ptest

# Package jobs module
cd ../apg-automation
mvn package -Ptest
mv target/apgautomation.war ~/workspace/

# Package web services module
cd ../apg-ws/
mvn package -Pbuild
mv target/apgws.war ~/workspace/

# Package GWT user interfare
cd ../apg-ui
#mvn gwt:compile -Dgwt.compiler.force
mvn package -Pbuild
mv target/apg.war ~/workspace/

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean
mvn install -Pdev

cd ~
