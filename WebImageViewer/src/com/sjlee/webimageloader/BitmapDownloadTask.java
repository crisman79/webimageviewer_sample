package com.sjlee.webimageloader;

import java.lang.ref.WeakReference;

import com.sjlee.cache.ImageCacheService;
import com.sjlee.ui.ViewHolder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

// ViewHolder, list view의 postion을 받아서 다운로드 받은다음 보여주는 Task
public class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private static final String TAG = "BitmapDownloadTask";
    
    private final ViewHolder mViewHolderRef;
    private String mRecId;
    private int mPos = 0;

    public BitmapDownloadTask(ViewHolder viewHolder, int pos) {
    	//mViewHolderRef = new WeakReference<ViewHolder>(viewHolder);
    	mViewHolderRef = viewHolder;
    	mViewHolderRef.icon.setTag(pos);
    	mRecId = viewHolder.icon.toString();    	
    	mPos = pos;
    }

    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
         // params comes from the execute() call: params[0] is the url.
    	url = params[0];
    	return WebImageLoader.getInstance().loadImage(params[0]);
    }

    
    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap result) {
    	super.onPostExecute(result);
    	
        if (isCancelled()) {
        	result = null;
        	return;
        }
        
        if (mViewHolderRef != null) {
        	ViewHolder viewHolder = mViewHolderRef;
            if (viewHolder != null) {
            	ImageView imageView = viewHolder.icon;
            	if( result != null ) {
              	
            		// POS가 다르면, Scrolling 을 해서 위치가 변했다는 뜻!
            		if (viewHolder.position == mPos) {
            			imageView.setImageBitmap(result);
               			imageView.setVisibility(View.VISIBLE);               			
                    }
            		else {
            			Log.d(TAG, String.format("viewHolder.position(%d) org pos(%d)", viewHolder.position, mPos));
            		}
           		
            	//}
//                .progress.setVisibility(View.GONE);
//                v.icon.setVisibility(View.VISIBLE);
                //imageView.notify();
            		ImageCacheService.getInstance().put(url, result);     
            		//result.recycle();
            	}
//            	else {
//            		Drawable placeholder = ImageCacheService.getPlaceHolder();
//            		if( placeholder != null ) {
//            			imageView.setImageDrawable(placeholder);
//            		}
//            	}
            }            
        }
    }
}