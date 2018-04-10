package com.bs.youmin.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.User;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.model.ResultModel;
import com.bs.youmin.model.ResultStatus;
import com.bs.youmin.util.DateUtil;
import com.bs.youmin.util.ImageCompressUtils;
import com.bs.youmin.util.L;
import com.bs.youmin.util.PhotoSelectedHelper;
import com.bs.youmin.util.SaveUserUtil;
import com.bs.youmin.util.upload.DefaultProgressListener;
import com.bs.youmin.util.upload.UploadFileRequestBody;
import com.bs.youmin.util.upload.UploadService;
import com.bs.youmin.view.UploadImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.adapter.MainGridAdapter;
import me.nereo.multi_image_selector.bean.Image;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import uk.co.senab.photoview.PhotoViewAttacher;

public class CreateAlbumActivity extends Activity implements View.OnClickListener, PopupWindow.OnDismissListener, MainGridAdapter.Callback {
    private static final int REQUEST_IMAGE = 2;
    public static final int RESULT_OK = -1;
    private ApiImp apiImp;
    LinearLayout ll_back;
    LinearLayout ll_enter;
    boolean cb_bool = false;
    EditText et_content;
    PhotoViewAttacher mAttacher;
    RadioButton rbPublic;
    RadioButton rbPrivate;
    CheckBox cbIsYuanTu;


    GridView mGridView;
    MainGridAdapter mainGridAdapter;
    public ArrayList<String> mSelectPath = new ArrayList<>();
    public boolean isYuantu;
    PopupWindow popupWindow;
    ImageView popImageView;

