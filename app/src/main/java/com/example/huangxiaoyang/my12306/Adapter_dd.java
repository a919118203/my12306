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
 * Created by HuangXiaoyang on 2018/09/07.
 */

public class Adapter_dd extends BaseAdapter {

    ArrayList<Map<String,Object>> data;
    Context context;
    public Adapter_dd(ArrayList<Map<String,Object>> data,Context context){
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
            convertView= LayoutInflater.from(context).inflate(R.layout.item_dd,null);
            viewHolder.checi=convertView.findViewById(R.id.dd_cbh);
            viewHolder.ddph=convertView.findViewById(R.id.dd_ddh);
            viewHolder.zfqk=convertView.findViewById(R.id.dd_zfqk);
            viewHolder.riqi=convertView.findViewById(R.id.riqi);
            viewHolder.city=convertView.findViewById(R.id.city);
            viewHolder.zonge=convertView.findViewById(R.id.zonge);

            convertView.setTag(viewHolder);
        }
        else viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.checi.setText((String) data.get(position).get("checi"));
        viewHolder.ddph.setText((String) data.get(position).get("ddph"));
        viewHolder.riqi.setText((String) data.get(position).get("riqi"));
        viewHolder.city.setText((String) data.get(position).get("city"));
        viewHolder.zonge.setText((String) data.get(position).get("zonge"));

        switch ((String) data.get(position).get("zfqk")){
            case "0":
                viewHolder.zfqk.setText("未支付");
                viewHolder.zfqk.setTextColor(android.graphics.Color.parseColor("#f1b53e"));
                break;
            case "1":
                viewHolder.zfqk.setText("已支付");
                viewHolder.zfqk.setTextColor(android.graphics.Color.parseColor("#00aacc"));
                break;
            case "2":
                viewHolder.zfqk.setText("已取消");
                viewHolder.zfqk.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                break;
        }
        return convertView;
    }

    class ViewHolder{
        TextView ddph,zfqk,checi,riqi,city,zonge;
    }
}