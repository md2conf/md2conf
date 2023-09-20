FROM eclipse-temurin:17.0.8_7-jre
RUN mkdir /opt/app
COPY md2conf-cli/target/*-shaded.jar /opt/app/md2conf-cli.jar
ENTRYPOINT ["java", "-jar", "/opt/app/md2conf-cli.jar"]
CMD ["help"]