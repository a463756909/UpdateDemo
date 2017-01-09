package com.inwatch.update.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.inwatch.update.UpdateHelper;
import com.inwatch.update.bean.Update;
import com.inwatch.update.download.DownloadManager;
import com.inwatch.update.download.ParamsManager;
import com.inwatch.update.server.DownloadingService;
import com.inwatch.update.util.InstallUtil;
import com.inwatch.update.util.NetworkUtil;
import com.inwatch.update.util.ResourceUtils;
import com.inwatch.update.util.UpdateConstants;
import com.inwatch.update.util.UpdateSP;

import java.io.File;
import java.math.BigDecimal;

public class UpdateDialogActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private View inwatch_update_wifi_indicator;
    private TextView inwatch_update_content;
    private CheckBox inwatch_update_id_check;
    private Button inwatch_update_id_ok;
    private Button inwatch_update_id_cancel;
    private Update mUpdate;
    private int mAction;
    private String mPath;
    private Context mContext;
    private String text;
    //是否已经下载完成
    private boolean finshDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        setContentView(ResourceUtils.getResourceIdByName(mContext, "layout", "inwatch_update_dialog"));
        Intent intent = getIntent();
        mUpdate = (Update) intent.getSerializableExtra(UpdateConstants.DATA_UPDATE);
        mAction = intent.getIntExtra(UpdateConstants.DATA_ACTION, 0);
        mPath = intent.getStringExtra(UpdateConstants.SAVE_PATH);
        String updateContent = null;
        inwatch_update_wifi_indicator = findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_wifi_indicator"));
        inwatch_update_content = (TextView) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_content"));
        inwatch_update_id_check = (CheckBox) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_id_check"));
        inwatch_update_id_ok = (Button) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_id_ok"));
        inwatch_update_id_cancel = (Button) findViewById(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_id_cancel"));
        if (NetworkUtil.isConnectedByWifi()) {
            //WiFi环境
            inwatch_update_wifi_indicator.setVisibility(View.INVISIBLE);
        } else {
            inwatch_update_wifi_indicator.setVisibility(View.VISIBLE);
        }
        finshDown = (DownloadManager.getInstance(mContext).state(mUpdate.getPath()) == ParamsManager.State_FINISH);
        if (finshDown) {
            //已经下载
            if (TextUtils.isEmpty(mPath)) {
                String url = mUpdate.getPath();
                mPath = DownloadManager.getInstance(mContext).getDownPath() + File.separator + url.substring(url.lastIndexOf("/") + 1, url.length());
                File file = new File(mPath);
                if (file.exists()){
                    System.out.println("apk exists " + file.getName());
                    text = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_dialog_installapk")) + "";

                }else {
                    System.out.println("apk not exists");
                    text = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_targetsize")) + " " + getFormatSize(mUpdate.getApkSize());
                    finshDown = false;
                }
            }else {
                text = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_dialog_installapk")) + "";
            }
            //text = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_dialog_installapk")) + "";
        } else {
            text = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_targetsize")) + " " + getFormatSize(mUpdate.getApkSize());
        }
        if (UpdateHelper.getInstance().getUpdateType() == UpdateHelper.UpdateType.checkupdate) {
            //手动更新
            inwatch_update_id_check.setVisibility(View.GONE);
        } else {
            inwatch_update_id_check.setVisibility(View.VISIBLE);
        }
        if (mAction == 0) {
            updateContent = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_newversion"))
                    +  " " + mUpdate.getVersion() + "\n"
                    + text + "\n\n"
                    + getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_updatecontent")) + "\n" + mUpdate.getUpdateContent() +
                    "\n";
            inwatch_update_id_ok.setText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_updatenow"));
            inwatch_update_content.setText(updateContent);
        } else {
            finshDown = true;
            updateContent = getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_newversion"))
                    + " " + mUpdate.getVersion() + "\n"
                    + text + "\n\n"
                    + getText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_updatecontent")) + "\n" + mUpdate.getUpdateContent() +
                    "\n";
            inwatch_update_id_ok.setText(ResourceUtils.getResourceIdByName(mContext, "string", "inwatch_update_installnow"));
            inwatch_update_content.setText(updateContent);
        }
        inwatch_update_id_check.setOnCheckedChangeListener(this);
        inwatch_update_id_ok.setOnClickListener(this);
        inwatch_update_id_cancel.setOnClickListener(this);
    }

    public static String getFormatSize(double size) {
        //double kiloByte = size / 1024.0D;
        double kiloByte = size;
        if (kiloByte < 1.0D) {
            return size + "Byte";
        } else {
            double megaByte = kiloByte / 1024.0D;
            if (megaByte < 1.0D) {
                BigDecimal gigaByte1 = new BigDecimal(Double.toString(kiloByte));
                return gigaByte1.setScale(2, 4).toPlainString() + "K";
            } else {
                double gigaByte = megaByte / 1024.0D;
                if (gigaByte < 1.0D) {
                    BigDecimal teraBytes1 = new BigDecimal(Double.toString(megaByte));
                    return teraBytes1.setScale(2, 4).toPlainString() + "M";
                } else {
                    double teraBytes = gigaByte / 1024.0D;
                    BigDecimal result4;
                    if (teraBytes < 1.0D) {
                        result4 = new BigDecimal(Double.toString(gigaByte));
                        return result4.setScale(2, 4).toPlainString() + "G";
                    } else {
                        result4 = new BigDecimal(teraBytes);
                        return result4.setScale(2, 4).toPlainString() + "T";
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_id_ok")) {
            if (finshDown) {
                InstallUtil.installApk(mContext, mPath);
            } else {
                Intent intent = new Intent(mContext, DownloadingService.class);
                intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.START_DOWN);
                intent.putExtra(UpdateConstants.DATA_UPDATE, mUpdate);
                startService(intent);
            }
            finish();
        } else if (id == ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_id_cancel")) {
            if (UpdateSP.isForced()) {
                finish();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        UpdateSP.setIgnore(isChecked ? mUpdate.getVersion() + "" : "");
    }
}
