package com.nolanofra.testHttpCall.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.nolanofra.testHttpCall.lib.MyLibrary;

import android.util.Log;

public class RemoteExecutor {
	
	private static String TAG = "RemoteExecutor";
	static int timeout = 5000;	
	
	private static InputStream instream;
	private static HttpGet get;
	
	public RemoteExecutor()
	{}
	
	/**
     * Execute a {@link HttpGet} request
     * and it returns json Text string
     */
	public String executeGet (String url)
	{
		Log.d(TAG, "executeGet");
		HttpParams httpParameters = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);		
		HttpClient client = new DefaultHttpClient();		
		
		String jsonText = "";
		get = new HttpGet(url);
		HttpResponse response = null;
		instream = null;
				
		try
		{		
			try 
			{
				response = client.execute(get);
			} 
			catch (ClientProtocolException e1)
			{
				// TODO Auto-generated catch block
				Log.e(TAG, e1.getMessage());
				e1.printStackTrace();		
			} 
			catch (IOException e1) 
			{
				// TODO Auto-generated catch block\
				Log.e(TAG, e1.getMessage());
				e1.printStackTrace();			
			}					
			int status = response.getStatusLine().getStatusCode();
			
			if (status == HttpStatus.SC_OK) 
			{
				HttpEntity entity = response.getEntity();
				
				try
				{
					instream = entity.getContent();			
				}
				catch (IllegalStateException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();			
				} 
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();			
				}
				
				jsonText = MyLibrary.convertStreamToString(instream);
			}
			try {
				closeStream();
				
				//instream.notifyAll();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e)
		{
			closeStream();
		}
		return jsonText;
	}
	
	public static void closeStream()
	{
		if (instream != null)
		{
			try {
				Log.d(TAG, "closeStream");
				instream.close();				
				instream = null;								
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "IOException in closeStream");				
				e.printStackTrace();
			}
			catch (Exception e)
			{
				Log.d(TAG, "Exception in closeStream");				
			}			
		}
		try
		{
			if (get != null && !get.isAborted())
			{
				get.abort();				
				get = null;
			}			
		}
		catch (Exception e)
		{Log.d(TAG, "Exception in get abort");}
	}
}