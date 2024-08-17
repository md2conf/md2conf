FROM eclipse-temurin:21.0.4_7-jre
RUN mkdir /opt/app
COPY md2conf-jar/target/md2conf.jar /opt/app/md2conf.jar
ENTRYPOINT ["java", "-jar", "/opt/app/md2conf.jar"]
CMD ["help"]