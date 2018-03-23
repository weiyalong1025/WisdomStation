package com.winsion.component.scene;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.scene.fragment.SceneRootFragment;

/**
 * Created by 10295 on 2018/3/23.
 * 现场组件功能实现
 */

public class ComponentScene implements IComponent {
    @Override
    public String getName() {
        return "ComponentScene";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "getSceneRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new SceneRootFragment()));
                break;
        }
        return false;
    }
}