    String str_content;
    String location;
    PhotoSelectedHelper mPhotoSelectedHelper;
    Intent intent;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_create_album);
        mPhotoSelectedHelper = new PhotoSelectedHelper(this);
        intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        initialView();
        initialPopups();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initialView() {
        ll_back = (LinearLayout) findViewById(R.id.luntan_state_layout_back);
        ll_enter = (LinearLayout) findViewById(R.id.luntan_state_layout_enter);
        rbPublic = (RadioButton) findViewById(R.id.rbPublic);
        rbPrivate = (RadioButton) findViewById(R.id.rbPrivate);
        cbIsYuanTu = (CheckBox)findViewById(R.id.isYuanTu);

        et_content = (EditText) findViewById(R.id.luntan_state_edittext_content);
        mGridView = (GridView) findViewById(R.id.luntan_state_gridview);
        mainGridAdapter = new MainGridAdapter(CreateAlbumActivity.this, this, 9);
        mGridView.setAdapter(mainGridAdapter);
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onGlobalLayout() {

                final int width = mGridView.getWidth();
                final int height = mGridView.getHeight();

                final int desireSize = getResources().getDimensionPixelOffset(me.nereo.multi_image_selector.R.dimen.image_size);
                final int numCount = width / desireSize;
                final int columnSpace = getResources().getDimensionPixelOffset(me.nereo.multi_image_selector.R.dimen.space_size);
                int columnWidth = (width - columnSpace * (numCount - 1)) / numCount;
                mainGridAdapter.setItemSize(columnWidth);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == mSelectPath.size()) {
                    Intent intent = new Intent(CreateAlbumActivity.this, MultiImageSelectorActivity.class);
                    // 是否显示拍摄图片
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                    // 最大可选择图片数量
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
                    // 选择模式
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 1);
                    // 默认选择
                    if (mSelectPath != null && mSelectPath.size() > 0) {
                        intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                    }
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    Picasso.with(CreateAlbumActivity.this).load(new File(mSelectPath.get(position))).into(popImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            mAttacher = new PhotoViewAttacher(popImageView);
                            popImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (popupWindow != null && popupWindow.isShowing()) {
                                        popupWindow.dismiss();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
                    popupWindow.showAtLocation(LayoutInflater.from(CreateAlbumActivity.this).inflate(R.layout.activity_create_album, null)
                            , Gravity.CENTER, 0, 0);
                }
            }
        });
        ll_back.setOnClickListener(this);
        ll_enter.setOnClickListener(this);
    }

    /**
     * 初始化popupwindow
     */
    private void initialPopups() {
        popImageView = new ImageView(this);
        // popImageView.setPadding(50, 50, 50, 50);
        popupWindow = new PopupWindow(popImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));

    }

    /**
     * 组装上传参数
     * @param content
     * @param isPrivate
     */
    private void assemblyParameters(String content,boolean isPrivate){

        final User user = SaveUserUtil.loadAccount(CreateAlbumActivity.this);
        String authorization = user.getUsername()+"_"+user.getToken();

        Map<String, Object> optionMap = new HashMap<>();
        optionMap.put("content",content) ;
        optionMap.put("isPrivate",isPrivate) ;

        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for(int i = 0;i < mSelectPath.size();i++){
            UploadFileRequestBody fileRequestBody = null;
            if(cbIsYuanTu.isChecked()){
                File file = new File(mSelectPath.get(i));
                fileRequestBody = new UploadFileRequestBody(file, new DefaultProgressListener(h,i));
            }else{
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageCompressUtils.getimage(mSelectPath.get(i)).compress(Bitmap.CompressFormat.JPEG,
                        80, stream);
                byte [] b = stream.toByteArray();
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),b);
                fileRequestBody = new UploadFileRequestBody(requestBody, new DefaultProgressListener(h,i));
            }
            requestBodyMap.put("file\"; filename=\"" + user.getUsername() +"_" + DateUtil.getCurDateStr() + "_" + DateUtil.genCurrentTimeMillisame() + ".jpg", fileRequestBody);
        }
        uploadImg(optionMap,requestBodyMap,authorization);
    }

    /**
     * 上传相片
     * @param optionMap
     * @param requestBodyMap
     * @param authorization
     */
    private void uploadImg( Map<String, Object> optionMap,Map<String, RequestBody> requestBodyMap,String authorization){

        OkHttpClient client = new OkHttpClient.Builder().
                connectTimeout(60, TimeUnit.SECONDS).
                readTimeout(60, TimeUnit.SECONDS).
                writeTimeout(60, TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();

        ApiImp apiImp = retrofit.create(ApiImp.class);
        Call<ResultModel> call = apiImp.createAlbum(optionMap, requestBodyMap,authorization);
        call.enqueue(new retrofit2.Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                if (response.isSuccessful()) {
                    if(ResultStatus.SUCCESS.getCode() == response.body().getCode()){
                        Toast.makeText(CreateAlbumActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        Intent intents = new Intent(CreateAlbumActivity.this, MainActivity.class);
                        setResult(666, intents);
                        finish();
                    }else {
                        Toast.makeText(CreateAlbumActivity.this, "发布失败", Toast.LENGTH_SHORT).show();
                        ll_enter.setEnabled(true);
                    }
                }
            }
            @Override
            public void onFailure(Call<ResultModel> call, Throwable t) {
                L.i(t.toString());
                ll_enter.setEnabled(true);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.luntan_state_layout_back:
                finish();
                break;
            case R.id.luntan_state_layout_enter:
                upData();
                break;
        }

    }

    private void upData() {
        str_content = et_content.getText().toString();
        if (str_content.length() < 1) {
            Toast.makeText(CreateAlbumActivity.this, "相册名称不能为空", Toast.LENGTH_SHORT).show();
            return;

        }
        if (mSelectPath == null || mSelectPath.size() == 0) {
            Toast.makeText(CreateAlbumActivity.this, "您还没有选择相片", Toast.LENGTH_SHORT).show();
            return;
        }
        Message m = h.obtainMessage(999);
        h.sendMessage(m);

    }


    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 999) {
                assemblyParameters(str_content,rbPrivate.isChecked());
            }else{
                System.out.println("===============msg.arg1"+msg.arg1);
                MainGridAdapter.Viewholder viewholder = mainGridAdapter.getViewholders(msg.arg1);
                ((UploadImageView)viewholder.getImage()).updatePercent(msg.what);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                isYuantu = data.getBooleanExtra("YUANTU", false);
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mainGridAdapter.setData(toImages(mSelectPath));
            }
        }
    }

    @Override
    public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow()
                .getAttributes();
        lp.alpha = 1f;
        getWindow().setAttributes(lp);
    }

    private List<Image> toImages(ArrayList<String> mmSelectPath) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < mmSelectPath.size(); i++) {
            Image image = new Image();
            image.path = mmSelectPath.get(i);
            images.add(image);
        }
        return images;
    }

    public void dataDelete(String str) {
        if (str == null) {
            return;
        } else {
            if (mSelectPath.contains(str)) {
                mSelectPath.remove(str);
                mainGridAdapter.setData(toImages(mSelectPath));
            }
        }
    }

    @Override
    public void callbackDelete(String str) {
        dataDelete(str);
    }

    @Override
    public void onBackPressed() {
        if (popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            finish();
        }
    }

}
