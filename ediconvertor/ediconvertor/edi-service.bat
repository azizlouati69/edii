@echo off
echo Starting EDI Service...
java -jar target\ediconvertor-0.0.1-SNAPSHOT.jar --server.port=8081
pause
