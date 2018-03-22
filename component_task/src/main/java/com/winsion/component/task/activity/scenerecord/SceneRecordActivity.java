package com.winsion.component.task.activity.scenerecord;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.winsion.component.basic.base.BaseActivity;
import com.winsion.component.basic.data.CacheDataSource;
import com.winsion.component.basic.listener.MyDownloadListener;
import com.winsion.component.basic.utils.DirAndFileUtils;
import com.winsion.component.basic.view.TitleView;
import com.winsion.component.media.adapter.RecordAdapter;
import com.winsion.component.media.constants.FileStatus;
import com.winsion.component.media.entity.LocalRecordEntity;
import com.winsion.component.media.entity.ServerRecordEntity;
import com.winsion.component.task.R;
import com.winsion.component.task.biz.TaskBiz;
import com.winsion.component.task.constants.Intents;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10295 on 2018/3/22.
 * 现场记录-任务监控三级界面-执行人上传的附件列表
 */

public class SceneRecordActivity extends BaseActivity implements SceneRecordContact.View {
    private FrameLayout flContainer;
    private SwipeRefreshLayout swipeRefresh;
    private ListView lvList;
    private ProgressBar pbLoading;
    private TextView tvHint;

    private SceneRecordContact.Presenter mPresenter;
    private List<LocalRecordEntity> recordEntities = new ArrayList<>();
    private RecordAdapter recordAdapter;
    private String jobOperatorsId;

    @Override
    protected int setContentView() {
        return R.layout.task_activity_scene_record;
    }

    @Override
    protected void start() {
        initView();
        initPresenter();
        initAdapter();
        initData();
    }

    private void initView() {
        flContainer = findViewById(R.id.fl_container);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        lvList = findViewById(R.id.lv_list);
        pbLoading = findViewById(R.id.progress_bar);
        tvHint = findViewById(R.id.tv_hint);

        addOnClickListeners(R.id.tv_hint);
        ((TitleView) findViewById(R.id.tv_title)).setOnBackClickListener(v -> finish());
        swipeRefresh.setColorSchemeResources(R.color.basic_blue1);
        swipeRefresh.setOnRefreshListener(() -> mPresenter.getPerformerUploadedFile(jobOperatorsId));
    }

    private void initPresenter() {
        mPresenter = new SceneRecordPresenter(this);
    }

    private void initAdapter() {
        recordAdapter = new RecordAdapter(mContext, recordEntities);
        lvList.setAdapter(recordAdapter);
    }

    private void initData() {
        jobOperatorsId = getIntent().getStringExtra(Intents.SceneRecord.JOB_OPERATORS_ID);
        // 获取作业执行人本地保存的和已经上传到服务器的附件记录
        ArrayList<LocalRecordEntity> localFile = ((TaskBiz) mPresenter).getPerformerLocalFile(jobOperatorsId);
        recordEntities.addAll(localFile);
        recordAdapter.notifyDataSetChanged();
        mPresenter.getPerformerUploadedFile(jobOperatorsId);
    }

    @Override
    public void onPerformerUploadFileGetSuccess(List<ServerRecordEntity> serverRecordFileList) {
        if (serverRecordFileList.size() == 0) {
            tvHint.setText(R.string.hint_no_data_click_retry);
            showView(flContainer, tvHint);
        } else {
            for (ServerRecordEntity entity : serverRecordFileList) {
                String[] split = entity.getFilepath().split("/");
                String fileName = split[split.length - 1];
                int position = checkFileExist(fileName, recordEntities);
                if (position == -1) {
                    // 本地没有
                    LocalRecordEntity localRecordEntity = new LocalRecordEntity();
                    localRecordEntity.setFileType(Integer.valueOf(entity.getType()));
                    localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                    localRecordEntity.setServerUri(entity.getFilepath());
                    localRecordEntity.setFileName(entity.getFilepath().split("/")[split.length - 1]);
                    recordEntities.add(localRecordEntity);
                } else {
                    // 本地存在
                    LocalRecordEntity localRecordEntity = recordEntities.get(position);
                    localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                }
            }
            swipeRefresh.setRefreshing(false);
            recordAdapter.notifyDataSetChanged();
            showView(flContainer, swipeRefresh);

            // 自动下载没有下载成功的文件
            for (LocalRecordEntity performerRecordEntity : recordEntities) {
                if (performerRecordEntity.getFileStatus() == FileStatus.NO_DOWNLOAD) {
                    try {
                        String userId = CacheDataSource.getUserId();
                        File performerDir = DirAndFileUtils.getPerformerDir(userId, jobOperatorsId);
                        ((TaskBiz) mPresenter).downloadFile(performerRecordEntity.getServerUri(),
                                performerDir.getAbsolutePath(), myDownloadListener);
                    } catch (IOException e) {
                        showToast(R.string.toast_check_sdcard);
                    }
                }
            }
        }
    }

    @Override
    public void onPerformerUploadFileGetFailed() {
        tvHint.setText(R.string.hint_load_failed_click_retry);
        showView(flContainer, tvHint);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        showView(flContainer, pbLoading);
        mPresenter.getPerformerUploadedFile(jobOperatorsId);
    }

    private MyDownloadListener myDownloadListener = new MyDownloadListener() {
        @Override
        public void downloadProgress(String serverUri, int progress) {
            for (LocalRecordEntity localRecordEntity : recordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.DOWNLOADING);
                    localRecordEntity.setProgress(progress);
                    recordAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void downloadSuccess(File file, String serverUri) {
            for (LocalRecordEntity localRecordEntity : recordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.SYNCHRONIZED);
                    localRecordEntity.setFile(file);
                    recordAdapter.notifyDataSetChanged();
                    showToast(R.string.toast_download_success);
                    break;
                }
            }
        }

        @Override
        public void downloadFailed(String serverUri) {
            for (LocalRecordEntity localRecordEntity : recordEntities) {
                if (TextUtils.equals(localRecordEntity.getServerUri(), serverUri)) {
                    localRecordEntity.setFileStatus(FileStatus.NO_DOWNLOAD);
                    recordAdapter.notifyDataSetChanged();
                    showToast(R.string.toast_download_failed);
                    break;
                }
            }
        }
    };

    /**
     * 检查文件是否存在
     *
     * @param fileName 文件名
     * @return 不存在返回-1，存在返回该文件在集合中的position
     */
    private int checkFileExist(String fileName, List<LocalRecordEntity> recordEntities) {
        int position = -1;
        for (int i = 0; i < recordEntities.size(); i++) {
            if (recordEntities.get(i).getFileName().equals(fileName)) {
                position = i;
                break;
            }
        }
        return position;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.exit();
    }
}
