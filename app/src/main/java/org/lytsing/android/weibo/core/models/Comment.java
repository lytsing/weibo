/*
 * Copyright (C) 2012 lytsing.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lytsing.android.weibo.core.models;

public class Comment {
    private long id;
    private String author;
    private String createdAt;
    private String text;
    private String source;
    private boolean isTruncated;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setCreationTime(String time) {
        this.createdAt = time;
    }

    public String getCreationTime() {
        return this.createdAt;
    }

    public void setBody(String body) {
        this.text = body;
    }

    public String getBody() {
        return this.text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return this.source;
    }

    public void setTruncated(boolean truncated) {
        this.isTruncated = truncated;
    }

    public boolean hasTruncated() {
        return this.isTruncated;
    }
}
