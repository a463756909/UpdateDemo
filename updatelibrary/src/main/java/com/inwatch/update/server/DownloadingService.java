
package com.inwatch.update.server;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.inwatch.update.bean.Update;
import com.inwatch.update.download.DownloadManager;
import com.inwatch.update.download.ParamsManager;
import com.inwatch.update.util.UpdateConstants;

import java.io.File;

/**
 * 描 述：原理
 * 首先是点击更新时，弹出进度对话框（进度，取消和运行在后台），
 * 如果是在前台完成下载，弹出安装对话框，
 * 如果是在后台完成下载，通知栏提示下载完成，
 * 如果进入后台后，还在继续下载点击时重新回到原界面
 * 如果强制更新无进入后台功能
 * 如果是静默更新，安静的安装
 */
public class DownloadingService extends Service {

    private NotificationManager notificationManager;
    private Update update;
    private NotificationCompat.Builder mBuilder;
    private String url;
    private Context mContext;
    private DownloadManager manage;
    private int lastPercent;
    private int maxPercent = 100;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String strUrl = (String) msg.obj;
            if (url != null && url.equals(strUrl)) {
                switch (msg.what) {
                    case ParamsManager.State_NORMAL:
                        break;

                    case ParamsManager.State_DOWNLOAD:
                        Bundle bundle = msg.getData();
                        long current = bundle.getLong("percent");
                        long speed = bundle.getLong("loadSpeed");
                        //if (total <= 0) {
                        //    return;
                        //}
                        Log.d("percent:", "percent:" + current + " speed:" + speed);
                        notifyNotification(current);
                        break;

                    case ParamsManager.State_FINISH:
                        File fil = new File(manage.getDownPath(), url.substring(url.lastIndexOf("/") + 1, url.length()));
                        //showInstallNotificationUI(fil);
                        //if (UpdateHelper.getInstance().getUpdateType() == UpdateHelper.UpdateType.autowifidown) {
                        installApk(mContext, fil);
                        notificationManager.cancel(UpdateConstants.NOTIFICATION_ACTION);
                        //} else {
                            //Intent intent = new Intent(mContext, UpdateDialogActivity.class);
                            //intent.putExtra(UpdateConstants.DATA_UPDATE, update);
                            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            //intent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.UPDATE_INSTALL);
                            //intent.putExtra(UpdateConstants.SAVE_PATH, fil.getAbsolutePath());
                            //startActivity(intent);
                        //}
                        break;

                    case ParamsManager.State_PAUSE:
                        break;

                    case ParamsManager.State_WAIT:
                        createNotification();
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        manage = DownloadManager.getInstance(mContext);
        manage.setHandler(handler);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            int action = intent.getIntExtra(UpdateConstants.DATA_ACTION, 0);
            if (action == UpdateConstants.START_DOWN) {
                update = (Update) intent.getSerializableExtra(UpdateConstants.DATA_UPDATE);
                url = update.getPath();
                if (update != null && !TextUtils.isEmpty(url)) {
                    manage.startDownload(url);
                }
            }else if (action == UpdateConstants.PAUSE_DOWN) {
                if (manage.isDownloading(url)) {
                    manage.pauseDownload(url);
                } else {
                    manage.startDownload(url);
                }
            } else if (action == UpdateConstants.CANCEL_DOWN) {
                manage.deleteDownload(url);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void createNotification() {
//        notification = new Notification(
//                mContext.getApplicationInfo().icon,
//                "正在下载:" + mContext.getApplicationInfo().name,
//                System.currentTimeMillis());
//        notification.flags = Notification.FLAG_ONGOING_EVENT;
//        /*** 自定义  Notification 的显示****/
//        contentView = new RemoteViews(getPackageName(), ResourceUtils.getResourceIdByName(mContext, "layout", "inwatch_common_download_notification"));
//        contentView.setTextViewText(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_title"), getApplicationInfo().name);
//        contentView.setProgressBar(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_progress_bar"), 100, 0, false);
//        contentView.setTextViewText(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_progress_text"), "0%");
//
//        /**暂停和开始*/
//        Intent pauseIntent = new Intent(this, DownloadingService.class);
//        pauseIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.PAUSE_DOWN);
//        pauseIntent.putExtra("update", update);
//        PendingIntent pendingIntent1 = PendingIntent.getService(this, 0, pauseIntent, 0);
//        contentView.setOnClickPendingIntent(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_rich_notification_continue"), pendingIntent1);
//
//        /**取消*/
//        Intent cancelIntent = new Intent(this, DownloadingService.class);
//        cancelIntent.putExtra(UpdateConstants.DATA_ACTION, UpdateConstants.CANCEL_DOWN);
//        cancelIntent.putExtra("update", update);
//        PendingIntent pendingIntent2 = PendingIntent.getService(this, 0, cancelIntent, 0);
//        contentView.setOnClickPendingIntent(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_rich_notification_cancel"), pendingIntent2);
//
//        notification.contentView = contentView;
        //notification.flags = Notification.FLAG_AUTO_CANCEL;

        Intent intent = new Intent(this, this.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(getApplicationInfo().icon)
                .setLargeIcon(drawableToBitamp(getApplicationInfo().loadIcon(mContext.getPackageManager())))
                //.setLargeIcon(drawableToBitamp(DownloadingService.this.getApplicationInfo().loadIcon(mContext.getPackageManager())))
                .setContentTitle("正在下载:" + getApplicationInfo().loadLabel(mContext.getPackageManager()))
                .setContentText("0%")
                .setProgress(maxPercent, 0, false)
                .setContentIntent(pendingIntent);

//        notification = new Notification.Builder(mContext)
//                .setSmallIcon(mContext.getApplicationInfo().icon)
//                .setContentTitle("正在下载")
//                .setProgress(100 , 0 , false)
//                .setContentIntent(pendingIntent)
//                .build();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, mBuilder.build());
    }

    private void notifyNotification(long percent) {
        //contentView.setTextViewText(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_progress_text"), (percent * 100 / length) + "%");
        //contentView.setProgressBar(ResourceUtils.getResourceIdByName(mContext, "id", "inwatch_update_progress_bar"), (int) length, (int) percent, false);
        //notification.contentView = contentView;

        //notification.contentView.
        int currentPercent = (int) percent;
        if (lastPercent != currentPercent) {//防止频繁更新notification 卡死
            lastPercent = currentPercent;
            mBuilder.setProgress(maxPercent, currentPercent, false).setContentText(currentPercent + "%");
            notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION, mBuilder.build());
        }
    }

    private void showInstallNotificationUI(File file) {
        if (mBuilder == null) {
            mBuilder = new NotificationCompat.Builder(this);
        }
        mBuilder.setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(getApplicationInfo().name)
                .setContentText("下载完成，点击安装").setTicker("任务下载完成");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
                Uri.fromFile(file),
                "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);
        if (notificationManager == null) {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.notify(UpdateConstants.NOTIFICATION_ACTION,
                mBuilder.build());
    }

    /**
     * 安装apk
     *
     * @param context 上下文
     * @param file    APK文件
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }
}