# Install APG Core in Maven repository
cd ~/workspace/gwt-eclipse/apg-project/apg-core
mvn clean
mvn install -Pdev

# Package GWT user interfare
cd ../apg-ui
rm -R src/main/webapp/apg
rm -R src/main/webapp/WEB-INF/lib
mvn clean
mvn gwt:debug -Pdev

cd ~
