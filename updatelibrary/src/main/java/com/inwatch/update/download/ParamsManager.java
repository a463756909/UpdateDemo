package com.inwatch.update.download;

/**
 * 常量管理
 */
public class ParamsManager {
	
	/** 一级打印tag */
	public final static String tag = "inwatch_down";
    /** OK */
    public final static int ERROR_NONE = 0;
    /** SD卡容量不足 */
    public final static int ERROR_SD_NO_MEMORY = 1;
    /** 网络异常 */
    public final static int ERROR_BLOCK_INTERNET = 2;
    /** 文件完整性异常 */
    public final static int ERROR_SIZE = 3;
    /** 未知错误 */
    public final static int ERROR_UNKONW = 4;
    /** 刷新时间间隔 */
    public final static long TIMEEXTRA=0;
    /** 一般状态 */
    public final static int State_NORMAL = 0;
    /** 下载中状态 */
    public final static int State_DOWNLOAD = 1;
    /** 暂停状态 */
    public final static int State_PAUSE = 2;
    /** 完成状态 */
    public final static int State_FINISH = 3;
    /** 等待状态 */
    public final static int State_WAIT = 4;

}
