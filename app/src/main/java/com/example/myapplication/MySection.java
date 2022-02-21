package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import static java.lang.Math.abs;

class PortFolioSection extends Section implements ItemTouchHelperAdapter {
    List<stock_item_data> itemList;
    local_data_helper data_helper;
    public PortFolioSection(List<stock_item_data> input_data, Context ctx) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.stock_item)
                .headerResourceId(R.layout.portfolio_header)
                .build());
        data_helper = new local_data_helper(ctx);
        itemList = input_data;
    }

    @Override
    public int getContentItemsTotal() {
        return itemList.size(); // number of items of this section
    }

   /* @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public boolean onItemDismiss(int position) {

    }*/

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MyItemViewHolder(view);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindHeaderViewHolder(holder);
        TextView date = (TextView) holder.itemView.findViewById(R.id.date);
        LocalDate currentDate = LocalDate.now();
        String month=currentDate.getMonth().toString();
        date.setText(month.substring(0,1)+month.substring(1).toLowerCase()+String.format(" %d,%d",currentDate.getDayOfMonth(),currentDate.getYear()));
        TextView networth = (TextView) holder.itemView.findViewById(R.id.net_worth);
        Double netvalue=0.0;
        for(int i=0;i!=itemList.size();i++){
            netvalue+=itemList.get(i).share*itemList.get(i).last;
        }
        netvalue+=data_helper.getCash();
        networth.setText(String.format("%.2f",(float)netvalue.doubleValue()));

    }
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;
            itemHolder.item_sticker.setText(itemList.get(position).sticker);
            itemHolder.item_name.setText(itemList.get(position).share.toString()+" shares");
            itemHolder.item_price.setText(String.format("%.2f",itemList.get(position).last));
            itemHolder.item_change.setText(String.format("%.2f",abs(itemList.get(position).change)));
        if(itemList.get(position).change>0)
        {
            itemHolder.item_image.setVisibility(View.VISIBLE);;
            itemHolder.item_image.setImageResource(R.drawable.ic_twotone_trending_up_24);
            itemHolder.item_change.setTextColor(Color.GREEN);
        }else if(itemList.get(position).change<0)
        {
            itemHolder.item_image.setVisibility(View.VISIBLE);;
            itemHolder.item_image.setImageResource(R.drawable.ic_baseline_trending_down_24);
            itemHolder.item_change.setTextColor(Color.RED);
        }else{
            itemHolder.item_image.setVisibility(View.GONE);;
            itemHolder.item_change.setTextColor(Color.GRAY);
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new SectionedRecyclerViewAdapter.EmptyViewHolder(view);
    }
}

class FavoriteSection extends Section implements ItemTouchHelperAdapter{
    List<stock_item_data> itemList;

    public FavoriteSection(List<stock_item_data> input_data) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.stock_item)
                .headerResourceId(R.layout.favorite_header)
                .footerResourceId(R.layout.footer)
                .build());
        itemList = input_data;
    }

   /* @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public boolean onItemDismiss(int position) {

    }*/

    @Override
    public int getContentItemsTotal() {
        return itemList.size(); // number of items of this section
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        // return a custom instance of ViewHolder for the items of this section
        return new MyItemViewHolder(view);
    }



    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyItemViewHolder itemHolder = (MyItemViewHolder) holder;

        // bind your view here

        itemHolder.item_sticker.setText(itemList.get(position).sticker);
        if(itemList.get(position).bought)
        {
            itemHolder.item_name.setText(itemList.get(position).share.toString()+" shares");
        }
        else{
            itemHolder.item_name.setText(itemList.get(position).name);
        }

        itemHolder.item_price.setText(String.format("%.2f",itemList.get(position).last));
        itemHolder.item_change.setText(String.format("%.2f",abs(itemList.get(position).change)));
        if(itemList.get(position).change>0)
        {
            itemHolder.item_image.setVisibility(View.VISIBLE);;
            itemHolder.item_image.setImageResource(R.drawable.ic_twotone_trending_up_24);
            itemHolder.item_change.setTextColor(Color.GREEN);
        }else if(itemList.get(position).change<0)
        {
            itemHolder.item_image.setVisibility(View.VISIBLE);;
            itemHolder.item_image.setImageResource(R.drawable.ic_baseline_trending_down_24);
            itemHolder.item_change.setTextColor(Color.RED);
        }else{
            itemHolder.item_image.setVisibility(View.GONE);;
            itemHolder.item_change.setTextColor(Color.GRAY);
        }

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        // return an empty instance of ViewHolder for the headers of this section
        return new SectionedRecyclerViewAdapter.EmptyViewHolder(view);
    }
}

class MyItemViewHolder extends RecyclerView.ViewHolder {
    public final TextView item_sticker;
    public final TextView item_name;
    public final TextView item_price;
    public final TextView item_change;
    public final ImageView item_image;

    public MyItemViewHolder(View itemView) {
        super(itemView);

        item_sticker = (TextView) itemView.findViewById(R.id.item_name);
        item_name = (TextView) itemView.findViewById(R.id.item_desc);
        item_price = (TextView) itemView.findViewById(R.id.item_price);
        item_change = (TextView) itemView.findViewById(R.id.item_change);
        item_image = (ImageView)itemView.findViewById(R.id.trending);
    }
}
class stock_item_data {
    public String name,sticker;
    public Double last,prev,change,share;
    public boolean bought;
    public stock_item_data(){
        name="name";
        sticker="sticker";
        last=prev=change=share=0.0;
        bought=false;
    }

}
