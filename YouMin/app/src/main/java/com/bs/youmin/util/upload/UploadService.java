package com.bs.youmin.util.upload;


import java.util.Map;

import retrofit2.Call;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;

/**
 * Created by guoli on 2016/7/4.
 * 上传文件接口
 */
public interface UploadService {

    @POST("yalbum/testuploadimg")
    @Multipart
    Call<ResponseBody> uploadFileInfo(@QueryMap Map<String, Object> options,
                                      @PartMap Map<String, RequestBody> externalFileParameters,@Header("authorization") String authorization) ;

}
