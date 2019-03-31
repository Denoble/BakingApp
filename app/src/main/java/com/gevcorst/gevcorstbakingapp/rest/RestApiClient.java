package com.gevcorst.gevcorstbakingapp.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestApiClient {
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
  //public static final String RecipeList = "/topher/2017/May/59121517_baking/baking.json";//put your end point here

    private static Retrofit retrofit = null;
    public static RestApiInterface getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
         return retrofit.create(RestApiInterface.class);
    }
}
