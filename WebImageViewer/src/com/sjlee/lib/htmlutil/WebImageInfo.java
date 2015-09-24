package com.sjlee.lib.htmlutil;

public class WebImageInfo {

	private String mCaption = "";
	private String mImageLink = "";
	private String mThumbnailLink = "";
	private String mKey = "";		//	CRC64??
	public WebImageInfo(String caption, String imageLink, String thumbnailLink) {
		// TODO Auto-generated constructor stub
		setCaption(caption);
		mImageLink = imageLink;
		setThumbnailLink(thumbnailLink);
		mKey = thumbnailLink;
	}

	public String getCaption() {
		return mCaption;
	}

	public void setCaption(String caption) {
		this.mCaption = caption;
	}

	public String getImageLink() {
		return mImageLink;
	}

	public void setImageLink(String imageLink) {
		this.mImageLink = imageLink;
	}

	public String getThumbnailLink() {
		return mThumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.mThumbnailLink = thumbnailLink;
	}
	
	public String getKey() {
		return mKey;
	}
}
