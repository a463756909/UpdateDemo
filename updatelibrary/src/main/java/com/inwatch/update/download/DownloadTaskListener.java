package com.inwatch.update.download;

/**
 * 下载状态监听
 */
public interface DownloadTaskListener {
	
	/** 更新进度 */
    void updateProcess(DownloadTask mgr);

    /** 完成下载 */
    void finishDownload(DownloadTask mgr);

    /** 准备下载 */
    void preDownload(DownloadTask mgr);
    
    /** 下载错误 */
    void errorDownload(DownloadTask mgr, int error);

    /** 暂停 */
    void cancelDownload(DownloadTask mgr);

}
