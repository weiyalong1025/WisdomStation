package com.winsion.component.contact.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({ContactType.TYPE_CONTACTS, ContactType.TYPE_TEAM, ContactType.TYPE_CONTACTS_GROUP})
@Retention(RetentionPolicy.SOURCE)
public @interface ContactType {
    /**
     * 联系人
     */
    int TYPE_CONTACTS = 0;
    /**
     * 班组
     */
    int TYPE_TEAM = 1;
    /**
     * 联系人组
     */
    int TYPE_CONTACTS_GROUP = 2;
}
