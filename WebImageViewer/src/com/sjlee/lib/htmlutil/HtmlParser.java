package com.sjlee.lib.htmlutil;

import java.io.IOException;
import java.io.InputStream;
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

// HTML Parsing from HTML BODY source
public class HtmlParser extends AsyncTask<String, Void, List<WebImageInfo>>{
	public interface HtmlParserListener {
		void onResult(List <WebImageInfo> imageInfos);
	}
	private ProgressDialog mProgressDialog = null;
	private Context mParentContext = null;
	private String mHtmlData = null;
	private static final String TAG = "HtmlParser";
	private HtmlParserListener mListener = null;
			
	public HtmlParser(Context context, HtmlParserListener listener) {
		// TODO Auto-generated constructor stub		
		mParentContext = context;		
		mListener = listener;
	}
	
	@Override
	protected void onPreExecute() {		
		if( mParentContext != null) { 
			mProgressDialog = new ProgressDialog(mParentContext);
			mProgressDialog.setTitle("HTML Parse");
			mProgressDialog.setMessage("Parsing...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}
		super.onPreExecute();
	}

	@Override
	protected List<WebImageInfo> doInBackground(String... params) {
		mHtmlData = params[0];
		// Connect to the web site
		//Log.d(TAG, "mHtmlData:"+mHtmlData);
		Document document = Jsoup.parse(mHtmlData);//Jsoup.connect(mHtmlData).get();
		
		return parseDocument(document);
	}

	@Override
	protected void onPostExecute(List<WebImageInfo> result) {
		
		super.onPostExecute(result);
		
		if( mProgressDialog != null ) {
			mProgressDialog.dismiss();
		}		
		
		if( mListener != null ) {
			// 결과 리턴, WebImageInfo object list
			mListener.onResult(result);
		}
		
	}
	
	private List<WebImageInfo> parseDocument(Document doc) {
		if( doc == null) { 
			return null;
		}
		
		List<WebImageInfo> imageInfos = new ArrayList<WebImageInfo>();

		/*
		Elements resultLinks = doc.select("h3.r > a"); // direct a after h3
		 */
		// div.gallery-item-group 뒤에 있는 a css
		Elements images = doc.select("div.gallery-item-group > a");
		
		Elements captions = doc.select("div.gallery-item-caption > p");
		/*
		 * <div class="gallery-item-caption">
		 */
        //<p><a href="/Picture-Library/Image.aspx?id=7120"></a></p>
        
	
//		<!-- REPEATER -->
//        <div class="gallery-item-group exitemrepeater">
//            <a href="/Picture-Library/Image.aspx?id=7120"><img src="/Images/Thumbnails/1517/151776.jpg" class="picture"/></a>
//    	        
//            <div class="gallery-item-caption">		// caption
//                <p><a href="/Picture-Library/Image.aspx?id=7120"></a>ddd</p>
//            </div>
//        </div>
//    <!-- REPEATER ENDS -->

		Log.d(TAG, "found picture count : " + images.size());

		int count = 0;
		Element c = null;
		for(Element p : images) {
			c = captions.get(count);
			String _image_link = p.attr("href");
			String _thumbnail_path = p.child(0) == null ? "" : p.child(0).attr("src");		// thumbnail
			
			String _image_caption = "";
			if( c.child(0).childNodeSize() > 0 )  {
				_image_caption = c.child(0).childNode(0) == null?"":c.child(0).childNode(0).toString();
			}
			
			String image_caption = _image_caption;
			String image_link = _image_link;
			String thumbnail_path = _thumbnail_path;
			
			String formatString = String.format("loaded image[%s] link[%s] thumbnailpath[%s]", image_caption, image_link, thumbnail_path);
			//Log.d(TAG, "[" + (++count) +"] " + formatString);
			++count;
			imageInfos.add(new WebImageInfo(image_caption, image_link, thumbnail_path));
		}
		
		
		return imageInfos;
		 
	}

}

