package com.winsion.dispatch.media.activity;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.winsion.dispatch.R;
import com.winsion.dispatch.base.BaseActivity;
import com.winsion.dispatch.utils.FileUtils;
import com.winsion.dispatch.view.TitleView;

import java.io.File;

import static com.winsion.dispatch.common.constants.Intents.Media.MEDIA_FILE;

/**
 * Created by admin on 2016/8/13.
 * 添加备注
 */
public class AddNoteActivity extends BaseActivity implements TextWatcher {
    private EditText etNoteContent;
    private TitleView tvTitle;
    private TextView tvCounter;

    private File file;

    @Override
    protected int setContentView() {
        return R.layout.activity_add_note;
    }

    @Override
    protected void start() {
        initView();
        initListener();
        initData();
    }

    private void initView() {
        etNoteContent = findViewById(R.id.et_note_content);
        tvTitle = findViewById(R.id.tv_title);
        tvCounter = findViewById(R.id.tv_counter);
    }

    private void initListener() {
        tvTitle.setOnBackClickListener(v -> finish());
        tvTitle.setOnConfirmClickListener(v -> {
            String content = etNoteContent.getText().toString();
            FileUtils.writeFileFromString(file, content, false);
            setResult(Activity.RESULT_OK);
            finish();
        });

        etNoteContent.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String note = s.toString();
        tvCounter.setText(String.format("%s/150", String.valueOf(note.length())));
    }

    private void initData() {
        file = (File) getIntent().getSerializableExtra(MEDIA_FILE);
        if (file.exists()) {
            String content = FileUtils.readFile2String(file, "UTF-8");
            etNoteContent.setText(content);
            // 设置光标位置在最后
            etNoteContent.setSelection(content.length());
        }
    }
}
