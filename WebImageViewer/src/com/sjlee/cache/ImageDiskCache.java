package com.sjlee.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

//http://developer.android.com/intl/ko/training/displaying-bitmaps/cache-bitmap.html
public class ImageDiskCache  {

	final String TAG = "ImageDiskCache";
	private DiskCache mDiskCache; 
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	
	public ImageDiskCache(Context context) {
		// TODO Auto-generated constructor stub
		synchronized (mDiskCacheLock) {
			
			mDiskCache = new DiskCache(context);
			mDiskCacheStarting = false; // Finished initialization
            mDiskCacheLock.notifyAll(); // Wake any waiting threads			
		}		
	}
	
	// cache에 key 값에 해당하는 bmp가 없는 경우에만 추가한다
	public void addBitmap(int key, Bitmap bmp) {		
		try {
			if (isExist(key) == false) {
				if( mDiskCache != null ) {
					//bmp.getRowBytes();
					boolean result = false;
					result = mDiskCache.put(key, bmp);
					if( result == false) {
						Log.w(TAG, "bitmap save to diskcache fail");
					}
					else {
						Log.d(TAG, "bitmap save to diskcache success");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// cache에서 key에 해당하는 bitmap을 가져온다.
	public Bitmap getBitmap(int key) throws Exception {
		if( mDiskCache == null ) {
			throw new Exception("DiskCache unavailable");
		}
		synchronized (mDiskCacheLock) {
			return mDiskCache.get(key);
		}
	}
	
	// 이미 있는 키 값에 대한 정보 인지 확인
	// TODO
	// 1. 파일을 집접 확이하는게 아니라 index map으로 처리하면 조금더 빠르고 안정적일 듯 
	private boolean isExist(int key) throws Exception {
		if(mDiskCache == null ) {
			throw new Exception("DiskCache Unavailable");
		}
		return mDiskCache.isExist(key);
	}
	
	private byte [] getBitmapBytes(Bitmap bitmap) { 
	    ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
	    bitmap.compress( CompressFormat.JPEG, 100, stream) ;  
	    byte[] byteArray = stream.toByteArray() ;  
	    return byteArray ;  
 
	}
}
