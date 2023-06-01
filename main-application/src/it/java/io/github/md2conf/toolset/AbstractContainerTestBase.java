package io.github.md2conf.toolset;

import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class AbstractContainerTestBase {


    String confluenceBaseUrl(){
        return "http://localhost:18090";
    }

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
    public String pageBodyStorageById(String id) {
        return givenAuthenticatedAsPublisher()
                .when()
                .get(confluenceBaseUrl()+ "/rest/api/content/"+id + "?expand=body.storage")
                .path("body.storage.value");
    }

    public List<String> pageAttachmentsTitles(String id) {
        return givenAuthenticatedAsPublisher()
                .when()
                .get(confluenceBaseUrl()+ "/rest/api/content/"+id + "/child/attachment")
                .path("results.title");
    }

    public void deletePageIfExists(String title) {
        String id = pageIdBy(title);
        if (id!=null) {
            givenAuthenticatedAsPublisher()
                    .when().delete(confluenceBaseUrl() + "/rest/api/content/" + id);
        }
    }


}
