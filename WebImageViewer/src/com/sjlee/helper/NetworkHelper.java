package com.sjlee.helper;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.telephony.TelephonyManager;
import android.util.Log;

// network helper 
// is online?
// is wifi?
// etc...
public class NetworkHelper {
	public static final String TAG = "NetworkHelper";
	
	public static final int NETWORK_TYPE_UNKNOWN 	= 0;
	public static final int NETWORK_TYPE_MOBILE 	= 1;
	public static final int NETWORK_TYPE_WIFI	 	= 2;
	/** WiFi lock unique instance. */
	private static WifiLock mWifiLock;
	  
	public static int getSignalLevel(Context context, int maxLevelStep)
	{
		if( context == null ) {
			return -1;
		}
		if( context != null ) {			
			WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);  
			if( manager != null ) {
				
				WifiInfo info = manager.getConnectionInfo();
				if( info != null ) {					
					int signalLevel = WifiManager.calculateSignalLevel(info.getRssi(), maxLevelStep);
					
					return signalLevel;
				}					
			}
		}
		return -1;
	}
	

    // wifi or 3g? 4g?
    public static final String getNetworkTypeToString(Context context)
    {
    	if( context == null ) {
    		return "Unknown";
    	}
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if( cm == null ) {
			return "Unknown";
		}
			
    	NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    	if (networkInfo != null) {
	    	if( networkInfo.getType() == ConnectivityManager.TYPE_MOBILE ) {
	    		return "MOBILE";
	    	} 
	    	else if( networkInfo.getType() == ConnectivityManager.TYPE_WIFI ) {
	    		return "WIFI";
	    	}
    	}
    	    	
    	return "Unknown";
    }
    
    public static int getNetworkTypeToInt(Context context)
    {
    	if( context == null ) {
    		return NETWORK_TYPE_UNKNOWN;
    	}
    		
    	if( IsConnectedWifi(context) == true ) {
    		return NETWORK_TYPE_WIFI;   		
    	}
    	
    	if( IsConnectedMobile(context) == true ) {
    		return NETWORK_TYPE_MOBILE;   		
    	}
		return NETWORK_TYPE_UNKNOWN;
    }
    
    public static boolean IsConnectedWifi(Context context)
	{
    	if( context == null ) {
    		return false;
    	}
		ConnectivityManager netConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean bConnect = false;
		try
		{
			if( netConnectMgr == null ) return false;

			NetworkInfo info = netConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			bConnect = (info.isAvailable() && info.isConnected());
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		
		return bConnect;
	}

	public static boolean IsConnectedMobile(Context context)
	{  
		if( context == null ) {
    		return false;
    	}
		
		ConnectivityManager netConnectMgr= (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean bConnect = false;
		try
		{
			if( netConnectMgr == null ) return false;
			NetworkInfo info = netConnectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			bConnect = (info.isAvailable() && info.isConnected());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}

		return bConnect;
	}
	
	public static String getSSID(Context context)
	{
		try {
			if( context == null ) {
	    		return "";
	    	}
			// 네트웍에 변경이 일어났을때 발생하는 부분
	        ConnectivityManager connectivityManager =
	            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        
	        if( connectivityManager == null ) {
	        	return "";
	        }
	        
	        NetworkInfo activiInfo = connectivityManager.getActiveNetworkInfo();
	        
	        if( activiInfo == null ) {
	        	return "";
	        }
	
	        if( activiInfo.getType() == ConnectivityManager.TYPE_WIFI) {
	        	WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	        	WifiInfo info = wifimanager.getConnectionInfo();
	        	if( info != null ) {
	        		return info.getSSID();
	        	}
	        }
	        
	        return "";        
		}catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
        
        
	}
	
	public static String getMobileMode(Context context)
	{
		try {
			if( context == null ) {
	    		return "";
	    	}
			// 네트웍에 변경이 일어났을때 발생하는 부분
	        ConnectivityManager connectivityManager =
	            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        
	        if( connectivityManager == null ) {
	        	return "";
	        }
	        
	        NetworkInfo activiInfo = connectivityManager.getActiveNetworkInfo();
	        
	        if( activiInfo == null ) {
	        	return "";
	        }
	
	        if( activiInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
	        	NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);	        	
	    		if( mobNetInfo != null ) {
	    			TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    			if( tm != null ) {
	    				//Log.d(TAG, "getNetworkOperatorName : " + tm.getNetworkOperatorName());	    				
	    				//Log.d(TAG, "getNetworkType : " + tm.getNetworkType());	    				
	    				//Log.d(TAG, " : " + tm.getDeviceId());
	    				//Log.d(TAG, "getDeviceSoftwareVersion : " + tm.getDeviceSoftwareVersion());	    				
	    			}
	    			//mobNetInfo.getSubtype();
	    			
	    			return mobNetInfo.getSubtypeName();  
	    		}
	        }
	        
	        return "";       
		}catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	/*
	 * TelephonyManager : NetworkInfo 에 getSubtypeName()을 호출 하면 이 이름이 출려된다. 예) 3G(TYPE_MOBILE)의 경우  getSubtypeName()은 "HSPA+"가 나오기도 한다. LTE의 경우는 LTE
	 * int	NETWORK_TYPE_1xRTT	Current network is 1xRTT
	 * int	NETWORK_TYPE_CDMA	Current network is CDMA: Either IS95A or IS95B
	 * int	NETWORK_TYPE_EDGE	Current network is EDGE
	 * int	NETWORK_TYPE_EHRPD	Current network is eHRPD
	 * int	NETWORK_TYPE_EVDO_0	Current network is EVDO revision 0
	 * int	NETWORK_TYPE_EVDO_A	Current network is EVDO revision A
	 * int	NETWORK_TYPE_EVDO_B	Current network is EVDO revision B
	 * int	NETWORK_TYPE_GPRS	Current network is GPRS
	 * int	NETWORK_TYPE_HSDPA	Current network is HSDPA
	 * int	NETWORK_TYPE_HSPA	Current network is HSPA
	 * int	NETWORK_TYPE_HSPAP	Current network is HSPA+
	 * int	NETWORK_TYPE_HSUPA	Current network is HSUPA
	 * int	NETWORK_TYPE_IDEN	Current network is iDen
	 * int	NETWORK_TYPE_LTE	Current network is LTE
	 * int	NETWORK_TYPE_UMTS	Current network is UMTS
	 * int	NETWORK_TYPE_UNKNOWN	Network type is unknown 
	 */
	
	
	/*
	 * ConnectivityManager
	 * int	TYPE_BLUETOOTH	The Bluetooth data connection.
	 * int	TYPE_DUMMY	Dummy data connection.
	 * int	TYPE_ETHERNET	The Ethernet data connection.
	 * int	TYPE_MOBILE	The Mobile data connection.
	 * int	TYPE_MOBILE_DUN	A DUN-specific Mobile data connection.
	 * int	TYPE_MOBILE_HIPRI	A High Priority Mobile data connection.
	 * int	TYPE_MOBILE_MMS	An MMS-specific Mobile data connection.
	 * int	TYPE_MOBILE_SUPL	A SUPL-specific Mobile data connection.
	 * int	TYPE_WIFI	The WIFI data connection.
	 * int	TYPE_WIMAX	The WiMAX data connection.
	 */
	public static boolean isOnline(Context context) {
		if( context == null ) {
			return false;
		}
			
	    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnected()) {
	        try {
	            URL url = new URL("http://www.google.com");
	            HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
	            if( urlc == null ) {
	            	return false;
	            }
	            urlc.setConnectTimeout(3000);
	            urlc.connect();
	            if (urlc.getResponseCode() == 200) {
	                return new Boolean(true);
	            }
	        } catch (MalformedURLException e1) {
	            // TODO Auto-generated catch block
	            e1.printStackTrace();
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }
	    return false;
	}
	
	// must request  android.permission.WAKE_LOCK
	public static void keepWiFiOn(Context context, boolean on) {
		//context.getPackageName()
		if (mWifiLock == null) {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			if (wm != null) {
				mWifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
				mWifiLock.setReferenceCounted(true);
			}
		}
		if (mWifiLock != null) { // May be null if wm is null
			if (on) {
				mWifiLock.acquire();
				Log.d(TAG, "Adquired WiFi lock");
			} else if (mWifiLock.isHeld()) {
				mWifiLock.release();
				Log.d(TAG, "Released WiFi lock");
			}
		}
	}
	
}
