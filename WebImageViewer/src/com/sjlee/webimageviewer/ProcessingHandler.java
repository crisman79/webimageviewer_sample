package com.sjlee.webimageviewer;

import java.util.List;
import com.sjlee.lib.htmlutil.WebImageInfo;

public interface ProcessingHandler {
	void onCompletedHtmlLoad(String htmlBody);
	void onCompletedHtmlParse(List<WebImageInfo> imageInfos);
}