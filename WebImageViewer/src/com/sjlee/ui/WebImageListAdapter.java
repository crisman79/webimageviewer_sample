/**
 * 
 */
package com.sjlee.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

import com.sjlee.cache.ImageCacheService;
import com.sjlee.cache.ImageMemoryCache;
import com.sjlee.lib.htmlutil.WebImageInfo;
import com.sjlee.webimageloader.BitmapDownloadTask;
import com.sjlee.webimageloader.WebImageLoader;
import com.sjlee.webimageviewer.BuildConfig;
import com.sjlee.webimageviewer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author sjlee1225
 *
 */
public class WebImageListAdapter extends BaseAdapter {
	
	private Context mContext = null;
	private List<WebImageInfo> mList = new ArrayList<WebImageInfo>();
	private static final String TAG = "WebImageListAdapter";
	private LayoutInflater mLayoutInflater;
	
	/**
	 * 
	 */
	public WebImageListAdapter(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
	}

	public void addItem(WebImageInfo item) {
		mList.add(item);		
	}
	
	public void addItems(List<WebImageInfo> items) {
		mList.addAll(items);
	}
	
	public void clearDatas() {
		this.mList.clear();
	}
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	//http://developer.android.com/intl/ko/training/improving-layouts/smooth-scrolling.html#ViewHolder
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		// for debug
//		if( BuildConfig.DEBUG == true) {
//			Log.d(TAG, "getView():"+position);
//		}
		
		final View v; 
		ViewHolder holder = null;
		if (convertView != null) {
			v = convertView;
			holder = (ViewHolder)v.getTag();	
			
		} else {
			LayoutInflater inflater = mLayoutInflater;
			v = inflater.inflate(R.layout.cell_image_list, parent, false);
			v.setPadding(6, 6, 6, 6);
			holder = new ViewHolder();
			holder.icon = (ImageView) v.findViewById(R.id.imageview);
			holder.text = (TextView) v.findViewById(R.id.caption);
			holder.position = position;
			//holder.progress = (ProgressBar) convertView.findViewById(R.id.ic_spinner1.png);
			v.setTag(holder);			
			
		}
		
		holder.position = position;
		
		final WebImageInfo data = mList.get(position);
		
		final ImageView imgView = (ImageView)holder.icon;
		final TextView captionView = (TextView)holder.text;

		if( captionView != null) { 
			captionView.setText(data.getCaption());
		}
				
		Bitmap bm = null;
		int hashCode = data.getKey().hashCode();
		bm = ImageCacheService.getInstance().get(hashCode);
		if( bm != null ) {			
			imgView.setImageBitmap(bm);
		}
		else {
			// REF : http://javatechig.com/android/loading-image-asynchronously-in-android-listview
			BitmapDrawable placeholder = (BitmapDrawable)ImageCacheService.getInstance().getPlaceHolder();
    		if( placeholder != null ) {
    			bm = placeholder.getBitmap();
    			imgView.setImageBitmap(bm);
    			imgView.setVisibility(View.VISIBLE);
    		}
    		else {
    			// default placeholder 직접 로딩
    			bm = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.placeholder_small);
    			imgView.setImageBitmap(bm);
    			imgView.setVisibility(View.VISIBLE);
    		}
			   		
    		
    		// TODO
    		// ThreadPoolExecutor.
    		// 1. avoid RejectedExecutionException
    		// 2. using ExcutorService
    		
			//REF : http://android-developers.blogspot.kr/2010/07/multithreading-for-performance.html
    		try { 
				BitmapDownloadTask task = new BitmapDownloadTask(holder, position);
				
				if(true){
					//executeOnExecutor, 병렬
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						// CPU_COUNT * 2 + 1; dual *2 + 1 = 5? 10?						
						task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data.getThumbnailLink());
					} else {
						task.execute(data.getThumbnailLink());
					}
				}
				else {
					// Excutor를 사용하여 관리를 해야한다.				
					// task를 runnable로 해야한다.
					// AsyncTask를 하려면 lifecycle을 관리해야한다.
					//WebImageLoader.getInstance().getExecutor().submit( task);
				}
    		}catch(Exception ex) {
    			ex.printStackTrace();
    		}
    		
            notifyDataSetChanged();
		}
		
		return v;
	}

}
