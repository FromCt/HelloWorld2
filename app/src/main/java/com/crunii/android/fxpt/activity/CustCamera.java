package com.crunii.android.fxpt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.crunii.android.fxpt.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustCamera extends Activity implements SurfaceHolder.Callback {
    private Bitmap[] bmps = new Bitmap[6];
    private Camera mCamera;
    private ImageView btnShoot, btnFinish, btnReshoot, changeCamera;
    private SurfaceView mSurfaceView;
    private SurfaceHolder holder;
    private AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback();
    // 存储在手机中的文件夹名称
    private String path = "temp";
    private Bitmap bmp = null;
    private int count;
    private String mResultPath = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.custcamera);

        mSurfaceView = (SurfaceView) findViewById(R.id.mSurfaceView);
        holder = mSurfaceView.getHolder();
        holder.addCallback(CustCamera.this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        btnShoot = (ImageView) findViewById(R.id.myButton); //拍照
        btnFinish = (ImageView) findViewById(R.id.myButton1); //完成
        btnReshoot = (ImageView) findViewById(R.id.myButton2); //重拍
        changeCamera = (ImageView) findViewById(R.id.changeCamera); //切换摄像头

        handleEvents();

    }

    private void handleEvents() {

		/*  切换摄像头  */
        changeCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                try {
            /* 打开相机 */
                    int count = Camera.getNumberOfCameras();
                    //现在是后置，变更为前置


                    if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                        mCamera.stopPreview();//停掉原来摄像头的预览
                        mCamera.release();//释放资源
                        mCamera = null;//取消原来摄像头
                        mCamera = mCamera.open(1);//打开前置摄像头
                    } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        mCamera.stopPreview();//停掉原来摄像头的预览
                        mCamera.release();//释放资源
                        mCamera = null;//取消原来摄像头
                        mCamera = mCamera.open(0);//打开后置摄像头
                    }
                    mCamera.setPreviewDisplay(holder);

                } catch (Exception exception) {
                    if (mCamera != null) {
                        mCamera.release();
                        mCamera = null;
                    }

                    btnShoot.setVisibility(View.GONE);
                    initFailed();
                }


            }
        });



		/* 按钮按下效果 */
        btnShoot.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bmps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_shoot2);
                    btnShoot.setImageBitmap(bmps[0]);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bmps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_shoot);
                    btnShoot.setImageBitmap(bmps[1]);
                }
                return false;
            }
        });
        btnFinish.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bmps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_finish2);
                    btnFinish.setImageBitmap(bmps[2]);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bmps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_finish);
                    btnFinish.setImageBitmap(bmps[3]);
                }
                return false;
            }
        });
        btnReshoot.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    bmps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_reshoot2);
                    btnReshoot.setImageBitmap(bmps[4]);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    bmps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.camera_reshoot);
                    btnReshoot.setImageBitmap(bmps[5]);
                }
                return false;
            }
        });


        btnShoot.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnShoot.setVisibility(View.GONE);
                if (bmps[0] != null && !bmps[0].isRecycled()) {
                    bmps[0].recycle();
                }

                mCamera.autoFocus(mAutoFocusCallback);
            }
        });

        btnFinish.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnFinish.setVisibility(View.GONE);
                btnReshoot.setVisibility(View.GONE);

                shootFinish();
            }
        });

        btnReshoot.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                btnShoot.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.GONE);
                btnReshoot.setVisibility(View.GONE);
                if (!bmp.isRecycled()) {
                    bmp.recycle();
                }

                stopCamera();
                initCamera();
            }
        });

    }

    private void shootFinish() {
		/* 保存文件 */
        if (bmp != null) {
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                Toast.makeText(CustCamera.this, "SD卡不存在!", Toast.LENGTH_LONG).show();
            } else {
                try {
                    File f = new File(Environment.getExternalStorageDirectory(), path);

                    if (!f.exists()) {
                        f.mkdir();
                    }
					/* 保存相片文件 */
                    String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".jpg";
                    File n = new File(f, fileName);
                    FileOutputStream bos = new FileOutputStream(n.getAbsolutePath());
                    bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                    bos.flush();
                    bos.close();

                    mResultPath = n.getAbsolutePath();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!bmp.isRecycled()) {
            bmp.recycle();
        }

        stopCamera();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        if (mResultPath != null) {
            bundle.putString("path", mResultPath);
            intent.putExtras(bundle);
            CustCamera.this.setResult(RESULT_OK, intent);
        } else {
            CustCamera.this.setResult(RESULT_CANCELED, intent);
        }
        finish();

    }

    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    @SuppressWarnings("static-access")
    @Override
    public void surfaceCreated(SurfaceHolder surfaceholder) {

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        try {
			/* 打开相机 */
            int count = Camera.getNumberOfCameras();
            //现在是后置，变更为前置
//			if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
//				mCamera.stopPreview();//停掉原来摄像头的预览
//				mCamera.release();//释放资源
//				mCamera = null;//取消原来摄像头
//			}
            mCamera = mCamera.open(0);//打开当前选中的摄像头
            mCamera.setPreviewDisplay(holder);
        } catch (Exception exception) {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }

            btnShoot.setVisibility(View.GONE);
            initFailed();
        }


//		//切换前后摄像头
//		int cameraCount = 0;
//		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//		cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
//
//		for(int i = 0; i < cameraCount; i++ ) {
//			Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
//			if(cameraPosition == 1) {
//				//现在是后置，变更为前置
//				if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
//					mCamera.stopPreview();//停掉原来摄像头的预览
//					mCamera.release();//释放资源
//					mCamera = null;//取消原来摄像头
//					mCamera = Camera.open(i);//打开当前选中的摄像头
//					try {
//						mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					cameraPosition = 0;
//				}
//			} else {//现在是前置， 变更为后置
//				if(cameraInfo.facing  == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
//					mCamera.stopPreview();//停掉原来摄像头的预览
//					mCamera.release();//释放资源
//					mCamera = null;//取消原来摄像头
//					mCamera = Camera.open(i);//打开当前选中的摄像头
//					try {
//						mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					cameraPosition = 1;
//				}
//			}
//
//		}


    }

    private void initFailed() {

        Dialog alertDialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("无法打开相机。请检查照相权限是否被禁用。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceholder, int format, int w, int h) {
		/* 相机初始化 */
        initCamera();
        count++;
        Log.i("changed", count + "times");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceholder) {
        stopCamera();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void camera_shoot() {
        if (mCamera != null) {
            mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
        }
    }

    private ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
			/* 按下快门瞬间会呼叫这里  */
        }
    };

    private PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
			/* 要处理raw data?写?否 */
        }
    };

    private PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            try {
				/* 取得相片Bitmap对象 */
                bmp = BitmapFactory.decodeByteArray(_data, 0, _data.length);

                btnShoot.setVisibility(View.GONE);
                //mButton1.setVisibility(View.VISIBLE);
                //mButton2.setVisibility(View.VISIBLE);
                //TODO 不显示重拍和确认按钮，直接结束
                shootFinish();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public final class AutoFocusCallback implements
            Camera.AutoFocusCallback {
        public void onAutoFocus(boolean focused, Camera camera) {
            if (focused) {
                camera_shoot();
            } else
                btnShoot.setVisibility(View.VISIBLE);
        }
    }

    ;

    private void initCamera() {
        if (mCamera != null) {
            try {
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPictureFormat(PixelFormat.JPEG);
                parameters.setRotation(0);

                //parameters.setPictureSize(720, 480);
                List<Camera.Size> pszize = parameters.getSupportedPictureSizes();
                if (null != pszize && 0 < pszize.size()) {
                    int height[] = new int[pszize.size()];
                    Map<Integer, Integer> map = new HashMap<Integer, Integer>();
                    for (int i = 0; i < pszize.size(); i++) {
                        Camera.Size size = (Camera.Size) pszize.get(i);
                        int sizeheight = size.height;
                        int sizewidth = size.width;
                        height[i] = sizeheight;
                        map.put(sizeheight, sizewidth);
                    }
                    Arrays.sort(height);

                    int finalWidth = display.getWidth();
                    int finalHeight = display.getHeight();
                    for (int i = 0; i < height.length; i++) {
                        if (height[i] >= 600) {
                            finalWidth = map.get(height[i]);
                            finalHeight = height[i];
                            break;
                        }
                    }
                    parameters.setPictureSize(finalWidth, finalHeight);
                } else {
                    parameters.setPictureSize(display.getWidth(), display.getHeight());
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();
                mCamera.cancelAutoFocus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopCamera() {
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
