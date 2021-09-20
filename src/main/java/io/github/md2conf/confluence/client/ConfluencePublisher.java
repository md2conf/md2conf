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

import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.confluence.client.http.ConfluenceApiClient;
import io.github.md2conf.confluence.client.http.ConfluencePage;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.confluence.client.metadata.ConfluencePageMetadata;
import io.github.md2conf.confluence.client.metadata.ConfluencePublisherMetadata;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.REMOVE_ORPHANS;
import static io.github.md2conf.confluence.client.utils.AssertUtils.assertMandatoryParameter;
import static io.github.md2conf.confluence.client.utils.InputStreamUtils.fileContent;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 */
public class ConfluencePublisher {

    static final String CONTENT_HASH_PROPERTY_KEY = "content-hash";
    static final int INITIAL_PAGE_VERSION = 1;

    private final ConfluencePublisherMetadata metadata;
    private final PublishingStrategy publishingStrategy;
    private final OrphanRemovalStrategy orphanRemovalStrategy;
    private final ConfluenceApiClient confluenceApiClient;
    private final ConfluencePublisherListener confluencePublisherListener;
    private final String versionMessage;
    private final boolean notifyWatchers;

    public ConfluencePublisher(ConfluencePublisherMetadata metadata, PublishingStrategy publishingStrategy, OrphanRemovalStrategy orphanRemovalStrategy,
                               ConfluenceApiClient confluenceApiClient, ConfluencePublisherListener confluencePublisherListener,
                               String versionMessage, boolean notifyWatchers) {
        this.metadata = metadata;
        this.publishingStrategy = publishingStrategy;
        this.orphanRemovalStrategy = orphanRemovalStrategy;
        this.confluenceApiClient = confluenceApiClient;
        this.confluencePublisherListener = confluencePublisherListener != null ? confluencePublisherListener : new NoOpConfluencePublisherListener();
        this.versionMessage = versionMessage;
        this.notifyWatchers = notifyWatchers;
    }

    public void publish() {
        assertMandatoryParameter(isNotBlank(this.metadata.getSpaceKey()), "spaceKey");
        assertMandatoryParameter(isNotBlank(this.metadata.getAncestorId()), "ancestorId");

        switch (this.publishingStrategy) {
            case APPEND_TO_ANCESTOR:
                startPublishingUnderAncestorId(this.metadata.getPages(), this.metadata.getSpaceKey(), this.metadata.getAncestorId());
                break;
            case REPLACE_ANCESTOR:
                startPublishingReplacingAncestorId(singleRootPage(this.metadata), this.metadata.getSpaceKey(), this.metadata.getAncestorId());
                break;
            default:
                throw new IllegalArgumentException("Invalid publishing strategy '" + this.publishingStrategy + "'");
        }

        this.confluencePublisherListener.publishCompleted();
    }

    private static ConfluencePageMetadata singleRootPage(ConfluencePublisherMetadata metadata) {
        List<ConfluencePageMetadata> rootPages = metadata.getPages();

        if (rootPages.size() > 1) {
            String rootPageTitles = rootPages.stream()
                    .map(page -> "'" + page.getTitle() + "'")
                    .collect(joining(", "));

            throw new IllegalArgumentException("Multiple root pages found (" + rootPageTitles + "), but '" + PublishingStrategy.REPLACE_ANCESTOR + "' publishing strategy only supports one single root page");
        }

        if (rootPages.isEmpty()) {
            throw new IllegalArgumentException("No root page found, but '" + PublishingStrategy.REPLACE_ANCESTOR + "' publishing strategy requires one single root page");
        }

        return rootPages.get(0);
    }

    private void startPublishingReplacingAncestorId(ConfluencePageMetadata rootPage, String spaceKey, String ancestorId) {
        if (rootPage != null) {
            updatePage(ancestorId, null, rootPage);

            addOrUpdateLabels(ancestorId, rootPage.getLabels());

            deleteConfluenceAttachmentsNotPresentUnderPage(ancestorId, rootPage.getAttachments());
            addAttachments(ancestorId, rootPage.getAttachments());

            startPublishingUnderAncestorId(rootPage.getChildren(), spaceKey, ancestorId);
        }
    }

