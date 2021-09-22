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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.md2conf.model.ConfluenceContent;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.confluence.client.http.InternalRestClient;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.confluence.client.metadata.ConfluenceContentInstance;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.KEEP_ORPHANS;
import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.REMOVE_ORPHANS;
import static io.github.md2conf.confluence.client.utils.InputStreamUtils.inputStreamAsString;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 */
public class ConfluenceClientTest {

    private static final String TEST_RESOURCES = "src/test/resources/io/github/md2conf/confluence/client";
    private static final String SOME_CONFLUENCE_CONTENT_SHA256_HASH = "7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae";

    @Test
    public void publish_withMetadataMissingSpaceKey_throwsIllegalArgumentException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            // arrange + act
            InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
            ConfluenceClient confluenceClient = confluencePublisher("without-space-key", confluenceRestClientMock);
            confluenceClient.publish();
        });
        assertTrue(exception.getMessage().contains("spaceKey must be set"));
    }

    @Test
    public void publish_withMetadataMissingAncestorId_throwsIllegalArgumentException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            // arrange + act
            InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
            ConfluenceClient confluenceClient = confluencePublisher("without-ancestor-id", confluenceRestClientMock);
            confluenceClient.publish();
        });
        assertTrue(exception.getMessage().contains("ancestorId must be set"));
    }

    @Test
    public void publish_oneNewPageWithAncestorId_delegatesToConfluenceRestClient() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageByTitle(anyString(), anyString())).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn("2345");

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("one-page-ancestor-id", confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq("~personalSpace"), eq("72189173"), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq("version message"));
        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_multiplePageWithAncestorId_delegatesToConfluenceRestClient() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageByTitle(anyString(), anyString())).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn("2345", "3456");

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("multiple-page-ancestor-id", confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq("~personalSpace"), eq("72189173"), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq("version message"));
        verify(confluenceRestClientMock, times(1)).addPageUnderAncestor(eq("~personalSpace"), eq("72189173"), eq("Some Other Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq("version message"));
        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Other Confluence Content", "<h1>Some Confluence Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_multipleRootPageAndReplaceAncestorPublishingStrategy_throwsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            ConfluenceClient confluenceClient = confluencePublisher("multiple-page-ancestor-id", PublishingStrategy.REPLACE_ANCESTOR, "version message");
            confluenceClient.publish();
        });
        assertTrue(exception.getMessage().contains("Multiple root pages found ('Some Confluence Content', 'Some Other Confluence Content'), but 'REPLACE_ANCESTOR' publishing strategy only supports one single root page"));
    }

    @Test
    public void publish_noRootPageAndReplaceAncestorPublishingStrategy_throwsException() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {

            ConfluenceClient confluenceClient = confluencePublisher("zero-page", PublishingStrategy.REPLACE_ANCESTOR, "version message");
            confluenceClient.publish();
        });
        assertTrue(exception.getMessage().contains("No root page found, but 'REPLACE_ANCESTOR' publishing strategy requires one single root page"));
    }

    @Test
    public void publish_multiplePagesInHierarchyWithAncestorIdAsRoot_delegatesToConfluenceRestClient() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn("1234", "2345");
        when(confluenceRestClientMock.getPageByTitle(anyString(), anyString())).thenThrow(new NotFoundException());

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-multiple-pages", confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        ArgumentCaptor<String> spaceKeyArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> ancestorIdArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> titleArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> contentArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(confluenceRestClientMock, times(2)).addPageUnderAncestor(spaceKeyArgumentCaptor.capture(), ancestorIdArgumentCaptor.capture(), titleArgumentCaptor.capture(), contentArgumentCaptor.capture(), messageArgumentCaptor.capture());
        assertThat(spaceKeyArgumentCaptor.getAllValues(), contains("~personalSpace", "~personalSpace"));
        assertThat(ancestorIdArgumentCaptor.getAllValues(), contains("72189173", "1234"));
        assertThat(titleArgumentCaptor.getAllValues(), contains("Some Confluence Content", "Some Child Content"));
        assertThat(contentArgumentCaptor.getAllValues(), contains("<h1>Some Confluence Content</h1>", "<h1>Some Child Content</h1>"));
        assertThat(messageArgumentCaptor.getAllValues(), contains("version message", "version message"));

        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Some Confluence Content", "<h1>Some Confluence Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Child Content", "<h1>Some Child Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataOnePageWithNewAttachmentsAndAncestorIdAsRoot_attachesAttachmentToContent() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.addPageUnderAncestor(anyString(), anyString(), anyString(), anyString(), nullable(String.class))).thenReturn("4321");
        when(confluenceRestClientMock.getPageByTitle(anyString(), anyString())).thenThrow(new NotFoundException());
        when(confluenceRestClientMock.getAttachmentByFileName(anyString(), anyString())).thenThrow(new NotFoundException());

        ArgumentCaptor<String> contentId = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> attachmentFileName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InputStream> attachmentContent = ArgumentCaptor.forClass(InputStream.class);

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-page-with-attachments", confluenceRestClientMock, confluenceClientListenerMock, null);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock).addPageUnderAncestor("~personalSpace", "72189173", "Some Confluence Content", "<h1>Some Confluence Content</h1>", null);
        verify(confluenceRestClientMock, times(2)).addAttachment(contentId.capture(), attachmentFileName.capture(), attachmentContent.capture());
        assertThat(contentId.getAllValues(), contains("4321", "4321"));
        assertThat(inputStreamAsString(attachmentContent.getAllValues().get(attachmentFileName.getAllValues().indexOf("attachmentOne.txt")), UTF_8), is("attachment1"));
        assertThat(inputStreamAsString(attachmentContent.getAllValues().get(attachmentFileName.getAllValues().indexOf("attachmentTwo.txt")), UTF_8), is("attachment2"));
        verify(confluenceRestClientMock).setPropertyByKey("4321", "attachmentOne.txt-hash", sha256Hex("attachment1"));
        verify(confluenceRestClientMock).setPropertyByKey("4321", "attachmentTwo.txt-hash", sha256Hex("attachment2"));

        verify(confluenceClientListenerMock, times(1)).pageAdded(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("4321", "Some Confluence Content", "<h1>Some Confluence Content</h1>", ConfluenceClient.INITIAL_PAGE_VERSION)));
        verify(confluenceClientListenerMock, times(1)).attachmentAdded(eq("attachmentOne.txt"), eq("4321"));
        verify(confluenceClientListenerMock, times(1)).attachmentAdded(eq("attachmentTwo.txt"), eq("4321"));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithDifferentContentUnderRootAncestor_sendsUpdateRequest() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Existing Page", "<h1>Some Other Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Existing Page")).thenReturn("3456");
        when(confluenceRestClientMock.getPageWithContentAndVersionById("3456")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("3456", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("someWrongHash");

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("existing-page-ancestor-id", confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq("version message"));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("3456"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(2), eq("version message"), eq(true));

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Existing Page", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithDifferentContentUnderRootAncestorAndReplaceAncestorStrategy_sendsUpdateRequest() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Existing Page", "<h1>Some Other Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("1234")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("1234", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("someWrongHash");

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("existing-page-ancestor-id", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq("version message"));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(2), eq("version message"), eq(true));

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Existing Page", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageWithSameContentButDifferentTitleAndReplaceAncestorStrategy_sendsUpdateRequest() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("1234")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("1234", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("existing-page-ancestor-id", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, null);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(eq("~personalSpace"), eq("1234"), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(null));
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Existing Page"), eq("<h1>Some Confluence Content</h1>"), eq(2), eq(null), eq(true));

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(existingPage), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Existing Page", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithExistingPageAndReplaceAncestorStrategy_sendsUpdate() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);
        ConfluenceClient confluenceClient = confluencePublisher("one-page-ancestor-id", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, null);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).addPageUnderAncestor(any(), any(), any(), any(), any());
        verify(confluenceRestClientMock).updatePage(eq("72189173"), eq(null), eq("Some Confluence Content"), eq("<h1>Some Confluence Content</h1>"), eq(2), eq(null), eq(true));
        verify(confluenceClientListenerMock).pageUpdated(existingPage, new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2));
        verify(confluenceClientListenerMock).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_whenAttachmentsHaveSameContentHash_doesNotUpdateAttachments() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "/download/attachmentOne.txt", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(sha256Hex("attachment1"));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "/download/attachmentTwo.txt", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(sha256Hex("attachment2"));

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-page-with-attachments", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).addAttachment(any(), any(), any());
        verify(confluenceRestClientMock, never()).updateAttachmentContent(any(), any(), any(), anyBoolean());
    }

    @Test
    public void publish_whenExistingAttachmentsHaveMissingHashProperty_updatesAttachmentsAndHashProperties() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(null);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(null);

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-page-with-attachments", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, null);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentOne.txt-hash");
        verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att1"), any(FileInputStream.class), eq(true));
        verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentOne.txt-hash", sha256Hex("attachment1"));

        verify(confluenceRestClientMock, never()).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");
        verify(confluenceRestClientMock).updateAttachmentContent(eq("72189173"), eq("att2"), any(FileInputStream.class), eq(true));
        verify(confluenceRestClientMock).setPropertyByKey("72189173", "attachmentTwo.txt-hash", sha256Hex("attachment2"));

        verify(confluenceRestClientMock, never()).addAttachment(anyString(), anyString(), any(InputStream.class));

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1)), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).attachmentUpdated(eq("attachmentOne.txt"), eq("72189173"));
        verify(confluenceClientListenerMock, times(1)).attachmentUpdated(eq("attachmentTwo.txt"), eq("72189173"));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_whenExistingAttachmentsHaveDifferentHashProperty_updatesAttachmentsAndHashProperties() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        ArgumentCaptor<InputStream> content = ArgumentCaptor.forClass(InputStream.class);

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn("otherHash1");

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn("otherHash2");

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-page-with-attachments", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);

        // act
        confluenceClient.publish();

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
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachments("72189173")).thenReturn(asList(
                new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1),
                new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1)
        ));

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("one-page-ancestor-id", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, null);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock).deleteAttachment("att1");
        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentOne.txt-hash");

        verify(confluenceRestClientMock).deleteAttachment("att2");
        verify(confluenceRestClientMock).deletePropertyByKey("72189173", "attachmentTwo.txt-hash");

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1)), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).attachmentDeleted(eq("attachmentOne.txt"), eq("72189173"));
        verify(confluenceClientListenerMock, times(1)).attachmentDeleted(eq("attachmentTwo.txt"), eq("72189173"));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_whenSomePreviouslyAttachedFilesHaveBeenRemovedFromPage_deletesAttachmentsNotPresentUnderPage() {
        // arrange
        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("72189173")).thenReturn(new io.github.md2conf.confluence.client.http.ConfluencePage("72189173", "Existing Page (Old Title)", "<h1>Some Confluence Content</h1>", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(SOME_CONFLUENCE_CONTENT_SHA256_HASH);

        when(confluenceRestClientMock.getAttachments("72189173")).thenReturn(asList(
                new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1),
                new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1),
                new ConfluenceAttachment("att3", "attachmentThree.txt", "", 1)
        ));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentOne.txt")).thenReturn(new ConfluenceAttachment("att1", "attachmentOne.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentOne.txt-hash")).thenReturn(sha256Hex("attachment1"));

        when(confluenceRestClientMock.getAttachmentByFileName("72189173", "attachmentTwo.txt")).thenReturn(new ConfluenceAttachment("att2", "attachmentTwo.txt", "", 1));
        when(confluenceRestClientMock.getPropertyByKey("72189173", "attachmentTwo.txt-hash")).thenReturn(sha256Hex("attachment2"));

        ConfluenceClient confluenceClient = confluencePublisher("root-ancestor-id-page-with-attachments", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock);

        // act
        confluenceClient.publish();

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
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = new io.github.md2conf.confluence.client.http.ConfluencePage("12", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingPage));
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("12");
        when(confluenceRestClientMock.getPageWithContentAndVersionById("12")).thenReturn(existingPage);
        when(confluenceRestClientMock.getPropertyByKey("12", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn(null);

        ConfluenceClient confluenceClient = confluencePublisher("one-page-space-key", confluenceRestClientMock);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).setPropertyByKey("12", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY, SOME_CONFLUENCE_CONTENT_SHA256_HASH);
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForAppendToAncestorPublishingStrategy_sendsDeletePageRequestForEachRemovedPage() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingParentPage = new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        io.github.md2conf.confluence.client.http.ConfluencePage existingChildPage = new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);
        io.github.md2conf.confluence.client.http.ConfluencePage existingChildChildPage = new io.github.md2conf.confluence.client.http.ConfluencePage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));
        when(confluenceRestClientMock.getChildPages("3456")).thenReturn(singletonList(existingChildChildPage));

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("zero-page", confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("3456"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("4567"));

        verify(confluenceClientListenerMock, times(1)).pageDeleted(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).pageDeleted(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3)));
        verify(confluenceClientListenerMock, times(1)).pageDeleted(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForAppendToAncestorPublishingStrategyAndKeepOrphansEnabled_doesNotDeleteRemovedPages() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage existingParentPage = new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        io.github.md2conf.confluence.client.http.ConfluencePage existingChildPage = new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);
        io.github.md2conf.confluence.client.http.ConfluencePage existingChildChildPage = new io.github.md2conf.confluence.client.http.ConfluencePage("4567", "Some Child Child Content", "<h1>Some Child Child Content</h1>", 3);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));
        when(confluenceRestClientMock.getChildPages("3456")).thenReturn(singletonList(existingChildChildPage));

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("zero-page", PublishingStrategy.APPEND_TO_ANCESTOR, KEEP_ORPHANS, confluenceRestClientMock, confluenceClientListenerMock, "version message", true);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(0)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(0)).deletePage(eq("3456"));
        verify(confluenceRestClientMock, times(0)).deletePage(eq("4567"));
    }

    @Test
    public void publish_metadataWithMultipleRemovedPagesInHierarchyForReplaceAncestorPublishingStrategy_sendsDeletePageRequestForEachRemovedPageExceptAncestor() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage ancestorPage = new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Some Ancestor Content", "<h1>Some Ancestor Content</h1>", 1);
        io.github.md2conf.confluence.client.http.ConfluencePage existingParentPage = new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2);
        io.github.md2conf.confluence.client.http.ConfluencePage existingChildPage = new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageWithContentAndVersionById("1234")).thenReturn(ancestorPage);
        when(confluenceRestClientMock.getChildPages("1234")).thenReturn(singletonList(existingParentPage));
        when(confluenceRestClientMock.getChildPages("2345")).thenReturn(singletonList(existingChildPage));

        ConfluenceClientListener confluenceClientListenerMock = mock(ConfluenceClientListener.class);

        ConfluenceClient confluenceClient = confluencePublisher("ancestor-only", PublishingStrategy.REPLACE_ANCESTOR, confluenceRestClientMock, confluenceClientListenerMock, "version message");

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).updatePage(eq("1234"), eq(null), eq("Ancestor Page"), eq("<h1>Some Ancestor Content</h1>"), eq(2), eq("version message"), eq(true));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deletePage(eq("3456"));

        verify(confluenceClientListenerMock, times(1)).pageUpdated(eq(ancestorPage), eq(new io.github.md2conf.confluence.client.http.ConfluencePage("1234", "Ancestor Page", "<h1>Some Ancestor Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).pageDeleted(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 2)));
        verify(confluenceClientListenerMock, times(1)).pageDeleted(eq(new io.github.md2conf.confluence.client.http.ConfluencePage("3456", "Some Child Content", "<h1>Some Child Content</h1>", 3)));
        verify(confluenceClientListenerMock, times(1)).publishCompleted();
        verifyNoMoreInteractions(confluenceClientListenerMock);
    }

    @Test
    public void publish_labels_withoutLabelOnPage() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage confluencePage = new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("2345");
        when(confluenceRestClientMock.getPageWithContentAndVersionById("2345")).thenReturn(confluencePage);
        when(confluenceRestClientMock.getPropertyByKey("2345", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae");
        when(confluenceRestClientMock.getLabels("2345")).thenReturn(emptyList());

        ConfluenceClient confluenceClient = confluencePublisher("page-with-labels", confluenceRestClientMock);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).getLabels(eq("2345"));
        verify(confluenceRestClientMock, times(0)).deleteLabel(eq("2345"), any(String.class));
        verify(confluenceRestClientMock, times(1)).addLabels(eq("2345"), eq(asList("label-one", "label-two")));
    }

    @Test
    public void publish_labels_withLabelsOnPage() {
        // arrange
        io.github.md2conf.confluence.client.http.ConfluencePage confluencePage = new io.github.md2conf.confluence.client.http.ConfluencePage("2345", "Some Confluence Content", "<h1>Some Confluence Content</h1>", 1);

        InternalRestClient confluenceRestClientMock = mock(InternalRestClient.class);
        when(confluenceRestClientMock.getPageByTitle("~personalSpace", "Some Confluence Content")).thenReturn("2345");
        when(confluenceRestClientMock.getPageWithContentAndVersionById("2345")).thenReturn(confluencePage);
        when(confluenceRestClientMock.getPropertyByKey("2345", ConfluenceClient.CONTENT_HASH_PROPERTY_KEY)).thenReturn("7a901829ba6a0b6f7f084ae4313bdb5d83bc2c4ea21b452ba7073c0b0c60faae");
        when(confluenceRestClientMock.getLabels("2345")).thenReturn(asList("label-two", "obsolete-label"));

        ConfluenceClient confluenceClient = confluencePublisher("page-with-labels", confluenceRestClientMock);

        // act
        confluenceClient.publish();

        // assert
        verify(confluenceRestClientMock, times(1)).getLabels(eq("2345"));
        verify(confluenceRestClientMock, times(1)).deleteLabel(eq("2345"), eq("obsolete-label"));
        verify(confluenceRestClientMock, times(0)).deleteLabel(eq("2345"), eq("label-two"));
        verify(confluenceRestClientMock, times(1)).addLabels(eq("2345"), eq(singletonList("label-one")));
    }

    private static ConfluenceClient confluencePublisher(String qualifier, InternalRestClient confluenceRestClient) {
        return confluencePublisher(qualifier, PublishingStrategy.APPEND_TO_ANCESTOR, REMOVE_ORPHANS, confluenceRestClient, mock(ConfluenceClientListener.class), null, true);
    }

    private static ConfluenceClient confluencePublisher(String qualifier, PublishingStrategy publishingStrategy, String versionMessage) {
        return confluencePublisher(qualifier, publishingStrategy, REMOVE_ORPHANS, mock(InternalRestClient.class), mock(ConfluenceClientListener.class), versionMessage, true);
    }

    private static ConfluenceClient confluencePublisher(String qualifier, PublishingStrategy publishingStrategy, InternalRestClient confluenceRestClient) {
        return confluencePublisher(qualifier, publishingStrategy, REMOVE_ORPHANS, confluenceRestClient, mock(ConfluenceClientListener.class), null, true);
    }

    private static ConfluenceClient confluencePublisher(String qualifier, InternalRestClient confluenceRestClient, ConfluenceClientListener confluenceClientListener, String versionedMessage) {
        return confluencePublisher(qualifier, PublishingStrategy.APPEND_TO_ANCESTOR, REMOVE_ORPHANS, confluenceRestClient, confluenceClientListener, versionedMessage, true);
    }

    private static ConfluenceClient confluencePublisher(String qualifier, PublishingStrategy publishingStrategy, InternalRestClient confluenceRestClient, ConfluenceClientListener confluenceClientListener, String versionMessage) {
        return confluencePublisher(qualifier, publishingStrategy, REMOVE_ORPHANS, confluenceRestClient, confluenceClientListener, versionMessage, true);
    }

    private static ConfluenceClient confluencePublisher(String qualifier, PublishingStrategy publishingStrategy, OrphanRemovalStrategy orphanRemovalStrategy, InternalRestClient confluenceRestClient, ConfluenceClientListener confluenceClientListener, String versionMessage, boolean notifyWatchers) {
        Path metadataFilePath = Paths.get(TEST_RESOURCES + "/metadata-" + qualifier + ".json");
        Path contentRoot = metadataFilePath.getParent().toAbsolutePath();

        ConfluenceContentInstance metadata = readConfig(metadataFilePath);
        resolveAbsoluteContentFileAndAttachmentsPath(metadata.getPages(), contentRoot);

        return new ConfluenceClient(metadata, publishingStrategy, orphanRemovalStrategy, confluenceRestClient, confluenceClientListener, versionMessage, notifyWatchers);
    }

    private static ConfluenceContentInstance readConfig(Path metadataFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        try {
            return objectMapper.readValue(newInputStream(metadataFile), ConfluenceContentInstance.class);
        } catch (IOException e) {
            throw new RuntimeException("Could not read metadata", e);
        }
    }

    private static void resolveAbsoluteContentFileAndAttachmentsPath(List<ConfluenceContent.ConfluencePage> pages, Path contentRoot) {
        pages.forEach((page) -> {
            page.setContentFilePath(contentRoot.resolve(page.getContentFilePath()).toString());
            page.setAttachments(page.getAttachments().entrySet().stream().collect(toMap(
                    (entry) -> entry.getValue(),
                    (entry) -> contentRoot.resolve(entry.getKey()).toString()
            )));

            resolveAbsoluteContentFileAndAttachmentsPath(page.getChildren(), contentRoot);
        });
    }

}
