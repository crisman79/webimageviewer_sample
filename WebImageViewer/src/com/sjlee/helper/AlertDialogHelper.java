package com.sjlee.helper;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

// debuging 용도의 alertdialog 
public class AlertDialogHelper {
	static public void showDialog(Context context, String title, String message)
	{		
		// 이건 앱버전이어야 하니깐...아래것으로 해야 하는가? 정확히 모르것네. / 3.0
		
		new AlertDialog.Builder(context)
        .setIconAttribute(android.R.attr.alertDialogIcon)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {            	
            }
        })
        .show();		
	}

}
