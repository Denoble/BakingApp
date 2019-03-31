package com.gevcorst.gevcorstbakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.models.Step;

import java.util.ArrayList;

public class StepsAdapter extends
        RecyclerView.Adapter<StepsAdapter.ViewHolder> {
    private static final String TAG = RecipeFeedListAdapter.class.getSimpleName();
    final private StepsAdapter.ListItemClickListener mOnClickListener;
    private static int viewHolderCount;
    private final Context mContext;
    private final ArrayList<Step> mStepsList;
    public StepsAdapter(int numberOfItems, StepsAdapter.ListItemClickListener mOnClickListener,
                        Context mContext, ArrayList<Step> mStepsList) {
        viewHolderCount = numberOfItems;
        this.mOnClickListener = mOnClickListener;
        this.mContext = mContext;
        this.mStepsList = mStepsList;
    }
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                      int position) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.steps_view_holder;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem,viewGroup,false);
        ViewHolder stepTitleViewHolder = new ViewHolder(view);
        viewHolderCount++;
        return stepTitleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder titleViewHolder,
                                 int position) {
        Step step = mStepsList.get(position);
        titleViewHolder.onBind(step,titleViewHolder.stepTitle);

    }

    @Override
    public int getItemCount() {
        return mStepsList.size();
    }
    public class ViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView stepTitle;
        private Context context;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.context = mContext;
            stepTitle = itemView.findViewById(R.id.stepsTitle);
            itemView.setOnClickListener(this);
        }
        private  void onBind(Step step,TextView textView){
            textView.setText(step.getShortDescription());
        }
        @Override
        public void onClick(View v) {
            int position  =  getAdapterPosition();
            mOnClickListener.onListItemClick(position);
        }
    }
}


