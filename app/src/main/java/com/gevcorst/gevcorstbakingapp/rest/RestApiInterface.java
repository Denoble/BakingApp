package com.gevcorst.gevcorstbakingapp.rest;



import com.gevcorst.gevcorstbakingapp.models.Recipe;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApiInterface {
    //https://newsapi.org/v2/everything?q=movies&apiKey=079dac74a5f94ebdb990ecf61c8854b7&pageSize=20&page=2


    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<ArrayList<Recipe>> fetchRecipes(
            @Query("source") String source,
            @Query("apiKey") String apiKey
    );
}
