/**
 * Copyright 2011-2012. Chongqing CRun Information Industry Co.,Ltd. 
 * All rights reserved. <a>http://www.crunii.com</a>
 */
package com.crunii.android.fxpt.util;

/**
 * @author Administrator
 * @date 2012-8-29
 */
public interface FileProgressListener {
	/**
	 * 获得当前下载数据大小
	 * @param size  数据大小
	 */
	public void onDownloadSize(long size);
	/**
	 * 获得当前上传数据大小
	 * @param size
	 */
	public void onUpSize(long size);
}
