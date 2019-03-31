package com.gevcorst.gevcorstbakingapp.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.models.Ingredient;

import java.util.ArrayList;

public class IngredientList extends AppCompatActivity {
    TextView textView;
    ArrayList<Ingredient> ingredients_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredient_list);
        textView = findViewById(R.id.ingredient_list_tv);
        getIngredientsFromIntent();
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                onBackPressed();
                return  true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getIngredientsFromIntent() {
        if (getIntent().getExtras() != null) {
            String title = getIntent().getExtras()
                    .getString("RECIPENAME_KEY");
            if(title !=null){
                setTitle(title+" Ingredients");
            }
            ingredients_list = getIntent().getExtras()
                    .getParcelableArrayList("INGREDIENT_LIST_KEY");
            if(ingredients_list != null){
                printOutIngredients(ingredients_list);
            }
        }
    }

    private void printOutIngredients(ArrayList<Ingredient> ingredients){
        StringBuilder stringBuilder = new StringBuilder();
        for (Ingredient ingr:ingredients){
            String ingredient =  "Ingredient: ";
            ingredient.toUpperCase();
            String tempIngred =  ingr.getIngredient();
            stringBuilder.append(ingredient+tempIngred);
            stringBuilder.append("\n");
            String quantity = "Quantity: ";
            quantity.toUpperCase();
            String tempQuant = String.valueOf(ingr.getQuantity());
            stringBuilder.append(quantity+tempQuant);
            stringBuilder.append("\n");
            String measurement = "Measurement: ";

            String tempMeasurement = ingr.getMeasure();
            Log.i(getClass().getSimpleName(),tempMeasurement);
            stringBuilder.append(measurement+tempMeasurement);
            stringBuilder.append("\n\n");

        }
        textView.setText(stringBuilder.toString());
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

}
