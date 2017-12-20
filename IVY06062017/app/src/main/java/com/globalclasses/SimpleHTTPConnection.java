package com.globalclasses;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class SimpleHTTPConnection {

	/**
	 * Create Connection to server to upload or receive data
	 *
	 */

	private static final String TAG = "SimpleHTTPConnection";
	private static final String ERROR = "Simple HTTP Connection Error";
	private Context _context;

	public SimpleHTTPConnection(Context context) {
		this._context = context;
	}

	// With image upload using post
	/**
	 * Set httppost method to communicate with server
	 *
	 * @return
	 */
	public static String sendByPOST(String mainUrl, MultipartEntity multipartEntity)
	{
		InputStream is;
		StringBuilder sb;
		String result = ERROR;
		BufferedReader mReader=null;
		try {
			HttpPost httppost = new HttpPost(mainUrl);
			httppost.setEntity(multipartEntity);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();

			mReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 20);
			sb = new StringBuilder();
			sb.append(mReader.readLine());
			String line = null;
			while ((line = mReader.readLine()) != null) {
				sb.append(line);
			}
			is.close();
			result = sb.toString();
		}catch (UnsupportedEncodingException e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
		} catch (IOException e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
			result = Constant.ConnectionTimeOut;
		}
		catch (Exception e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
		}
		finally {
			if (mReader != null) {
				try {
					mReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static String sendByPOST(String mainUrl,StringBuilder postData)
	{
		String result = ERROR;
		Reader mReader = null;
		try {
			URL url = new URL(mainUrl);
			byte[] postDataBytes = postData.toString().trim().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			mReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = mReader.read()) >= 0; )
				sb.append((char) c);
			result = sb.toString();

			return result;
		}catch (UnsupportedEncodingException e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
		} catch (IOException e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
			result = Constant.ConnectionTimeOut;
		}
		catch (Exception e)
		{
			Log.i (TAG, e.getMessage ());
			e.printStackTrace ();
		}
		finally {
			if (mReader != null) {
				try {
					mReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

}
