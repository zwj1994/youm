package com.bs.youmin.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bs.youmin.R;
import com.bs.youmin.entity.Ip;
import com.bs.youmin.entity.User;
import com.bs.youmin.imp.ApiImp;
import com.bs.youmin.model.ResultModel;
import com.bs.youmin.model.ResultStatus;
import com.bs.youmin.model.TokenModel;
import com.bs.youmin.util.L;
import com.bs.youmin.util.SaveUserUtil;
import com.bs.youmin.util.encryption.des.AndroidDes3Util;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*
 *  项目名：  YouMin
 *  包名：    com.bs.youmin.fragment
 *  文件名:   LoginFragment
 *  创建者:   ZWJ
 *  创建时间:  2018/03/23 19:48
 *  描述：    登录
 */
public class LoginFragment extends Fragment {

    private Button login_btn;
    private EditText login_input_password;
    private EditText login_input_username;
    private Button forgive_pwd;
    private ApiImp apiImp;
    private OnButtonClick onButtonClick;//2、定义接口成员变量
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_login, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        login_btn = (Button) view.findViewById(R.id.login_btn);
        login_input_username = (EditText) view.findViewById(R.id.login_input_username);
        login_input_password = (EditText) view.findViewById(R.id.login_input_password);
        forgive_pwd = (Button) view.findViewById(R.id.forgive_pwd);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = login_input_username.getText().toString().trim();
                String password = login_input_password.getText().toString().trim();
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getActivity(),"用户名不能为空",Toast.LENGTH_SHORT).show();
                    showInput(login_input_username);
                    return;
                }else if(TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    showInput(login_input_password);
                    return;
                }
                final User user = SaveUserUtil.loadAccount(getActivity());
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                apiImp = retrofit.create(ApiImp.class);
                try {
                    username = AndroidDes3Util.encode(username,user.getKey());
                    password = AndroidDes3Util.encode(password,user.getKey());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Call<ResultModel<TokenModel>> call = apiImp.login(user.getIdentifier(), username,password);
                //请求数据
                call.enqueue(new Callback<ResultModel<TokenModel>>() {
                    @Override
                    public void onResponse(Call<ResultModel<TokenModel>> call, Response<ResultModel<TokenModel>> response) {
                        if (response.isSuccessful()) {
                            ResultModel resultModel =  response.body();
                            if(ResultStatus.SUCCESS.getCode() == response.body().getCode()){
                                TokenModel tokenModel = response.body().getContent();
                                user.setToken(tokenModel.getToken());
                                user.setUsername(tokenModel.getUserId());
                                user.setHeaderImg(tokenModel.getHeaderImg());
                                user.setSign(tokenModel.getSign());
                                user.setUid(tokenModel.getuId());
                                SaveUserUtil.saveAccount(getActivity(),user);
                                Toast.makeText(getActivity(), resultModel.getMessage(),Toast.LENGTH_SHORT).show();

                                if(onButtonClick!=null){
                                    onButtonClick.onClick(login_btn);
                                }
                            }else{
                                Toast.makeText(getActivity(), resultModel.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResultModel<TokenModel>> call, Throwable t) {
                        Toast.makeText(getActivity(), "请求失败，请稍后再试",Toast.LENGTH_SHORT).show();
                        L.i(t.toString());
                    }
                });
            }
        });

        forgive_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final User user = SaveUserUtil.loadAccount(getActivity());
                Retrofit retrofit = new Retrofit.Builder().baseUrl(Ip.SERVER_URL)
                        .addConverterFactory(GsonConverterFactory.create()).build();
                apiImp = retrofit.create(ApiImp.class);
                Call<ResultModel<TokenModel>> call = apiImp.logout(user.getUsername()+"_"+user.getToken());
                //请求数据
                call.enqueue(new Callback<ResultModel<TokenModel>>() {
                    @Override
                    public void onResponse(Call<ResultModel<TokenModel>> call, Response<ResultModel<TokenModel>> response) {
                        if (response.isSuccessful()) {
                            if(ResultStatus.SUCCESS.getCode() == response.body().getCode()){
                                Toast.makeText(getActivity(), "登出成功",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResultModel<TokenModel>> call, Throwable t) {
                        Toast.makeText(getActivity(), "请求失败，请稍后再试",Toast.LENGTH_SHORT).show();
                        L.i(t.toString());
                    }
                });
            }
        });
    }

    /**
     * 弹出输入法
     * @param view
     */
    private void showInput(View view){
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,0);
    }
    //定义接口变量的get方法
    public OnButtonClick getOnButtonClick() {
        return onButtonClick;
    }
    //定义接口变量的set方法
    public void setOnButtonClick(OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }
    //1、定义接口
    public interface OnButtonClick{
        public void onClick(View view);
    }


}
