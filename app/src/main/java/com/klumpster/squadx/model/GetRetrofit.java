package com.klumpster.squadx.model;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by BOX on 06-01-2018.
 */

public class GetRetrofit {
    private static final String BASE_URL_PRICE = "https://api.coinmarketcap.com/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        if (retrofit == null){
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL_PRICE)
                    .addConverterFactory(GsonConverterFactory.create());

            retrofit = builder.build();
        }
        return retrofit;
    }
}
