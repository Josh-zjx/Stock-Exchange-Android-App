package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Float.parseFloat;
import static java.lang.Math.abs;

public class StockDetail extends AppCompatActivity {
    private String sticker;
    private stock_detail_data data;
    private float trade_amount;
    private Integer requestCounter;
    private boolean is_favorite;
    List<news_item_data> news_data;
    local_data_helper data_helper;
    RecyclerView news_recycler;
    MyAdapter myAdapter;
    NestedScrollView main_layout;
    ProgressBar progressBar;
    AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        Intent intent = getIntent();
        sticker = intent.getStringExtra("sticker");
        data_helper = new local_data_helper(this);
        is_favorite=data_helper.isFavorite(sticker);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        main_layout = findViewById(R.id.main_layout);
        main_layout.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        data = new stock_detail_data();
        news_data = new ArrayList<news_item_data>();
        news_recycler = (RecyclerView) findViewById(R.id.news_recycler_view) ;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        news_recycler.setLayoutManager(layoutManager);
        myAdapter = new MyAdapter(news_data,this);
        news_recycler.setAdapter(myAdapter);
        news_recycler.setNestedScrollingEnabled(false);
        getData();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.unfav_menu, menu);
        if(is_favorite){
            menu.findItem(R.id.unfav).setIcon(R.drawable.ic_baseline_star_24);
        }else{
            menu.findItem(R.id.unfav).setIcon(R.drawable.ic_baseline_star_border_24);
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.unfav:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                if(is_favorite){
                    item.setIcon(R.drawable.ic_baseline_star_border_24);
                    data_helper.removeFavorite(sticker);
                }
                else{
                    item.setIcon(R.drawable.ic_baseline_star_24);
                    data_helper.addFavorite(sticker);
                }
                is_favorite=data_helper.isFavorite(sticker);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
    public void getData(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://mynodejsproject-135423.wl.r.appspot.com/";
        Log.d("HTTP","FETCHING");
        requestCounter=3;
        JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url + "query?type=daily&name=" + sticker, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response){

                try {
                    data.name=(String)response.get("name");
                    data.description=(String)response.get("description");
                    data.sticker = (String)response.get("ticker");
                    Log.d("HTTP","NAME FETCHED");
                    requestCounter--;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("HTTP","NAME ERROR");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        JsonRequest jsonRequest2 = new JsonArrayRequest(Request.Method.GET, url + "query?type=iex&name=" + sticker, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response){

                try {
                    JSONObject temp = (JSONObject) response.getJSONObject(0);
                    //Log.d("SHOW",temp.get("open").toString());
                    data.open=(Double) temp.get("open");
                    data.low=(Double)temp.get("low");
                    data.last=(Double)temp.get("last");
                    if(temp.get("bidPrice").toString()=="null")
                    {
                        data.bid=0.0;
                    }
                    else{
                        data.bid=(Double)temp.get("bidPrice");
                    }
                    //data.bid=(Double)temp.get("bidPrice");
                    data.prev=(Double)temp.get("prevClose");
                    data.volume=(Integer) temp.get("volume");
                    data.high=(Double)temp.get("high");
                    if(temp.get("mid").toString()=="null")
                    {
                        data.mid=0.0;
                    }else{
                        data.mid=(Double)temp.get("mid");
                    }
                    data.change = data.last-data.prev;
                    //data.mid=(Double)temp.get("mid");
                    //Log.d("DATA","OPEN"+data.open.toString());
                    Log.d("HTTP","IEX FETCHED");
                    requestCounter--;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("HTTP","IEX ERROR");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        JsonRequest jsonRequest3 = new JsonArrayRequest(Request.Method.GET, url + "query?type=news&name=" + sticker, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response){

                try {
                    for(int i=0;i!=response.length();i++)
                    {
                        JSONObject temp = (JSONObject) response.getJSONObject(i);
                        news_item_data nitem= new news_item_data();
                        nitem.source = (String)temp.get("source");
                        nitem.title = (String)temp.get("title");
                        nitem.description = (String)temp.get("description");
                        nitem.link = (String)temp.get("url");
                        nitem.image = (String)temp.get("urlToImage");
                        nitem.time = (String)temp.get("publishedAt");
                        news_data.add(nitem);
                    }
                    Log.d("HTTP","IEX FETCHED");
                    requestCounter--;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("HTTP","IEX ERROR");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonRequest);
        queue.add(jsonRequest2);
        queue.add(jsonRequest3);
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request object)
            {
                if(requestCounter==0)
                {
                    myAdapter.notifyDataSetChanged();
                    renderPage();
                    main_layout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            }
        });

    }
    public void renderPage(){
        setHeader();
        setChart();
        setPortfolio();
        setStats();
        setAbout();
        setNews();
    }
    public void setHeader(){
        TextView name = findViewById(R.id.stock_name);
        TextView ticker = findViewById(R.id.stock_ticker);
        TextView price = findViewById(R.id.stock_price);
        TextView change = findViewById(R.id.stock_change);
        name.setText(data.name);
        ticker.setText(data.sticker);
        price.setText("$"+String.format("%.2f",data.last));
        if(data.change>0){
            change.setText("$"+String.format("%.2f",data.change));
            change.setTextColor(Color.GREEN);
        }else if(data.change<0){
            change.setText("-$"+String.format("%.2f",abs(data.change)));
            change.setTextColor(Color.RED);
        }else{
            change.setText("$"+String.format("%.2f",data.change));
            change.setTextColor(Color.GRAY);
        }
    }
    public void setChart(){
        WebView web = (WebView) findViewById(R.id.stock_chart);
        web.getSettings().setJavaScriptEnabled(true);
        //web.setInitialScale(100);
        web.loadUrl("file:///android_asset/chart.html?name="+sticker);
    }
    public void setPortfolio(){
        TextView detail_amount = (TextView) findViewById(R.id.detail_amount);
        TextView detail_marketvalue = (TextView) findViewById(R.id.detail_marketvalue);
        if(!data_helper.isPortfolio(data.sticker))
        {
            detail_amount.setText("You have 0 shares of "+data.sticker+".");
            detail_marketvalue.setText("Start trading!");
        }
        else{
            detail_amount.setText(String.format("Shares owned: %.4f",data_helper.getPortfolioAmount(data.sticker)));
            detail_marketvalue.setText(String.format("Market Value: $%.4f",data_helper.getPortfolioAmount(data.sticker)*data.last));
        }
    }
    public void showToast(int resID){
        Toast.makeText(this, resID, Toast.LENGTH_SHORT).show();
    }
    public void setStats(){
        TextView high = findViewById(R.id.stock_high);
        high.setText("High:"+data.high.toString());
        TextView cp = findViewById(R.id.stock_current_price);
        cp.setText("Current Price:"+data.last.toString());
        TextView low  = findViewById(R.id.stock_low);
        low.setText("Low:"+data.low.toString());
        TextView bid = findViewById(R.id.stock_bid);
        bid.setText("Bid Price:"+data.bid.toString());
        TextView open = findViewById(R.id.stock_open);
        open.setText("OpenPrice:"+data.open.toString());
        TextView mid = findViewById(R.id.stock_mid);
        mid.setText("Mid:"+data.mid.toString());
        TextView volume = findViewById(R.id.stock_volume);
        volume.setText("Volume:"+data.volume.toString());
    }
    public void setAbout(){
        TextView desc = findViewById(R.id.stock_description);
        desc.setText(data.description);
    }
    public void setNews(){

    }
    private class stock_detail_data {
        public String name,sticker,description;
        public Double last,prev,low,bid,open,mid,high,change;
        public Integer volume;
    }
    public void onTrade(View view){
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog,null);
        TextView title = customLayout.findViewById(R.id.dialog_title);
        title.setText("Trade "+data.name+" shares");
        TextView calculation = customLayout.findViewById(R.id.dialog_calculation);
        calculation.setText(String.format("0 x $%.2f/share = $0.0",data.last));
        EditText input_area = customLayout.findViewById(R.id.dialog_input);
        trade_amount=0.0f;
        input_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input=s.toString();

                try {
                    trade_amount=parseFloat(input);
                }
                catch(NumberFormatException e){
                    trade_amount=0.0f;
                }
                String calc = input+ String.format(" x $%.2f/share = $%.2f",data.last,trade_amount*data.last);
                calculation.setText(calc);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        TextView cash = customLayout.findViewById(R.id.dialog_cash);
        cash.setText("$"+((Float)data_helper.getCash()).toString()+" available to buy "+data.sticker);

        builder.setView(customLayout);
// 2. Chain together various setter methods to set the dialog characteristics
        Button buy_button = (Button) customLayout.findViewById(R.id.dialog_buy);
        Button sell_button = (Button) customLayout.findViewById(R.id.dialog_sell);
        buy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = input_area.getText().toString();
                if(!input.isEmpty()) {
                    try {
                        trade_amount = parseFloat(input);
                        if (trade_amount <= 0) {
                            showToast(R.string.buy_less_0);
                        }else{
                            if(trade_amount*data.last>data_helper.getCash())
                            {
                                showToast(R.string.not_enough_money);
                            }else{
                                data_helper.addPortfolio(data.sticker,trade_amount);
                                data_helper.removeCash((Float)(float)(trade_amount*data.last));
                                setPortfolio();
                                dialog.dismiss();
                            }
                        }
                    } catch (NumberFormatException e) {
                        showToast(R.string.invalid_amount);
                    }
                }else{
                    showToast(R.string.buy_less_0);
                }
            }
        });
        sell_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = input_area.getText().toString();
                if(!input.isEmpty()) {
                    try {
                        trade_amount = parseFloat(input);
                        if (trade_amount <= 0) {
                             showToast(R.string.sell_less_0);
                        }else{
                            if(trade_amount>data_helper.getPortfolioAmount(data.sticker))
                            {
                                showToast(R.string.not_enough_share);
                            }else{
                                data_helper.removePortfolio(data.sticker,trade_amount);
                                data_helper.addCash((Float)(float)(trade_amount*data.last));
                                setPortfolio();
                                dialog.dismiss();
                            }
                        }
                    } catch (NumberFormatException e) {
                        showToast(R.string.invalid_amount);
                    }
                }else{
                    showToast(R.string.sell_less_0);
                }
            }
        });
        dialog = builder.create();
        dialog.show();

    }


}