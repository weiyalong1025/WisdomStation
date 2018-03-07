package com.winsion.component.user;

import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.user.login.activity.LoginActivity;

/**
 * Created by 10295 on 2018/3/7.
 * User组件
 */

public class ComponentUser implements IComponent {
    @Override
    public String getName() {
        return "ComponentUser";
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        Context context = cc.getContext();
        switch (actionName) {
            case "checkLoginState":
                boolean loginState = CacheDataSource.getLoginState();
                if (loginState) {
                    CC.sendCCResult(cc.getCallId(), CCResult.success());
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("callId", cc.getCallId());
                    return true;
                }
                break;
            case "toLoginActivity":
                Intent intent = new Intent(context, LoginActivity.class);
                intent.putExtra("callId", cc.getCallId());
                return true;
            case "toUserActivity":
                break;
        }
        return false;
    }
}
