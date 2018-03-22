package com.winsion.dispatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponent;
import com.winsion.dispatch.activity.main.MainActivity;

/**
 * Created by 10295 on 2018/3/9.
 * App组件
 */

public class ComponentApp implements IComponent {
    @Override
    public String getName() {
        return "ComponentApp";
    }

    @Override
    public boolean onCall(CC cc) {
        String actionName = cc.getActionName();
        Context context = cc.getContext();
        switch (actionName) {
            case "toMainActivity":
                Intent intent = new Intent(context, MainActivity.class);
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
                return true;
        }
        return false;
    }
}
