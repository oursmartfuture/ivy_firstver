package com.ivy;

import android.*;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ivy.R;
import com.globalclasses.Constant;
import com.globalclasses.GlobalMethod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Class Name: ChooseImageActivity.Class
 * Class description: This class contains methods and classes for selecting the image for uploading it to the server.
 * Either through gallery or through camera.
 * Created by Sobhit Gupta(00253)
 * Created by sing sys-118 on 7/31/2015.
 */

public class ChooseImageActivity extends Activity implements OnClickListener {
    private static final int REQUEST_CAMERA = 12345;
    private static final int REQUEST_EXTERNAL_STORAGE = 123;
    private static final int REQUEST_CAMERA_EXTERNAL_STORAGE = 12;
    private Button btnFromGallery, btnFromCamera, cancel;
    LinearLayout back_id;
    String filePath = "";
    private String imgPath;
    Uri uri = null;
    View view2;

    private File imageFile;
    private Uri imageUri;
    String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_image_dialog);
        findViewById();
        registerListners();
        view2 = (View) findViewById(android.R.id.content);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ChooseImageActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    private void registerListners() {
        btnFromGallery.setOnClickListener(this);
        btnFromCamera.setOnClickListener(this);
        back_id.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void findViewById() {
        btnFromGallery = (Button) findViewById(R.id.btnFromGallery);
        btnFromCamera = (Button) findViewById(R.id.btnFromCamera);
        back_id = (LinearLayout) findViewById(R.id.back_id);
        cancel = (Button) findViewById(R.id.cancel);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFromGallery:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(ChooseImageActivity.this)) {
                        if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE, getString(R.string.storage_permission))) {
                            initGallery();
                        }

////                            if (!checkWriteExternalPermission()) {
////                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
////                        } else {
////                            initGallery();
////                        }
                    } else {
                        initGallery();
                    }
                } else {
                    initGallery();
                }

                break;
            case R.id.btnFromCamera:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(ChooseImageActivity.this)) {
                        if (mayRequestPermission(CAMERA, REQUEST_CAMERA, getString(R.string.camera_permission))) {
                            if (mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.storage_permission))) {
                                initCamera();
                            }
                        }
//                            if (!checkCameraPermission()) {
//                                requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
//                            } else {
//                                if (!checkWriteExternalPermission()) {
//                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_EXTERNAL_STORAGE);
//                                } else {
//                                    initCamera();
//                                }
//                            }
                    } else {
                        initCamera();
                    }
                } else {
                    initCamera();
                }

                break;
            case R.id.cancel:
                finish();
//				ChooseImageActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                break;
            case R.id.back_id:
                finish();
//				ChooseImageActivity.this.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
                break;
            default:
                break;
        }
    }

    private void initGallery() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ChooseImageActivity.this, "No Image In Gallery.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkWriteExternalPermission() {
        String permission = WRITE_EXTERNAL_STORAGE;
        int res = ChooseImageActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private boolean checkCameraPermission() {
        String permission = android.Manifest.permission.CAMERA;
        int res = ChooseImageActivity.this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void initCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.CROP_IMAGE && resultCode == RESULT_OK && data != null) {
            try {
                Intent in = new Intent();
                GlobalMethod.write("==222==saveimage_crop" + data.getStringExtra("filepath"));
                in.putExtra("filepath", data.getStringExtra("filepath"));
                setResult(RESULT_OK, in);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (Constant.CAMERA_REQUEST_CODE == requestCode && resultCode == RESULT_OK && !TextUtils.isEmpty(getImagePath())) {
            filePath = getImagePath();
            GlobalMethod.write("====capturefilepath" + filePath);
            Intent intent = new Intent(ChooseImageActivity.this, ImagecropingActivity.class);
            intent.putExtra("filepath", filePath);
            startActivityForResult(intent, Constant.CROP_IMAGE);
        } else if (requestCode == Constant.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            imagePath = getRealPathFromURI(uri);
            GlobalMethod.write("====selectedimagepath" + imagePath);
            Intent intent = new Intent(ChooseImageActivity.this, ImagecropingActivity.class);
            intent.putExtra("filepath", imagePath);
            startActivityForResult(intent, Constant.CROP_IMAGE);
        }
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

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = ChooseImageActivity.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    private Uri setImageUri() {
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "Ivy" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.filePath = file.getAbsolutePath();
        return imgUri;
    }

    private String getImagePath() {
        return filePath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mayRequestPermission(WRITE_EXTERNAL_STORAGE, REQUEST_CAMERA_EXTERNAL_STORAGE, getString(R.string.storage_permission));
//                    initCamera();
//                }
            } else {
                Log.e("Permission", "Denied");
            }
        } else if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGallery();
            } else {
                Log.e("Permission", "Denied");
            }
        } else if (requestCode == REQUEST_CAMERA_EXTERNAL_STORAGE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    Log.e("Permission", "Denied");
                }
            }
        }


    }

    /**
     * @param permission   that has to be accessed in the app
     * @param requestCode  for callback purpose
     * @param errorMessage to display in case of error
     * @return
     */
    public boolean mayRequestPermission(String permission, int requestCode, String errorMessage) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            GlobalMethod.write("====<Build.VERSION_CODES.M");
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            GlobalMethod.write("====>= Build.VERSION_CODES.M");
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                GlobalMethod.write("====PERMISSION_GRANTED");
                return true;
            } else if (shouldShowRequestPermissionRationale(permission)) {
                GlobalMethod.write("====ifPermissionRationale");
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                requestPermissions(new String[]{permission}, requestCode);
//                GlobalMethod.showCustomToastInCenter(this, errorMessage);
                GlobalMethod.write("====elsePermission");
//                return true;
            }
        }
        return false;
    }

    /*
    end of class
	 */

}