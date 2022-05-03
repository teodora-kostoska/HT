package com.example.harjoitustyo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

//This is adapter to populate the custom grid view in the list movies by review activity
public class MovieAdapter extends ArrayAdapter<Movie> {
    public MovieAdapter(@NonNull Context context, ArrayList<Movie> movieArrayList) {
        super(context, 0, movieArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }
        Movie movie = getItem(position);
        TextView firstView = listitemView.findViewById(R.id.firstText);
        TextView secondView = listitemView.findViewById(R.id.secondText);
        TextView thirdView = listitemView.findViewById(R.id.thirdText);
        TextView fourthView = listitemView.findViewById(R.id.fourthText);
        firstView.setText(movie.getMovieName());
        secondView.setText(movie.getGenre());
        thirdView.setText(movie.getDuration());
        fourthView.setText(movie.getReleaseYear());
        return listitemView;
    }
}
