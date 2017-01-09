package com.inwatch.update;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.inwatch.update.download.DownloadManager;
import com.inwatch.update.download.DownloadModel;
import com.inwatch.update.download.SqliteManager;
import com.inwatch.update.listener.OnlineCheckListener;
import com.inwatch.update.listener.UpdateListener;

import java.util.List;

public class UpdateHelper {

    private SqliteManager manager;
    private Context mContext;
    private String checkUrl;
    private String appId;
    private String appKey;
    private String onlineUrl;
    private ParseData parserCheckJson;
    private ParseData parserOnlineJson;

    private static UpdateHelper instance;
    private UpdateListener mUpdateListener;
    private OnlineCheckListener mOnlineCheckListener;

    //双重嵌套一级是否强制更新
    private boolean updateForce = false;

    //二级（1.手动更新2.自动更新（有网更新，只有WiFi更新，只有WiFi下载））
    public enum UpdateType {
        checkupdate,
        autoupdate,
        autowifiupdate,
        autowifidown
    }

    //默认需要检测更新
    private UpdateType mUpdateType = UpdateType.autoupdate;

    public static UpdateHelper getInstance() {
        if (instance == null) {
            throw new RuntimeException("UpdateHelper not initialized!");
        } else {
            return instance;
        }
    }

    public static void init(Context appContext) {
        instance = new UpdateHelper(appContext);
    }

    public UpdateHelper(Context context) {
        this.mContext = context;
        //添加下载状态列表
        manager = SqliteManager.getInstance(mContext);
        List<DownloadModel> models = manager.getAllDownloadInfo();
        DownloadManager.getInstance(mContext).addStateMap(models);

    }

    public UpdateHelper setCheckUrl(String url) {
        this.checkUrl = url;
        return this;
    }

    public UpdateHelper setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public UpdateHelper setAppKey(String appKey) {
        this.appKey = appKey;
        return this;
    }

    public UpdateHelper setOnlineUrl(String url) {
        this.onlineUrl = url;
        return this;
    }

    public UpdateHelper setUpdateListener(UpdateListener listener) {
        this.mUpdateListener = listener;
        return this;
    }

    public UpdateHelper setOnlineCheckListener(OnlineCheckListener listener) {
        this.mOnlineCheckListener = listener;
        return this;
    }

    public UpdateHelper setCheckJsonParser(ParseData jsonParser) {
        this.parserCheckJson = jsonParser;
        return this;
    }

    public UpdateHelper setOnlineJsonParser(ParseData jsonParser) {
        this.parserOnlineJson = jsonParser;
        return this;
    }

    public UpdateHelper setUpdateType(UpdateType updateType) {
        this.mUpdateType = updateType;
        return this;
    }

    public Context getContext() {
        if (mContext == null) {
            throw new RuntimeException("should call UpdateConfig.init first");
        }
        return mContext;
    }

    public UpdateType getUpdateType() {
        return mUpdateType;
    }

    public String getCheckUrl() {
        if (TextUtils.isEmpty(checkUrl)) {
            throw new IllegalArgumentException("checkUrl is null");
        }
        return checkUrl;
    }

    public String getAppId() {
        if (TextUtils.isEmpty(appId)) {
            throw new IllegalArgumentException("appId is null");
        }
        return appId;
    }

    public String getAppKey() {
        if (TextUtils.isEmpty(appKey)) {
            throw new IllegalArgumentException("appKey is null");
        }
        return appKey;
    }

    public String getOnlineUrl() {
        return onlineUrl;
    }

    public ParseData getCheckJsonParser() {
        if (parserCheckJson == null) {
            throw new IllegalStateException("update parser is null");
        }
        return parserCheckJson;
    }

    public ParseData getOnlineJsonParser() {
        return parserOnlineJson;
    }


    public void check(Activity activity) {
        UpdateAgent.getInstance().checkUpdate(activity);
    }

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public OnlineCheckListener getOnlineCheckListener() {
        return mOnlineCheckListener;
    }
}
