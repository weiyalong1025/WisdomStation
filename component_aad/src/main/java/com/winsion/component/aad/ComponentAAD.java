package com.winsion.component.aad;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.aad.fragment.AADRootFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 到发组件功能实现
 */

public class ComponentAAD implements IComponent {
    @Override
    public String getName() {
        return "ComponentAAD";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getAADRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new AADRootFragment()));
                break;
        }
        return false;
    }
}
