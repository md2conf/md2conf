/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.md2conf.confluence.client;


import io.github.md2conf.model.ConfluenceContent;
import io.github.md2conf.model.ConfluencePage;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static java.util.Collections.emptyMap;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 * @author qwazer resheto@gmail.com
 */
public class ConfluenceClientIntegrationTest extends AbstractContainerTestBase {

    private static final String ANCESTOR_ID = "65551"; //todo replace by dynamic resolution by title

    @Test
    public void publish_singlePageWithAttachments_pageIsCreatedAndAttachmentsAddedInConfluence() {
        // arrange
        String title = uniqueTitle("Single Page");

        Map<String, String> attachments = new HashMap<>();
        attachments.put("attachmentOne.txt", absolutePathTo("attachments/attachmentOne.txt"));
        attachments.put("attachmentTwo.txt", absolutePathTo("attachments/attachmentTwo.txt"));

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, absolutePathTo("single-page/single-page.xhtml"), attachments);
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);
        ConfluenceClient confluenceClient = createConfluenceClient(confluenceContent);

        // act
        confluenceClient.publish();

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(childPages())
                .then().body("results.title", hasItem(title));

        givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageIdBy(title)))
                .then()
                .body("results", hasSize(2))
                .body("results.title", hasItems("attachmentOne.txt", "attachmentTwo.txt"));
    }

    @Test
    public void publish_sameAttachmentsPublishedMultipleTimes_publishProcessDoesNotFail() {
        // arrange
        String title = uniqueTitle("Single Page");
        Map<String, String> attachments = new HashMap<>();
        attachments.put("attachmentOne.txt", absolutePathTo("attachments/attachmentOne.txt"));
        attachments.put("attachmentTwo.txt", absolutePathTo("attachments/attachmentTwo.txt"));

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, absolutePathTo("single-page/single-page.xhtml"), attachments);
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);
        ConfluenceClient confluenceClient = createConfluenceClient(confluenceContent);

        // act
        confluenceClient.publish();
        confluenceClient.publish();

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageIdBy(title)))
                .then()
                .body("results", hasSize(2))
                .body("results.title", hasItems("attachmentOne.txt", "attachmentTwo.txt"));
    }

    @Test
    public void publish_attachmentIsDeleted_attachmentIsReuploaded() {
        // arrange
        String title = uniqueTitle("Single Page");
        Map<String, String> attachments = new HashMap<>();
        attachments.put("attachmentOne.txt", absolutePathTo("attachments/attachmentOne.txt"));
        attachments.put("attachmentTwo.txt", absolutePathTo("attachments/attachmentTwo.txt"));

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, absolutePathTo("single-page/single-page.xhtml"), attachments);
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);
        ConfluenceClient confluenceClient = createConfluenceClient(confluenceContent);

        // act
        confluenceClient.publish();

        // assert

        String pageId = pageIdBy(title);

        givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageId))
                .then()
                .body("results", hasSize(2))
                .body("results.title", hasItems("attachmentOne.txt", "attachmentTwo.txt"));

        // act
        givenAuthenticatedAsPublisher()
                .when().delete(attachment(firstAttachmentId(pageId)));

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageId))
                .then()
                .body("results", hasSize(1))
                .body("results.title", anyOf(hasItem("attachmentTwo.txt"), hasItem("attachmentOne.txt")));

        // act
        confluenceClient.publish();

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageId))
                .then()
                .body("results", hasSize(2))
                .body("results.title", hasItems("attachmentOne.txt", "attachmentTwo.txt"));
    }


    @Test
    public void publish_sameContentPublishedMultipleTimes_doesNotProduceMultipleVersions() {
        // arrange
        String title = uniqueTitle("Single Page");
        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, absolutePathTo("single-page/single-page.xhtml"));
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);
        ConfluenceClient confluenceClient = createConfluenceClient(confluenceContent);

        // act
        confluenceClient.publish();
        confluenceClient.publish();

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(pageVersionOf(pageIdBy(title)))
                .then().body("version.number", is(1));
    }

    @Test
    public void publish_validPageContentThenInvalidPageContentThenValidContentAgain_validPageContentWithNonEmptyContentHashIsInConfluenceAtTheEndOfPublication() {
        // arrange
        String title = uniqueTitle("Invalid Markup Test Page");

        ConfluencePage confluencePage = createConfluencePageWithAttachments(title, absolutePathTo("single-page/single-page.xhtml"));
        ConfluenceContent confluenceContent = new ConfluenceContent(confluencePage);
        ConfluenceClient confluenceClient = createConfluenceClient(confluenceContent);

        // act
        confluenceClient.publish();

        confluencePage.setContentFilePath(absolutePathTo("single-page/invalid-xhtml.xhtml"));
        try {
            confluenceClient.publish();
            fail("publish with invalid XHTML is expected to fail");
        } catch (Exception ignored) {
        }

        confluencePage.setContentFilePath(absolutePathTo("single-page/single-page.xhtml"));
        confluenceClient.publish();

        // assert
        givenAuthenticatedAsPublisher()
                .when().get(propertyValueOf(pageIdBy(title), "content-hash"))
                .then().body("value", is(notNullValue()));
    }

    private static String uniqueTitle(String title) {
        return title + " - " + randomUUID();
    }

    private static ConfluencePage createConfluencePageWithAttachments(String title, String contentFilePath) {
        return createConfluencePageWithAttachments(title, contentFilePath, emptyMap());
    }


    private static ConfluencePage createConfluencePageWithAttachments(String title, String contentFilePath, Map<String, String> attachments) {
        ConfluencePage confluencePage = new ConfluencePage();
        confluencePage.setTitle(title);
        confluencePage.setContentFilePath(contentFilePath);
        confluencePage.setAttachments(attachments);
        return confluencePage;
    }


    private static String absolutePathTo(String relativePath) {
        return Paths.get("src/it/resources/").resolve(relativePath).toAbsolutePath().toString();
    }

    private String childPages() {
        return confluenceBaseUrl()+ "/rest/api/content/" + ANCESTOR_ID + "/child/page";
    }

    private String attachmentsOf(String contentId) {
        return confluenceBaseUrl()+ "/rest/api/content/" + contentId + "/child/attachment";
    }

    private String attachment(String attachmentId) {
        return confluenceBaseUrl()+ "/rest/api/content/" + attachmentId;
    }

    private String pageVersionOf(String contentId) {
        return confluenceBaseUrl()+"/rest/api/content/" + contentId + "?expand=version";
    }

    private String propertyValueOf(String contentId, String key) {
        return confluenceBaseUrl()+"/rest/api/content/" + contentId + "/property/" + key;
    }

    private String firstAttachmentId(String pageId) {
        return givenAuthenticatedAsPublisher()
                .when().get(attachmentsOf(pageId))
                .path("results[0].id");
    }

    private String pageIdBy(String title) {
        return givenAuthenticatedAsPublisher()
                .when().get(childPages())
                .path("results.find({it.title == '" + title + "'}).id");
    }

    private ConfluenceClient createConfluenceClient(ConfluenceContent confluenceContent) {
        ConfluenceClientConfigurationProperties properties = aDefaultConfluenceClientConfigurationProperties().build();
        return ConfluenceClientFactory.confluenceClient(properties, confluenceContent, null );
    }

    private static RequestSpecification givenAuthenticatedAsPublisher() {
        return given().auth().preemptive().basic("admin", "admin");
    }

}
