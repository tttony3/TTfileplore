package com.changhong.ttfileplore.application;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.changhong.alljoyn.simpleclient.ClientBusHandler;
import com.changhong.alljoyn.simpleclient.DeviceInfo;
import com.changhong.ttfileplore.R;
import com.changhong.ttfileplore.activities.ShowPushFileActivity;
import com.changhong.ttfileplore.fragment.ReciveDialogFragment;
import com.changhong.ttfileplore.utils.CrashHandler;
import com.changhong.ttfileplore.utils.Utils;
import com.chobit.corestorage.CoreApp;
import com.chobit.corestorage.CoreHttpServerCB;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MyApp extends CoreApp {	
	static public Context context;
	static public  Context mainContext;
	String ip;
	int port;
	public DeviceInfo devinfo;
	DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.file_icon_photo)
			.showImageForEmptyUri(R.drawable.file_icon_photo).showImageOnFail(R.drawable.file_icon_photo)
			.cacheInMemory(true).cacheOnDisk(false).bitmapConfig(Bitmap.Config.RGB_565)
			//.displayer(new RoundedBitmapDisplayer(20)) // 设置图片的解码类型
			.displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
			.build();
	static public ArrayList<List<String>> recivePushList = new ArrayList<>();
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Context getMainContext() {
		return mainContext;
	}

	public void setMainContext(Context mainContext) {
		MyApp.mainContext = mainContext;
	}

	ArrayList<File> fileList = new ArrayList<>();

	/**
	 * 获取复制剪贴的文件列表
	 * 
	 * @return ArrayList
	 */
	public ArrayList<File> getFileList() {
		return fileList;
	}

	public Context getContext() {
		return context;
	}


	public void setContext(Context context) {
		MyApp.context = context;
	}

	public void setFileList(ArrayList<File> fileList) {
		this.fileList = fileList;
	}

	/**
	 * 清空复制剪贴的文件列表
	 */
	public void clearFileList() {
		fileList.clear();
		;
	}

	@Override
	public void onLowMemory() {

		super.onLowMemory();
	}

	@Override
	public void onTerminate() {

		super.onTerminate();
	}

	public void onCreate() {
		super.onCreate();
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);
		File folder = new File(Utils.getPath(this, "cache"));
		if (!folder.exists())
			folder.mkdir();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration  
			    .Builder(this)  
		//	    .memoryCacheExtraOptions(200, 200) // max width, max height，即保存的每个缓存文件的最大长宽
		//	    .diskCacheExtraOptions(200, 200, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
				.threadPoolSize(2)//线程池内加载的数量
			    .threadPriority(Thread.NORM_PRIORITY - 2)
		//	    .denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
			    .memoryCacheSize(2 * 1024 * 1024)    
			    .diskCacheSize(50 * 1024 * 1024)    
			    .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  
			    .tasksProcessingOrder(QueueProcessingType.LIFO)  
			    .diskCacheFileCount(300) //缓存的文件数量  
		//	    .diskCache(new UnlimitedDiskCache(folder))//自定义缓存路径
			    .defaultDisplayImageOptions(options)  
			    .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间  
			    .writeDebugLogs() // Remove for release app  
				.build();//开始构建
		ImageLoader.getInstance().init(config);
	}



	public CoreHttpServerCB httpServerCB = new CoreHttpServerCB() {

		@Override
		public void onTransportUpdata(String arg0, String arg1, long arg2, long arg3, long arg4) {
			Log.e("onTransportUpdata",
					"agr0 " + arg0 + " arg1 " + arg1 + " arg2 " + arg2 + " arg3 " + arg3 + " arg4  " + arg4);

		}

		@Override
		public void onHttpServerStop() {

		}

		@Override
		public void onHttpServerStart(String ip, int port) {
			setIp(ip);
			setPort(port);
		}

		@Override
		public String onGetRealFullPath(String arg0) {
			return null;
		}

		@Override
		public void recivePushResources(List<String> pushList) {
			recivePushList .add(pushList);
			final List<String> list = new ArrayList<>();
			list.addAll(pushList);
			String jsonString = list.remove(0);
			Log.e("pushList",pushList.size()+"");
			Log.e("list", list.size()+"");
			Log.e("recivePushList", recivePushList.size()+"");
			String message = null;
			int filenum =0;
			String http1 ="";
			String device_id1="";
			try {
				JSONObject jsonObj = new JSONObject(jsonString);
				message = jsonObj.getString("message");
				filenum = jsonObj.getInt("filenum");
				http1= jsonObj.getString("http");
				device_id1 = jsonObj.getString("device_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			final String device_id=device_id1;
			final String http=http1;
			final String msg=message;
				ReciveDialogFragment reciveDialogFragment = new ReciveDialogFragment() {
					@Override
					public void onReciveFragmentEnter() {
						if(list.size()>0){
						Intent intent = new Intent();
						intent.setClass(getContext(), ShowPushFileActivity.class);
						intent.putStringArrayListExtra("pushList", (ArrayList<String>) list);
						startActivity(intent);
						dismiss();
						}
					}

					@Override
					public void setReciveFragmentMessage(TextView tv_message) {
						tv_message.setText(msg==null?"":msg);
					}

					@Override
					public void onReciveFragmentReply(String message) {
						DeviceInfo info = null;
						for(DeviceInfo tmp : ClientBusHandler.List_DeviceInfo){
							if(tmp.getM_deviceid().equals(device_id)){
								info=tmp;
								break;
							}
						}
						if(info !=null) {
							JSONObject pushJson = new JSONObject();
							try {
								TelephonyManager tm = (TelephonyManager) MyApp.this
										.getSystemService(Context.TELEPHONY_SERVICE);
								String DEVICE_ID = tm.getDeviceId();
								pushJson.put("device_id", DEVICE_ID);
								pushJson.put("message", message);
								pushJson.put("filenum", 0);
								pushJson.put("http", "http://" + ip + ":" + port);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							ArrayList<String> tmp = new ArrayList<>();
							tmp.add(0, pushJson.toString());
							CoreApp.mBinder.PushResourceToDevice(info, tmp);

						}
						}


				};
			if(context != null) {
				ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
				@SuppressWarnings("deprecation")
				List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);

				if (runningTaskInfos != null){
					 if((runningTaskInfos.get(0).topActivity).getPackageName().equals("com.changhong.ttfileplore"))
					reciveDialogFragment.show(((Activity) context).getFragmentManager(), "reciveDialogFragment");
				}


			}


		}
	};

}
