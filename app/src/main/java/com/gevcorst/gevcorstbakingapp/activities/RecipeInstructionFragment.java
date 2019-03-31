package com.gevcorst.gevcorstbakingapp.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gevcorst.gevcorstbakingapp.R;
import com.gevcorst.gevcorstbakingapp.models.Step;
import com.gevcorst.gevcorstbakingapp.utils.AppUtils;
import com.gevcorst.gevcorstbakingapp.utils.ExoPlayerConfig;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RecipeInstructionFragment extends Fragment
        implements Player.EventListener{
    private Step step;
    @BindView(R.id.step_instruction_tv)
    public TextView stepInstruction;
    @BindView(R.id.prev_button)
    public Button previous;
    @BindView(R.id.next_button)
    public Button  next;
    @BindView(R.id.btn_back_Recipes)
    public Button backToRecipeList;
    @BindView(R.id.exovideoPlayer)
    PlayerView viewPlayer;
    private int stepIndex; // step index in the Steps array
    String videoUri;
    String imageUri;
    @BindView(R.id.instructionImage)
    ImageView imageView;
    @BindView(R.id.spinnerVideoDetails)
    public ProgressBar spinnerVideoDetails;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;

    //private OnRecipeInstructionVideoListener mListener;
    private PreviousButtonOnClick mPreviousButtonListener;
    private NextButtonOnClick mNextButtonListener;
    private IFBackToRecipeListButton mBackToRecipeListButton;

    public RecipeInstructionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView  = inflater.inflate(R.layout.fragment_recipe_instruction,
                container, false);
        ButterKnife.bind(this, rootView);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            stepIndex = bundle.getInt("INDEX_KEY", 0);
            loadData();
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PreviousButtonOnClick) {
             mPreviousButtonListener = (PreviousButtonOnClick) context;

        }
        if(context instanceof NextButtonOnClick ){
            mNextButtonListener = (NextButtonOnClick)context;
        }
        if(context instanceof  IFBackToRecipeListButton){
            mBackToRecipeListButton = (IFBackToRecipeListButton)context;
        }
            else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        resumePlayer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPreviousButtonListener= null;
        mNextButtonListener = null;
        mBackToRecipeListButton = null;
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {

            case Player.STATE_BUFFERING:
                spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_ENDED:
                // Activate the force enable
                break;
            case Player.STATE_IDLE:

                break;
            case Player.STATE_READY:
                spinnerVideoDetails.setVisibility(View.GONE);

                break;
            default:
                // status = PlaybackStatus.IDLE;
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

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

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    private void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }
    public interface PreviousButtonOnClick{
        public void previousButtonClicked(int index);
    }
    @OnClick(R.id.prev_button)
    public void goToPreviousStep(){

        if(stepIndex > 0 ){
            stepIndex -= 1;
            mPreviousButtonListener.previousButtonClicked(stepIndex);
        }
        else{
            Toast.makeText(getActivity(),
                    "No previous steps found !",
                    Toast.LENGTH_LONG).show();
        }
    }
    public interface NextButtonOnClick{
        public void nextButtonClicked(int index);
    }
    @OnClick(R.id.next_button)
    public void goToNextStep(){
        stepIndex+=1;
        if(stepIndex < (AppUtils.steps.size()-1)){
            mNextButtonListener.nextButtonClicked(stepIndex);
        }
        else{
            Toast.makeText(getActivity(),
                    "No next steps found !",
                    Toast.LENGTH_LONG).show();
        }
    }
    private void loadData(){
        step = AppUtils.steps.get(stepIndex);
        stepInstruction.setText(step.getDescription());
        stepInstruction.setMovementMethod(new ScrollingMovementMethod());
        videoUri = step.getVideoURL();
        if(videoUri.length() == 0){
            videoUri = null;
        }

        if(videoUri == null){
            viewPlayer.setVisibility(View.GONE);
            spinnerVideoDetails.setVisibility(View.GONE);
        }
        else {
            setUpVideoPlayer();
        }
        imageUri = step.getThumbnailURL();
        if(imageUri.length() == 0){
            imageUri = null;

        }
        if(imageUri == null){
            imageView.setVisibility(View.GONE);
        }

        else {

            Picasso.get().load(imageUri).into(imageView);
        }
    }
    private void setUpVideoPlayer(){
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }
    private void initializePlayer(){
        if (player == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    ExoPlayerConfig.MIN_BUFFER_DURATION,
                    ExoPlayerConfig.MAX_BUFFER_DURATION,
                    ExoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    ExoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);
            viewPlayer.setPlayer(player);
        }

    }
    private void buildMediaSource(Uri mUri){
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(),
                        getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }
    public interface IFBackToRecipeListButton{
        public void backToRecipeListButtonOnClick();
    }
    @OnClick(R.id.btn_back_Recipes)
    public void backToRecipeListOnClick(){
        mBackToRecipeListButton.backToRecipeListButtonOnClick();
    }
}
