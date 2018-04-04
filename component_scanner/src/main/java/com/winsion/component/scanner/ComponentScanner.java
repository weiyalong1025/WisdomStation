package com.winsion.component.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.scanner.activity.CaptureActivity;

public class ComponentScanner implements IComponent {
    @Override
    public String getName() {
        return "ComponentScanner";
    }

    @Override
    public boolean onCall(CC cc) {
        switch (cc.getActionName()) {
            case "toCaptureActivity":
                Context context = cc.getContext();
                Intent intent = new Intent(context, CaptureActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
        }
        return false;
    }
}
