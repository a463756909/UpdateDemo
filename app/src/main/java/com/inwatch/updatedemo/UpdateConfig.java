package com.inwatch.updatedemo;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.inwatch.update.ParseData;
import com.inwatch.update.UpdateHelper;
import com.inwatch.update.bean.Update;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 更新配置
 */
public class UpdateConfig {

    private static String checkUrl = "http://shouhu.api.inwatch.cc/rest.php/api/package/get-version";
    private static String appId = "ac1595c151471254504";
    private static String appKey = "43cdf3eaeb699ca18999da8b9546205a";

    public static void init(Context context) {
        UpdateHelper.init(context);
        UpdateHelper.getInstance()
                .setCheckUrl(checkUrl)
                .setAppId(appId).setAppKey(appKey)
                .setCheckJsonParser(new ParseData() {
                    @Override
                    public Update parse(String response) {
                        Update update = new Update();
                        Gson gson = new Gson();
                        Type type = new TypeToken<Update>() {}.getType();
                        try {
                            JSONObject updateObject = new JSONObject(response);
                            String status = updateObject.optString("status");
                            if (status.equals("true")) {
                                update = gson.fromJson(updateObject.optJSONObject("data").toString(), type);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return update;
                    }
                });
    }
}
