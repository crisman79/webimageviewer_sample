package com.sjlee.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

// application helper
public class AppHelper {
	private static final String TAG = "AppHelper";
			
	public static boolean isExternalStorageReady() 
	{
		String cardstatus = Environment.getExternalStorageState();
		if (cardstatus.equals(Environment.MEDIA_REMOVED)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTED)
				|| cardstatus.equals(Environment.MEDIA_UNMOUNTABLE)
				|| cardstatus.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
			return false;
		} else {
			if (cardstatus.equals(Environment.MEDIA_MOUNTED)) {
				return true;
			} else { 
				return false;
			}
		}
	}
	
	public static boolean isExistPath(String path)
	{
		if( path == null || path.isEmpty() == true ) 
		{
			return false;
		}

		File baseDir = new File(path);
		
		if(baseDir.exists() == true ) {
		    // do something here
			return true;
		} 
		
		return false;		
	}
	
	public static boolean isDirectory(String path)
	{
		if( path == null || path.isEmpty() == true ) 
		{
			return false;
		}
		
		File baseDir = new File(path);

		if( baseDir.isDirectory() == true)
		{  
			return true;
		}
		return false;
	}
	
	public static void makeDirectory(String path)
	{
		if( path == null || path.isEmpty() == true ) 
		{
			return;
		}
		
		File baseDir = new File(path);
		if(baseDir.exists() == true ) {
		    // do something here			
		} 
		else  
		{  
			// 없으면 만들어라!!
			baseDir.mkdir();
		}
		
		return;
	}
	
	static public void createFile(File outputFile)
	{
		if( outputFile != null )
		{
			if( outputFile.exists() == true )
			{
				outputFile.delete();
			}
			try {				
				if( outputFile.createNewFile() == false )
				{
					Log.w(TAG, "createFile() : can not create file.");
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.w(TAG, "createFile() : can not create file.("+outputFile.getAbsolutePath()+")");
				e.printStackTrace();
			}
		}
	}
		
	static public boolean renameFile(File file , File new_name){
        boolean result;
        if(file!=null&&file.exists()&&file.renameTo(new_name))
        {
            result=true;
        }
        else
        {
            result=false;
        }
        return result;
    }
	
	static public File makeFile(File dir , String file_path){
        File file = null;
        boolean isSuccess = false; 
        if(dir.isDirectory()){
            file = new File(file_path);
            if(file!=null&&!file.exists()){
                Log.i( TAG , "!file.exists" );
                try {
                    isSuccess = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally{
                    Log.i(TAG, "파일생성 여부 = " + isSuccess);
                }
            }else{
                Log.i( TAG , "file.exists" );
            }
        }
        return file;
    }
	
	static public boolean saveBitmapToFile(String path, Bitmap bmp) {
		File saveFile = new File(path);
		OutputStream out = null;
		 
        try
        {
        	saveFile.createNewFile();
            out = new FileOutputStream(saveFile); 
            bmp.compress(CompressFormat.JPEG, 100, out);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return true;
	}
	
}
	