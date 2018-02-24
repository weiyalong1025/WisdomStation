package com.winsion.dispatch.modules.reminder.receiver.todo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Vibrator;
import android.view.Window;
import android.view.WindowManager;

import com.winsion.dispatch.R;
import com.winsion.dispatch.data.DBDataSource;
import com.winsion.dispatch.modules.reminder.entity.TodoEntity;
import com.winsion.dispatch.modules.reminder.fragment.todo.TodoListFragment;
import com.winsion.dispatch.view.CustomDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;
import static com.winsion.dispatch.modules.reminder.constants.Intents.Todo.TODO_ID;


/**
 * 作者：10295
 * 邮箱：10295010@qq.com
 * 创建时间：2017/12/27 4:03
 */

public class TodoReceiver extends BroadcastReceiver {
    private TodoEntity todoEntity;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private CustomDialog customDialog;

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(TODO_ID, 0);
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
        // 当前为标准模式才响铃
        AudioManager audioService = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        if (audioService != null && audioService.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
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
    }

    private void startVibrator(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(new long[]{400, 1000}, 0);
        }
    }

    private boolean isFinish = false;

    /**
     * 该方法会发出一个事件，接收者在{@link TodoListFragment}
     */
    @SuppressLint("InflateParams")
    private void showDialog(Context context) {
        customDialog = new CustomDialog.NormalBuilder(context)
                .setTitle(R.string.tab_reminder)
                .setMessage(todoEntity.getContent())
                .setPositiveButton((dialog, which) -> {
                    isFinish = true;
                    operateState();
                })
                .create();

        Window window = customDialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST);
            customDialog.setOnDismissListener(dialog -> {
                if (!isFinish) {
                    operateState();
                }
            });
            customDialog.show();
        }
    }

    private void operateState() {
        todoEntity.setFinished(isFinish);
        DBDataSource.getInstance().updateOrAddTodo(todoEntity);
        EventBus.getDefault().post(new TodoEntity());
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (vibrator != null) vibrator.cancel();
        customDialog.dismiss();
    }
}
