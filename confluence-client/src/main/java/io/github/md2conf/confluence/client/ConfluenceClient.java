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
import io.github.md2conf.confluence.client.http.InternalApiClient;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.confluence.client.metadata.ConfluenceContentInstance;
import io.github.md2conf.model.ConfluencePage;

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
public class ConfluenceClient {

    static final String CONTENT_HASH_PROPERTY_KEY = "content-hash";
    static final int INITIAL_PAGE_VERSION = 1;

    private final ConfluenceContentInstance metadata;
    private final PublishingStrategy publishingStrategy;
    private final OrphanRemovalStrategy orphanRemovalStrategy;
    private final InternalApiClient internalApiClient;
    private final ConfluenceClientListener confluenceClientListener;
    private final String versionMessage;
    private final boolean notifyWatchers;

    public ConfluenceClient(ConfluenceContentInstance metadata, PublishingStrategy publishingStrategy, OrphanRemovalStrategy orphanRemovalStrategy,
                            InternalApiClient internalApiClient, ConfluenceClientListener confluenceClientListener,
                            String versionMessage, boolean notifyWatchers) {
        this.metadata = metadata;
        this.publishingStrategy = publishingStrategy;
        this.orphanRemovalStrategy = orphanRemovalStrategy;
        this.internalApiClient = internalApiClient;
        this.confluenceClientListener = confluenceClientListener != null ? confluenceClientListener : new NoOpConfluenceClientListener();
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

        this.confluenceClientListener.publishCompleted();
    }

    private static ConfluencePage singleRootPage(ConfluenceContentInstance metadata) {
        List<ConfluencePage> rootPages = metadata.getPages();

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

    private void startPublishingReplacingAncestorId(ConfluencePage rootPage, String spaceKey, String ancestorId) {
        if (rootPage != null) {
            updatePage(ancestorId, null, rootPage);

            addOrUpdateLabels(ancestorId, rootPage.getLabels());

            deleteConfluenceAttachmentsNotPresentUnderPage(ancestorId, rootPage.getAttachments());
            addAttachments(ancestorId, rootPage.getAttachments());

            startPublishingUnderAncestorId(rootPage.getChildren(), spaceKey, ancestorId);
        }
    }

    private void startPublishingUnderAncestorId(List<ConfluencePage> pages, String spaceKey, String ancestorId) {
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

    private void deleteConfluencePagesNotPresentUnderAncestor(List<ConfluencePage> pagesToKeep, String ancestorId) {
        List<io.github.md2conf.confluence.client.http.ConfluencePage> childPagesOnConfluence = this.internalApiClient.getChildPages(ancestorId);

        List<io.github.md2conf.confluence.client.http.ConfluencePage> childPagesOnConfluenceToDelete = childPagesOnConfluence.stream()
                                                                                                                             .filter(childPageOnConfluence -> pagesToKeep.stream().noneMatch(page -> page.getTitle().equals(childPageOnConfluence.getTitle())))
                                                                                                                             .collect(toList());

        childPagesOnConfluenceToDelete.forEach(pageToDelete -> {
            List<io.github.md2conf.confluence.client.http.ConfluencePage> pageScheduledForDeletionChildPagesOnConfluence = this.internalApiClient.getChildPages(pageToDelete.getContentId());
            pageScheduledForDeletionChildPagesOnConfluence.forEach(parentPageToDelete -> this.deleteConfluencePagesNotPresentUnderAncestor(emptyList(), pageToDelete.getContentId()));
            this.internalApiClient.deletePage(pageToDelete.getContentId());
            this.confluenceClientListener.pageDeleted(pageToDelete);
        });
    }

    private void deleteConfluenceAttachmentsNotPresentUnderPage(String contentId, Map<String, String> attachments) {
        List<ConfluenceAttachment> confluenceAttachments = this.internalApiClient.getAttachments(contentId);

        confluenceAttachments.stream()
                .filter(confluenceAttachment -> attachments.keySet().stream().noneMatch(attachmentFileName -> attachmentFileName.equals(confluenceAttachment.getTitle())))
                .forEach(confluenceAttachment -> {
                    this.internalApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(confluenceAttachment.getTitle()));
                    this.internalApiClient.deleteAttachment(confluenceAttachment.getId());
                    this.confluenceClientListener.attachmentDeleted(confluenceAttachment.getTitle(), contentId);
                });
    }

    private String addOrUpdatePageUnderAncestor(String spaceKey, String ancestorId, ConfluencePage page) {
        String contentId;

        try {
            contentId = this.internalApiClient.getPageByTitle(spaceKey, page.getTitle());
            updatePage(contentId, ancestorId, page);
        } catch (NotFoundException e) {
            String content = fileContent(page.getContentFilePath(), UTF_8);
            contentId = this.internalApiClient.addPageUnderAncestor(spaceKey, ancestorId, page.getTitle(), content, this.versionMessage);
            this.internalApiClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, hash(content));
            this.confluenceClientListener.pageAdded(new io.github.md2conf.confluence.client.http.ConfluencePage(contentId, page.getTitle(), content, INITIAL_PAGE_VERSION));
        }

        return contentId;
    }

