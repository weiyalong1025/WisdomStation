package com.winsion.dispatch;

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
                context.startActivity(intent);
                return true;
        }
        return false;
    }
}
