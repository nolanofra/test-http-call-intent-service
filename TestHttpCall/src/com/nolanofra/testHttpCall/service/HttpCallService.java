package com.nolanofra.testHttpCall.service;

import com.nolanofra.testHttpCall.io.RemoteExecutor;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class HttpCallService extends IntentService {
	
	private static final String TAG = "HttpCallService";	

	public static final String EXTRA_STATUS_RECEIVER = 
						"com.nolanofra.testHttpCall.extra.STATUS_RECEIVER";
	
	public static final String URL_WS = "urlWS";
	public static final String JSON_TEXT = "jsonText"; 	
	
	public static final int STATUS_RUNNING = 0x1;
    public static final int STATUS_ERROR = 0x2;
    public static final int STATUS_FINISHED = 0x3;
    
    
    public HttpCallService() {
        super(TAG);        
    }   

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		
		Log.d(TAG, "onHandleIntent(intent=" + intent.toString() + ")");
		
		String url = intent.getStringExtra(URL_WS);
		
		Log.d(TAG, "url is: " + url);
				
		
		final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_STATUS_RECEIVER);
        if (receiver != null) receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        
        String jsonText = new RemoteExecutor().executeGet(url);        
        
        if (jsonText == null || jsonText.equals(""))
        {
        	final Bundle bundle = new Bundle();
            bundle.putString(Intent.EXTRA_TEXT, "No Connection");
            receiver.send(STATUS_ERROR, bundle);
        }
        else
        {
        	 Log.d(TAG, "sync finished");
        	 final Bundle bundle = new Bundle();
        	 bundle.putString(JSON_TEXT, jsonText);        	 
             if (receiver != null) receiver.send(STATUS_FINISHED, bundle);
        }        
	} 	
}