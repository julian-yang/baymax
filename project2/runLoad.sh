#!/bin/bash

# Run the drop.sql batch file to drop existing tables
# Inside the drop.sql, you sould check whether the table exists. Drop them ONLY if they exists.
echo Dropping prexisting tables...
mysql -u cs144 CS144 < drop.sql
echo ...Done
# Run the create.sql batch file to create the database and tables
echo Creating new tables...
mysql -u cs144 CS144 < create.sql
echo ...Done
# Compile and run the parser to generate the appropriate load files
echo Compiling and running parser...
ant
ant run-all
echo ...Done

# Run the load.sql batch file to load the data
echo Loading data into MySQL...
mysql -u cs144 CS144 < load.sql
echo ...Done

# Remove all temporary files
echo Remove temp files...
rm *.dat
echo ...Done

echo All steps completed!

