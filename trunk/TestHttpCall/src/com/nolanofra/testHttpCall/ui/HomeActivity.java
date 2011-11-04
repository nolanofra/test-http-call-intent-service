package com.nolanofra.testHttpCall.ui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.nolanofra.testHttpCall.R;
import com.nolanofra.testHttpCall.service.HttpCallService;
import com.nolanofra.testHttpCall.util.DetachableResultReceiver;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity  extends Activity implements
									DetachableResultReceiver.Receiver{

	
	private static final String TAG = "HomeActivity";
	
	private State mState;
	private Intent mIntentService;
	
	private String title, description;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        mState = new State();
        mState.mReceiver.setReceiver(this);
        
        callWs();
        
	}
	
	private void callWs()
	{
		ConnectivityManager conn = (ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
    	if (conn.getActiveNetworkInfo() != null && conn.getActiveNetworkInfo().isConnected())
    	{
    		Log.d(TAG, "connected");
    		this.mIntentService = new Intent(Intent.ACTION_SYNC, null, this, HttpCallService.class);
    		this.mIntentService.putExtra(HttpCallService.EXTRA_STATUS_RECEIVER, mState.mReceiver);
    		this.mIntentService.putExtra(HttpCallService.URL_WS, "http://www.nolanofra.com/test");
    		startService(this.mIntentService);
    	}
    	else
    	{
    		Log.d(TAG, "no connection");	     		
    		Toast.makeText(HomeActivity.this, "no connection", Toast.LENGTH_LONG).show();
    	}    		
	}
	
	private void updateRefreshStatus() {
		
        findViewById(R.id.refresh_progress).setVisibility(
                mState.mSyncing ? View.VISIBLE : View.GONE);                
    }
	
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {

		Log.d(TAG, "onReceiveResult() Called");
        switch (resultCode) {
	        case HttpCallService.STATUS_RUNNING: {
	        	Log.d(TAG, "running");
	            mState.mSyncing = true;
	            updateRefreshStatus();
	            break;
	        }
	        case HttpCallService.STATUS_FINISHED: {
	        	Log.d(TAG, "finished");
	        	mState.mSyncing = false;
	        	//((TextView)findViewById(R.id.testHttpCall)).setText(resultData.getString(HttpCallService.JSON_TEXT));
	        	
	        	new JsonParserAsyncTask().execute(resultData.getString(HttpCallService.JSON_TEXT));
	        	
	            updateRefreshStatus();          
	            break;
	        }
	        case HttpCallService.STATUS_ERROR: {
	            // Error happened down in SyncService, show as toast.
	            mState.mSyncing = false;
	            updateRefreshStatus();
	            final String errorText = resultData.getString(Intent.EXTRA_TEXT);
	            Toast.makeText(HomeActivity.this, errorText, Toast.LENGTH_LONG).show();
	            break;
	        }
        }
	}

	
	private void ParseJSONString (String jsonString)
	{
		try
		{
			JSONTokener tokener = new JSONTokener(jsonString);
			JSONArray testJSONArray = null;
			
			testJSONArray = new JSONArray(tokener);
			
			for (int i = 0; i < testJSONArray.length(); i++ )
			{
				JSONObject testJSONObject = testJSONArray.getJSONObject(i);
				
				this.title = testJSONObject.getString("Title");
				this.description = testJSONObject.getString("Description");
			}
		}
		catch (Exception ex)
		{}
	}
	
	public class JsonParserAsyncTask extends AsyncTask<String, Void, Void>
	{

		@Override
		protected Void doInBackground(String... jsonStrings) {
			
			for (String json : jsonStrings)
			{
				ParseJSONString (json);
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void x)
		{
			((TextView)findViewById(R.id.title)).setText(title);
			((TextView)findViewById(R.id.description)).setText(description);
		}
		
	}
	
	@Override
	public void onDestroy()
	{		
		super.onDestroy();
		
		if (this.mIntentService != null)
			stopService(this.mIntentService);
		
		mState.mReceiver.clearReceiver();
		mState.mReceiver = null;
		mState = null;
		finish();
		Log.d(TAG, "onDestroy");
	}
	
	 private static class State
	{
		public DetachableResultReceiver mReceiver;
		public boolean mSyncing = true;		
		private State ()
		{
			this.mReceiver = new DetachableResultReceiver(new Handler());			
		}
	}
}
