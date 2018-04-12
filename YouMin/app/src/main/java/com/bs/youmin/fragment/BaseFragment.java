package com.bs.youmin.fragment;


import android.support.v4.app.Fragment;
import android.widget.Toast;

/**
 * Created by Mrzhu on 2018/4/12.
 */

public class BaseFragment extends Fragment {


    /**
     * 显示提示信息
     * @param msg
     */
    protected void showToast(String msg){
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
}
