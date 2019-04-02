package com.example.Table_Top_Gaming;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "RecyclerViewAdapter";
    private List<PlayingCard> currentPlayerHand;
    private List<PlayingCard> discardPile;
    private Context context;
    private boolean hideHand;

    public RecyclerViewAdapter(List<PlayingCard> hand, List<PlayingCard> discardPile, boolean hideHand, Context context) {
        currentPlayerHand = hand;
        this.context = context;
        this.discardPile = discardPile;
        this.hideHand = hideHand;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_card_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        if (!hideHand) {
            Log.d(TAG, "onBindViewHolder: called! position: " + i);
            String cardImage = "";
            cardImage += currentPlayerHand.get(i).getSuit();
            cardImage += currentPlayerHand.get(i).getNumber();

            int id = context.getResources().getIdentifier(cardImage, "drawable", context.getPackageName());
            viewHolder.imageView.setImageResource(id);

            viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = viewHolder.getAdapterPosition();
                    Log.d(TAG, "onClick: clicked position " + viewHolder.getAdapterPosition());
                    PlayingCard playingCard = currentPlayerHand.get(index);
                    currentPlayerHand.remove(index);
                    discardPile.add(playingCard);
                    ((CardGameActivity) context).updateImagesForCardLocations();
                }
            });
        } else {
            viewHolder.imageView.setImageResource(R.drawable.red_back);
        }
    }

    public void notifyDataSetChangedWithExtras(boolean hideHand, List <PlayingCard> currentPlayerHand) {
        this.hideHand = hideHand;
        this.currentPlayerHand = currentPlayerHand;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return currentPlayerHand.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        RelativeLayout parentLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.card);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
