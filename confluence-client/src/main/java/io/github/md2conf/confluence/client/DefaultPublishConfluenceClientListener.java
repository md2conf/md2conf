package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ConfluenceApiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPublishConfluenceClientListener implements PublishConfluenceClientListener {

    private final static Logger logger = LoggerFactory.getLogger(DefaultPublishConfluenceClientListener.class);


    @Override
    public void pageAdded(ConfluenceApiPage addedPage) {
        logger.info("Added page '" + addedPage.getTitle() + "' (id " + addedPage.getContentId() + ")");
    }

    @Override
    public void pageUpdated(ConfluenceApiPage existingPage, ConfluenceApiPage updatedPage) {
        logger.info("Updated page '" + updatedPage.getTitle() + "' (id " + updatedPage.getContentId() + ", version " + existingPage.getVersion() + " -> " + updatedPage.getVersion() + ")");
    }

    @Override
    public void pageDeleted(ConfluenceApiPage deletedPage) {
        logger.info("Deleted page '" + deletedPage.getTitle() + "' (id " + deletedPage.getContentId() + ")");
    }

    @Override
    public void attachmentAdded(String attachmentFileName, String contentId) {
        logger.info("Added attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public void attachmentUpdated(String attachmentFileName, String contentId) {
        logger.info("Updated attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public void attachmentDeleted(String attachmentFileName, String contentId) {
        logger.info("Deleted attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public void publishCompleted() {
    }

}
