package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ConfluencePage;

/**
 * @author Christian Stettler
 */
public interface PublishConfluenceClientListener {

    void pageAdded(ConfluencePage addedPage);

    void pageUpdated(ConfluencePage existingPage, ConfluencePage updatedPage);

    void pageDeleted(ConfluencePage deletedPage);

    void attachmentAdded(String attachmentFileName, String contentId);

    void attachmentUpdated(String attachmentFileName, String contentId);

    void attachmentDeleted(String attachmentFileName, String contentId);

    void publishCompleted();

}
