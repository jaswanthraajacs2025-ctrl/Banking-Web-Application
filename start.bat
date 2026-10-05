@echo off
title Apex Horizon Online Banking
echo ========================================================
echo Starting Apex Horizon Online Banking Application...
echo ========================================================

if "%JAVA_HOME%"=="" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
)

echo Starting server on http://localhost:8080 (dev profile with H2 database)...
echo When the server starts up, open http://localhost:8080 in your browser.
echo Press Ctrl+C in this window whenever you want to stop the application.
echo.

call .\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
pause
