package com.jimcloudy.yamba;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class TimelineActivity extends BaseActivity{
	DBHelper dbHelper;
	SQLiteDatabase db;
	Cursor cursor;
	ListView listTimeline;
	SimpleCursorAdapter adapter;
	static final String[] FROM = { DBHelper.C_CREATED_AT, DBHelper.C_USER,DBHelper.C_TEXT };
	static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);		
		
		setContentView(R.layout.timeline);
		
		if(yamba.getPrefs().getString("username",null) == null){
			startActivity(new Intent(this,PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
		
		listTimeline = (ListView) findViewById(R.id.listTimeline);
		
		dbHelper = new DBHelper(this);
		db = dbHelper.getReadableDatabase();
		
		if(yamba.getPrefs().getString("username",null)==null){
			startActivity(new Intent(this,PrefsActivity.class));
			Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		yamba.getStatusData().close();
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		this.setupList();
	}
	
	private void setupList(){
		cursor = yamba.getStatusData().getStatusUpdate();
		startManagingCursor(cursor);
		
		adapter = new SimpleCursorAdapter(this,R.layout.row,cursor,FROM,TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeline.setAdapter(adapter);
	}
	
	static final ViewBinder VIEW_BINDER = new ViewBinder(){
		public boolean setViewValue(View view, Cursor cursor, int columnIndex){
			if (view.getId() != R.id.textCreatedAt){
				return false;
			}
			
			long timestamp = cursor.getLong(columnIndex);
			CharSequence relTime = DateUtils.getRelativeTimeSpanString(view.getContext(), timestamp);
			((TextView) view).setText(relTime);
			
			return true;
		}
	};
}
