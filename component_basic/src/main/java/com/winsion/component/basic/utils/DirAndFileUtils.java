package com.winsion.component.basic.utils;

import android.os.Environment;

import com.winsion.component.basic.utils.FileUtils;
import com.winsion.component.basic.utils.constants.DirName;

import java.io.File;
import java.io.IOException;

/**
 * Created by 10295 on 2017/12/17 0017.
 * 获取目录/文件工具类
 */

public class DirAndFileUtils {
    private static StringBuilder stringBuilder = new StringBuilder();

    private static String getRootDir() throws IOException {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DirName.ROOT;
        }
        throw new IOException("Please check SDCard state!");
    }

    /**
     * 获取版本更新包存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getUpdateDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.UPDATE)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取发布命令/协作附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getLogDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.LOG)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取任务执行人附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getPerformerDir(String userId, String jobOperatorId) throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.RECORD)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(jobOperatorId)
                .append(File.separator)
                .append(DirName.PERFORMER)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new IOException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取任务监控人附件存储目录(主要指接收的命令/协作/网格附带的附件)
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getMonitorDir(String userId, String jobsId) throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.RECORD)
                .append(File.separator)
                .append(userId)
                .append(File.separator)
                .append(jobsId)
                .append(File.separator)
                .append(DirName.MONITOR)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new IOException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取发布命令/协作/网格附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getIssueDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.ISSUE)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }

    /**
     * 获取失误招领附件存储目录
     *
     * @return 对应的目录
     * @throws IOException 没有挂载SD卡或创建文件失败抛出异常
     */
    public static File getLostDir() throws IOException {
        stringBuilder.setLength(0);
        String filePath = stringBuilder
                .append(getRootDir())
                .append(File.separator)
                .append(DirName.LOST)
                .toString();
        File file = new File(filePath);
        boolean orExistsDir = FileUtils.createOrExistsDir(file);
        if (!orExistsDir) {
            throw new NullPointerException("Create folder failed!");
        }
        return file;
    }
}
