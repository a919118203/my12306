package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/09/05.
 */

public class Adapter_wdlxr extends BaseAdapter {

    private ArrayList<Map<String,Object>> data;
    private Context context;
    private boolean[] flag=new boolean[1000];
    private boolean checkbox=true;

    public Adapter_wdlxr(ArrayList<Map<String,Object>> data,Context context){
        this.data=data;
        this.context=context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_cpyd3_lxr,null);
            viewHolder.name=convertView.findViewById(R.id.cpyd3_lxr_name);
            viewHolder.idcard=convertView.findViewById(R.id.cpyd3_lxr_idcard);
            viewHolder.tel =convertView.findViewById(R.id.cpyd3_lxr_tel);
            viewHolder.xuan=convertView.findViewById(R.id.cpyd3_lxr_checkbox);

            convertView.setTag(viewHolder);
        }
        else viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.name.setText((String)data.get(position).get("name"));
        viewHolder.idcard.setText((String)data.get(position).get("idcard"));
        viewHolder.tel.setText((String)data.get(position).get("tel"));
        viewHolder.xuan.setChecked(flag[position]);
        if(!checkbox)
            viewHolder.xuan.setVisibility(View.GONE);

        viewHolder.xuan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                flag[position]=isChecked;
            }
        });


        return convertView;
    }

    class ViewHolder{
        TextView name,idcard,tel;
        CheckBox xuan;
    }

    public boolean isChecked(int position){
        return flag[position];
    }

    public void goneCheckBox(boolean f){
        checkbox=f;
    }
}
