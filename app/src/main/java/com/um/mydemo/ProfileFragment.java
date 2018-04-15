package com.um.mydemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.um.mydemo.utils.PermissionsChecker;
import com.um.mydemo.view.CircleImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Sunyuan on 2016/12/26.
 */

public class ProfileFragment extends Fragment {
    public static final String TAG = "PhotoGalleryFragment";
    public static final String PHOTO_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/tal/" + "temp.jpg";
    private static final String PACKAGE_URL_SCHEME = "package:";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 0;
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    @BindView(R.id.imageButton_profile)
    CircleImageView mProfileImage;

    private RelativeLayout layout_choose;
    private RelativeLayout layout_photo;
    private RelativeLayout layout_close;

    private View mRootView;
    protected int mScreenWidth;
    Uri mContentUri;
    PermissionsChecker mPermissionsChecker;
    boolean isRequireCheck;

    /**
     * 定义三种状态
     */
    private static final int REQUESTCODE_PIC = 1;//相册
    private static final int REQUESTCODE_CAM = 2;//相机
    private static final int REQUESTCODE_CUT = 3;//图片裁剪

    private Bitmap mBitmap;
    private File mFile;
    private Context mContext;
    private PopupWindow avatorPop;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mPermissionsChecker = new PermissionsChecker(mContext);
        isRequireCheck = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.profile_settings_page, container, false);
        ButterKnife.bind(this, mRootView);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (isRequireCheck) {
//            //权限没有授权，进入授权界面
//            if(mPermissionsChecker.judgePermissions(PERMISSIONS)){
//                ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_REQUEST_CODE);
//            }
//        }else{
//            isRequireCheck = true;
//        }
//        requestPermissions();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Background thread destroyed");
    }

    @OnClick(R.id.imageButton_profile)
    void callImage() {
        showMyDialog();
    }


    private void showMyDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_show_dialog,
                null);
        layout_choose = (RelativeLayout) view.findViewById(R.id.layout_choose);
        layout_photo = (RelativeLayout) view.findViewById(R.id.layout_photo);
        layout_close = (RelativeLayout) view.findViewById(R.id.layout_close);

        layout_choose.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));
        layout_photo.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.pop_bg_press));
        layout_close.setBackgroundColor(getResources().getColor(
                R.color.base_color_text_white));


        layout_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_photo.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));

                requestPermissions();
//                openCamera();
            }
        });

        layout_choose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_choose.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_close.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                openPic();

            }
        });

        layout_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_photo.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                layout_close.setBackgroundDrawable(getResources().getDrawable(
                        R.drawable.pop_bg_press));
                layout_choose.setBackgroundColor(getResources().getColor(
                        R.color.base_color_text_white));
                avatorPop.dismiss();
            }
        });



        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        avatorPop = new PopupWindow(view, mScreenWidth, 200);
        avatorPop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    avatorPop.dismiss();
                    return true;
                }
                return false;
            }
        });

        avatorPop.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        avatorPop.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        avatorPop.setTouchable(true);
        avatorPop.setFocusable(true);
        avatorPop.setOutsideTouchable(true);
        avatorPop.setBackgroundDrawable(new BitmapDrawable());
        // 动画效果 从底部弹起
        avatorPop.setAnimationStyle(R.style.Animations_GrowFromBottom);
        avatorPop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
    }

    /**
     * 打开相册
     */
    private void openPic() {
        Intent picIntent = new Intent(Intent.ACTION_PICK,null);
        picIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        startActivityForResult(picIntent,REQUESTCODE_PIC);
    }

    /**
     * 调用相机
     */
    private void openCamera() {

        File file = new File(PHOTO_DIR);
        if (!file.exists()){
            file.getParentFile().mkdirs();
        }

        // 启动系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri mImageCaptureUri;
        // 判断7.0android系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            mContentUri = FileProvider.getUriForFile(mContext,
                    "com.tal.abctime.fileProvider",
                    file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mContentUri);
        } else {
//            mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
            mImageCaptureUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        }
        startActivityForResult(intent, REQUESTCODE_CAM);
        avatorPop.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("sy__test","resultCode:"+resultCode+",requestCode="+requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_CAM:
//                    startPhotoZoom(Uri.fromFile(mFile));
                    Uri pictur;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果是7.0android系统
                        pictur = mContentUri;
                    } else {
                        pictur = Uri.fromFile(new File(PHOTO_DIR));
                    }
                    startPhotoZoom(pictur);
                    break;
                case REQUESTCODE_PIC:
                    if (data == null || data.getData() == null) {
                        return;
                    }
                    startPhotoZoom(data.getData());
                    break;
                case REQUESTCODE_CUT:
                    if (data != null) {
                        setPicToView(data);
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

        private void setPicToView(Intent data) {
            Bundle bundle =  data.getExtras();
            if (bundle != null){
                //这里也可以做文件上传
                mBitmap = bundle.getParcelable("data");
                mProfileImage.setImageBitmap(mBitmap);
            }
        }

        /**
         * 打开系统图片裁剪功能
         * @param uri
         */
        private void startPhotoZoom(Uri uri) {
            Log.i("sy__test","startPhotoZoom");
            Intent intent = new Intent("com.android.camera.action.CROP");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri,"image/*");
            intent.putExtra("crop",true);
            intent.putExtra("aspectX",1);
            intent.putExtra("aspectY",1);
            intent.putExtra("outputX",300);
            intent.putExtra("outputY",300);
            intent.putExtra("scale",true); //黑边
            intent.putExtra("scaleUpIfNeeded",true); //黑边
            intent.putExtra("return-data",true);
            intent.putExtra("noFaceDetection",true);
            startActivityForResult(intent,REQUESTCODE_CUT);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(),
                            "READ_CONTACTS Denied",
                            Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
        }

    }

      public void requestPermissions() {
          Log.i("sy__test","requestPermissions");
          if (ContextCompat.checkSelfPermission(getActivity(),
                  Manifest.permission.WRITE_EXTERNAL_STORAGE)
                  != PackageManager.PERMISSION_GRANTED) {
              Log.i("sy__test","requestPermissions2");
              // Should we show an explanation?
              if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                      Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                  // Show an expanation to the user *asynchronously* -- don't block
                  // this thread waiting for the user's response! After the user
                  // sees the explanation, try again to request the permission.

              } else {

                  ActivityCompat.requestPermissions(getActivity(),
                          new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                          MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

              }
          }
      }

}
