package com.inwatch.update.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.inwatch.update.UpdateHelper;

/**
 */
public class UpdateSP {

    public static final String KEY_DOWN_SIZE = "update_download_size";

    public static boolean isIgnore(String version) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getString("_update_version_ignore", "").equals(version);
    }

    public static boolean isForced() {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        return sp.getBoolean("_update_version_forced", false);
    }

    public static void setIgnore(String version) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("_update_version_ignore", version);
        editor.commit();
    }

    public static void setForced(boolean def) {
        SharedPreferences sp = UpdateHelper.getInstance().getContext().getSharedPreferences(KEY_DOWN_SIZE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("_update_version_forced", def);
        editor.commit();
    }
}
