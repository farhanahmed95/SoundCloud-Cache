package com.farhanahmed.soundcloudcache;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by farhanahmed on 23/07/15.
 */
public class SoundCloudAdapter extends ArrayAdapter<SoundCloudModel> {
    ArrayList<SoundCloudModel> data;

    public SoundCloudAdapter(Context context, ArrayList<SoundCloudModel> resource) {
        super(context,R.layout.item_layout, resource);
        data = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
          View v = LayoutInflater.from(getContext()).inflate(R.layout.item_layout,parent,false);
        TextView name = (TextView) v.findViewById(R.id.textView);
        final ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        String url = data.get(position).getArtworkUrl();

        Picasso.with(getContext())
                .load(url)
                .placeholder(R.drawable.ic_load_error)
                .error(R.drawable.ic_load_error)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.debug("PICASSO","onSuccess");
                    }

                    @Override
                    public void onError() {
                        Log.debug("PICASSO","onError");
                        imageView.setImageResource(R.drawable.ic_load_error);
                    }
                });



            name.setText(data.get(position).

                            getTitle()

            );

            return v;
        }

        @Override
    public int getCount() {
        return data.size();
    }
}
