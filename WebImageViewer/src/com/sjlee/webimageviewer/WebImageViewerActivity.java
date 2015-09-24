package com.sjlee.webimageviewer;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.sjlee.cache.ImageCacheService;
import com.sjlee.helper.AlertDialogHelper;
import com.sjlee.lib.htmlutil.HtmlLoader;
import com.sjlee.lib.htmlutil.HtmlLoader.HtmlLoaderListener;
import com.sjlee.lib.htmlutil.HtmlParser;
import com.sjlee.lib.htmlutil.HtmlParser.HtmlParserListener;
import com.sjlee.lib.htmlutil.WebImageInfo;
import com.sjlee.ui.WebImageListAdapter;
import com.sjlee.webimageloader.WebImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;

// TODO
// need to test
// 1. 네트워크가 안되거나 변경될때
// 2. 화면 전환시
// 3. Thread나 task가 동작 중에 무언가 변경이 일어날때.
// 4. progress 취소?
public class WebImageViewerActivity extends Activity implements ProcessingHandler{
	private final String TAG = "WebImageViewerActivity";
	
	private Context mCurrentApplicationContext = null;
	private Context mCurrentActivityContext = null;
	
	private final String LOADING_HTTP_URL	=	"http://www.gettyimagesgallery.com/collections/archive/slim-aarons.aspx";

	private HtmlLoader mHtmlLoader = null;
	private HtmlParser mHtmlParser = null;
	private String mHtmlBody = null;
	
	private HtmlLoaderListener mHtmlLoaderListener = null;
	private HtmlParserListener mHtmlParserListener = null;
	
	private List<WebImageInfo> mImageInfos = new ArrayList<WebImageInfo>();

	// Handler 클래스
	private final int IMAGE_LIST_COMPLETED = 1;
	private final int IMAGE_LIST_ERROR = 2;
	private MessgeHandler mMainHandler = null;
	
	private ListView mImageListView = null;
	private GridView mImageGridView = null;
	
	private WebImageListAdapter mListViewAdapter = null;
	
    class MessgeHandler extends Handler {
         
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             
            switch (msg.what) {
            case IMAGE_LIST_COMPLETED:
            	// 이미지 로딩이 끝나면 타이틀레 갯수를 넣어준다.
            	setViewTitle(mImageInfos.size());
            	// 이미지 로딩이 끝나면 ImageLoading Task를 동작시킨다. 
            	runLoadImageTask(mImageInfos);
            	
            	break;
            case IMAGE_LIST_ERROR:
            	break;
            default:
                break;
            }
        }
         
    };
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_image_viewer);
		
		Log.d(TAG, "onCreate");
		
		mCurrentApplicationContext = this.getApplicationContext();
		mCurrentActivityContext = this;
				
		mMainHandler = new MessgeHandler();
		
