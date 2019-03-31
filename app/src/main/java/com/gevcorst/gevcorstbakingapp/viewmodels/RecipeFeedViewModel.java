package com.gevcorst.gevcorstbakingapp.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;


import com.gevcorst.gevcorstbakingapp.models.Recipe;
import com.gevcorst.gevcorstbakingapp.rest.RestApiClient;
import com.gevcorst.gevcorstbakingapp.rest.RestApiInterface;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeFeedViewModel extends AndroidViewModel {
  private MutableLiveData<ArrayList<Recipe>> recipeList;
  private  Recipe recipe;

    public RecipeFeedViewModel(@NonNull Application application) {
        super(application);
    }
    private void loadRecipeList(){

        RestApiInterface apiService = RestApiClient.getClient();
        Call<ArrayList<Recipe>> call = apiService.fetchRecipes(null,null);
        call.enqueue(new Callback<ArrayList<Recipe>>() {
            @Override
            public void onResponse(Call<ArrayList<Recipe>> call, Response<ArrayList<Recipe>> response) {
            ArrayList<Recipe> recipes =  new ArrayList<>();
                recipes = response.body();
                recipeList.setValue(recipes);
            }

            @Override
            public void onFailure(Call<ArrayList<Recipe>> call, Throwable t) {
                Log.e(getClass().getSimpleName(), t.toString());
            }
        });
    }
    public MutableLiveData<ArrayList<Recipe>> getRecipeList()
    {
        if(recipeList == null){
            recipeList =  new MutableLiveData<>();
            loadRecipeList();
        }
        return recipeList;
    }
    public Recipe getRecipeFromRecipeListAt(int index){
        if(recipeList == null){
            getRecipeList();
        }
        ArrayList<Recipe> recipes = recipeList.getValue();
        recipe = recipes.get(index);
        return  recipe;
    }
}
