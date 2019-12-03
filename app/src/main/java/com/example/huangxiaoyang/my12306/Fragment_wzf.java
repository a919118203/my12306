package com.example.huangxiaoyang.my12306;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/08/27.
 */

public class Fragment_wzf extends Fragment {
    private ListView lv;
    private Adapter_dd adapter;
    private ArrayList<Map<String,Object>> data=new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_wzf,null);
        lv=view.findViewById(R.id.wzf_lv);
        adapter=new Adapter_dd(data,getActivity());
        lv.setAdapter(adapter);
        createData();
        return view;
    }

    private void createData() {
        data.clear();
        if(!NetUtils.check(getActivity())){
            Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    URL url=new URL(CONSTANT.HOST+"/otn/OrderList");
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                    SharedPreferences sp=getActivity().getSharedPreferences("info",getActivity().MODE_PRIVATE);
                    connection.setRequestProperty("Cookie",sp.getString("cookie",""));

                    PrintWriter writer=new PrintWriter(connection.getOutputStream());
                    writer.write("status=0");
                    writer.flush();
                    writer.close();

                    int responseCode=connection.getResponseCode();
                    String result="";
                    if(responseCode==HttpURLConnection.HTTP_OK){
                        String str;
                        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while((str=reader.readLine())!=null){
                            result+=str;
                        }
                        System.out.println(result);
                        JSONArray zxinxi=new JSONArray(result);
                        JSONObject xinxi,train;

                        for(int i=0;i<zxinxi.length();i++){
                            xinxi=zxinxi.getJSONObject(i);
                            Map<String,Object>map=new HashMap<>();
                            train=xinxi.getJSONObject("train");
                            map.put("checi",train.getString("trainNo"));
                            map.put("ddph",xinxi.getString("id"));
                            map.put("zfqk","已支付");
                            map.put("riqi",train.getString("startTrainDate"));
                            if(train.has("fromStationName"))
                            map.put("city",train.getString("fromStationName")+"->"+train.getString("toStationName")
                                    +"  "+xinxi.getString("status")+"人");
                            else map.put("city",train.getString("startStationName"));
                            map.put("zonge",xinxi.getString("orderPrice"));
                            data.add(map);
                        }
                        handler.sendEmptyMessage(1);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
