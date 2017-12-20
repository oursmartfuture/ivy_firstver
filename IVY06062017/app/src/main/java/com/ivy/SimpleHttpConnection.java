package com.ivy;

/**
 * Class Name: SimpleHttppConnection.Class
 * Class description: This class contains methods and classes for maintaining the simple HTTP connection.
 * Created by Sobhit Gupta(00253)
 * Created by sing sys-118 on 8/4/2015.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class SimpleHttpConnection
{
    private static final String TAG = "SimpleHTTPConnection";
    private static final String ERROR = "Simple HTTP Connection Error";

    public static String sendByGET (String url)
    {
        InputStream is;
        StringBuilder sb;
        String result = ERROR;

        try
        {
            HttpClient httpclient = new DefaultHttpClient ();
            HttpGet httpget = new HttpGet (url);
            HttpResponse response = httpclient.execute (httpget);
            HttpEntity entity = response.getEntity ();
            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;
    }
    public static boolean isNetworkAvailable(Context c) {
        boolean available = false;
        /** Getting the system's connectivity service */
        ConnectivityManager connMgr = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        /** Getting active network interface to get the network's status */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        NetworkInfo wifiInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (networkInfo != null && networkInfo.isAvailable()) {
            available = true;
        } else if (wifiInfo.isConnected()) {
            available = true;
        }

        /** Returning the status of the network */
        return available;
    }
    /*
    public static String sendImageData(String url,byte[] data,String name,String email,String phone){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            entity.addPart("username", new StringBody(name));
            entity.addPart("email", new StringBody(email));
            entity.addPart("avatar", new ByteArrayBody(data,"image1.jpeg"));
            entity.addPart("phone", new StringBody(phone));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost,
                    localContext);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent(), "UTF-8"));

            String sResponse = reader.readLine();
            return sResponse;
        } catch (Exception e) {

            Log.e(e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }
    */
    // With image upload using post
    public static String sendByPOST (String url)
    {
        InputStream is;
        StringBuilder sb;
        String result = ERROR;
        try
        {

			/* ** Set TIME OUT of HTTPPOST
			HttpPost httppost = new HttpPost (url);
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse response = client.execute (httppost);
			*/


            HttpClient httpclient = new DefaultHttpClient ();
            HttpPost httppost = new HttpPost (url);
            //httppost.setEntity (new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute (httppost);
            HttpEntity entity = response.getEntity ();

            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;
    }
    // With image upload using put
    public static String sendByPUT (HttpPut httpput)//(String url, ArrayList<NameValuePair> data)
    {
        InputStream is;
        StringBuilder sb;
        String result = ERROR;
        try
        {
            HttpClient httpclient = new DefaultHttpClient ();
//			HttpPost httppost = new HttpPost (url);
//			httppost.setEntity (new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute (httpput);
            HttpEntity entity = response.getEntity ();

            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;
    }

    public static String sendByPOST(String url, ArrayList<NameValuePair> data)
    {
        InputStream is;
        StringBuilder sb;
        String result = ERROR;
        try
        {

            //System.out.println("sendByPOST URL="+url);

            HttpClient httpclient = new DefaultHttpClient ();
            HttpPost httppost = new HttpPost (url);
            httppost.setEntity (new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute (httppost);
            HttpEntity entity = response.getEntity ();

            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;
    }

    public static String sendByPUT(String url, ArrayList<NameValuePair> data){
        InputStream is;
        StringBuilder sb;
        String result = ERROR;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPut httpput= new HttpPut(url);
            httpput.setEntity (new UrlEncodedFormEntity (data));
            HttpResponse response = httpclient.execute (httpput);
            HttpEntity entity = response.getEntity ();
            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();

        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;

    }
    public static String sendByPOST (HttpPost httppost)//(String url, ArrayList<NameValuePair> data)
    {
        InputStream is;
        StringBuilder sb;
        String result = ERROR;
        try
        {
            HttpClient httpclient = new DefaultHttpClient ();
//            HttpPost httppost = new HttpPost (url);
//            httppost.setEntity (new UrlEncodedFormEntity(data));
            HttpResponse response = httpclient.execute (httppost);
            HttpEntity entity = response.getEntity ();

            is = entity.getContent ();

            BufferedReader reader = new BufferedReader (new InputStreamReader (is, "iso-8859-1"), 20);
            sb = new StringBuilder ();
            sb.append (reader.readLine ());
            String line = null;
            while ((line = reader.readLine ()) != null)
            {
                sb.append (line);

            }
            is.close ();
            result = sb.toString ();
        }
        catch (UnsupportedEncodingException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (ClientProtocolException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }
        catch (IOException e)
        {
            Log.i (TAG, e.getMessage ());
            e.printStackTrace ();
        }

        return result;
    }
    public static ArrayList<NameValuePair> generateParams (String[] keys, String[] values)
    {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair> ();
        for (int i = 0; i < keys.length; i++)
        {
            params.add (new BasicNameValuePair (keys[i], values[i]));
        }

        return params;
    }

    public static String buildGetUrl (String url, String[] keys, String[] values)
    {
        if (!url.endsWith ("?"))
            url += "?";
        url += URLEncodedUtils.format (generateParams (keys, values), "utf-8");

        return url;
    }
    /*
    end of class
     */
}