    private void startPublishingUnderAncestorId(List<ConfluencePageMetadata> pages, String spaceKey, String ancestorId) {
        if (this.orphanRemovalStrategy == REMOVE_ORPHANS) {
            deleteConfluencePagesNotPresentUnderAncestor(pages, ancestorId);
        }
        pages.forEach(page -> {
            String contentId = addOrUpdatePageUnderAncestor(spaceKey, ancestorId, page);

            addOrUpdateLabels(contentId, page.getLabels());

            deleteConfluenceAttachmentsNotPresentUnderPage(contentId, page.getAttachments());
            addAttachments(contentId, page.getAttachments());

            startPublishingUnderAncestorId(page.getChildren(), spaceKey, contentId);
        });
    }

    private void deleteConfluencePagesNotPresentUnderAncestor(List<ConfluencePageMetadata> pagesToKeep, String ancestorId) {
        List<ConfluencePage> childPagesOnConfluence = this.confluenceApiClient.getChildPages(ancestorId);

        List<ConfluencePage> childPagesOnConfluenceToDelete = childPagesOnConfluence.stream()
                .filter(childPageOnConfluence -> pagesToKeep.stream().noneMatch(page -> page.getTitle().equals(childPageOnConfluence.getTitle())))
                .collect(toList());

        childPagesOnConfluenceToDelete.forEach(pageToDelete -> {
            List<ConfluencePage> pageScheduledForDeletionChildPagesOnConfluence = this.confluenceApiClient.getChildPages(pageToDelete.getContentId());
            pageScheduledForDeletionChildPagesOnConfluence.forEach(parentPageToDelete -> this.deleteConfluencePagesNotPresentUnderAncestor(emptyList(), pageToDelete.getContentId()));
            this.confluenceApiClient.deletePage(pageToDelete.getContentId());
            this.confluencePublisherListener.pageDeleted(pageToDelete);
        });
    }

