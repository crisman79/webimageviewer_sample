package com.sjlee.lib.htmlutil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

// HTML Loading fomr URL
public class HtmlLoader extends AsyncTask<String, Void, String>{
	
	public interface HtmlLoaderListener {
		void onResult(String htmlBody);
	}
	
	private ProgressDialog mProgressDialog = null;
	private Context mParentContext = null;
	private static final String TAG = "HtmlLoader";
	private String mUrl = null;
	private HtmlLoaderListener mLoaderListener = null;
	
	
	public HtmlLoader(Context context, HtmlLoaderListener listener) {
		// TODO Auto-generated constructor stub		
		mParentContext = context;		
		mLoaderListener = listener;
	}
	
	// 프로그레스바 초기화 
	@Override
	protected void onPreExecute() {		
		if( mParentContext != null) { 
			mProgressDialog = new ProgressDialog(mParentContext);
			mProgressDialog.setTitle("HTML Load");
			mProgressDialog.setMessage("Loading...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... params) {
		// Parameter = Page URL 
		mUrl = params[0];
		try {
			// Connect to the web site
			Log.d(TAG, "mUrl:"+mUrl);						
			return getHtml(mUrl);
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		} 
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		
		if( mProgressDialog != null ) {
			mProgressDialog.dismiss();
		}
		
		if( mLoaderListener != null) { 
			// 등록된 Listener 호출, 결과값(HTML BODY)
			mLoaderListener.onResult(result);
		}
		
	}
	
	// REF : http://developer.android.com/intl/ko/reference/java/net/HttpURLConnection.html
	private String getHtml(String url) throws Exception{
		if( url == null) { 
			return null;
		}
				
		// get html url
		String htmlBody = null;
		
		// REF : http://developer.android.com/intl/ko/reference/java/net/MalformedURLException.html
		// TODO 
		URL connectUrl = new URL(url);
		HttpURLConnection urlConnection = (HttpURLConnection) connectUrl.openConnection();
		try {
		     InputStream in = new BufferedInputStream(urlConnection.getInputStream());
		     htmlBody = readStream(in);
		} 
		catch (MalformedURLException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } finally {
			urlConnection.disconnect();
		}
		    
		
		//Log.d(TAG, "HtmlBody:"+htmlBody);
		return htmlBody;		 
	}

	//
	private String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while(i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString();
		} catch (IOException e) {
			return "";
	    }
	}

	private List<String> parseDocument(Document doc) {
		if( doc == null) { 
			return null;
		}
		
		List<String> urlStringList = new ArrayList<String>();
		
		Elements pictures = doc.select("div.innerImageArea > img.innerImage");
		if (pictures.size() == 0) {
			return null;
		}
		
		Element p = pictures.get(0);
		if (p == null) {
			return null;
		}
		
		String image_path = p.attr("src");
		String image_full_url = "";
		
		return urlStringList;
		 
	}
}

