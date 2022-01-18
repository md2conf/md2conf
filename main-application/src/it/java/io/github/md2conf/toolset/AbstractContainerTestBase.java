package io.github.md2conf.toolset;

import io.restassured.specification.RequestSpecification;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static io.restassured.RestAssured.given;

@Testcontainers
public class AbstractContainerTestBase {

    @Container
    public static GenericContainer confluence = new GenericContainer(DockerImageName.parse("qwazer/atlassian-sdk-confluence:latest"))
            .withExposedPorts(8090)
            .waitingFor(Wait.forHttp("/")
                            .forStatusCode(200)
                            .forStatusCode(302)
                            .withStartupTimeout(Duration.ofMinutes(10)));

    String confluenceBaseUrl(){
        return String.format("http://localhost:%s", confluence.getFirstMappedPort());
    }

//     String confluenceBaseUrl(){
//        return String.format("http://localhost:%s", 8090);
//    }

    static String PARENT_PAGE_TITLE = "Welcome to Confluence";

    static String[] CLI_OPTIONS = {"--username", "admin", "--password", "admin", "-s", "ds", "-pt", PARENT_PAGE_TITLE};


    public static RequestSpecification givenAuthenticatedAsPublisher() {
        return given().auth().preemptive().basic("admin", "admin");
    }

    private static final String ANCESTOR_ID = "65551"; //todo replace by dynamic resolution by title
    private String childPages() {
        return confluenceBaseUrl()+ "/rest/api/content/" + ANCESTOR_ID + "/child/page";
    }

    public String pageIdBy(String title) {
        return givenAuthenticatedAsPublisher()
                .when().get(childPages())
                .path("results.find({it.title == '" + title + "'}).id");
    }

    public void deletePageIfExists(String title) {
        String id = pageIdBy(title);
        if (id!=null) {
            givenAuthenticatedAsPublisher()
                    .when().delete(confluenceBaseUrl() + "/rest/api/content/" + id);
        }
    }


}
