# Install APG Core in Maven repository
cd ~/workspace/eclipse_gwt/apg-project/apg-core
mvn clean
mvn install -Pdev

# Package GWT user interfare
cd ../apg-ui
rm -R src/main/webapp/apg
mvn clean
mvn gwt:debug -Pdev

cd ~
