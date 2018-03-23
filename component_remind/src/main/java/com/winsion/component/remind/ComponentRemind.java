package com.winsion.component.remind;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.remind.fragment.RemindRootFragment;

/**
 * Created by 10295 on 2018/3/22.
 * 提醒组件功能实现
 */

public class ComponentRemind implements IComponent {
    @Override
    public String getName() {
        return "ComponentRemind";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getRemindRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new RemindRootFragment()));
                break;
        }
        return false;
    }
}
