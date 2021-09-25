/*
 * Copyright 2016-2017 the original author or authors.
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

package io.github.md2conf.confluence.client.metadata;

import io.github.md2conf.confluence.client.support.RuntimeUse;
import io.github.md2conf.model.ConfluenceContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Confluence content resolved against a particular Confluence installation.
 * The same as ConfluenceContent, but with spaceKey and ancestorId.
 *
 * @author Alain Sahli
 * @author qwazer resheto@gmail.com
 */
public class ConfluenceContentInstance {

    private String spaceKey;
    private String ancestorId;
    private List<ConfluenceContent.ConfluencePage> pages = new ArrayList<>();

    public String getSpaceKey() {
        return this.spaceKey;
    }

    @RuntimeUse
    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getAncestorId() {
        return this.ancestorId;
    }

    @RuntimeUse
    public void setAncestorId(String ancestorId) {
        this.ancestorId = ancestorId;
    }

    public List<ConfluenceContent.ConfluencePage> getPages() {
        return this.pages;
    }

    @RuntimeUse
    public void setPages(List<ConfluenceContent.ConfluencePage> pages) {
        this.pages = pages;
    }

}
