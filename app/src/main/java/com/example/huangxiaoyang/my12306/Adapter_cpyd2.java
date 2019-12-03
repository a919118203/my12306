package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/09/04.
 */

public class Adapter_cpyd2 extends BaseAdapter {

    ArrayList<Map<String,Object>> data;
    Context context;
    public Adapter_cpyd2(ArrayList<Map<String,Object>> data,Context context){
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
            convertView= LayoutInflater.from(context).inflate(R.layout.item_cpyd2,null);
            viewHolder.zwlx=convertView.findViewById(R.id.tv_zwlx);
            viewHolder.syzs=convertView.findViewById(R.id.tv_syzs);
            viewHolder.qian=convertView.findViewById(R.id.tv_qian);
            viewHolder.bt_yuding=convertView.findViewById(R.id.bt_yuding);

            convertView.setTag(viewHolder);
        }
        else viewHolder= (ViewHolder) convertView.getTag();
        viewHolder.zwlx.setText((String) data.get(position).get("zwlx"));
        viewHolder.syzs.setText((String) data.get(position).get("syzs"));
        viewHolder.qian.setText((String) data.get(position).get("qian"));

        viewHolder.bt_yuding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapSerializable map=new MapSerializable();
                map.setMap(data.get(position));
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",map);
                Intent intent=new Intent(context,Activity_cpyd3.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    class ViewHolder{
        TextView zwlx,syzs,qian;
        Button bt_yuding;
    }
}
