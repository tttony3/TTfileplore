package com.changhong.ttfileplore.implement;

import java.util.HashMap;
import com.changhong.ttfileplore.data.DownData;

public interface DownStatusInterface {

	/**
	 * 返回下载刘表
	 * 
	 * @return 下载列表 HashMap<String,DownData>
	 */
	 HashMap<String, DownData> getAllDownStatus();

	/**
	 * 据对应的uri返回DownData,或者返回null
	 * 
	 * @param uri
	 *            查询的uri
	 * @return DownData
	 */
	 DownData getDownStatus(String uri);

	/**
	 * 停止下载
	 * 
	 * @param uri
	 *            下载任务uri
	 */
	 void stopDownload(String uri);

	/**
	 * 停止所有下载任务
	 */
	public void stopAllDownload();

	public boolean cancelDownload(String uri);
}