    private void updatePage(String contentId, String ancestorId, ConfluencePage page) {
        String content = fileContent(page.getContentFilePath(), UTF_8);
        io.github.md2conf.confluence.client.http.ConfluencePage existingPage = this.internalApiClient.getPageWithContentAndVersionById(contentId);
        String existingContentHash = this.internalApiClient.getPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
        String newContentHash = hash(content);

        if (notSameHash(existingContentHash, newContentHash) || !existingPage.getTitle().equals(page.getTitle())) {
            this.internalApiClient.deletePropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
            int newPageVersion = existingPage.getVersion() + 1;
            this.internalApiClient.updatePage(contentId, ancestorId, page.getTitle(), content, newPageVersion, this.versionMessage, this.notifyWatchers);
            this.internalApiClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, newContentHash);
            this.confluenceClientListener.pageUpdated(existingPage, new io.github.md2conf.confluence.client.http.ConfluencePage(contentId, page.getTitle(), content, newPageVersion));
        }
    }

    private void addAttachments(String contentId, Map<String, String> attachments) {
        attachments.forEach((attachmentFileName, attachmentPath) -> addOrUpdateAttachment(contentId, attachmentPath, attachmentFileName));
    }

    private void addOrUpdateAttachment(String contentId, String attachmentPath, String attachmentFileName) {
        Path absoluteAttachmentPath = absoluteAttachmentPath(attachmentPath);
        String newAttachmentHash = hash(fileInputStream(absoluteAttachmentPath));

        try {
            ConfluenceAttachment existingAttachment = this.internalApiClient.getAttachmentByFileName(contentId, attachmentFileName);
            String attachmentId = existingAttachment.getId();
            String existingAttachmentHash = this.internalApiClient.getPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));

            if (notSameHash(existingAttachmentHash, newAttachmentHash)) {
                if (existingAttachmentHash != null) {
                    this.internalApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
                }
                this.internalApiClient.updateAttachmentContent(contentId, attachmentId, fileInputStream(absoluteAttachmentPath), this.notifyWatchers);
                this.internalApiClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
                this.confluenceClientListener.attachmentUpdated(attachmentFileName, contentId);
            }

        } catch (NotFoundException e) {
            this.internalApiClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
            this.internalApiClient.addAttachment(contentId, attachmentFileName, fileInputStream(absoluteAttachmentPath));
            this.internalApiClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
            this.confluenceClientListener.attachmentAdded(attachmentFileName, contentId);
        }
    }

    private String getAttachmentHashKey(String attachmentFileName) {
        return attachmentFileName + "-hash";
    }

    private Path absoluteAttachmentPath(String attachmentPath) {
        return Paths.get(attachmentPath);
    }

    private void addOrUpdateLabels(String contentId, List<String> labels) {
        List<String> existingLabels = this.internalApiClient.getLabels(contentId);

        existingLabels.stream()
                .filter((existingLabel) -> !(labels.contains(existingLabel)))
                .forEach((labelToDelete) -> this.internalApiClient.deleteLabel(contentId, labelToDelete));

        List<String> labelsToAdd = labels.stream()
                .filter((label) -> !(existingLabels.contains(label)))
                .collect(toList());

        if (labelsToAdd.size() > 0) {
            this.internalApiClient.addLabels(contentId, labelsToAdd);
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


    private static class NoOpConfluenceClientListener implements ConfluenceClientListener {

        @Override
        public void pageAdded(io.github.md2conf.confluence.client.http.ConfluencePage addedPage) {
        }

        @Override
        public void pageUpdated(io.github.md2conf.confluence.client.http.ConfluencePage existingPage, io.github.md2conf.confluence.client.http.ConfluencePage updatedPage) {
        }

        @Override
        public void pageDeleted(io.github.md2conf.confluence.client.http.ConfluencePage deletedPage) {
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
