package com.inwatch.update.download;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

/**
 * 下载管理
 */
public class DownloadManager {

	/** 上下文 */
	static Context context = null;
	/** 文件下载路径 */
	static String downPath = "";
	/** 处理器 */
	Handler handler = null;
	/** 下载管理器对象 */
	static DownloadManager manager = null;
	/** 下载队列map */
	static HashMap<String, DownloadTask> taskmap = null;
	/** 状态map */
	static HashMap<String, Integer> statemap = null;
	/** 上一次刷新页面的时间戳 */
	long lastRefresh = 0;

	private DownloadManager() {

	}

	public synchronized static DownloadManager getInstance(Context context_) {
		if (manager == null) {
			manager = new DownloadManager();
			taskmap = new HashMap<String, DownloadTask>();
			statemap = new HashMap<String, Integer>();
			context = context_;
			String packageName = context.getPackageName();
			downPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator + "Download" + File.separator +".inwatch" + File.separator
					+ packageName;
		}
		return manager;
	}

	/** 下载监听 */
	DownloadTaskListener listener = new DownloadTaskListener() {
		@Override
		public void updateProcess(DownloadTask mgr) {
			statemap.put(mgr.getDownloadUrl(), ParamsManager.State_DOWNLOAD);
			if (handler != null) {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastRefresh > ParamsManager.TIMEEXTRA) {
					lastRefresh = currentTime;
					Message m = new Message();
					m.what = ParamsManager.State_DOWNLOAD;
					m.obj = mgr.getDownloadUrl();
					Bundle bundle = new Bundle();
					bundle.putLong("percent", mgr.getDownloadPercent());
					bundle.putLong("loadSpeed", mgr.getDownloadSpeed());
					m.setData(bundle);
					handler.sendMessage(m);
				}
			}
		}

		@Override
		public void finishDownload(DownloadTask mgr) {
			SqliteManager.getInstance(context).updateDownloadData(
					mgr.getDownloadUrl(), ParamsManager.State_FINISH, "0");
			statemap.put(mgr.getDownloadUrl(), ParamsManager.State_FINISH);
			if (taskmap.containsKey(mgr.getDownloadUrl())) {
				taskmap.remove(mgr.getDownloadUrl());
			}
			if (handler != null) {
				Message m = new Message();
				m.what = ParamsManager.State_FINISH;
				m.obj = mgr.getDownloadUrl();
				handler.sendMessage(m);
			}
		}

		@Override
		public void preDownload(DownloadTask mgr) {
			statemap.put(mgr.getDownloadUrl(), ParamsManager.State_WAIT);
			if (handler != null) {
				Message m = new Message();
				m.what = ParamsManager.State_WAIT;
				m.obj = mgr.getDownloadUrl();
				handler.sendMessage(m);
			}
		}

		@Override
		public void errorDownload(DownloadTask mgr, int error) {
			SqliteManager.getInstance(context).updateDownloadData(
					mgr.getDownloadUrl(), ParamsManager.State_PAUSE, "0");
			statemap.put(mgr.getDownloadUrl(), ParamsManager.State_NORMAL);
			if (taskmap.containsKey(mgr.getDownloadUrl())) {
				taskmap.remove(mgr.getDownloadUrl());
			}
			if (handler != null) {
				Message m = new Message();
				m.what = ParamsManager.State_NORMAL;
				m.obj = mgr.getDownloadUrl();
				handler.sendMessage(m);
			}
		}

		@Override
		public void cancelDownload(DownloadTask mgr) {
			SqliteManager.getInstance(context).updateDownloadData(
					mgr.getDownloadUrl(), ParamsManager.State_PAUSE, "0");
			statemap.put(mgr.getDownloadUrl(), ParamsManager.State_PAUSE);
			if (taskmap.containsKey(mgr.getDownloadUrl())) {
				taskmap.remove(mgr.getDownloadUrl());
			}
			if (handler != null) {
				Message m = new Message();
				m.what = ParamsManager.State_PAUSE;
				m.obj = mgr.getDownloadUrl();
				handler.sendMessage(m);
			}
		}
	};

	/** 设置处理器 */
	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	/** 设置下载路径 */
	public void setDownPath(String path) {
		downPath = path;
	}

	public String getDownPath(){
		return downPath;
	}

	/**
	 * 开启下载
	 * 
	 * @param url
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void startDownload(String url) {
		try {
			if (taskmap.containsKey(url)) {
				return;
			}
			DownloadTask task = new DownloadTask(context, url, downPath,
					listener);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			taskmap.put(url, task);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Android AsyncTask两种线程池分析和总结 在android AsyncTask里面有两种线程池供我们调用 1．
	 * THREAD_POOL_EXECUTOR, 异步线程池
	 * AsyncTask.THREAD_POOL_EXECUTOR线程池中，它的核心线程是5，队列容量是128，最大线程数是9。 2．
	 * SERIAL_EXECUTOR，同步线程池 正如上面名称描述的那样，一个是异步线程池，多个任务在线程池中并发执行；还有一个是同步执行的。
	 * 默认的话，直接调用execute的话，是使用SERIAL_EXECUTOR
	 * */

	/**
	 * 这是ThreadPoolExecutor的构造函数，首先需要明白的是这几个参数的含义 A． corePoolSize： 线程池维护线程的最少数量
	 * B． maximumPoolSize：线程池维护线程的最大数量 C． keepAliveTime： 线程池维护线程所允许的空闲时间 D．
	 * unit： 线程池维护线程所允许的空闲时间的单位 E． workQueue： 线程池所使用的缓冲队列 F． handler：
	 * 线程池对拒绝任务的处理策略
	 * */

	/**
	 * 暂停下载
	 * 
	 * @param url
	 */
	public void pauseDownload(String url) {
		if (taskmap.containsKey(url)) {
			taskmap.remove(url).cancel();
		}
	}

	/**
	 * 删除指定下载
	 * 
	 * @param url
	 */
	public void deleteDownload(String url) {
		SqliteManager.getInstance(context).deleteByUrl(url);
		if (taskmap.containsKey(url)) {
			taskmap.remove(url).cancel();
		}
		statemap.remove(url);
	}

	/**
	 * 删除所有下载
	 */
	public void deleteAllDownload() {
		Iterator it = taskmap.keySet().iterator();
		while (it.hasNext()) {
			DownloadTask dt = (DownloadTask) it.next();
			dt.cancel();
			it.remove();
		}
		statemap.clear();
	}

	/**
	 * 判断是否在下载队列中
	 * 
	 * @param url
	 * @return
	 */
	public boolean isDownloading(String url) {
		return statemap.containsKey(url)
				&& statemap.get(url) == ParamsManager.State_DOWNLOAD;
	}

	public int state(String url) {
		if (!statemap.containsKey(url)) {
			return ParamsManager.State_NORMAL;
		} else {
			return statemap.get(url);
		}
	}

	/**
	 * 添加下载状态列表
	 * 
	 * @param models
	 */
	public void addStateMap(List<DownloadModel> models) {
		for (int i = 0; i < models.size(); i++) {
			DownloadModel model = models.get(i);
			if (model.getDOWNLOAD_STATE() == ParamsManager.State_FINISH) {
				statemap.put(model.getDOWNLOAD_NAME(),
						ParamsManager.State_FINISH);
			} else {
				statemap.put(model.getDOWNLOAD_NAME(),
						ParamsManager.State_PAUSE);
				SqliteManager.getInstance(context).updateDownloadData(
						model.getDOWNLOAD_NAME(), ParamsManager.State_PAUSE,
						"0");
			}
		}
	}

}
