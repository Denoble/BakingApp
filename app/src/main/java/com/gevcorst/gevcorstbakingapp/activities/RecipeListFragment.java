package com.gevcorst.gevcorstbakingapp.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.adapters.StepsAdapter;
import com.gevcorst.gevcorstbakingapp.models.Recipe;
import com.gevcorst.gevcorstbakingapp.models.Step;
import com.gevcorst.gevcorstbakingapp.utils.AppUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecipeListFragment extends Fragment
implements StepsAdapter.ListItemClickListener{

    public TextView ingredientList;
    public RecyclerView recipeStepRecyclerView;
    private OnRecipeStepClickListener mListener;
    private OnTextViewClick mTextViewClick;
    public Recipe recipe;
    public RecipeListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView  =
                inflater.inflate(R.layout.fragment_recipe_list, container, false);
        ingredientList = rootView.findViewById(R.id.tv_ingredients);
        recipeStepRecyclerView = rootView.findViewById(R.id.stepsRecyclerView);
        recipeStepRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        StepsAdapter stepsAdapter =
                new StepsAdapter(AppUtils.steps.size(),
                        this,getContext(),
                        AppUtils.steps);
       recipeStepRecyclerView.setAdapter(stepsAdapter);
        ingredientList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextViewClick.viewIngredients();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecipeStepClickListener) {
            mListener = (OnRecipeStepClickListener) context;
            mTextViewClick = (OnTextViewClick)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        mListener.onRecipeStepClick(clickedItemIndex);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRecipeStepClickListener {
        // TODO: Update argument type and name
        void onRecipeStepClick(int clickindex);
    }
    public interface OnTextViewClick{
        void viewIngredients();
    }
}
