/**
 * The MIT License
 * Copyright © 2017, 2019 WebFolder OÜ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.webfolder.cormorant.api.model;

public class Account {

    private String name;

    private Long timestamp;

    private Long totalBytesUsed;

    private Long totalObjectCount;

    private Integer totalContainerCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTotalBytesUsed() {
        return totalBytesUsed;
    }

    public void setTotalBytesUsed(Long totalBytesUsed) {
        this.totalBytesUsed = totalBytesUsed;
    }

    public Long getTotalObjectCount() {
        return totalObjectCount;
    }

    public void setTotalObjectCount(Long totalObjectCount) {
        this.totalObjectCount = totalObjectCount;
    }

    public Integer getTotalContainerCount() {
        return totalContainerCount;
    }

    public void setTotalContainerCount(Integer totalContainerCount) {
        this.totalContainerCount = totalContainerCount;
    }
}
