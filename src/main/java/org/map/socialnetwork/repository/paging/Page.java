package org.map.socialnetwork.repository.paging;

import java.util.HashSet;
import java.util.Set;

public class Page<E> {

    Set<E> content = new HashSet<>();
    int pageNumber;
    int pageSize;

    public Page(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Set<E> getContent() {
        return content;
    }

    public void setContent(Set<E> content) {
        this.content = content;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
