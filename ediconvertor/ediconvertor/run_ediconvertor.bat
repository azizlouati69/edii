@echo off
echo Starting EDI Converter Service on port 8085...
java -jar -Dserver.port=8085 target\ediconvertor-0.0.1-SNAPSHOT.jar
pause
