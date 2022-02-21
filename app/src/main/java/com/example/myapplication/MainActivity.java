package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {
    private RecyclerView portfolioview;
    private SectionedRecyclerViewAdapter portfolioAdapter;
    private RecyclerView.LayoutManager layoutManager1;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    local_data_helper data_helper;
    public AutoCompleteAdapter autoCompleteAdapter;
    private Handler handler;
    public String[] favorite_dataset,portfolio_dataset; //= {"AAPL","IBM","MSFT"};
    public Integer requestCounter;
    Section portfolio_section;
    Section favorite_section;
    Double netvalue;
    ConstraintLayout main_layout;
    ProgressBar progressBar;
    public List<stock_item_data> favorite_data,portfolio_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        data_helper = new local_data_helper(this);
        portfolio_data = new ArrayList<stock_item_data>();
        favorite_data = new ArrayList<stock_item_data>();
        //mocking_data();

        initializeData();
        Log.d("Data","Initialized");
        main_layout = findViewById(R.id.main_layout);
        progressBar = findViewById(R.id.progressBar);
        main_layout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        portfolioview = (RecyclerView) findViewById(R.id.portfolio_recyclerview);
        layoutManager1 = new LinearLayoutManager(this);
        portfolioview.setLayoutManager(layoutManager1);
        portfolioAdapter = new SectionedRecyclerViewAdapter();
        portfolio_section = new PortFolioSection(portfolio_data,this);
        favorite_section =new FavoriteSection(favorite_data);
        portfolioAdapter.addSection(portfolio_section);
        portfolioAdapter.addSection(favorite_section);
        portfolioview.setAdapter(portfolioAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(portfolioview);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem searchMenu = menu.findItem(R.id.action_favorite);
        SearchView searchView = (SearchView) searchMenu.getActionView();
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setBackgroundColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);
        autoCompleteAdapter = new AutoCompleteAdapter(this,android.R.layout.simple_dropdown_item_1line);
         searchAutoComplete.setAdapter(autoCompleteAdapter);
        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString=(String)adapterView.getItemAtPosition(itemIndex);
                int index = queryString.indexOf(" ");
                searchAutoComplete.setText(queryString.substring(0,index));
                //Toast.makeText(ActionBarSearchActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(s.length()>2) {
                    handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                    handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                            AUTO_COMPLETE_DELAY);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())&&searchAutoComplete.getText().length()>2) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent =new Intent(MainActivity.this,StockDetail.class);
                intent.putExtra("sticker", query);
                searchView.clearFocus();
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
        //return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_favorite:
                
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Resume","Called");
        main_layout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        initializeData();
        getData();
    }
    public void jump(View v){
        TextView name = v.findViewById(R.id.item_name);
        Intent intent =new Intent(MainActivity.this,StockDetail.class);
        intent.putExtra("sticker", name.getText());
        startActivity(intent);
    }
    public void mocking_data(){
        data_helper.removeFavorite("AAPL");
        data_helper.removeFavorite("IBM");
        data_helper.removeFavorite("MSFT");
        data_helper.addFavorite("MSFT");
        data_helper.addFavorite("IBM");
        data_helper.addFavorite("AAPL");
        data_helper.addPortfolio("AAPL",50f);
    }
    private void makeApiCall(String text) {
        AutoCompleteHelper.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONArray array = new JSONArray(response);
                    //JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        stringList.add(row.getString("ticker")+" - "+row.getString("name"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoCompleteAdapter.setData(stringList);
                autoCompleteAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
    public void initializeData(){

        favorite_dataset=data_helper.getFavorite();
        portfolio_dataset=data_helper.getPortfolio();
        favorite_data.clear();
        portfolio_data.clear();
        for(int i=0;i!=favorite_dataset.length;i++)
        {
            favorite_data.add(i,new stock_item_data());
        }
        for(int i=0;i!=portfolio_dataset.length;i++)
        {
            portfolio_data.add(i,new stock_item_data());
        }
    }
    public void getData(){

        initializeData();
        ArrayList<String> requestList = new ArrayList<String>();
        requestList.addAll(Arrays.asList(favorite_dataset));
        for(int i=0;i!=portfolio_dataset.length;i++){
            if(!requestList.contains(portfolio_dataset[i])){
                requestList.add(portfolio_dataset[i]);
            }
        }
        requestCounter = requestList.size()+1;
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://mynodejsproject-135423.wl.r.appspot.com/";
        Log.d("REQUEST",requestList.toString());
        for(int i=0;i!=requestList.size();i++)
        {

            JsonRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url + "query?type=daily&name=" + requestList.get(i), null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response){

                    try {
                        String name=(String)response.get("name");
                        String sticker = (String)response.get("ticker");
                        if(data_helper.isFavorite(sticker))
                        {
                            int index = data_helper.getFavoriteIndex(sticker);
                            favorite_data.get(index).name=name;
                            favorite_data.get(index).sticker=sticker;

                        }
                        if(data_helper.isPortfolio(sticker))
                        {
                            int index = data_helper.getPortfolioIndex(sticker);
                            portfolio_data.get(index).name=name;
                            portfolio_data.get(index).sticker=sticker;
                        }
                        Log.d("HTTP",sticker+"NAME FETCHED");
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
            queue.add(jsonRequest);
        }
        if(requestList.size()>0) {
            String IEX_quest = "";
            for (int i = 0; i != requestList.size(); i++) {
                IEX_quest += "," + requestList.get(i);
            }
            IEX_quest = IEX_quest.substring(1);
            Log.d("HTTP", IEX_quest);
            JsonRequest IEX = new JsonArrayRequest(Request.Method.GET, url + "query?type=iex&name=" + IEX_quest, null, new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {

                    try {

                        for (int j = 0; j != requestList.size(); j++) {
                            JSONObject temp = (JSONObject) response.getJSONObject(j);
                            String sticker = (String) temp.get("ticker");
                            Double price = (Double) temp.get("last");
                            Double prev = (Double) temp.get("prevClose");
                            Double change = price - prev;
                            if (data_helper.isFavorite(sticker)) {
                                int index = data_helper.getFavoriteIndex(sticker);
                                favorite_data.get(index).last = price;
                                favorite_data.get(index).change = change;

                                if (data_helper.isPortfolio(sticker)) {
                                    favorite_data.get(index).bought = true;
                                    favorite_data.get(index).share = (Double) (double) data_helper.getPortfolioAmount(sticker);
                                }
                            }
                            if (data_helper.isPortfolio(sticker)) {
                                int index = data_helper.getPortfolioIndex(sticker);
                                portfolio_data.get(index).last = price;
                                portfolio_data.get(index).change = change;
                                portfolio_data.get(index).bought = true;
                                portfolio_data.get(index).share = (Double) (double) data_helper.getPortfolioAmount(sticker);
                            }

                        }
                        Log.d("HTTP", "IEX FETCHED");
                        requestCounter--;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("HTTP", "IEX ERROR");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            queue.add(IEX);
            queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                @Override
                public void onRequestFinished(Request object)
                {
                    if(requestCounter==0) {

                        //Log.d("value",market_value.toString());
                        main_layout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        portfolioAdapter.notifyDataSetChanged();
                    /*TextView date = (TextView) findViewById(R.id.date);
                    LocalDate currentDate = LocalDate.now();
                    String month=currentDate.getMonth().toString();
                    date.setText(month.substring(0,1)+month.substring(1).toLowerCase()+String.format(" %d,%d",currentDate.getDayOfMonth(),currentDate.getYear()));
                    TextView networth = (TextView) findViewById(R.id.net_worth);
                    Double netvalue=0.0;
                    for(int i=0;i!=portfolio_data.size();i++){
                        netvalue+=portfolio_data.get(i).share*portfolio_data.get(i).last;
                    }
                    netvalue+=data_helper.getCash();
                    networth.setText(String.format("%.2f",(float)netvalue.doubleValue()));*/
                    }
                }
            });
        }else{
            requestCounter--;
            main_layout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return ;
        }

    }
    ItemTouchHelper.Callback touchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            final int fromPosition = viewHolder.getAdapterPosition();
            Section tempsec = (Section) portfolioAdapter.getSectionForPosition(fromPosition);
            if(fromPosition==portfolioAdapter.getHeaderPositionInAdapter(favorite_section)||fromPosition==portfolioAdapter.getFooterPositionInAdapter(favorite_section)||fromPosition==portfolioAdapter.getHeaderPositionInAdapter(portfolio_section))
            {
                return makeMovementFlags(0,0);
            }
            else if(tempsec!=portfolio_section)
            {
                return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,ItemTouchHelper.LEFT);
            }else
            {
                return makeMovementFlags(ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END,0);
            }

        }
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            if(viewHolder.getItemViewType()!=target.getItemViewType())
            {
                return false;
            }
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            final int itemCount = portfolioAdapter.getItemCount();
            if(fromPosition<0||toPosition<0||fromPosition>=itemCount||toPosition>=itemCount)return false;
            if(portfolioAdapter.getSectionForPosition(fromPosition)!=portfolioAdapter.getSectionForPosition(toPosition)) {
                return false;
            }
            Section tempsec = (Section) portfolioAdapter.getSectionForPosition(fromPosition);
            final int fpinSection = portfolioAdapter.getPositionInSection(fromPosition);
            final int tpinSection = portfolioAdapter.getPositionInSection(toPosition);
            if(tempsec==portfolio_section)
            {
                Collections.swap(portfolio_data,fpinSection,tpinSection);
                portfolioAdapter.notifyItemMovedInSection(portfolio_section,fpinSection,tpinSection);
                for(int i=0;i!=portfolio_data.size();i++)
                {
                    data_helper.setPortfolio(portfolio_data.get(i).sticker,i);
                }
            }else{
                Collections.swap(favorite_data,fpinSection,tpinSection);
                portfolioAdapter.notifyItemMovedInSection(favorite_section,fpinSection,tpinSection);
                for(int i=0;i!=favorite_data.size();i++)
                {
                    data_helper.setFavorite(favorite_data.get(i).sticker,i);
                }
            }
            return true;
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                View v = viewHolder.itemView.findViewById(R.id.datacell);
                if(v!=null)
                v.setBackgroundColor(Color.GRAY);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            View v = viewHolder.itemView.findViewById(R.id.datacell);
            if(v!=null)
                v.setBackgroundColor(Color.WHITE);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final int index = portfolioAdapter.getPositionInSection(position);
            data_helper.removeFavorite(favorite_data.get(index).sticker);
            favorite_data.remove(index);
            portfolioAdapter.notifyItemRemovedFromSection(favorite_section,index);
            for(int i=0;i!=favorite_data.size();i++)
            {
                data_helper.setFavorite(favorite_data.get(i).sticker,i);
            }
        }
        @Override
        public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
            return 0.5f;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(MainActivity.this,c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.red))
                    .create().decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


}



