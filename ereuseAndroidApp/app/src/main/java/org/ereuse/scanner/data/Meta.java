package org.ereuse.scanner.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jamgo SCCL.
 */
public class Meta {
    private int page;
    @SerializedName("max_results")
    private int maxResults;
    private int total;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
