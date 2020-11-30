# Install APG Core in Maven repository
cd ~/development/workspaces/workspace-giunti/lmeoni/apg-core

export MAVEN_HOME=~/development/maven/apache-maven-3.6.3
export PATH=$PATH:$MAVEN_HOME/bin

export TARGET=~/development/workspaces/workspace-giunti/

#mvn generate-sources
mvn clean install -Pqlt

# Package export module
cd ../apg-export
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apgexport.war $TARGET
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package web services module
cd ../apg-ws
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apgws.war $TARGET
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package jobs module
cd ../apg-automation
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apgautomation.war $TARGET
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Package GWT user interfare
cd ../apg-ui
rm -R src/main/webapp/apg
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes
mvn clean compile war:inplace package -Pqlt
mv target/apg.war $TARGET
rm -R src/main/webapp/WEB-INF/lib
rm -R src/main/webapp/WEB-INF/classes

# Leaves in repository apg-core jar with dev profile
cd ../apg-core
#mvn build-helper:remove-project-artifact
mvn clean install -Pdev

cd ~
