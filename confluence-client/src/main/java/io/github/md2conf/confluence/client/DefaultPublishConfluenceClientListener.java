package io.github.md2conf.confluence.client;

import io.github.md2conf.confluence.client.http.ConfluenceApiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPublishConfluenceClientListener implements PublishConfluenceClientListener {

    private final static Logger logger = LoggerFactory.getLogger(DefaultPublishConfluenceClientListener.class);

    private int pageAddedCnt;
    private int pageUpdatedCnt;
    private int pageUpdateSkippedCnt;
    private int pageNotModifiedCnt;
    private int pageDeletedCnt;
    private int attachmentAddedCnt;
    private int attachmentUpdatedCnt;
    private int attachmentNotModifiedCnt;
    private int attachmentDeletedCnt;


    @Override
    public synchronized void pageAdded(ConfluenceApiPage addedPage) {
        pageAddedCnt++;
        logger.info("Added page '" + addedPage.getTitle() + "' (id " + addedPage.getContentId() + ")");
    }

    @Override
    public synchronized void pageUpdated(ConfluenceApiPage existingPage, ConfluenceApiPage updatedPage) {
        pageUpdatedCnt++;
        logger.info("Updated page '" + updatedPage.getTitle() + "' (id " + updatedPage.getContentId() + ", version " + existingPage.getVersion() + " -> " + updatedPage.getVersion() + ")");
    }

    @Override
    public synchronized void pageNotModified(ConfluenceApiPage existingPage) {
        pageNotModifiedCnt++;
        logger.info("Not modified page '" + existingPage.getTitle() + "' (id " + existingPage.getContentId() + ")");
    }

    @Override
    public synchronized void pageDeleted(ConfluenceApiPage deletedPage) {
        pageDeletedCnt++;
        logger.info("Deleted page '" + deletedPage.getTitle() + "' (id " + deletedPage.getContentId() + ")");
    }

    @Override
    public synchronized void attachmentAdded(String attachmentFileName, String contentId) {
        attachmentAddedCnt++;
        logger.info("Added attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public synchronized void attachmentUpdated(String attachmentFileName, String contentId) {
        attachmentUpdatedCnt++;
        logger.info("Updated attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public synchronized void attachmentNotModified(String attachmentFileName, String contentId) {
        attachmentNotModifiedCnt++;
        logger.info("Not modified attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public synchronized void attachmentDeleted(String attachmentFileName, String contentId) {
        attachmentDeletedCnt++;
        logger.info("Deleted attachment '" + attachmentFileName + "' (page id " + contentId + ")");
    }

    @Override
    public void publishCompleted() {
        logger.info("Publishing completed. Summary");
        logger.info(getPagesStats());
        logger.info(getAttachmentStats());
    }

    @Override
    public void pageSkippedUpdate(ConfluenceApiPage page) {
        pageUpdateSkippedCnt++;
        logger.info("Skipped page update '" + page.getTitle() + "' (id " + page.getContentId() + ")");
    }

    private String getPagesStats() {
        int pageCnt = pageAddedCnt + pageDeletedCnt + pageUpdatedCnt + pageNotModifiedCnt;
        return String.format("Total pages count is %d (%d added , %d updated , %d deleted, %d not modified, %s update skipped).",
                pageCnt, pageAddedCnt, pageUpdatedCnt, pageDeletedCnt, pageNotModifiedCnt, pageUpdateSkippedCnt);
    }

    private String getAttachmentStats() {
        int attachmentCnt = attachmentAddedCnt + attachmentDeletedCnt + attachmentUpdatedCnt + attachmentNotModifiedCnt;
        return String.format("Total attachments count is %d (%d added , %d updated , %d deleted, %d not modified).",
                attachmentCnt, attachmentAddedCnt, attachmentUpdatedCnt, attachmentDeletedCnt, attachmentNotModifiedCnt);
    }

}
