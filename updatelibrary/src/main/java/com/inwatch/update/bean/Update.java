package com.inwatch.update.bean;

import java.io.Serializable;

/**
 * Created by cbw on 2016/6/22.
 */
public class Update implements Serializable {

    private String version;
    private int ver_code;
    private int is_current;
    private String path;
    private long size;
    private String description;

    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public int getVer_code(){
        return ver_code;
    }

    public void setVer_code(int ver_code){
        this.ver_code = ver_code;
    }

    public int getIs_current(){
        return is_current;
    }

    public void setIs_current(int is_current){
        this.is_current = is_current;
    }

    public String getPath(){
        return path;
    }

    public void setPath(String path){
        this.path = path;
    }

    public String getUpdateContent() {
        return description;
    }

    public void setUpdateContent(String description) {
        this.description = description;
    }

    public long getApkSize() {
        return size;
    }

    public void setApkSize(long apkSize) {
        this.size = apkSize;
    }
}
