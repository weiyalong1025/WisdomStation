package com.winsion.wisdomstation.media.activity;

import android.app.Activity;
import android.widget.EditText;

import com.winsion.wisdomstation.R;
import com.winsion.wisdomstation.base.BaseActivity;
import com.winsion.wisdomstation.utils.FileUtils;
import com.winsion.wisdomstation.view.TitleView;

import java.io.File;

import butterknife.BindView;

/**
 * Created by admin on 2016/8/13.
 * 添加备注
 */
public class AddNoteActivity extends BaseActivity {
    @BindView(R.id.et_note_content)
    EditText etNoteContent;
    @BindView(R.id.tv_title)
    TitleView tvTitle;

    public static final String FILE = "file";
    private File file;

    @Override
    protected int setContentView() {
        return R.layout.activity_add_note;
    }

    @Override
    protected void start() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> {
            String content = etNoteContent.getText().toString();
            FileUtils.writeFileFromString(file, content, false);
            setResult(Activity.RESULT_OK);
            finish();
        });
        file = (File) getIntent().getSerializableExtra(FILE);
        if (file.exists()) {
            String content = FileUtils.readFile2String(file, "UTF-8");
            etNoteContent.setText(content);
            // 设置光标位置在最后
            etNoteContent.setSelection(content.length());
        }
    }
}
