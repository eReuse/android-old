package org.ereuse.scanner.data;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Link implements Serializable {
    private String title;
    private String href;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

}
