package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/08/28.
 */

public class Adapter_cpyd1 extends BaseAdapter {

    ArrayList<Map<String,Object>> data;
    Context context;
    public Adapter_cpyd1(ArrayList<Map<String,Object>> data,Context context){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=new ViewHolder();
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.item_cpyd1,null);
            viewHolder.checi=convertView.findViewById(R.id.tv_checi);
            viewHolder.startt=convertView.findViewById(R.id.tv_startt);
            viewHolder.endt=convertView.findViewById(R.id.tv_endt);
            viewHolder.zw1=convertView.findViewById(R.id.tv_zw1);
            viewHolder.zw2=convertView.findViewById(R.id.tv_zw2);
            viewHolder.zw3=convertView.findViewById(R.id.tv_zw3);
            viewHolder.zw4=convertView.findViewById(R.id.tv_zw4);
            viewHolder.shang=convertView.findViewById(R.id.image_shang);
            viewHolder.xia=convertView.findViewById(R.id.image_xia);
            convertView.setTag(viewHolder);
        }
        else viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.checi.setText((String) data.get(position).get("checi"));
        viewHolder.startt.setText((String) data.get(position).get("startt"));
        viewHolder.endt.setText((String) data.get(position).get("endt"));
        viewHolder.zw1.setText((String) data.get(position).get("zw1"));
        viewHolder.zw2.setText((String) data.get(position).get("zw2"));
        viewHolder.zw3.setText((String) data.get(position).get("zw3"));
        viewHolder.zw4.setText((String) data.get(position).get("zw4"));
        viewHolder.shang.setImageResource((Integer) data.get(position).get("shang"));
        viewHolder.xia.setImageResource((Integer) data.get(position).get("xia"));

        return convertView;
    }

    class ViewHolder{
        TextView checi,startt,endt,zw1,zw2,zw3,zw4;
        ImageView shang,xia;
    }
}
