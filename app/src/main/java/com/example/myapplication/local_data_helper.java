package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.lang.Math.abs;

public class local_data_helper {
    SharedPreferences portfolio_data,favorite_data;
    public local_data_helper(Context ctx){
        portfolio_data = ctx.getSharedPreferences("portfolio",ctx.MODE_PRIVATE);
        favorite_data = ctx.getSharedPreferences("favorite",ctx.MODE_PRIVATE);
        if(!portfolio_data.contains("ticker_set")){
            SharedPreferences.Editor editor = portfolio_data.edit();
            Set<String> newstringset = (Set<String>) new HashSet<String>();
            editor.putStringSet("ticker_set",newstringset);
            editor.putFloat("cash",20000.0f);
            editor.commit();
        }
        if(!favorite_data.contains("ticker_set")){
            SharedPreferences.Editor editor = favorite_data.edit();
            Set<String> newstringset = (Set<String>) new HashSet<String>();
            editor.putStringSet("ticker_set",newstringset);

            editor.commit();
        }
    }
    public String[] getFavorite(){
        Set<String> ticker_list = favorite_data.getStringSet("ticker_set",null);
        Iterator<String> ticker_iter = ticker_list.iterator();
        String[] result = new String[ticker_list.size()];
        while(ticker_iter.hasNext())
        {
            String ticker_name = ticker_iter.next();
            int index = favorite_data.getInt(ticker_name,-1);
            result[index] = ticker_name;
        }
        return result;
    }
    public void addFavorite(String ticker_name){
        Set<String> ticker_list = favorite_data.getStringSet("ticker_set",null);
        SharedPreferences.Editor editor = favorite_data.edit();
        if(!ticker_list.contains(ticker_name))
        {
            editor.putInt(ticker_name,ticker_list.size());
            ticker_list.add(ticker_name);
            editor.putStringSet("ticker_set",ticker_list);
            editor.commit();
        }
    }
    public void removeFavorite(String ticker_name){
        Set<String> ticker_list = favorite_data.getStringSet("ticker_set",null);
        SharedPreferences.Editor editor = favorite_data.edit();
        if(ticker_list.contains(ticker_name))
        {
            int index = favorite_data.getInt(ticker_name,-1);
            ticker_list.remove(ticker_name);
            editor.remove(ticker_name);
            editor.putStringSet("ticker_set",ticker_list);
            Iterator<String> ticker_iter = ticker_list.iterator();
            while(ticker_iter.hasNext())
            {
                String temp_name = ticker_iter.next();
                int temp_index = favorite_data.getInt(temp_name,-1);
                if(temp_index>index)
                {
                    editor.putInt(temp_name,temp_index-1);
                }
            }
            editor.commit();
        }
    }
    public void setFavorite(String ticker,int position)
    {
        SharedPreferences.Editor editor = favorite_data.edit();
        editor.putInt(ticker,position);
        editor.commit();
    }
    public void setPortfolio(String ticker,int position)
    {
        SharedPreferences.Editor editor = portfolio_data.edit();
        editor.putInt(ticker,position);
        editor.commit();
    }
    public int getFavoriteIndex(String ticker_name){
        return favorite_data.getInt(ticker_name,-1);
    }
    public boolean isFavorite(String ticker_name){
        return favorite_data.contains(ticker_name);
    }
    public String[] getPortfolio(){
        Set<String> ticker_list = portfolio_data.getStringSet("ticker_set",null);
        Iterator<String> ticker_iter = ticker_list.iterator();
        String[] result = new String[ticker_list.size()];
        while(ticker_iter.hasNext())
        {
            String ticker_name = ticker_iter.next();
            int index = portfolio_data.getInt(ticker_name,-1);
            result[index] = ticker_name;
        }
        return result;
    }
    public void addPortfolio(String ticker_name,Float amount){
        Set<String> ticker_list = portfolio_data.getStringSet("ticker_set",null);
        SharedPreferences.Editor editor = portfolio_data.edit();
        if(!ticker_list.contains(ticker_name))
        {
            editor.putInt(ticker_name,ticker_list.size());
            ticker_list.add(ticker_name);
            editor.putFloat("num_"+ticker_name,amount);
            editor.putStringSet("ticker_set",ticker_list);
        }else{
            editor.putFloat("num_"+ticker_name,portfolio_data.getFloat("num_"+ticker_name,-1)+amount);
        }
        editor.commit();
    }
    public void removePortfolio(String ticker_name,Float amount){
        Set<String> ticker_list = portfolio_data.getStringSet("ticker_set",null);
        SharedPreferences.Editor editor = portfolio_data.edit();
        Float current_amount = portfolio_data.getFloat("num_"+ticker_name,-1);
        if(abs(current_amount-amount)<0.00000001)
        {
            int index = portfolio_data.getInt(ticker_name,-1);
            ticker_list.remove(ticker_name);
            editor.remove(ticker_name);
            editor.remove("num_"+ticker_name);
            editor.putStringSet("ticker_set",ticker_list);
            Iterator<String> ticker_iter = ticker_list.iterator();
            while(ticker_iter.hasNext())
            {
                String temp_name = ticker_iter.next();
                int temp_index = portfolio_data.getInt(temp_name,-1);
                if(temp_index>index)
                {
                    editor.putInt(temp_name,temp_index-1);
                }
            }
        }
        else{
            editor.putFloat("num_"+ticker_name,portfolio_data.getFloat("num_"+ticker_name,-1)-amount);
        }
        editor.commit();
    }
    public boolean isPortfolio(String ticker_name){
        return portfolio_data.contains(ticker_name);
    }
    public Float getPortfolioAmount(String ticker_name){
        return portfolio_data.getFloat("num_"+ticker_name,-1);
    }
    public int getPortfolioIndex(String ticker_name){
        return portfolio_data.getInt(ticker_name,-1);
    }
    public float getCash(){
        return portfolio_data.getFloat("cash",-1);
    }
    public void addCash(Float amount){
        SharedPreferences.Editor editor = portfolio_data.edit();
        editor.putFloat("cash",getCash()+amount);
        editor.commit();
    }
    public void removeCash(Float amount){
        SharedPreferences.Editor editor = portfolio_data.edit();
        editor.putFloat("cash",getCash()-amount);
        editor.commit();
    }
}