    private void deleteConfluenceAttachmentsNotPresentUnderPage(String contentId, Map<String, String> attachments) {
        List<ConfluenceAttachment> confluenceAttachments = this.confluenceApiClient.getAttachments(contentId);

        confluenceAttachments.stream()
                .filter(confluenceAttachment -> attachments.keySet().stream().noneMatch(attachmentFileName -> attachmentFileName.equals(confluenceAttachment.getTitle())))
                .forEach(confluenceAttachment -> {
                    this.confluenceApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(confluenceAttachment.getTitle()));
                    this.confluenceApiClient.deleteAttachment(confluenceAttachment.getId());
                    this.confluencePublisherListener.attachmentDeleted(confluenceAttachment.getTitle(), contentId);
                });
    }

    private String addOrUpdatePageUnderAncestor(String spaceKey, String ancestorId, ConfluencePageMetadata page) {
        String contentId;

        try {
            contentId = this.confluenceApiClient.getPageByTitle(spaceKey, page.getTitle());
            updatePage(contentId, ancestorId, page);
        } catch (NotFoundException e) {
            String content = fileContent(page.getContentFilePath(), UTF_8);
            contentId = this.confluenceApiClient.addPageUnderAncestor(spaceKey, ancestorId, page.getTitle(), content, this.versionMessage);
            this.confluenceApiClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, hash(content));
            this.confluencePublisherListener.pageAdded(new ConfluencePage(contentId, page.getTitle(), content, INITIAL_PAGE_VERSION));
        }

        return contentId;
    }

    private void updatePage(String contentId, String ancestorId, ConfluencePageMetadata page) {
        String content = fileContent(page.getContentFilePath(), UTF_8);
        ConfluencePage existingPage = this.confluenceApiClient.getPageWithContentAndVersionById(contentId);
        String existingContentHash = this.confluenceApiClient.getPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
        String newContentHash = hash(content);

        if (notSameHash(existingContentHash, newContentHash) || !existingPage.getTitle().equals(page.getTitle())) {
            this.confluenceApiClient.deletePropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
            int newPageVersion = existingPage.getVersion() + 1;
            this.confluenceApiClient.updatePage(contentId, ancestorId, page.getTitle(), content, newPageVersion, this.versionMessage, this.notifyWatchers);
            this.confluenceApiClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, newContentHash);
            this.confluencePublisherListener.pageUpdated(existingPage, new ConfluencePage(contentId, page.getTitle(), content, newPageVersion));
        }
    }

    private void addAttachments(String contentId, Map<String, String> attachments) {
        attachments.forEach((attachmentFileName, attachmentPath) -> addOrUpdateAttachment(contentId, attachmentPath, attachmentFileName));
    }

    private void addOrUpdateAttachment(String contentId, String attachmentPath, String attachmentFileName) {
        Path absoluteAttachmentPath = absoluteAttachmentPath(attachmentPath);
        String newAttachmentHash = hash(fileInputStream(absoluteAttachmentPath));

        try {
            ConfluenceAttachment existingAttachment = this.confluenceApiClient.getAttachmentByFileName(contentId, attachmentFileName);
            String attachmentId = existingAttachment.getId();
            String existingAttachmentHash = this.confluenceApiClient.getPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));

            if (notSameHash(existingAttachmentHash, newAttachmentHash)) {
                if (existingAttachmentHash != null) {
                    this.confluenceApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
                }
                this.confluenceApiClient.updateAttachmentContent(contentId, attachmentId, fileInputStream(absoluteAttachmentPath), this.notifyWatchers);
                this.confluenceApiClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
                this.confluencePublisherListener.attachmentUpdated(attachmentFileName, contentId);
            }

        } catch (NotFoundException e) {
            this.confluenceApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
            this.confluenceApiClient.addAttachment(contentId, attachmentFileName, fileInputStream(absoluteAttachmentPath));
            this.confluenceApiClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
            this.confluencePublisherListener.attachmentAdded(attachmentFileName, contentId);
        }
    }

    private String getAttachmentHashKey(String attachmentFileName) {
        return attachmentFileName + "-hash";
    }

    private Path absoluteAttachmentPath(String attachmentPath) {
        return Paths.get(attachmentPath);
    }

    private void addOrUpdateLabels(String contentId, List<String> labels) {
        List<String> existingLabels = this.confluenceApiClient.getLabels(contentId);

        existingLabels.stream()
                .filter((existingLabel) -> !(labels.contains(existingLabel)))
                .forEach((labelToDelete) -> this.confluenceApiClient.deleteLabel(contentId, labelToDelete));

        List<String> labelsToAdd = labels.stream()
                .filter((label) -> !(existingLabels.contains(label)))
                .collect(toList());

        if (labelsToAdd.size() > 0) {
            this.confluenceApiClient.addLabels(contentId, labelsToAdd);
        }
    }

    private static boolean notSameHash(String actualHash, String newHash) {
        return actualHash == null || !actualHash.equals(newHash);
    }

    private static String hash(String content) {
        return sha256Hex(content);
    }

    private static String hash(InputStream content) {
        try {
            return sha256Hex(content);
        } catch (IOException e) {
            throw new RuntimeException("Could not compute hash from input stream", e);
        } finally {
            try {
                content.close();
            } catch (IOException ignored) {
            }
        }
    }

    private static FileInputStream fileInputStream(Path filePath) {
        try {
            return new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find attachment ", e);
        }
    }


    private static class NoOpConfluencePublisherListener implements ConfluencePublisherListener {

        @Override
        public void pageAdded(ConfluencePage addedPage) {
        }

        @Override
        public void pageUpdated(ConfluencePage existingPage, ConfluencePage updatedPage) {
        }

        @Override
        public void pageDeleted(ConfluencePage deletedPage) {
        }

        @Override
        public void attachmentAdded(String attachmentFileName, String contentId) {
        }

        @Override
        public void attachmentUpdated(String attachmentFileName, String contentId) {
        }

        @Override
        public void attachmentDeleted(String attachmentFileName, String contentId) {
        }

        @Override
        public void publishCompleted() {
        }

    }

}
