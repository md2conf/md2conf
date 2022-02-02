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

package io.github.md2conf.confluence.client.http;

import io.github.md2conf.model.ConfluenceContentModel;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @author Alain Sahli
 */
public interface ApiInternalClient {

    String addPageUnderAncestor(String spaceKey, String ancestorId, String title, String content, ConfluenceContentModel.Type type, String versionMessage);

    void updatePage(String contentId, String ancestorId, String title, String content, ConfluenceContentModel.Type type, int newVersion, String versionMessage, boolean notifyWatchers);

    void deletePage(String contentId);

    String getPageByTitle(String spaceKey, String title) throws NotFoundException, MultipleResultsException;

    void saveUrlToFile(String downloadUrl, File outputFile);

    void addAttachment(String contentId, String attachmentFileName, InputStream attachmentContent);

    void updateAttachmentContent(String contentId, String attachmentId, InputStream attachmentContent, boolean notifyWatchers);

    void deleteAttachment(String attachmentId);

    ConfluenceAttachment getAttachmentByFileName(String contentId, String attachmentFileName) throws NotFoundException, MultipleResultsException;

    ConfluenceApiPage getPageWithViewContentAndVersionById(String contentId);

    List<ConfluenceApiPage> getChildPages(String contentId);

    List<ConfluenceAttachment> getAttachments(String contentId);

    void setPropertyByKey(String contentId, String key, String value);

    String getPropertyByKey(String contentId, String key);

    void deletePropertyByKey(String contentId, String key);

    List<String> getLabels(String contentId);

    void addLabels(String contentId, List<String> labels);

    void deleteLabel(String contentId, String label);

}
