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
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.model.ResultModel;
import com.bs.youmin.model.ResultStatus;
import com.bs.youmin.model.TokenModel;
import com.bs.youmin.util.Base64Coder;
import com.bs.youmin.util.ImageCompressUtils;
import com.bs.youmin.util.L;
import com.bs.youmin.util.PhotoSelectedHelper;
import com.bs.youmin.util.SaveUserUtil;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.adapter.MainGridAdapter;
import me.nereo.multi_image_selector.bean.Image;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 13);
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
        //rl_location.setOnClickListener(this);

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
        Message m = h.obtainMessage(1);
        h.sendMessage(m);

    }

    /**
     * 创建相册
     * @param content
     * @param isPrivate
     * @param image_size
     * @param authorization
     * @param files
     */
    private void createAlbum(String content,boolean isPrivate,int image_size,String authorization,String files){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        apiImp = retrofit.create(ApiImp.class);
        RequestBody files_body = RequestBody.create(MediaType.parse("application/json"),files);
        Call<ResultModel> call = apiImp.createAlbum(authorization,content,isPrivate,image_size,files_body);
        //请求数据
        call.enqueue(new retrofit2.Callback<ResultModel>() {
            @Override
            public void onResponse(Call<ResultModel> call, Response<ResultModel> response) {
                L.i(response.isSuccessful()+"");
                if (response.isSuccessful()) {
                    if(ResultStatus.SUCCESS.getCode() == response.body().getCode()){
                        Toast.makeText(CreateAlbumActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        Intent intents = new Intent(CreateAlbumActivity.this, MainActivity.class);
                        setResult(200, intents);
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

    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                final User user = SaveUserUtil.loadAccount(CreateAlbumActivity.this);
                String authorization = user.getUsername()+"_"+user.getToken();
                ll_enter.setEnabled(false);
                int image_size = 0;
                List<FileModel> list= new ArrayList<>();
                String strs = "";
                if (mSelectPath != null && mSelectPath.size() > 0) {
                    image_size = mSelectPath.size();
                    for (int i = 0; i < mSelectPath.size(); i++) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ImageCompressUtils.getimage(mSelectPath.get(i)).compress(Bitmap.CompressFormat.JPEG,
                                80, stream);
                        byte[] b = stream.toByteArray();
                        // 将图片流以字符串形式存储下来
                        String file = new String(Base64Coder.encodeLines(b));
                        String filename = user.getUsername()+ "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+"_"+  genImageName() + ".jpg";
                        FileModel model = new FileModel();
                        model.setFile(file);
                        model.setName(filename);
                        list.add(model);
                    }
                }
                Gson gson = new Gson();
                createAlbum(str_content,rbPrivate.isChecked(),image_size,authorization,gson.toJson(list));
            }
        }
    };

    class FileModel{
        private String file;
        private String name;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static String genImageName() {
        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //long millis = System.nanoTime();
        //加上三位随机数
        Random random = new Random();
        int end3 = random.nextInt(999);
        //如果不足三位前面补0
        String str = millis + String.format("%03d", end3);

        return str;
    }

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
