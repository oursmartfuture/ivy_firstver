package com.ivy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.globalclasses.GlobalMethod;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;


public class ImagecropingActivity extends FragmentActivity {
    // Static final constants
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 100;
    private static final int ROTATE_NINETY_DEGREES = 90;
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int ON_TOUCH = 1;
    private File imageFile;
    private Uri imageUri;


    // Instance variables
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    String imagePath;

    Bitmap croppedImage;
    CropImageView cropImageView;
    SharedPreferences sharedpref;
    View view;
    TextView headerText;
    ProgressDialog progressDialog;
    TextView refresh, edit_prof, done;
    ImageButton back_btn;
    Bitmap photoBitmap;

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    // Restores the state upon rotating the screen/restarting the activity
    @Override
    protected void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        mAspectRatioX = bundle.getInt(ASPECT_RATIO_X);
        mAspectRatioY = bundle.getInt(ASPECT_RATIO_Y);
    }

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.imagecrop);

        edit_prof = (TextView) findViewById(R.id.edit_prof);
        edit_prof.setVisibility(View.VISIBLE);
        edit_prof.setText("Done");

        headerText = (TextView) findViewById(R.id.headerText);
        headerText.setText("Crop Image");
        back_btn = (ImageButton) findViewById(R.id.backBtn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cropImageView = (CropImageView) findViewById(R.id.CropImageView);
        cropImageView.setFixedAspectRatio(true);
        cropImageView.setEnabled(false);

        edit_prof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImageinSDcard();
            }
        });

        showOnCropImageView(getIntent().getStringExtra("filepath"));
    }


    public void saveImageinSDcard() {

        new AsyncTask<SharedPreferences, Object, Boolean>() {
            File file = null;

            protected void onPreExecute() {
                progressDialog = new ProgressDialog(ImagecropingActivity.this);
                progressDialog.setMessage(getResources().getString(R.string.progress_msg));
                progressDialog.setCancelable(false);
                progressDialog.show();
                super.onPreExecute();
                croppedImage = cropImageView.getCroppedImage(cropImageView.getWidth(), cropImageView.getHeight());
                GlobalMethod.write("width and height" + cropImageView.getHeight() + "" + cropImageView.getWidth());
            }

            ;

            @Override
            protected Boolean doInBackground(SharedPreferences... params) {
                try {

                    File dir = new File(Environment.getExternalStorageDirectory() + "/DCIM/");
                    dir.mkdir();
                    file = new File(dir, "IvyCrop" + new Date().getTime() + ".png");
                    try {
                        FileOutputStream out;
                        out = new FileOutputStream(file);
                        croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                        return true;
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return false;
            }

            @Override
            protected void onPostExecute(Boolean isAuthenticated) {
                progressDialog.cancel();
                if (isAuthenticated) {
                    Intent in = new Intent();
                    GlobalMethod.write("====saveimage" + file.getAbsolutePath());
                    progressDialog.cancel();
                    in.putExtra("filepath", file.getAbsolutePath());
                    setResult(RESULT_OK, in);
                    finish();
                }
            }
        }.execute();
    }

    private Bitmap resizeBitmap(String path) {
        try {
            if (path != null && path.length() > 5) {
                Runtime.getRuntime().totalMemory();
                Runtime.getRuntime().freeMemory();
                System.gc();

                Bitmap photoBitmap = null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                ExifInterface exif = new ExifInterface(path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                Matrix matrix = new Matrix();

                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                        matrix.preScale(true ? -1 : 1, false ? -1 : 1);
                        break;
                    case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                        matrix.preScale(false ? -1 : 1, true ? -1 : 1);
                        break;
                    default:
                        matrix.postRotate(0);
                        break;
                }
                int outWidth, outHeight;
                if (path != null) {
                    BitmapFactory.decodeFile(path, options);
                    outWidth = options.outWidth;
                    outHeight = options.outHeight;
                } else {
                    if (photoBitmap != null) {
                        outWidth = photoBitmap.getWidth();
                        outHeight = photoBitmap.getHeight();
                    } else {
                        return null;
                    }
                }
                if (path != null) {
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 0;

                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, o);

                    // The new size we want to scale to
                    final int REQUIRED_SIZE = 400;

                    // Find the correct scale value. It should be the power of 2.
                    int scale = 1;
                    while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                        scale *= 2;
                    }
                    // Decode with inSampleSize
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    Bitmap bmt = BitmapFactory.decodeStream(new FileInputStream(new File(path)), null, o2);
                    photoBitmap = Bitmap.createBitmap(bmt, 0, 0, bmt.getWidth(), bmt.getHeight(), matrix, true);
                    return photoBitmap;
                } else {
                    if (photoBitmap != null)
                        photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, outWidth, outWidth, matrix, true);
                    return photoBitmap;
                }
            }
        } catch (OutOfMemoryError e) {
            GlobalMethod.showToast(ImagecropingActivity.this, "Retry");
            finish();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
        return null;
    }

    private static Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = 1;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    void showOnCropImageView(String filepath) {
        GlobalMethod.write("====RecivefilePath" + filepath);


//       UniversalImageDownloader.loadImageFromLocalUrl(ImagecropingActivity.this, getIntent().getStringExtra("filepath"),cropImageView);

        cropImageView.setImageBitmap(resizeBitmap(filepath));
//        cropImageView.setImageUri(Uri.parse(getIntent().getStringExtra("filepath")));
    }

//    private Bitmap resizeBitmap(String path) {
//        try {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            ExifInterface exif = new ExifInterface(path);
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            Matrix matrix = new Matrix();
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    matrix.postRotate(90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    matrix.postRotate(180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    matrix.postRotate(270);
//                    break;
//                default:
//                    break;
//            }
//
//            int outWidth, outHeight;
//            if (path != null) {
//                BitmapFactory.decodeFile(path, options);
//                outWidth = options.outWidth;
//                outHeight = options.outHeight;
//
//            } else {
//                if (photoBitmap != null) {
//                    outWidth = photoBitmap.getWidth();
//                    outHeight = photoBitmap.getHeight();
//                } else {
//                    return null;
//                }
//            }
//
////            if (outHeight > outWidth) {
////                outWidth = getWallpaperDesiredMinimumHeight();
////            } else {
////                outWidth = getWallpaperDesiredMinimumWidth();
////            }
//
//            if (path != null) {
//                options.inJustDecodeBounds = false;
//                options.inSampleSize = 0;
//                photoBitmap = BitmapFactory.decodeFile(path, options);
//                if (photoBitmap != null)
////                    photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0,
////                            photoBitmap.getWidth(), photoBitmap.getHeight(), matrix, true);
//                    photoBitmap = Bitmap.createScaledBitmap(photoBitmap, outWidth / 2, outHeight / 2,  true);
////                GlobalMethod.write("====photoBitmap" + photoBitmap);
//
//                return photoBitmap;
//            } else {
//
//                if (photoBitmap != null)
//                    photoBitmap = Bitmap.createBitmap(photoBitmap, 0, 0, outWidth, outWidth, matrix, true);
//
////                GlobalMethod.write("====photoBitmap_else" + photoBitmap);
//                return photoBitmap;
//            }
//        } catch (OutOfMemoryError e) {
//            GlobalMethod.showToast(ImagecropingActivity.this, "Retry");
//            finish();
//            e.printStackTrace();
//
//        } catch (Exception e) {
//            GlobalMethod.showToast(ImagecropingActivity.this, "Image is wrong, Please select another image");
//            e.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        super.onLowMemory();
        if (photoBitmap != null) {
            photoBitmap.recycle();
        }
        System.gc();
        finish();
    }


}
