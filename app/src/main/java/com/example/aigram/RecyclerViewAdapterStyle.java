package com.example.aigram;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapterStyle extends RecyclerView.Adapter<RecyclerViewAdapterStyle.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mTitle = new ArrayList<>();
    private ArrayList<Integer> mImages = new ArrayList<>();
    private Context mContext;
    OnNoteListenner onNoteListenner;

    public RecyclerViewAdapterStyle(Context context, ArrayList<String> mTitle, ArrayList<Integer> mImages, OnNoteListenner onNoteListenner){
        mContext = context;
        this.mTitle = mTitle;
        this.mImages = mImages;
        this.onNoteListenner = onNoteListenner;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem_categories, parent, false);
        ViewHolder holder = new ViewHolder(view, onNoteListenner);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(mImages.get(position))
                .into(holder.imageViewCategories);
        holder.textViewTitle.setText(mTitle.get(position));
    }

    @Override
    public int getItemCount() {
        return mTitle.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textViewTitle;
        ImageView imageViewCategories;
        OnNoteListenner onNoteListenner;
        public ViewHolder(@NonNull View itemView, OnNoteListenner onNoteListenner) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewCategories);
            imageViewCategories = itemView.findViewById(R.id.imageViewCategories);
            this.onNoteListenner = onNoteListenner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListenner.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListenner{
        void onNoteClick(int postition);
    }
}
