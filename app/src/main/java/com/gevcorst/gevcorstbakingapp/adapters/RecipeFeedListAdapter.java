package com.gevcorst.gevcorstbakingapp.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.models.Recipe;

import java.util.ArrayList;


public class RecipeFeedListAdapter
        extends RecyclerView.Adapter<RecipeFeedListAdapter.ViewHolder> {

    private static final String TAG = RecipeFeedListAdapter.class.getSimpleName();
    /*
     * An on-click handler that makes it easy for an Activity to interface with
     * the RecyclerView
     */
    final private ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private final Context mContext;
    private final ArrayList<Recipe> mRecipeList;

    public RecipeFeedListAdapter(int numberOfItems,ListItemClickListener listener,
                                ArrayList<Recipe> mRecipeList,Context contex) {
        this.mContext = contex;
        this.mOnClickListener = listener;
        this.viewHolderCount = numberOfItems;
        this.mRecipeList = mRecipeList;
    }
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.cardview;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        ViewHolder titleHolderHolder = new ViewHolder(view);
        viewHolderCount++;
        return titleHolderHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeFeedListAdapter.ViewHolder titleViewHolder,
                                 int position) {
        Recipe recipe = mRecipeList.get(position);
        titleViewHolder.bind(recipe,titleViewHolder.recipeTitle);

    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {
        final Context context;
        TextView recipeTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context =  mContext;
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            itemView.setOnClickListener(this);
        }
        private  void bind(Recipe recipe,TextView textView){

            textView.setText(recipe.getName());
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
