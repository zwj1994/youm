package com.bs.youmin.imp;



import com.bs.youmin.entity.WallpaperApiModel;
import com.bs.youmin.entity.WeatherApiModel;
import com.bs.youmin.entity.WeatherEveryDayApiModel;
import com.bs.youmin.entity.WeatherLifeApiModel;
import com.bs.youmin.entity.YAlbum;
import com.bs.youmin.entity.YPhoto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiImp {

    /**
     * 获取相册喜欢排名
     * @param page
     * @return
     */
    @GET("yalbum/ranking")
    Call<List<YAlbum>> getAlbumRanking(@Query("page")int page);

    /**
     * 获取相册内照片
     * @param aId
     * @return
     */
    @GET("yalbum/photo")
    Call<List<YPhoto>> getAlbumPhoto(@Query("aId")String aId,@Query("page")int page);

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
