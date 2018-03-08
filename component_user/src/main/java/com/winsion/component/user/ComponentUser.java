package com.winsion.component.user;

import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.user.login.activity.LoginActivity;
import com.winsion.component.user.user.activity.UserActivity;

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
        Intent intent;
        switch (actionName) {
            case "toLoginActivity":
                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("callId", cc.getCallId());
                context.startActivity(intent);
                return true;
            case "toLoginActivityClearTask":
                intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("callId", cc.getCallId());
                context.startActivity(intent);
                return true;
            case "toUserActivity":
                intent = new Intent(context, UserActivity.class);
                intent.putExtra("callId", cc.getCallId());
                context.startActivity(intent);
                return true;
        }
        return false;
    }
}
