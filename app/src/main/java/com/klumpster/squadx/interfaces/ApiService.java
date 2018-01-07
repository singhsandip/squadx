package com.klumpster.squadx.interfaces;

import com.klumpster.squadx.model.ChartResponse;
import com.klumpster.squadx.model.PriceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by BOX on 06-01-2018.
 */

public interface ApiService {

    @GET("v1/ticker/bitcoin/")
    Call<List<PriceResponse>> getBitCoinPrice();

    @GET("charts/market-price?timespan=4months&format=json&cors=true&includeLastPoint=true")
    Call<ChartResponse> getChartData();

}
