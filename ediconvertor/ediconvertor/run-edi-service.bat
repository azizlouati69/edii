@echo off
title EDI Service Console
echo Starting EDI Service on port 8081...
echo.
echo Press Ctrl+C to stop the service
echo.
java -jar ediconvertor-0.0.1-SNAPSHOT.jar --server.port=8081
pause
