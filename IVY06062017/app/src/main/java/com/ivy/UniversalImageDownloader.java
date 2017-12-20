package com.ivy;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.net.URI;
import java.net.URL;

/**
 * Class Name: UniverssalImageDownloader.Class
 * Class description: This class contains methods and classes for downloading the image .
 * Created by Sobhit Gupta(00253)
 * Created by sing sys-118 on 7/31/2015.
 */
public class UniversalImageDownloader {

    public static boolean isNotificationDialogShown;
    static DisplayImageOptions options;

    public static void print(String msg) {
        // System.out.println(msg);
    }

    public static void print(String msg1, String msg2) {
        // System.out.println(msg1+"  "+msg2);
    }

    public static void loadImageFromURL(Activity ctx, String url,
                                        ImageView image, int pic) {


        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(pic)
                .showImageOnFail(pic)
                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();


        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));


        imageLoader.displayImage(url, image, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }
                });

    }


    public static void loadImageFromLocalUrl(Activity ctx, String url, ImageView image) {


        options = new DisplayImageOptions.Builder()

                .resetViewBeforeLoading(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();


        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));


        imageLoader.displayImage("file://" + url, image, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }
                });

    }

    public static void loadImageFrom_Drawable(Context ctx, ImageView image, Bitmap imageRes) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .resetViewBeforeLoading(true)
//				.showImageOnLoading(imageRes)
                .build();
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited())
            imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
        imageLoader.displayImage("drawable://" + imageRes, image, options,
                new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view,
                                                FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        // TODO Auto-generated method stub

                    }
                });

    }


    public static String encodeURL(String urlStr) {

        URL url = null;
        try {
            // System.out.println("image url=="+urlStr);
            if (urlStr != null) {
                if (urlStr.length() > 4) {
                    if (urlStr.startsWith("http") || urlStr.contains("http://")) {
                    } else
                        urlStr = "http://" + urlStr;
                    url = new URL(urlStr);
                    URI uri = new URI(url.getProtocol(), url.getUserInfo(),
                            url.getHost(), url.getPort(), url.getPath(),
                            url.getQuery(), url.getRef());
                    url = uri.toURL();
                    return url.toString().replaceAll("&amp;", "&");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urlStr;
    }
    /*

	end of class.
	 */
}
