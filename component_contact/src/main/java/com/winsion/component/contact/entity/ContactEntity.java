package com.winsion.component.contact.entity;

import java.io.Serializable;

public abstract class ContactEntity implements Serializable {
    public abstract int getConType();

    public abstract String getConName();

    public abstract String getConId();

    public abstract String getConPhotoUrl();

    public abstract String getConMmpId();

    public abstract String getConLoginState();
}
