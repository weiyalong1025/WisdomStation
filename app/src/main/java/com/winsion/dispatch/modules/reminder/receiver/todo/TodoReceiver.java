package com.winsion.dispatch.modules.reminder.receiver.todo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.data.DBDataSource;
import com.winsion.dispatch.modules.reminder.constants.ExtraName;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;
import com.winsion.dispatch.modules.reminder.event.UpdateTodoEvent;
import com.winsion.dispatch.modules.reminder.fragment.todo.TodoListFragment;

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
    private AlertDialog alertDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(ExtraName.NAME_TODO_ID, 0);
        if (id == 0) return;
        todoEntity = DBDataSource.getInstance().getTodoEntityById(id);
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
            mediaPlayer.setDataSource(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
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

    private boolean isFinish = false;

    /**
     * 该方法会发出一个事件，接收者在{@link TodoListFragment}
     */
    @SuppressLint("InflateParams")
    private void showDialog(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_todo_remind, null);
        alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert)
                .setView(inflate)
                .create();

        TextView tvDesc = inflate.findViewById(R.id.tv_desc);
        tvDesc.setText(todoEntity.getContent());
        TextView tvConfirm = inflate.findViewById(R.id.tv_confirm);
        tvConfirm.setOnClickListener(v -> {
            isFinish = true;
            operateState();
        });

        Window window = alertDialog.getWindow();
        assert window != null;
        window.setType(WindowManager.LayoutParams.TYPE_TOAST);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setOnDismissListener(dialog -> {
            if (!isFinish) {
                operateState();
            }
        });
        alertDialog.show();
    }

    private void operateState() {
        todoEntity.setFinished(isFinish);
        DBDataSource.getInstance().updateOrAddTodo(todoEntity);
        EventBus.getDefault().post(new UpdateTodoEvent());
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (vibrator != null) vibrator.cancel();
        alertDialog.dismiss();
    }
}
