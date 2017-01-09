package com.inwatch.update.download;

import com.inwatch.update.ParseData;
import com.inwatch.update.UpdateHelper;
import com.inwatch.update.bean.Update;
import com.inwatch.update.listener.UpdateListener;
import com.inwatch.update.util.HandlerUtil;
import com.inwatch.update.util.InstallUtil;
import com.inwatch.update.util.UpdateSP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateWorker implements Runnable {

    protected String url;
    protected String appid;
    protected String appkey;
    protected UpdateListener checkCB;
    protected ParseData parser;

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }
    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public void setUpdateListener(UpdateListener checkCB) {
        this.checkCB = checkCB;
    }

    public void setParser(ParseData parser) {
        this.parser = parser;
    }

    @Override
    public void run() {
        try {
            String response = check(url);
            Update parse = parser.parse(response);
            if (parse == null) {
                throw new IllegalArgumentException("parse response to update failed by " + parser.getClass().getCanonicalName());
            }
            int curVersion = InstallUtil.getApkVersion(UpdateHelper.getInstance().getContext());
            if (parse.getVer_code() > curVersion && (!UpdateSP.isIgnore(parse.getVer_code() + "") || UpdateHelper.getInstance().getUpdateType() == UpdateHelper.UpdateType.checkupdate)) {
                sendHasUpdate(parse);
            } else {
                sendNoUpdate();
            }
        } catch (HttpException he) {
            sendOnErrorMsg(he.getCode(), he.getErrorMsg());
        } catch (Exception e) {
            sendOnErrorMsg(-1, e.getMessage());
        }
    }

    protected String check(String urlStr) throws Exception {
        urlStr = urlStr + "?type=android";
        URL getUrl = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) getUrl.openConnection();
        urlConn.setDoInput(true);
        urlConn.setDoOutput(true);
        urlConn.setUseCaches(false);
        urlConn.setConnectTimeout(10000);
        urlConn.setRequestMethod("GET");
        urlConn.setRequestProperty("appid", appid);
        urlConn.setRequestProperty("appkey", appkey);
        urlConn.connect();

        int responseCode = urlConn.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new HttpException(responseCode, urlConn.getResponseMessage());
        }

        BufferedReader bis = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "utf-8"));

        StringBuilder sb = new StringBuilder();
        String lines;
        while ((lines = bis.readLine()) != null) {
            sb.append(lines);
        }

        System.out.println(sb.toString());
        return sb.toString();
    }

    private void sendHasUpdate(final Update update) {
        if (checkCB == null) return;
        HandlerUtil.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                checkCB.hasUpdate(update);
            }
        });
    }

    private void sendNoUpdate() {
        if (checkCB == null) return;
        HandlerUtil.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                checkCB.noUpdate();
            }
        });
    }

    private void sendOnErrorMsg(final int code, final String errorMsg) {
        if (checkCB == null) return;
        HandlerUtil.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                checkCB.onCheckError(code, errorMsg);
            }
        });
    }
}
