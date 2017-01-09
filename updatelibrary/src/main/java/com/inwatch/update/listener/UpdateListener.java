package com.inwatch.update.listener;

import com.inwatch.update.bean.Update;

/**
 * The update check callback
 */
public abstract class UpdateListener {

    /**
     * There are a new version of APK on network
     */
    public void hasUpdate(Update update) {

    }

    /**
     * There are no new version for update
     */
    public abstract void noUpdate();

    /**
     * http check error,
     *
     * @param code     http code
     * @param errorMsg http error msg
     */
    public abstract void onCheckError(int code, String errorMsg);

    /**
     * to be invoked by user press cancel button.
     */
    public void onUserCancel() {

    }
}
