package com.farhanahmed.soundcloudcache;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    EditText songName;
    ImageButton download;
    ListView songsListView;
    ProgressDialog dialog;
    SoundCloudAdapter adapter;
    DownloadManager downloadManager;
    ArrayList<SoundCloudModel> data = new ArrayList<SoundCloudModel>();
    AlertDialog.Builder alertDialog;
    ConnectivityManager manager;
    String cacheText;
    ActionBar actionBar;
    MediaPlayer mediaPlayer;
    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        setActionBar();
        mediaPlayer = new MediaPlayer();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main,container,false);

        manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        songsListView = (ListView) rootView.findViewById(R.id.listView);

        TextView emptyView = (TextView) rootView.findViewById(R.id.emptyElement);

        data = new ArrayList<SoundCloudModel>();

        adapter = new SoundCloudAdapter(getActivity(), data);
        songsListView.setEmptyView(emptyView);
        songsListView.setAdapter(adapter);
        downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Please Connect to Internet");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        songsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startDownload(position);
            }
        });

        actionBar.setCustomView(R.layout.menu_layout);
        songName = (EditText) actionBar.getCustomView().findViewById(R.id.searchEditText);
        songName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTrack();

                    View view = getActivity().getCurrentFocus();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });

        return rootView;
    }
    private void sendRequest(String q) {
        Log.debug("", "Sending Request");
        String url = null;
        try {
            url = SoundCloudConfig.URL + "&q=" + URLEncoder.encode(q, "UTF-8")+"&limit=50";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.debug("", "URL " + url);
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    Log.debug("", "RESPONSE");
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        data.add(new SoundCloudModel(
                                jsonObject.getString(SoundCloudConfig.Key.TITLE_KEY),
                                jsonObject.getString(SoundCloudConfig.Key.STREAM_URL_KEY),
                                jsonObject.getString(SoundCloudConfig.Key.URI_KEY),
                                jsonObject.getString(SoundCloudConfig.Key.DURATION_KEY),
                                jsonObject.getString(SoundCloudConfig.Key.ARTWORK_URL_KEY)
                        ));


                    }

                    adapter.notifyDataSetChanged();
                    dialog.dismiss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.debug("", "jsonError");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.debug("", "onErrorResponse");
            }
        }) {
            @Override
            public Priority getPriority() {
                return Priority.IMMEDIATE;
            }
        };
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Loading ...");
        dialog.show();
        Volley.newRequestQueue(getActivity()).add(arrayRequest);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
    private void fillList()
    {

        if (cacheText != null)
        {
            sendRequest(cacheText);
        }
        else{

        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_menu, menu);


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_search:
            {
                searchTrack();
            }break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void startDownload(final int pos)
    {
        AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
        ad.setTitle("Confirm Download");
        ad.setMessage("Start Download ?");
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            int position = pos;

            @Override
            public void onClick(DialogInterface dialog, final int which) {

                /*mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    final Thread play = null;
                    if(mediaPlayer.isPlaying())
                    {
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(data.get(position).getStreamUrl());

                        mediaPlayer.prepareAsync();

                    }else{
                        mediaPlayer.setDataSource(data.get(position).getStreamUrl());

                        mediaPlayer.prepareAsync();
                    }
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            android.util.Log.d("WHAT", "" + what);
                            android.util.Log.d("MP", "" + mp.getDuration());
                            return true;
                        }
                    });
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                        @Override
                        public void onPrepared(final MediaPlayer mp) {
                            mp.start();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                        while(true)
                                        {

                                            if(mp.isPlaying())
                                            {
                                                int startTime = mediaPlayer.getCurrentPosition();

                                                long min = TimeUnit.MILLISECONDS.toMinutes((long) startTime);
                                                long sec = TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime));

                                                android.util.Log.d("MP Thread",min+":"+sec);
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            else{
                                                break;
                                            }

                                        }
                                    mp.release();
                                }
                            }).start();

                        }
                    });



                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (!data.isEmpty()) {
                    Uri downloadUri = Uri.parse(data.get(position).getStreamUrl());
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                    request.setTitle(data.get(position).getTitle());
                    request.setDescription("Downloading " + data.get(position).getTitle());
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, data.get(position).getTitle().trim() + ".mp3");
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE | DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadManager.enqueue(request);
                    Toast.makeText(getActivity(), "Download will be in Download App", Toast.LENGTH_LONG).show();

                }
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.create().show();

    }
    private void searchTrack()
    {
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.isConnectedOrConnecting()) {
            adapter.clear();

            if (songName.getText().toString().length() > 0) {
                cacheText = songName.getText().toString();
                sendRequest(cacheText);
            } else {
                cacheText = null;
                songName.setError(getResources().getString(R.string.empty_edittext));
                Log.debug("", "empty");
            }
        } else {
            alertDialog.create().show();

            Log.debug("", "Net Error");
        }
    }

    private void setActionBar()
    {
        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
    }

    public void setOnOrientationChanges()
    {
        this.setActionBar();
        this.fillList();
    }
    public void stopMediaPlayer()
    {
        if(mediaPlayer !=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            //mediaPlayer = null;
        }
    }
}
