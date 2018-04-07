package com.bs.youmin.imp;



import com.bs.youmin.entity.WallpaperApiModel;
import com.bs.youmin.entity.WeatherApiModel;
import com.bs.youmin.entity.WeatherEveryDayApiModel;
import com.bs.youmin.entity.WeatherLifeApiModel;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.entity.YBanner;
import com.bs.youmin.entity.YPhoto;
import com.bs.youmin.model.ResultModel;
import com.bs.youmin.model.TokenModel;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiImp {

    /**
     * 获取相册喜欢排名
     * @param page
     * @return
     */
    @POST("yalbum/ranking")
    Call<List<YAlbum>> getAlbumRanking(@Query("page")int page);

    /**
     * 获取相册内照片
     * @param aId
     * @return
     */
    @POST("yalbum/photo")
    Call<List<YPhoto>> getAlbumPhoto(@Query("aId")String aId,@Query("page")int page);

    /**
     * 登录
     * @param identifier
     * @param username
     * @param password
     * @return
     */
    @POST("sys/login")
    Call<ResultModel<TokenModel>> login(@Query("identifier")String identifier, @Query("username")String username, @Query("password")String password);

    /**
     * 登出
     * @return
     */
    @DELETE("sys/logout")
    Call<ResultModel<TokenModel>> logout(@Header("authorization") String authorization);

    /**
     * 获取我的相册
     * @param page
     * @return
     */
    @POST("yalbum/myAlbum")
    Call<List<YAlbum>> getMyAlbum(@Query("page")int page,@Header("authorization") String authorization);

    /**
     * 获取首页轮播图
     * @return
     */
    @POST("sys/getBanner")
    Call<List<YBanner>> getBanner();

    /**
     * 获取所有公开的相册
     * @param page
     * @return
     */
    @POST("yalbum/allPublicAlbum")
    Call<List<YAlbum>> allPublicAlbum(@Query("page")int page);

    /**
     * 创建相册
     * @param content
     * @param isPrivate
     * @param image_size
     * @param authorization
     * @param files
     * @return
     */
    @POST("yalbum/createAlbum")
    Call<ResultModel> createAlbum(@Header("authorization") String authorization,@Query("content")String content,@Query("isPrivate")boolean isPrivate,@Query("image_size")int image_size,@Body RequestBody files);

    //获取爱壁纸接口
    @GET("category/homePage")
    Call<WallpaperApiModel> getHomePage();

    //获取心知天气
    @GET("v3/weather/now.json?")
    Call<WeatherApiModel> getWeatherApi(@Query("key") String key, @Query("location") String city);

    //获取未来三天的天气
    @GET("v3/weather/daily.json?")
    Call<WeatherEveryDayApiModel> getWeatherEveryDayApi(@Query("key") String key, @Query("location") String city);

    //获取生活指数
    @GET("v3/life/suggestion.json?")
    Call<WeatherLifeApiModel> getWeatherLifeApi(@Query("key") String key, @Query("location") String city);

}
