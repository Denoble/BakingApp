package com.gevcorst.gevcorstbakingapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.adapters.RecipeFeedListAdapter;
import com.gevcorst.gevcorstbakingapp.models.Recipe;
import com.gevcorst.gevcorstbakingapp.utils.AppUtils;
import com.gevcorst.gevcorstbakingapp.viewmodels.RecipeFeedViewModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements RecipeFeedListAdapter.ListItemClickListener{
    private RecipeFeedListAdapter adapter;
    private ArrayList<Recipe> mRecipes;
    private RecipeFeedViewModel recipeListViewModel;
    private  RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isTable;
    double screenSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recipeFeedRecyclerView);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.orange) ,
                getResources().getColor(R.color.green) ,getResources().getColor(R.color.blue) );
        screenSize = getScreenDimen();
        recipeListViewModel =
                ViewModelProviders.of(this).get(RecipeFeedViewModel.class);
        setupLayoutManager();
        setUpViewModel();
        refreshList(mSwipeRefreshLayout);
    }
    private void setupAdapter(ArrayList<Recipe>recipes){
        adapter = new RecipeFeedListAdapter(recipes.size(),this,
                recipes,getApplication());
        mRecyclerView.setAdapter(adapter);
    }
    private void refreshList(SwipeRefreshLayout refreshLayout){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setUpViewModel();
                        refreshLayout.setRefreshing(false);
                    }
                },2500);
            }
        });
    }
    private void setupLayoutManager() {

        Log.i(getClass().getSimpleName()+" UCHE",String.valueOf(screenSize));
        //Toast.makeText(this,String.valueOf(screenSize),Toast.LENGTH_LONG).show();
        isTable = AppUtils.isTablet(this);
        Log.i(getClass().getSimpleName(),String.valueOf(isTable));
        //Set RecyclerView layoutmanager based on the device screen size
        setRecyclerViewLayoutManager(isTable);

    }


    private void setRecyclerViewLayoutManager(boolean isTablet){
        if(isTablet){
            mRecyclerView.setLayoutManager(
                    new GridLayoutManager(this,3));
        }
        else{
            mRecyclerView.setLayoutManager(
                    new LinearLayoutManager(this));
        }
    }

    private void setUpViewModel() {
        if (mRecipes == null && !isNetworkAvailable()) {
            Toast toast =
                    Toast.makeText(getApplicationContext(),
                            "No INTERNET connection!", Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            recipeListViewModel.getRecipeList().observe(this, recipes -> {
                mRecipes = recipes;
                setupAdapter(recipes);
            });
        }

    }

    private  double getScreenDimen(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int height=dm.heightPixels;
        double wi=(double)width/(double)dm.xdpi;
        double hi=(double)height/(double)dm.ydpi;
        double x = Math.pow(wi,2);
        double y = Math.pow(hi,2);
        return  Math.sqrt(x+y);
    }
    private Boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null && activeNetwork.isConnected());
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {

        Recipe recipe =  mRecipes.get(clickedItemIndex);
        Intent intent = new Intent(getApplicationContext(), RecipeSteps.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("RFN_KEY",recipe);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
