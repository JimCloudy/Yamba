package com.jimcloudy.yamba;

import winterwell.jtwitter.TwitterException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener,TextWatcher{
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button buttonUpdate;
	TextView textCount;
	SharedPreferences prefs;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        
        //find views
        editText = (EditText) findViewById(R.id.editText);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);
        
        textCount = (TextView) findViewById(R.id.textCount);
        textCount.setText(Integer.toString(140));
        textCount.setTextColor(Color.GREEN);
        
        editText.addTextChangedListener(this);
        editText.setBackgroundColor(5);
        }
    
    public void onClick(View v){
    	String status = editText.getText().toString();
    	new PostToTwitter().execute(status);
    	editText.setText("");
    	Log.d(TAG, "onClicked");
    }

    class PostToTwitter extends AsyncTask<String, Integer, String>{
    	@Override
    	protected String doInBackground(String...statuses){
    		YambaApplication yamba = ((YambaApplication)getApplication());
    		try{
    			yamba.getTwitter();
    			yamba.twitter.updateStatus(statuses[0]);
    			return "Posted";
    		}
    		catch(TwitterException e){
    			Log.e(TAG, e.toString());
    			e.printStackTrace();
    			return "Failed to post";
    		}
    	}
    	
    	@Override
    	protected void onProgressUpdate(Integer...values){
    		super.onProgressUpdate(values);
    	}
    	
    	@Override
    	protected void onPostExecute(String result){
    		Toast.makeText(StatusActivity.this,result,Toast.LENGTH_LONG).show();
    	}
    }
    
    public void afterTextChanged(Editable statusText){
    	int count = 140 - statusText.length();
    	textCount.setText(Integer.toString(count));
    	textCount.setTextColor(Color.GREEN);
    	if(count<10)
    	{
    		textCount.setTextColor(Color.YELLOW);
    	}
    	if(count<0)
    	{
    		textCount.setTextColor(Color.RED);
    	}
    }
    
    public void beforeTextChanged(CharSequence s, int start, int count, int after){
    }
    
    public void onTextChanged(CharSequence s, int start, int before, int count){
    }    
}


