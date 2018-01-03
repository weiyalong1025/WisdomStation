package com.winsion.wisdomstation.reminder.modules.todo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.data.DBDataSource;
import com.winsion.wisdomstation.reminder.constants.ExtraName;
import com.winsion.wisdomstation.reminder.entity.TodoEntity;
import com.winsion.wisdomstation.reminder.event.UpdateTodoEvent;
import com.winsion.wisdomstation.reminder.modules.todo.fragment.TodoFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;


/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 4:03
 */

public class TodoReceiver extends BroadcastReceiver {
    private TodoEntity todoEntity;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(ExtraName.NAME_TODO_ID, 0);
        if (id == 0) return;
        todoEntity = DBDataSource.getInstance().getTodoById(id);
        // 响铃
        playRing(context);
        // 震动
        startVibrator(context);
        // 弹出对话框
        showDialog(context);
    }

    private void playRing(Context context) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(context, RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_ALARM));
            mediaPlayer.setOnCompletionListener(MediaPlayer::release);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startVibrator(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        assert vibrator != null;
        vibrator.vibrate(new long[]{400, 1000}, 0);
    }

    /**
     * 该方法会发出一个事件，接收者在{@link TodoFragment}
     *
     * @param context
     */
    private void showDialog(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_todo_remind, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert);
        AlertDialog alertDialog = builder.setView(inflate).create();

        TextView tvDesc = inflate.findViewById(R.id.tv_desc);
        tvDesc.setText(todoEntity.getContent());
        TextView tvConfirm = inflate.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(v -> {
            todoEntity.setFinished(true);
            DBDataSource.getInstance().updateOrAddTodo(todoEntity);
            EventBus.getDefault().post(new UpdateTodoEvent());
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            if (vibrator != null) vibrator.cancel();
            alertDialog.dismiss();
        });

        Window window = alertDialog.getWindow();
        assert window != null;
        window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
