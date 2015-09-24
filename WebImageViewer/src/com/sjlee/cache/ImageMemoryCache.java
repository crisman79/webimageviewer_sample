package com.sjlee.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class ImageMemoryCache extends LruCache<Integer, Bitmap>{

	final String TAG = "ImageChche";
	
	public ImageMemoryCache(int maxSize) {
		// TODO Auto-generated constructor stub
		super(maxSize);		
	}
	
	// cache에 key 값에 해당하는 bmp가 없는 경우에만 추가한다
	public void addBitmap(int key, Bitmap bmp) {
		if (get(key) == null) {
			this.put(key, bmp);
		}
	}

	// cache에서 key에 해당하는 bitmap을 가져온다.
	public Bitmap getBitmap(int key) {
		return this.get(key);
	}
	
}