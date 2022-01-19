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

import io.github.md2conf.confluence.client.http.ApiInternalClient;
import io.github.md2conf.confluence.client.http.ConfluenceApiPage;
import io.github.md2conf.confluence.client.http.ConfluenceAttachment;
import io.github.md2conf.confluence.client.http.NotFoundException;
import io.github.md2conf.model.ConfluenceContentModel;
import io.github.md2conf.model.ConfluencePage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static io.github.md2conf.confluence.client.OrphanRemovalStrategy.REMOVE_ORPHANS;
import static io.github.md2conf.confluence.client.utils.AssertUtils.assertMandatoryParameter;
import static io.github.md2conf.confluence.client.utils.InputStreamUtils.fileContent;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 * @author qwazer
 */
public class PublishConfluenceClient {

    static final String CONTENT_HASH_PROPERTY_KEY = "content-hash";
    static final int INITIAL_PAGE_VERSION = 1;
    private final PublishingStrategy publishingStrategy;
    private final OrphanRemovalStrategy orphanRemovalStrategy;
    private final ApiInternalClient apiInternalClient;
    private final PublishConfluenceClientListener publishConfluenceClientListener;
    private final String versionMessage;
    private final boolean notifyWatchers;

    public PublishConfluenceClient( PublishingStrategy publishingStrategy, OrphanRemovalStrategy orphanRemovalStrategy,
                                   ApiInternalClient apiInternalClient, PublishConfluenceClientListener publishConfluenceClientListener,
                                   String versionMessage, boolean notifyWatchers) {
        this.publishingStrategy = publishingStrategy;
        this.orphanRemovalStrategy = orphanRemovalStrategy;
        this.apiInternalClient = apiInternalClient;
        this.publishConfluenceClientListener = publishConfluenceClientListener != null ? publishConfluenceClientListener : new NoOpPublishConfluenceClientListener();
        this.versionMessage = versionMessage;
        this.notifyWatchers = notifyWatchers;
    }

    public void publish(ConfluenceContentModel model, String spaceKey, String parentTitle) {
        assertMandatoryParameter(model != null, "model");
        assertMandatoryParameter(isNotBlank(spaceKey), "spaceKey");
        assertMandatoryParameter(isNotBlank(parentTitle), "parentTitle");
        String ancestorId = findPageIdByTitle(spaceKey, parentTitle);
        switch (this.publishingStrategy) {
            case APPEND_TO_ANCESTOR:
                startPublishingUnderAncestorId(model.getPages(), spaceKey, ancestorId);
                break;
            case REPLACE_ANCESTOR:
                startPublishingReplacingAncestorId(singleRootPage(model), spaceKey, ancestorId);
                break;
            default:
                throw new IllegalArgumentException("Invalid publishing strategy '" + this.publishingStrategy + "'");
        }
        this.publishConfluenceClientListener.publishCompleted();
    }

    private String findPageIdByTitle(String spaceKey, String parentTitle) {
        String ancestorId;
        try {
            ancestorId = apiInternalClient.getPageByTitle(spaceKey, parentTitle);
        } catch (NotFoundException e) {
            throw new IllegalArgumentException(String.format("Cannot find pageId by title. There is no page with title %s in %s space found",
                    parentTitle, spaceKey));
        }
        return ancestorId;
    }

    @Deprecated
    public void publish() {
        this.publishConfluenceClientListener.publishCompleted();
    }

    private static ConfluencePage singleRootPage(ConfluenceContentModel metadata) {
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
        List<ConfluenceApiPage> childPagesOnConfluence = this.apiInternalClient.getChildPages(ancestorId);

        List<ConfluenceApiPage> childPagesOnConfluenceToDelete = childPagesOnConfluence.stream()
                                                                                       .filter(childPageOnConfluence -> pagesToKeep.stream().noneMatch(page -> page.getTitle().equals(childPageOnConfluence.getTitle())))
                                                                                       .collect(toList());

        childPagesOnConfluenceToDelete.forEach(pageToDelete -> {
            List<ConfluenceApiPage> pageScheduledForDeletionChildPagesOnConfluence = this.apiInternalClient.getChildPages(pageToDelete.getContentId());
            pageScheduledForDeletionChildPagesOnConfluence.forEach(parentPageToDelete -> this.deleteConfluencePagesNotPresentUnderAncestor(emptyList(), pageToDelete.getContentId()));
            this.apiInternalClient.deletePage(pageToDelete.getContentId());
            this.publishConfluenceClientListener.pageDeleted(pageToDelete);
        });
    }

    private void deleteConfluenceAttachmentsNotPresentUnderPage(String contentId, Map<String, String> attachments) {
        List<ConfluenceAttachment> confluenceAttachments = this.apiInternalClient.getAttachments(contentId);

        confluenceAttachments.stream()
                .filter(confluenceAttachment -> attachments.keySet().stream().noneMatch(attachmentFileName -> attachmentFileName.equals(confluenceAttachment.getTitle())))
                .forEach(confluenceAttachment -> {
                    this.apiInternalClient.deletePropertyByKey(contentId, getAttachmentHashKey(confluenceAttachment.getTitle()));
                    this.apiInternalClient.deleteAttachment(confluenceAttachment.getId());
                    this.publishConfluenceClientListener.attachmentDeleted(confluenceAttachment.getTitle(), contentId);
                });
    }

