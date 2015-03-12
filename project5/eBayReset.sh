#!/bin/bash

# remove eBay.war and eBay directory in webapps folder
rm -rf $CATALINA_BASE/webapps/eBay*

# use ant build on project4
cd /home/cs144/shared/baymax/project5
ant build

# copy new eBay.war file to webapps
cp /home/cs144/shared/baymax/project5/build/eBay.war $CATALINA_BASE/webapps
