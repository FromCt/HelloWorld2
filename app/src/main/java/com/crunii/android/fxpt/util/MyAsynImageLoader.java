/**
 * AsynImageLoader.java
 * 
 * Create Version: 1.0
 * Author: 徐舟骏
 * Create Date: 2012-5-9
 */
package com.crunii.android.fxpt.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import com.crunii.android.base.http.HttpClient;
import com.crunii.android.base.http.HttpResponse;
import com.crunii.android.base.util.NullUtils;

public class MyAsynImageLoader {
	private static final String tag = "AsynImageLoader";

	private static Map<String, SoftReference<Bitmap>> caches;
	private static File cacheDir;
	private static List<Task> taskQueue;
	private static ImageLoaderHandler handler;
	private static Runnable runnable;

	public MyAsynImageLoader(HttpClient client, Context context) {
		if(caches == null) {
			caches = new HashMap<String, SoftReference<Bitmap>>();
		}

		if(cacheDir == null) {
			cacheDir = context.getCacheDir();
		}

		if(taskQueue == null) {
			taskQueue = new ArrayList<Task>();
		}
		
		if(handler == null) {
			handler = new ImageLoaderHandler();
		}
		
		if(runnable == null) {
			runnable = new Runnable() {
				@Override
				public void run() {
					while (true) {
						while (taskQueue.size() > 0) {
							Task task = taskQueue.remove(0);
							
							if (isCached(task.url)) { //队列中之前的任务可能已经下载了相同的url，所以这里必须检查缓存
								Log.i(tag, "already have: " + task.url);
								
								Message msg = handler.obtainMessage();
								msg.obj = task;
								// 发送消息回主线程
								handler.sendMessage(msg);
							} else {
								loadViaHttp(task);
							}
						}

						// 如果队列为空,则令线程等待
						synchronized (runnable) {
							try {
								this.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			};
			
			new Thread(runnable).start();
		}

	}

	public void showImageAsyn(SoftReference<?> imageView, String url, Integer resId) {
		
		if(url.contains("136.3.243.")) { //无效IP
			return;
		}

		this.addTask(imageView, url, resId);

	}
	
	private static boolean isCached(String url) {
		boolean isCached = false;
		if (caches.containsKey(url) && !NullUtils.isNull(caches.get(url).get())) {
			isCached = true;
		}
		return isCached;
	}

	private void addTask(SoftReference<?> imageView, String url, Integer resId) {
		synchronized(taskQueue) {
			taskQueue.add(new Task(url, imageView, resId));
		}
		
		synchronized (runnable) {
			runnable.notify();
		}
	}
	
	private void loadViaHttp(Task task) {
		HttpClient httpClient = new HttpClient();
		InputStream is = null;
		String url = task.url.replace("http://", "").replace("https://", "");
		if (url.indexOf("/") > -1) {
			url = url.substring(url.indexOf("/"));
		}
		
		File file = new File(cacheDir, url);
		if (!file.exists() || file.length() == 0L) {
			file.getParentFile().mkdirs();
			file.delete();
		}

		try {
			synchronized (httpClient) {

				file.createNewFile();
				HttpResponse rp = httpClient.get(task.url);
				is = rp.asStream();

				OutputStream output = new FileOutputStream(file);
				final byte[] buffer = new byte[1024 * 10];
				int read;

				while ((read = is.read(buffer)) != -1) {
					output.write(buffer, 0, read);
				}

				output.flush();
				output.close();

				caches.put(task.url, new SoftReference<Bitmap>(BitmapFactory.decodeFile(file.getPath())));
				Log.i(tag, "download success: " + task.url);

			}
		} catch (Exception e) {
			file.delete();
			e.printStackTrace();
			Log.i(tag, "error occured while loading from: " + task.url);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}

		Message msg = handler.obtainMessage();
		msg.obj = task;
		// 发送消息回主线程
		handler.sendMessage(msg);
	}

	private class Task {
		String url; //图片地址
		SoftReference<?> imageView;
		Integer resId; //默认显示的图片资源id

		private Task(String url, SoftReference<?> imageView, Integer resId) {
			this.url = url;
			this.imageView = imageView;
			this.resId = resId;
		}

	}

	static class ImageLoaderHandler extends Handler {
		public void handleMessage(Message msg) {
			// 子线程中返回的下载完成的任务
			Task task = (Task) msg.obj;

			if (NullUtils.isNotNull(task.imageView.get())) {
				if (isCached(task.url)) {
					//下载成功
					((ImageView) task.imageView.get()).setImageBitmap(caches.get(task.url).get());
				} else {
					//下载不成功
					//TODO 这里应该显示一个X，而不是Loading图片
					((ImageView) task.imageView.get()).setImageResource(task.resId);
				}
			}
		}
	}

}
