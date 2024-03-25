package io.github.md2conf.maven.plugin;

import io.restassured.specification.RequestSpecification;
import org.apache.maven.shared.verifier.VerificationException;
import org.apache.maven.shared.verifier.Verifier;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static org.apache.maven.shared.verifier.util.ResourceExtractor.extractResourcePath;

public class AbstractMd2ConfMojoIT {


    @TempDir
    private File tmpDir;

    protected static Map<String, String> mandatoryProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("confluenceUrl", "http://localhost:8090");
        properties.put("username", "admin");
        properties.put("password", "admin");
        properties.put("spaceKey", "ds");
        properties.put("parentPageTitle", "Welcome to Confluence");
        return properties;
    }



    protected File invokeGoalAndVerify(String goal, String pathToContent, Map<String, String> pomProperties) {
        return invokeGoalAndVerify(goal, pathToContent, pomProperties, emptyMap(), null);
    }


   private File invokeGoalAndVerify(String goal, String pathToContent, Map<String, String> pomProperties, Map<String, String> serverProperties, String encryptedMasterPassword) {
        boolean useCommandLineArguments = false; //todo this.propertiesMode.equals(COMMAND_LINE_ARGUMENTS);

        try {
            File projectDir = extractResourcePath(getClass(), "/" + pathToContent, tmpDir, true);
            invokeGoalAndVerify(goal, projectDir, pomProperties, serverProperties, encryptedMasterPassword, useCommandLineArguments);
            return projectDir;
        } catch (Exception e) {
            throw new IllegalStateException("goal failed", e);
        }
    }

    private static void invokeGoalAndVerify(String goal, File projectDir, Map<String, String> mavenProperties, Map<String, String> serverProperties, String encryptedMasterPassword, boolean useCommandLineArguments) throws IOException, VerificationException, VerificationException, VerificationException {
        Map<String, String> pomProperties = useCommandLineArguments ? emptyMap() : mavenProperties;

        Path pomPath = projectDir.toPath().resolve("pom.xml");
        Files.write(pomPath, generatePom(pomProperties).getBytes(UTF_8));

        Path settingsPath = projectDir.toPath().resolve("settings.xml");
        Files.write(settingsPath, generateSettings(serverProperties).getBytes(UTF_8));

        Verifier verifier = new Verifier(projectDir.getAbsolutePath());

        if (encryptedMasterPassword != null) {
            Path mavenSettingsDirectoryPath = projectDir.toPath().resolve(".m2");
            Files.createDirectories(mavenSettingsDirectoryPath);

            Path securitySettingsPath = mavenSettingsDirectoryPath.resolve("settings-security.xml");
            Files.write(securitySettingsPath, generateSecuritySettings(encryptedMasterPassword).getBytes(UTF_8));
            verifier.addCliArgument("-s " + settingsPath.toAbsolutePath());
            verifier.addCliArgument("-Duser.home=" + projectDir.getAbsolutePath());
        }

        try {
            if (useCommandLineArguments) {
                mavenProperties.forEach((key, value) -> {
                    if (value.contains("//")) {
                        // maven verifier cli options parsing replaces // with /
                        value = value.replaceAll("//", "////");
                    }

                    if (value.contains(" ")) {
                        value = "'" + value + "'";
                    }

                    verifier.addCliArgument("-Dmd2conf." + key + "=" + value);
                });
            }


            verifier.addCliArgument("io.github.md2conf:md2conf-maven-plugin:"+goal);
            verifier.execute();

            verifier.verifyErrorFreeLog();
        } finally {
            displayMavenLog(verifier);
        }

    }

    private static void displayMavenLog(Verifier verifier) throws IOException {
        File logFile = new File(verifier.getBasedir(), verifier.getLogFileName());
         Files.readAllLines(logFile.toPath()).forEach(System.out::println);
    }

    private static String generatePom(Map<String, String> properties) {
        String pluginVersion = System.getProperty("md2conf.version", "0.2.11-SNAPSHOT");

        return "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">" +
                "    <modelVersion>4.0.0</modelVersion>" +
                "" +
                "    <groupId>io.github.md2conf</groupId>" +
                "    <artifactId>md2conf-it</artifactId>" +
                "    <version>0.0.0-SNAPSHOT</version>" +
                "    <packaging>pom</packaging>" +
                "" +
                "    <build>" +
                "        <plugins>" +
                "            <plugin>" +
                "                <groupId>io.github.md2conf</groupId>" +
                "                <artifactId>md2conf-maven-plugin</artifactId>" +
                "                <version>" + pluginVersion + "</version>" +
                "                <configuration>" +

                properties.entrySet().stream()
                        .map((property) -> "<" + property.getKey() + "  xml:space=\"preserve\">" + property.getValue() + "</" + property.getKey() + ">")
                        .collect(joining("")) +

                "                </configuration>" +
                "            </plugin>" +
                "        </plugins>" +
                "    </build>" +
                "" +
                "</project>";
    }

    private static String generateSettings(Map<String, String> properties) {
        return "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"\n" +
                "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd\">\n" +
                "  <servers>\n" +
                "    <server>\n" +

                properties.entrySet().stream()
                        .map((property) -> "<" + property.getKey() + ">" + property.getValue() + "</" + property.getKey() + ">")
                        .collect(joining("")) +

                "    </server>\n" +
                "  </servers>\n" +
                "</settings>";
    }

    private static String generateSecuritySettings(String encryptedMasterPassword) {
        return "<settingsSecurity>\n" +
                "  <master>" + encryptedMasterPassword + "</master>\n" +
                "</settingsSecurity>";
    }


    protected static RequestSpecification givenAuthenticatedAsPublisher() {
        return given().auth().preemptive().basic("admin", "admin");
    }

    private static String rootPage() {
        return page("65551");
    }

    protected static String page(String pageId) {
        return "http://localhost:8090/rest/api/content/" + pageId + "?expand=body.view,history.lastUpdated";
    }

    protected static String childPages() {
        return "http://localhost:8090/rest/api/content/65551/child/page";
    }

    protected static String childPages(String pageId) {
        return "http://localhost:8090/rest/api/content/"+pageId+"/child/page";
    }

    protected static String pageIdBy(String title) {
        return givenAuthenticatedAsPublisher()
                .when().get(childPages())
                .then().extract().jsonPath().getString("results.find({it.title == '" + title + "'}).id");
    }

}
