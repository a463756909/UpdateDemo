package com.inwatch.update.util;

import android.content.Context;

import com.inwatch.update.UpdateHelper;

import java.io.File;

public class FileUtils {
    public static File createFile(String versionName) {
        File cacheDir = getCacheDir();
        cacheDir.mkdirs();
        return new File(cacheDir, "update_v_" + versionName);
    }

    private static File getCacheDir() {
        Context context = UpdateHelper.getInstance().getContext();
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        cacheDir = new File(cacheDir, "update");
        return cacheDir;
    }
}