//		if (Build.VERSION.SDK_INT > 9) {
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//			StrictMode.setThreadPolicy(policy);
//		}
		
		// UI INIT
		initUi();

		// Web Image Loader init
		initWebImageLoader();
		
		//load html & image load from http url
		loadFromUrl(LOADING_HTTP_URL);		
	}

	// http url 페이지로 부터 데이터를 읽어들인다.
	private void loadFromUrl(String urlString) {		
		// loading
		mHtmlLoaderListener = new HtmlLoaderListener() {
			@Override
			public void onResult(String htmlBody) {
				// TODO Auto-generated method stub
				if( htmlBody == null) {
					// CLOSE error displey
					AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "HTML BODY IS NULL");
					return;
				}
				
				// debug
				//AlertDialogHelper.showDialog(mCurrentActivityContext, "OK",  "HTML BODY NOT EMPTY");
				
				// html page loading finish
				onCompletedHtmlLoad(htmlBody);
			}
			
		};
		
		mHtmlParserListener = new HtmlParserListener() {

			@Override
			public void onResult(List<WebImageInfo> imageInfos) {
				// TODO Auto-generated method stub
				if( imageInfos == null || imageInfos.isEmpty() == true ) {
					// CLOSE error displey
					AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "imageUrls is empty");
					return;
				}
				// debug
				//AlertDialogHelper.showDialog(mCurrentActivityContext, "OK",  "imageUrls not empty");
				
				onCompletedHtmlParse(imageInfos);
			}
			
		};
		
		
		// 리스너 설정해서 끝나면 리스너 호출
		mHtmlLoader = new HtmlLoader(this, mHtmlLoaderListener);
		mHtmlLoader.execute(LOADING_HTTP_URL);
		
				
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_image_viewer, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		if( mHtmlParser != null ) {
			mHtmlParser.cancel(true);
			mHtmlParser = null;
		}
		
		if( mHtmlLoader != null ) {
			mHtmlLoader.cancel(true);
			mHtmlParser = null;
		}
		
		WebImageLoader.getInstance().closeAll();
		
		Log.d(TAG, "onDestroy");		
		
		if( mImageInfos != null ) {
			if( mImageInfos.size() > 0 ) {
				mImageInfos.clear();
				mImageInfos = null;
			}
		}
		
		if( mMainHandler != null) {
			mMainHandler = null;
		}
		
		if( mListViewAdapter != null ) {
			mListViewAdapter.clearDatas();
			mListViewAdapter = null;
		}
		ImageCacheService.getInstance().deinit();		
	}

	// loading된 html body 데이터를 paring하여 이미지 url들을 분리한다.
	@Override
	public void onCompletedHtmlLoad(String body) {
		// TODO Auto-generated method stub
		// html loading이 끈나면 html 결과 body를 넘긴다.
		
		if( body == null) {
			// CLOSE error displey
			AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "HTML BODY IS NULL");
			return;
		}
		
		mHtmlParser = new HtmlParser(mCurrentActivityContext, mHtmlParserListener);
		mHtmlParser.execute(body);
		
		if(mHtmlLoader != null) {
			mHtmlLoader.cancel(true);
		}
		
	}

	// parsing된 이미지 정보를 리스트형태로 획득하고 메인 핸들러에게 메시지를 보냄. 
	@Override
	public void onCompletedHtmlParse(List<WebImageInfo> imageInfos) {
		// TODO Auto-generated method stub
		
		if( imageInfos == null || imageInfos.isEmpty() == true ) {
			//CLOSE error displey
			AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "IMAGE URL IS EMPTY");
			return;
		}
		
		makeImageLoadingList(imageInfos);
		
		// 이미지 리스팅이 끝나면 처리하는 Handler로 메시지를 보낸다. 
		if( mMainHandler != null ) {
			Message msg = mMainHandler.obtainMessage();
			msg.what = IMAGE_LIST_COMPLETED;
			mMainHandler.sendMessage(msg);
		}
		
	}
	
	private void makeImageLoadingList(List<WebImageInfo> imageInfos) {
		if(imageInfos == null || imageInfos.isEmpty() == true ) {
			AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "IMAGES INFO IS NULL");
			return;
		}
		
		mImageInfos.clear();
		
		int index = 0;
		for(WebImageInfo img:imageInfos) {
			if(img == null) { 
				continue;
			}
			String caption = img.getCaption();
			String thumbnail = img.getThumbnailLink();
			String image = img.getImageLink();
			
			//로그 : [421] caption[/Picture-Library/Image.aspx?id=7120], thumbnail[/Images/Thumbnails/1476/147666.jpg], image[/Picture-Library/Image.aspx?id=6298]
			String logString = String.format("[%3d] caption[%s], thumbnail[%s], image[%s]", (++index), caption, thumbnail, image);
			//Log.d(TAG, logString);
			
			mImageInfos.add(img);
			
		}
	}
	
	// 이미지 로딩한다. async하도록 해야한다. 
	public void runLoadImageTask(final List<WebImageInfo> images) {
		new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if( images == null || images.isEmpty() == true ) {
					AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "error");
					return;
				}		

				for( WebImageInfo img:images ){
		            //System.out.println( String.format("키 : %s, 값 : %s", key, images.get(key)) );
					mListViewAdapter.addItem(img);					
		        }	
				
				runOnUiThread(new Runnable() {					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						mListViewAdapter.notifyDataSetChanged();
					}}); 
			}}).start();	
		
	}
	
	// ui 관련 설정
	private void initUi() {
		mImageListView = (ListView) findViewById(R.id.listview);
		if( mListViewAdapter == null ) {
			mListViewAdapter = new WebImageListAdapter(mCurrentActivityContext);
		}
		if( mImageListView != null ){
			mImageListView.setAdapter(mListViewAdapter);
			// 터치, 클릭 이벤트 처리!!
			
			mImageListView.setOnItemLongClickListener(new OnItemLongClickListener(){
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					// TODO Auto-generated method stub
					
					if( view == null ) {
						return false;
					}
					WebImageInfo imageInfo = (WebImageInfo) mListViewAdapter.getItem(position);
					if( imageInfo == null ) {
						return false;
					}
					AlertDialog.Builder alertDlg = new AlertDialog.Builder(view.getContext());
					alertDlg.setTitle("세부정보");
		            alertDlg.setPositiveButton( "OK", new DialogInterface.OnClickListener()
		            {
		                 @Override
		                 public void onClick( DialogInterface dialog, int which ) {
		                     dialog.dismiss();  // AlertDialog를 닫는다.
		                 }
		            });
		            
		            String viewMsg = String.format("Title:%s\n\nUrl\n%s", imageInfo.getCaption(), imageInfo.getImageLink());
		            alertDlg.setMessage( viewMsg );
		            alertDlg.show();
					return false;
				}});
			
			mImageListView.setOnScrollListener(new OnScrollListener() {
			    
			    @Override
			    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			            // TODO Auto-generated method stub
			    	// for debug
//			    	if( BuildConfig.DEBUG == true) {
//			    		Log.d(TAG, String.format("%d, %d, %d", firstVisibleItem, visibleItemCount, totalItemCount));
//			    	}
			    }

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					// TODO Auto-generated method stub
					if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
			            //imageLoader.stopProcessingQueue();
			        } else {
			            //imageLoader.startProcessingQueue();
			        }
				}
				
			});			
			
			//mImageListView.getFirstVisiblePosition()
		}
		/*
    	// Tab 이벤트 인터페이스
	    public interface OnItemTapLister {
	        public void OnDoubleTap(AdapterView<?> parent, View view, int position,long id);
	        public void OnSingleTap(AdapterView<?> parent, View view, int position,long id);
	    }
			 * tabListView.setOnItemTapListener(new SingleColumnListView.OnItemTapLister() {
	    @Override
	    public void OnSingleTap(AdapterView<?> parent, View view, int position,long id) {
	        //TODO 싱글 탭 시 작업
	    }
	    @Override
	    public void OnDoubleTap(AdapterView<?> parent, View view, int position,long id) {
	        /TODO 더블 탭 시 작업
	    }
	});
		 */
		
	}
	
	// web image loader초기화 한다.
	private void initWebImageLoader() {
		URL hostUrl = null;
		String host = null;
		try {
			hostUrl = new URL(LOADING_HTTP_URL);
			host = hostUrl.getHost();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if( host == null) {
			AlertDialogHelper.showDialog(mCurrentActivityContext, "ERROR",  "error");		
		}
		else {
			WebImageLoader.getInstance().initLoader(mCurrentActivityContext, host);
		}
	}
	
	public void setViewTitle(int imgCnt) {
    	//title_bar_name
    	Resources res = getResources();
    	if( res == null ) {
    		return;
    	}
    	String text = String.format(res.getString(R.string.title_bar_name), imgCnt);
    	
    	this.setTitle(text);
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.activity_web_image_viewer);
		this.initUi();
		
		Log.d(TAG, "onConfigurationChanged");
	}
	
	
}
