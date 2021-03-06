package com.ev.dialer.phonebook.utils;


/**
 * 
 * log工具类区分是否是debug模式
 * 
 */
public class LogUtils {
	public static boolean isDebug = true;

	public static void v(String tag, String msg) {
		if (isDebug) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void v(String tag, String msg, Throwable t) {
		if (isDebug) {
			android.util.Log.v(tag, msg, t);
		}
	}

	public static void d(String tag, String msg) {
		if (isDebug) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void d(String tag, String msg, Throwable t) {
		if (isDebug) {
			android.util.Log.d(tag, msg, t);
		}
	}

	public static void i(String tag, String msg) {
		if (isDebug) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void i(String tag, String msg, Throwable t) {
		if (isDebug) {
			android.util.Log.i(tag, msg, t);
		}
	}

	public static void w(String tag, String msg) {
		if (isDebug) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void w(String tag, String msg, Throwable t) {
		if (isDebug) {
			android.util.Log.w(tag, msg, t);
		}
	}

	public static void e(String tag, String msg) {
		if (isDebug) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable t) {
		if (isDebug) {
			android.util.Log.e(tag, msg, t);
		}
	}
}
