package com.jimcloudy.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service{
	public static final String TAG = "UpdaterService";
	static final int DELAY = 60000;
	private boolean runFlag = false;
	private Updater updater;
	private YambaApplication yamba;
	DBHelper dbHelper;
	SQLiteDatabase db;
	
	@Override
	public IBinder onBind(Intent intent){
		return null;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		this.updater = new Updater();
		this.yamba = (YambaApplication) getApplication();
		dbHelper = new DBHelper(this);
		Log.d(TAG,"onCreate");
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		super.onStartCommand(intent, flags, startId);
		this.runFlag = true;
		this.updater.start();
		this.yamba.setServiceRunning(true);
		Log.d(TAG,"onStarted");
		return START_STICKY;
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		this.runFlag = false;
		this.updater.interrupt();
		this.updater = null;
		this.yamba.setServiceRunning(false);
		Log.d(TAG,"onDestroy");
	}
	
	private class Updater extends Thread{
		List<Twitter.Status> timeline;
		
		public Updater(){
			super("UpdaterService-Updater");
		}
		
		@Override
		public void run(){
			UpdaterService updaterService = UpdaterService.this;
			while(updaterService.runFlag)
			{
				Log.d(TAG,"Updater Running");
				try{
					try{
						timeline = yamba.getTwitter().getFriendsTimeline();
					}
					catch(TwitterException e){
						Log.e(TAG,"Failed to connect to twitter service", e);
					}
					db = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					for(Twitter.Status status : timeline){
						values.clear();
						values.put(DBHelper.C_ID,status.id);
						values.put(DBHelper.C_CREATED_AT,status.createdAt.getTime());
						values.put(DBHelper.C_TEXT,status.text);
						values.put(DBHelper.C_USER,status.user.name);
						db.insertOrThrow(DBHelper.TABLE, null, values);
						Log.d(TAG,String.format("%s: %s", status.user.name, status.text));
					}
					db.close();
					Log.d(TAG,"Updater Ran");
					Thread.sleep(DELAY);
				}
				catch(InterruptedException e){
					updaterService.runFlag = false;
				}
			}
		}
	}
}
