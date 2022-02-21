package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<news_item_data> data;
    public Context ctx;
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout inner_layout;
        public TextView title,desc,source,time;
        public ImageView image;


        public MyViewHolder(View v) {
            super(v);
            inner_layout = (LinearLayout) v;
            source= v.findViewById(R.id.news_source);
            title = v.findViewById(R.id.news_title);
            time = v.findViewById(R.id.news_time);
            image = v.findViewById(R.id.news_image);
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(List<news_item_data> myDataset,Context context) {
        ctx = context;
        data = myDataset;
    }
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(position==0)return 0;
        else return 1;
    }
    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v;
        if(viewType==1) {
             v= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_item, parent, false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.news_head_item, parent, false);
        }
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.source.setText(data.get(position).source);
        holder.title.setText(data.get(position).title);
        holder.time.setText(data.get(position).time);
        if(position!=0 && data.get(position).image != null)
        Picasso.with(ctx).load(data.get(position).image).resize(100,100).transform(new RoundedTransformation(10, 0)).into(holder.image);
        else if (data.get(position).image != null)
            Picasso.with(ctx).load(data.get(position).image).resize(360,180).transform(new RoundedTransformation(10, 0)).into(holder.image);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                openURL.setData(Uri.parse(data.get(position).link));
                ctx.startActivity(openURL);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                //LayoutInflater li = (LayoutInflater) LayoutInflater.from(ctx);
                //final View customLayout = li.inflate(R.layout.dialog,null);

                //AlertDialog dialog = builder.create();
                //dialog.show();
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.news_dialog);
                ImageView dialog_image = dialog.findViewById(R.id.dialog_image);
                Picasso.with(ctx).load(data.get(position).image).resize(300,225).into(dialog_image);
                TextView dialog_content = dialog.findViewById(R.id.news_dialog_title);
                dialog_content.setText(data.get(position).title);
                dialog.show();
                return true;
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return data.size();
    }

}
class news_item_data {
    public String source,title,link,image,description,time;
    public news_item_data(){
        source = "BBC";
        title="MASS";
        link="www.google.com";
        image="";
        description="Nothing";
        time="";
    }

}