package com.winsion.component.contact;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.contact.fragment.ContactRootFragment;
import com.winsion.component.contact.fragment.messagelist.MessageListFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 联系人组件功能实现
 */

public class ComponentContact implements IComponent {
    @Override
    public String getName() {
        return "ComponentContact";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getContactRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new ContactRootFragment()));
                break;
            case "getMessageListFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new MessageListFragment()));
                break;
        }
        return false;
    }
}
