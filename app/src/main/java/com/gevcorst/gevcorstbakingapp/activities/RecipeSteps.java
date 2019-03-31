package com.gevcorst.gevcorstbakingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.adapters.StepsAdapter;
import com.gevcorst.gevcorstbakingapp.models.Recipe;
import com.gevcorst.gevcorstbakingapp.utils.AppUtils;

import java.util.ArrayList;

public class RecipeSteps extends AppCompatActivity
 implements StepsAdapter.ListItemClickListener,RecipeListFragment.OnRecipeStepClickListener,RecipeListFragment.OnTextViewClick,
RecipeInstructionFragment.PreviousButtonOnClick,RecipeInstructionFragment.NextButtonOnClick,
RecipeInstructionFragment.IFBackToRecipeListButton{
    private final String LOG_TAG= getClass().getSimpleName();
    private ArrayList<Recipe> mRecipes;
    private RecyclerView recyclerView;
    private Recipe mRecipe;
    private int mRecipeIndex;
    private StepsAdapter stepsAdapter;
    private TextView textView;
    FragmentManager fragmentManager;
    RecipeListFragment recipeListFragment;
    RecipeInstructionFragment recipeInstructionFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_steps);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getRecipeList();
        boolean is_A_Tablet = is_A_Tablet = AppUtils.isTablet(this);
        if(is_A_Tablet){
            // In two-pane mode, add initial BodyPartFragments to the screen
            fragmentManager = getSupportFragmentManager();
            recipeListFragment
                    = new RecipeListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.listFragment,recipeListFragment)
                    .commit();
            recipeInstructionFragment
                    = new RecipeInstructionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("INDEX_KEY",mRecipeIndex);
            recipeInstructionFragment.setArguments(bundle);
            fragmentManager.beginTransaction()
                    .add(R.id.videoFragment,recipeInstructionFragment)
                    .commit();
        }
        else {
            textView = findViewById(R.id.tv_ingredients);
            recyclerView = findViewById(R.id.stepsRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewIngredientList();
                }
            });
            setUpRecyclerViewAdapter(mRecipe);
        }
    }
    private void viewIngredientList() {
        Intent intent = new Intent(getApplicationContext(), IngredientList.class);
        Bundle bundle = new Bundle();
        bundle.putString("RECIPENAME_KEY",mRecipe.getName());
        bundle.putParcelableArrayList("INGREDIENT_LIST_KEY",mRecipe.getIngredients());
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void setUpRecyclerViewAdapter(Recipe recipe){
        stepsAdapter = new StepsAdapter(recipe.getSteps().size(),this,
                getApplicationContext(),recipe.getSteps());
        recyclerView.setAdapter(stepsAdapter);
    }
    private void getRecipeList() {
        if (getIntent().getExtras() != null) {
            mRecipe = getIntent().getExtras().getParcelable("RFN_KEY");
            if(mRecipe != null){
                setTitle(mRecipe.getName());
                AppUtils.steps = mRecipe.getSteps();
                AppUtils.recipeName = mRecipe.getName();
            }

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent =  new Intent(getApplicationContext(),Instruction.class);
        Bundle bundle = new Bundle();
        bundle.putInt("STEP_INDEX_KEY",clickedItemIndex);
       /* bundle.putParcelable("STEP_KEY",
                mRecipe.getSteps().get(clickedItemIndex));*/
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void viewIngredients() {
        viewIngredientList();
    }

    @Override
    public void onRecipeStepClick(int clickedindex) {
       mRecipeIndex = clickedindex;
        loadNewRecipeInstructionVideo();
    }

    private void loadNewRecipeInstructionVideo() {
        RecipeInstructionFragment recipeInstructionFragment
                = new RecipeInstructionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("INDEX_KEY",mRecipeIndex);
        recipeInstructionFragment.setArguments(bundle);
        FragmentTransaction transaction =  fragmentManager.beginTransaction();
        transaction.replace(R.id.videoFragment,recipeInstructionFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void previousButtonClicked(int index) {
        mRecipeIndex = index;
        loadNewRecipeInstructionVideo();
    }

    @Override
    public void nextButtonClicked(int index) {
        mRecipeIndex = index;
        loadNewRecipeInstructionVideo();
    }

    @Override
    public void backToRecipeListButtonOnClick() {
        Intent intent =  new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
