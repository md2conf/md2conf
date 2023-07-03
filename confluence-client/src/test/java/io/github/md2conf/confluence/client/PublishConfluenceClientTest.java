/*
 * Copyright 2016-2019 the original author or authors.
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

import io.github.md2conf.confluence.client.http.ConfluenceApiPage;
import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.confluence.client.http.RestApiInternalClient;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluenceContentModel.Type;
import io.github.md2conf.model.ConfluencePage;
import io.github.md2conf.model.util.ModelReadWriteUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.KEEP_ORPHANS;
import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.REMOVE_ORPHANS;
import static io.github.md2conf.confluence.client.utils.InputStreamUtils.inputStreamAsString;
import static io.github.md2conf.model.ConfluenceContentModel.Type.STORAGE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 * @author qwazer
 */
public class PublishConfluenceClientTest {

    private static final String TEST_RESOURCES = "src/test/resources/io/github/md2conf/confluence/client";
    private static final String TEST_SPACE = "~personalSpace";
    private static final String PARENT_PAGE_TITLE = "test title";
    private static final String PARENT_PAGE_ID = "1234";
    private static final String SOME_CONFLUENCE_CONTENT_SHA256_HASH = "7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae";

    @Test
    public void publish_withMetadataMissingSpaceKey_throwsIllegalArgumentException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            // arrange + act
            RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
            PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock);
            confluenceClient.publish(new ConfluenceContentModel(), null, "tt");
        });
        assertTrue(exception.getMessage().contains("spaceKey must be set"));
    }

    @Test
    public void publish_withMetadataMissingAncestorId_throwsIllegalArgumentException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            // arrange + act
            RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
            PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock);
            confluenceClient.publish(new ConfluenceContentModel(), "any", null);
        });
        assertTrue(exception.getMessage().contains("parentTitle must be set"));
    }

    @Test
    public void publish_oneNewPageWithAncestorId_delegatesToConfluenceRestClient() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), any(Type.class), anyString())).thenReturn("2345");

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);


        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("one-page-ancestor-id");

        // act
        confluenceClient.publish(model, TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq(TEST_SPACE), eq(PARENT_PAGE_ID), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq("version message"));
        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("2345", "Some Confluence Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_multiplePageWithAncestorId_delegatesToConfluenceRestClient() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Other Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), any(Type.class), anyString())).thenReturn("2345", "3456");

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("multiple-page-ancestor-id");

        // act
        confluenceClient.publish(model, TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq("~personalSpace"), eq(PARENT_PAGE_ID), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq("version message"));
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq("~personalSpace"), eq(PARENT_PAGE_ID), eq("Some Other Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq("version message"));
        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("2345", "Some Confluence Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("3456", "Some Other Confluence Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_multipleRootPageAndReplaceAncestorPublishingStrategy_throwsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, "version message");
            ConfluenceContentModel model = readFromFilePrefix("multiple-page-ancestor-id");
            confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);
        });
        assertTrue(exception.getMessage().contains("Multiple root pages found ('Some Confluence Content', 'Some Other Confluence Content'), but 'REPLACE_ANCESTOR' publishing strategy only supports one single root page"));
    }

    @Test
    public void publish_noRootPageAndReplaceAncestorPublishingStrategy_throwsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, "version message");
            ConfluenceContentModel model = readFromFilePrefix("zero-page");
            confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);
        });
        assertTrue(exception.getMessage().contains("No root page found, but 'REPLACE_ANCESTOR' publishing strategy requires one single root page"));
    }

    @Test
    public void publish_multiplePagesInHierarchyWithAncestorIdAsRoot_delegatesToConfluenceRestClient() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Other Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Child Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), any(Type.class), anyString())).thenReturn("2345", "3456");

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-multiple-pages");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        ArgumentCaptor<String> spaceKeyArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ancestorIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Type> contentTypeArgumentCaptor = ArgumentCaptor.forClass(Type.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(confluenceRestClientMock, times(2)).addPageUnderAncestor(spaceKeyArgumentCaptor.capture(), ancestorIdArgumentCaptor.capture(), titleArgumentCaptor.capture(), contentArgumentCaptor.capture(), contentTypeArgumentCaptor.capture(), messageArgumentCaptor.capture());
        assertThat(spaceKeyArgumentCaptor.getAllValues(), contains("~personalSpace", "~personalSpace"));
        assertThat(ancestorIdArgumentCaptor.getAllValues(), contains("1234", "2345"));
        assertThat(titleArgumentCaptor.getAllValues(), contains("Some Confluence Content", "Some Child Content"));
        assertThat(contentArgumentCaptor.getAllValues(), contains("<h1>Some Confluence Content</h1>", "<h1>Some Child Content</h1>"));
        assertThat(contentTypeArgumentCaptor.getAllValues(), Matchers.hasItems(STORAGE));
        assertThat(messageArgumentCaptor.getAllValues(), contains("version message", "version message"));

        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("2345", "Some Confluence Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("3456", "Some Child Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataOnePageWithNewAttachmentsAndAncestorIdAsRoot_attachesAttachmentToContent() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), any(Type.class), nullable(String.class))).thenReturn("4321");
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Other Confluence Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, "Some Child Content")).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getAttachmentByFileName(anyString(), anyString())).thenThrow(new NotFoundException());

        ArgumentCaptor<String> contentId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> attachmentFileName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> attachmentContent = ArgumentCaptor.forClass(InputStream.class);

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, null);
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-page-with-attachments");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock).addPageUnderAncestor("~personalSpace", PARENT_PAGE_ID, "Some Confluence Content", "<h1>Some Confluence Content</h1>", STORAGE, null);
        verify(confluenceRestClientMock, times(2)).addAttachment(contentId.capture(), attachmentFileName.capture(), attachmentContent.capture());
        assertThat(contentId.getAllValues(), contains("4321", "4321"));
        assertThat(inputStreamAsString(attachmentContent.getAllValues().get(attachmentFileName.getAllValues().indexOf("attachmentOne.txt")), UTF_8), is("attachment1"));
        assertThat(inputStreamAsString(attachmentContent.getAllValues().get(attachmentFileName.getAllValues().indexOf("attachmentTwo.txt")), UTF_8), is("attachment2"));
        verify(confluenceRestClientMock).setPropertyByKey("4321", "attachmentOne.txt-hash", sha256Hex("attachment1"));
        verify(confluenceRestClientMock).setPropertyByKey("4321", "attachmentTwo.txt-hash", sha256Hex("attachment2"));

        verify(publishConfluenceClientListenerMock, times(1)).pageAdded(eq(new ConfluenceApiPage("4321", "Some Confluence Content", null, PublishConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentAdded(eq("attachmentOne.txt"), eq("4321"));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentAdded(eq("attachmentTwo.txt"), eq("4321"));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithDifferentContentUnderRootAncestor_sendsUpdateRequest() {
        // arrange
        ConfluenceApiPage existingPage = new ConfluenceApiPage("3456", "Existing Page", "<h1>Some Other Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Existing Page")).thenReturn("3456");
        when(confluenceRestClientMock.getPageWithViewContent("3456")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("3456", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("someWrongHash");

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("existing-page-ancestor-id");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), any(Type.class), eq("version message"));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("3456"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq(2), eq("version message"), eq(true));

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new ConfluenceApiPage("3456", "Existing Page", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithDifferentContentUnderRootAncestorAndReplaceAncestorStrategy_sendsUpdateRequest() {
        // arrange
        ConfluenceApiPage existingPage = new ConfluenceApiPage("1234", "Existing Page", "<h1>Some Other Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageWithViewContent("1234")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("1234", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("someWrongHash");

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("existing-page-ancestor-id");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), any(Type.class), eq("version message"));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq(2), eq("version message"), eq(true));

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new ConfluenceApiPage("1234", "Existing Page", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithSameContentButDifferentTitleAndReplaceAncestorStrategy_sendsUpdateRequest() {
        // arrange
        ConfluenceApiPage existingPage = new ConfluenceApiPage("1234", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageWithViewContent("1234")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("1234", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, null);
        ConfluenceContentModel model = readFromFilePrefix("existing-page-ancestor-id");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), any(Type.class), eq(null));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq(2), eq(null), eq(true));

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new ConfluenceApiPage("1234", "Existing Page", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageAndReplaceAncestorStrategy_sendsUpdate() {
        // arrange
        ConfluenceApiPage existingPage = new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);
        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, null);
        ConfluenceContentModel model = readFromFilePrefix("one-page-ancestor-id");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(any(), any(), any(), any(), any(Type.class), any());
        verify(confluenceRestClientMock).updatePage(eq("72189173"), eq(null), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq(STORAGE), eq(2), eq(null), eq(true));
        verify(publishConfluenceClientListenerMock).pageUpdated(existingPage, new ConfluenceApiPage("72189173", "Some Confluence Content", null, 2));
        verify(publishConfluenceClientListenerMock).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_whenAttachmentsHaveSameContentHash_doesNotUpdateAttachments() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "/download/attachmentOne.txt", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(sha256Hex("attachment1"));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "/download/attachmentTwo.txt", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(sha256Hex("attachment2"));

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-page-with-attachments");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).addAttachment(any(), any(), any());
        verify(confluenceRestClientMock, never()).updateAttachmentContent(any(), any(), any(), anyBoolean());
    }

    @Test
    public void publish_whenExistingAttachmentsHaveMissingHashProperty_updatesAttachmentsAndHashProperties() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(null);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(null);

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, null);
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-page-with-attachments");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentOne.txt-hash");
        verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att1"), any(FileInputStream.class), eq(true));
        verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentOne.txt-hash", sha256Hex("attachment1"));

        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");
        verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att2"), any(FileInputStream.class), eq(true));
        verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentTwo.txt-hash", sha256Hex("attachment2"));

        verify(confluenceRestClientMock, never()).addAttachment(anyString(), anyString(), any(InputStream.class));

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1)), eq(new ConfluenceApiPage("72189173", "Some Confluence Content", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentUpdated(eq("attachmentOne.txt"), eq("72189173"));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentUpdated(eq("attachmentTwo.txt"), eq("72189173"));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_whenExistingAttachmentsHaveDifferentHashProperty_updatesAttachmentsAndHashProperties() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        ArgumentCaptor<InputStream> content = ArgumentCaptor.forClass(InputStream.class);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn("otherHash1");

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn("otherHash2");

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-page-with-attachments");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        InOrder inOrder = inOrder(confluenceRestClientMock);
        inOrder.verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentOne.txt-hash");
        inOrder.verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att1"), content.capture(), eq(true));
        inOrder.verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentOne.txt-hash", sha256Hex("attachment1"));
        assertThat(inputStreamAsString(content.getValue(), UTF_8), is("attachment1"));

        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");
        verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att2"), content.capture(), eq(true));
        verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentTwo.txt-hash", sha256Hex("attachment2"));
        assertThat(inputStreamAsString(content.getValue(), UTF_8), is("attachment2"));

        verify(confluenceRestClientMock, never()).addAttachment(anyString(), anyString(), any(InputStream.class));
    }

    @Test
    public void publish_whenNewAttachmentsAreEmpty_deletesAttachmentsPresentOnConfluence() {
        //arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachments("72189173")).thenReturn(asList(
                new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1),
                new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1)
        ));

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, null);
        ConfluenceContentModel model = readFromFilePrefix("one-page-ancestor-id");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock).deleteAttachment("att1");
        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentOne.txt-hash");

        verify(confluenceRestClientMock).deleteAttachment("att2");
        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1)), eq(new ConfluenceApiPage("72189173", "Some Confluence Content", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentDeleted(eq("attachmentOne.txt"), eq("72189173"));
        verify(publishConfluenceClientListenerMock, times(1)).attachmentDeleted(eq("attachmentTwo.txt"), eq("72189173"));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_whenSomePreviouslyAttachedFilesHaveBeenRemovedFromPage_deletesAttachmentsNotPresentUnderPage() {
        // arrange
        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn("72189173");
        when(confluenceRestClientMock.getPageWithViewContent("72189173")).thenReturn(new ConfluenceApiPage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachments("72189173")).thenReturn(asList(
                new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1),
                new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1),
                new ConfluenceAttachment("att3", "attachmentThree.txt", "", 1)
        ));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(sha256Hex("attachment1"));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(sha256Hex("attachment2"));

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("root-ancestor-id-page-with-attachments");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, never()).deleteAttachment("att1");
        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentOne.txt-hash");

        verify(confluenceRestClientMock, never()).deleteAttachment("att2");
        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");

        verify(confluenceRestClientMock).deleteAttachment("att3");
        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentThree.txt-hash");
    }

    @Test
    public void publish_metadataWithOneExistingPageButConfluencePageHasMissingHashPropertyValue_pageIsUpdatedAndHashPropertyIsSet() {
        // arrange
        ConfluenceApiPage existingPage = new ConfluenceApiPage("12", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingPage));
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("12");
        when(confluenceRestClientMock.getPageWithViewContent("12")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("12", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(null);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("one-page-space-key");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).setPropertyByKey("12", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY, SOME_CONFLUENCE_CONTENT_SHA256_HASH);
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForAppendToAncestorPublishingStrategy_sendsDeletePageRequestForEachRemovedPage() {
        // arrange
        ConfluenceApiPage existingParentPage = new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        ConfluenceApiPage existingChildPage = new ConfluenceApiPage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);
        ConfluenceApiPage existingChildChildPage = new ConfluenceApiPage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));
        when(confluenceRestClientMock.getChildPages("3456")).thenReturn(singletonList(existingChildChildPage));

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("zero-page");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("3456"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("4567"));

        verify(publishConfluenceClientListenerMock, times(1)).pageDeleted(eq(new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(publishConfluenceClientListenerMock, times(1)).pageDeleted(eq(new ConfluenceApiPage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3)));
        verify(publishConfluenceClientListenerMock, times(1)).pageDeleted(eq(new ConfluenceApiPage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForAppendToAncestorPublishingStrategyAndKeepOrphansEnabled_doesNotDeleteRemovedPages() {
        // arrange
        ConfluenceApiPage existingParentPage = new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        ConfluenceApiPage existingChildPage = new ConfluenceApiPage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);
        ConfluenceApiPage existingChildChildPage = new ConfluenceApiPage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));
        when(confluenceRestClientMock.getChildPages("3456")).thenReturn(singletonList(existingChildChildPage));

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.APPEND_TO_ANCESTOR, KEEP_ORPHANS, confluenceRestClientMock, publishConfluenceClientListenerMock, "version message", true);
        ConfluenceContentModel model = readFromFilePrefix("zero-page");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(0)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(0)).deletePage(eq("3456"));
        verify(confluenceRestClientMock, times(0)).deletePage(eq("4567"));
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForReplaceAncestorPublishingStrategy_sendsDeletePageRequestForEachRemovedPageExceptAncestor() {
        // arrange
        ConfluenceApiPage ancestorPage = new ConfluenceApiPage("1234", "Some Ancestor Content", "<h1>Some Ancestor Content</h1>", 1);
        ConfluenceApiPage existingParentPage = new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        ConfluenceApiPage existingChildPage = new ConfluenceApiPage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle(TEST_SPACE, PARENT_PAGE_TITLE)).thenReturn(PARENT_PAGE_ID);
        when(confluenceRestClientMock.getPageWithViewContent("1234")).thenReturn(ancestorPage);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));

        PublishConfluenceClientListener publishConfluenceClientListenerMock = mock(PublishConfluenceClientListener.class);

        PublishConfluenceClient confluenceClient = confluencePublisher(PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, publishConfluenceClientListenerMock, "version message");
        ConfluenceContentModel model = readFromFilePrefix("ancestor-only");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);
        // assert
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Ancestor Page"), eq("<h1>Some Ancestor Content</h1>"), eq(STORAGE), eq(2), eq("version message"), eq(true));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("3456"));

        verify(publishConfluenceClientListenerMock, times(1)).pageUpdated(eq(ancestorPage), eq(new ConfluenceApiPage("1234", "Ancestor Page", null, 2)));
        verify(publishConfluenceClientListenerMock, times(1)).pageDeleted(eq(new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(publishConfluenceClientListenerMock, times(1)).pageDeleted(eq(new ConfluenceApiPage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3)));
        verify(publishConfluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(publishConfluenceClientListenerMock);
    }

    @Test
    public void publish_labels_withoutLabelOnPage() {
        // arrange
        ConfluenceApiPage confluenceApiPage = new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("2345");
        when(confluenceRestClientMock.getPageWithViewContent("2345")).thenReturn(confluenceApiPage);
        when(confluenceRestClientMock.getPropertyByKey("2345", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae");
        when(confluenceRestClientMock.getLabels("2345")).thenReturn(emptyList());

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("page-with-labels");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).getLabels(eq("2345"));
        verify(confluenceRestClientMock, times(0)).deleteLabel(eq("2345"), any(String.class));
        verify(confluenceRestClientMock, times(1)).addLabels(eq("2345"), eq(asList("label-one", "label-two")));
    }

    @Test
    public void publish_labels_withLabelsOnPage() {
        // arrange
        ConfluenceApiPage confluenceApiPage = new ConfluenceApiPage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        RestApiInternalClient confluenceRestClientMock = mock(RestApiInternalClient.class);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("2345");
        when(confluenceRestClientMock.getPageWithViewContent("2345")).thenReturn(confluenceApiPage);
        when(confluenceRestClientMock.getPropertyByKey("2345", PublishConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae");
        when(confluenceRestClientMock.getLabels("2345")).thenReturn(asList("label-two", "obsolete-label"));

        PublishConfluenceClient confluenceClient = confluencePublisher(confluenceRestClientMock);
        ConfluenceContentModel model = readFromFilePrefix("page-with-labels");

        // act
        confluenceClient.publish(model,TEST_SPACE, PARENT_PAGE_TITLE);

        // assert
        verify(confluenceRestClientMock, times(1)).getLabels(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deleteLabel(eq("2345"), eq("obsolete-label"));
        verify(confluenceRestClientMock, times(0)).deleteLabel(eq("2345"), eq("label-two"));
        verify(confluenceRestClientMock, times(1)).addLabels(eq("2345"), eq(singletonList("label-one")));
    }

    private static PublishConfluenceClient confluencePublisher(RestApiInternalClient confluenceRestClient) {
        return confluencePublisher(PublishingStrategy.APPEND_TO_ANCESTOR, REMOVE_ORPHANS, confluenceRestClient, mock(PublishConfluenceClientListener.class), null, true);
    }

    private static PublishConfluenceClient confluencePublisher(PublishingStrategy publishingStrategy, String versionMessage) {
        return confluencePublisher(publishingStrategy, REMOVE_ORPHANS, mock(RestApiInternalClient.class), mock(PublishConfluenceClientListener.class), versionMessage, true);
    }

    private static PublishConfluenceClient confluencePublisher(PublishingStrategy publishingStrategy, RestApiInternalClient confluenceRestClient) {
        return confluencePublisher(publishingStrategy, REMOVE_ORPHANS, confluenceRestClient, mock(PublishConfluenceClientListener.class), null, true);
    }

    private static PublishConfluenceClient confluencePublisher(RestApiInternalClient confluenceRestClient, PublishConfluenceClientListener publishConfluenceClientListener, String versionedMessage) {
        return confluencePublisher(PublishingStrategy.APPEND_TO_ANCESTOR, REMOVE_ORPHANS, confluenceRestClient, publishConfluenceClientListener, versionedMessage, true);
    }

    private static PublishConfluenceClient confluencePublisher(PublishingStrategy publishingStrategy, RestApiInternalClient confluenceRestClient, PublishConfluenceClientListener publishConfluenceClientListener, String versionMessage) {
        return confluencePublisher(publishingStrategy, REMOVE_ORPHANS, confluenceRestClient, publishConfluenceClientListener, versionMessage, true);
    }

    private static ConfluenceContentModel readFromFilePrefix(String qualifier){
        Path path = Paths.get(TEST_RESOURCES + "/metadata-" + qualifier + ".json");
       ConfluenceContentModel model =  ModelReadWriteUtil.readFromYamlOrJson(path.toFile());
       resolveAbsoluteContentFileAndAttachmentsPath(model.getPages(), path.getParent().toAbsolutePath());
       return model;
    }

    private static PublishConfluenceClient confluencePublisher(PublishingStrategy publishingStrategy, OrphanRemovalStrategy orphanRemovalStrategy, RestApiInternalClient confluenceRestClient, PublishConfluenceClientListener publishConfluenceClientListener, String versionMessage, boolean notifyWatchers) {

        return new PublishConfluenceClient( publishingStrategy, orphanRemovalStrategy, confluenceRestClient, publishConfluenceClientListener, versionMessage, notifyWatchers);
    }

    private static void resolveAbsoluteContentFileAndAttachmentsPath(List<ConfluencePage> pages, Path contentRoot) {
        pages.forEach((page) -> {
            page.setContentFilePath(contentRoot.resolve(page.getContentFilePath()).toString());
            page.setType(STORAGE);
            page.setAttachments(page.getAttachments().entrySet().stream().collect(toMap(
                    (entry) -> entry.getValue(),
                    (entry) -> contentRoot.resolve(entry.getKey()).toString()
            )));

            resolveAbsoluteContentFileAndAttachmentsPath(page.getChildren(), contentRoot);
        });
    }

}
