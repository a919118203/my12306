package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CsAdapter extends BaseAdapter {
    private List<Cs_Name> list;
    private Cs_Name cs_name;
    private Context context;
    private LayoutInflater mInflater;

    public CsAdapter(Context context, List list) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list, null);
        }
        ViewHolder holder = getViewHolder(convertView);
        cs_name = list.get(position);
        if(position == 0){
            holder.tv_firstWord.setVisibility(View.VISIBLE);
            holder.tv_firstWord.setText(cs_name.getFirstWord());
            holder.tv_name.setText(cs_name.getCs_Name());
        } else{
            if (CharacterUtils.getCnAscii(list.get(position - 1).getFirstWord().charAt(0)) <
                    CharacterUtils.getCnAscii(cs_name.getFirstWord().charAt(0))) {
                holder.tv_firstWord.setVisibility(View.VISIBLE);
                holder.tv_firstWord.setText(cs_name.getFirstWord());
                holder.tv_name.setText(cs_name.getCs_Name());
            } else {
                holder.tv_firstWord.setVisibility(View.GONE);
                holder.tv_name.setText(cs_name.getCs_Name());
            }
        }
        return convertView;
    }

    private ViewHolder getViewHolder(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder == null) {
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        return holder;
    }

    private class ViewHolder {

        private TextView tv_firstWord, tv_name;

        ViewHolder(View view) {
            tv_firstWord = (TextView) view.findViewById(R.id.tv_firstWord);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
        }
    }
    public int getSelectPosition(String s){
        for(int i = 0; i < getCount(); i++){
            String first_word = list.get(i).getFirstWord();
            if(first_word.equals(s)){
                return i;
            }
        }
        return -1;
    }
}