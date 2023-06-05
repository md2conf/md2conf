package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ConfluenceApiPage;

/**
 * @author Christian Stettler
 */
public interface PublishConfluenceClientListener {

    void pageAdded(ConfluenceApiPage addedPage);

    void pageUpdated(ConfluenceApiPage existingPage, ConfluenceApiPage updatedPage);

    void pageNotModified(ConfluenceApiPage existingPage);

    void pageDeleted(ConfluenceApiPage deletedPage);

    void attachmentAdded(String attachmentFileName, String contentId);

    void attachmentUpdated(String attachmentFileName, String contentId);

    void attachmentNotModified(String attachmentFileName, String contentId);

    void attachmentDeleted(String attachmentFileName, String contentId);

    void publishCompleted();

}
