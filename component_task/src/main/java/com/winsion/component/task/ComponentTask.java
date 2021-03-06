package com.winsion.component.task;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.billy.cc.core.component.CC;
import com.billy.cc.core.component.CCResult;
import com.billy.cc.core.component.IComponent;
import com.winsion.component.task.activity.issue.IssueActivity;
import com.winsion.component.task.activity.patrolplan.PatrolPlanActivity;
import com.winsion.component.task.fragment.OperationRootFragment;

import static com.winsion.component.task.constants.Intents.Issue.ISSUE_TYPE;

/**
 * Created by 10295 on 2018/3/22.
 * 任务组件功能实现
 */

public class ComponentTask implements IComponent {
    @Override
    public String getName() {
        return "ComponentTask";
    }

    @Override
    public boolean onCall(CC cc) {
        Context context = cc.getContext();
        Intent intent;
        switch (cc.getActionName()) {
            case "getOperationRootFragment":
                CC.sendCCResult(cc.getCallId(), CCResult.success("fragment", new OperationRootFragment()));
                break;
            case "toPatrolPlanActivity":
                intent = new Intent(context, PatrolPlanActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
            case "toIssueActivity":
                intent = new Intent(context, IssueActivity.class);
                intent.putExtra("callId", cc.getCallId());
                if (!(context instanceof Activity)) {
                    //调用方没有设置context或app间组件跳转，context为application
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                intent.putExtra(ISSUE_TYPE, (int) cc.getParamItem("ISSUE_TYPE"));
                intent.putExtra("toTeamsName", (String) cc.getParamItem("toTeamsName"));
                intent.putExtra("toTeamsId", (String) cc.getParamItem("toTeamsId"));

                context.startActivity(intent);
                CC.sendCCResult(cc.getCallId(), CCResult.success());
                break;
        }
        return false;
    }
}
