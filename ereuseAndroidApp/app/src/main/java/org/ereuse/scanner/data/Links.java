package org.ereuse.scanner.data;

import java.io.Serializable;

/**
 * Created by Jamgo SCCL.
 */
public class Links implements Serializable {
    private Link next;
    private Link self;
    private Link parent;
    private Link last;

    public Link getNext() {
        return next;
    }

    public void setNext(Link next) {
        this.next = next;
    }

    public Link getSelf() {
        return self;
    }

    public void setSelf(Link self) {
        this.self = self;
    }

    public Link getParent() {
        return parent;
    }

    public void setParent(Link parent) {
        this.parent = parent;
    }

    public Link getLast() {
        return last;
    }

    public void setLast(Link last) {
        this.last = last;
    }

}
