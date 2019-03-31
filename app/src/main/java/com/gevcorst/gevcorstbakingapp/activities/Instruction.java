package com.gevcorst.gevcorstbakingapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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

public class Instruction extends AppCompatActivity
        implements Player.EventListener{
    private Step step;
    @BindView(R.id.step_instruction_tv)
    public TextView stepInstruction;
    @BindView(R.id.prev_button)
    public Button previous;
    @BindView(R.id.next_button)
    public Button  next;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().
                setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_instruction);

        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getStep();
        if(AppUtils.recipeName != null){
            setTitle("Making "+AppUtils.recipeName+" step "
                    +String.valueOf(stepIndex));
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    private void getStep() {
        if (getIntent().getExtras() != null) {
            stepIndex = getIntent().getExtras().getInt("STEP_INDEX_KEY");
            if(stepIndex != -1 && AppUtils.steps != null){
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
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this), trackSelector, loadControl);
            viewPlayer.setPlayer(player);
        }

    }
    private void buildMediaSource(Uri mUri){
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }
    @OnClick(R.id.prev_button)
    public void goToPreviousStep(){
        int localIndex;
        if(stepIndex > 0){
           localIndex = stepIndex - 1;
            Intent intent =  new Intent(getApplicationContext(),Instruction.class);
            Bundle bundle = new Bundle();
            bundle.putInt("STEP_INDEX_KEY",localIndex);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "No previous steps found !",
                    Toast.LENGTH_LONG).show();
        }
    }
    @OnClick(R.id.next_button)
    public void goToNextStep(){
        int localIndex;
        if(stepIndex < (AppUtils.steps.size()-1)){
            localIndex = stepIndex + 1;
            Intent intent =  new Intent(getApplicationContext(),Instruction.class);
            Bundle bundle = new Bundle();
            bundle.putInt("STEP_INDEX_KEY",localIndex);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "No next steps found !",
                    Toast.LENGTH_LONG).show();
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
}

