#!/bin/sh

# Iniciar Selenium en segundo plano
java -Dwebdriver.chrome.driver=/usr/bin/chromedriver -jar /selenium-server.jar &

# Iniciar la aplicación Spring Boot
java -jar /app.jar
