package com.fvstrange.eyeful.core;

import com.fvstrange.eyeful.data.GirlData;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hasee on 2017/10/20.
 */

public interface DataService
{
    @GET("data/福利/{pagesize}/{page}")
    Observable<GirlData> getGirlData(@Path("pagesize") int pagesize, @Path("page") int page);
}
