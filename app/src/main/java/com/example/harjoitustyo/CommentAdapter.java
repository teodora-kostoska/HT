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

//Adapter that is used to populate the comments by movie activities custom grid view
public class CommentAdapter extends ArrayAdapter<Reviews> {
    public CommentAdapter(@NonNull Context context, ArrayList<Reviews> reviewsArrayList) {
        super(context, 0, reviewsArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }
        Reviews reviews = getItem(position);
        //Add correct values to the correct view
        TextView firstView = listitemView.findViewById(R.id.firstText);
        TextView secondView = listitemView.findViewById(R.id.secondText);
        TextView thirdView = listitemView.findViewById(R.id.thirdText);
        TextView fourthView = listitemView.findViewById(R.id.fourthText);
        firstView.setText(reviews.getTimeStamp() + "\n" +reviews.getUser().getName() + "\n"+ reviews.getUser().getEmail());
        secondView.setText(reviews.getMovie().getMovieName());
        thirdView.setText(reviews.getRating());
        fourthView.setText(reviews.getComment());
        return listitemView;
    }
}
