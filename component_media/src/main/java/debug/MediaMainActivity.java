package debug;

import android.content.Intent;
import android.view.View;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.media.R;
import com.winsion.component.media.activity.RecordAudioActivity;
import com.winsion.component.media.activity.RecordVideoActivity;
import com.winsion.component.media.activity.TakePhotoActivity;
import com.winsion.component.media.biz.MediaBiz;
import com.winsion.component.media.constants.FileType;

import java.io.File;
import java.io.IOException;

import static com.winsion.component.media.constants.Intents.Media.MEDIA_FILE;

/**
 * Created by 10295 on 2018/3/13.
 * 多媒体录制组件DEBUG版本MainActivity
 */

public class MediaMainActivity extends BaseActivity {
    private File mediaFile;

    @Override
    protected int setContentView() {
        return R.layout.media_activity_main;
    }

    @Override
    protected void start() {
        addOnClickListeners(R.id.btn_take_photo, R.id.btn_video, R.id.btn_record);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Class activity = TakePhotoActivity.class;
        int fileType = FileType.PICTURE;
        if (id == R.id.btn_take_photo) {
            fileType = FileType.PICTURE;
            activity = TakePhotoActivity.class;
        } else if (id == R.id.btn_video) {
            fileType = FileType.VIDEO;
            activity = RecordVideoActivity.class;
        } else if (id == R.id.btn_record) {
            fileType = FileType.AUDIO;
            activity = RecordAudioActivity.class;
        }
        try {
            mediaFile = MediaBiz.getMediaFile(DirAndFileUtils.getIssueDir(), fileType);
            Intent intent = new Intent(mContext, activity);
            intent.putExtra(MEDIA_FILE, mediaFile);
            startActivityForResult(intent);
        } catch (IOException e) {
            showToast(R.string.toast_check_sdcard);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            showToast("文件路径：" + mediaFile.getAbsolutePath());
        }
    }
}
