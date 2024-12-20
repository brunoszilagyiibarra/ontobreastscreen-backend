FROM selenium/standalone-chrome:latest

# Instalar Java 17
USER root

# Añadir el repositorio de OpenJDK 17
RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    && apt-get clean;

# Configurar JAVA_HOME
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# Verificar que Java 17 está instalado
RUN java -version


# Copiar el archivo JAR de la aplicación Spring Boot al contenedor
COPY build/libs/breast-cancer-recommendation-api-5.2.jar /app.jar

# Exponer el puerto de la aplicación Spring Boot (si fuera necesario)
EXPOSE 8080

# Copiar el script start.sh
COPY start.sh /start.sh

# Darle permisos de ejecución
RUN chmod +x /start.sh


# Establecer el comando de entrada para ejecutar el servidor de Selenium y la aplicación Spring Boot
ENTRYPOINT ["/start.sh"]