    private String addOrUpdatePageUnderAncestor(String spaceKey, String ancestorId, ConfluencePage page) {
        String contentId;

        try {
            contentId = this.apiInternalClient.getPageByTitle(spaceKey, page.getTitle());
            updatePage(contentId, ancestorId, page);
        } catch (NotFoundException e) {
            String content = fileContent(page.getContentFilePath(), UTF_8);
            contentId = this.apiInternalClient.addPageUnderAncestor(spaceKey, ancestorId, page.getTitle(), content, page.getType(), this.versionMessage );
            this.apiInternalClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, hash(content));
            this.publishConfluenceClientListener.pageAdded(new ConfluenceApiPage(contentId, page.getTitle(), INITIAL_PAGE_VERSION));
        }

        return contentId;
    }

    private void updatePage(String contentId, String ancestorId, ConfluencePage page) {
        String content = fileContent(page.getContentFilePath(), UTF_8);
        ConfluenceApiPage existingPage = this.apiInternalClient.getPageWithContentAndVersionById(contentId);
        String existingContentHash = this.apiInternalClient.getPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
        String newContentHash = hash(content);

        if (notSameHash(existingContentHash, newContentHash) || !existingPage.getTitle().equals(page.getTitle())) {
            this.apiInternalClient.deletePropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY);
            int newPageVersion = existingPage.getVersion() + 1;
            this.apiInternalClient.updatePage(contentId, ancestorId, page.getTitle(), content, page.getType(), newPageVersion, this.versionMessage, this.notifyWatchers);
            this.apiInternalClient.setPropertyByKey(contentId, CONTENT_HASH_PROPERTY_KEY, newContentHash);
            this.publishConfluenceClientListener.pageUpdated(existingPage, new ConfluenceApiPage(contentId, page.getTitle(), newPageVersion));
        }
    }

    private void addAttachments(String contentId, Map<String, String> attachments) {
        attachments.forEach((attachmentFileName, attachmentPath) -> addOrUpdateAttachment(contentId, attachmentPath, attachmentFileName));
    }

    private void addOrUpdateAttachment(String contentId, String attachmentPath, String attachmentFileName) {
        Path absoluteAttachmentPath = absoluteAttachmentPath(attachmentPath);
        String newAttachmentHash = hash(fileInputStream(absoluteAttachmentPath));

        try {
            ConfluenceAttachment existingAttachment = this.apiInternalClient.getAttachmentByFileName(contentId, attachmentFileName);
            String attachmentId = existingAttachment.getId();
            String existingAttachmentHash = this.apiInternalClient.getPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));

            if (notSameHash(existingAttachmentHash, newAttachmentHash)) {
                if (existingAttachmentHash != null) {
                    this.apiInternalClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
                }
                this.apiInternalClient.updateAttachmentContent(contentId, attachmentId, fileInputStream(absoluteAttachmentPath), this.notifyWatchers);
                this.apiInternalClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
                this.publishConfluenceClientListener.attachmentUpdated(attachmentFileName, contentId);
            }

        } catch (NotFoundException e) {
            this.apiInternalClient.deletePropertyByKey(contentId, getAttachmentHashKey(attachmentFileName));
            this.apiInternalClient.addAttachment(contentId, attachmentFileName, fileInputStream(absoluteAttachmentPath));
            this.apiInternalClient.setPropertyByKey(contentId, getAttachmentHashKey(attachmentFileName), newAttachmentHash);
            this.publishConfluenceClientListener.attachmentAdded(attachmentFileName, contentId);
        }
    }

    private String getAttachmentHashKey(String attachmentFileName) {
        return attachmentFileName + "-hash";
    }

    private Path absoluteAttachmentPath(String attachmentPath) {
        return Paths.get(attachmentPath);
    }

    private void addOrUpdateLabels(String contentId, List<String> labels) {
        List<String> existingLabels = this.apiInternalClient.getLabels(contentId);

        existingLabels.stream()
                .filter((existingLabel) -> !(labels.contains(existingLabel)))
                .forEach((labelToDelete) -> this.apiInternalClient.deleteLabel(contentId, labelToDelete));

        List<String> labelsToAdd = labels.stream()
                .filter((label) -> !(existingLabels.contains(label)))
                .collect(toList());

        if (labelsToAdd.size() > 0) {
            this.apiInternalClient.addLabels(contentId, labelsToAdd);
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


    private static class NoOpPublishConfluenceClientListener implements PublishConfluenceClientListener {

        @Override
        public void pageAdded(ConfluenceApiPage addedPage) {
        }

        @Override
        public void pageUpdated(ConfluenceApiPage existingPage, ConfluenceApiPage updatedPage) {
        }

        @Override
        public void pageDeleted(ConfluenceApiPage deletedPage) {
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
