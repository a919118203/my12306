package com.example.huangxiaoyang.my12306;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.Md5Utils;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by HuangXiaoyang on 2018/7/3.
 */

public class Fragment_wode extends Fragment {
    private ListView lv;
    private Button bt;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(msg.arg1==1){
                        Intent intent2=new Intent(getActivity(),mypassword.class);
                        startActivity(intent2);
                    }
                    else{
                        Toast.makeText(getActivity(),"密码不正确",Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_rtwode,container,false);
        lv=view.findViewById(R.id.listview);
        bt=view.findViewById(R.id.button2);

        List<String> list=new ArrayList<>();
        list.add("我的联系人");
        list.add("我的账户");
        list.add("我的密码");

        lv.setAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1,list));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        Intent intent=new Intent(getActivity(),Wodelianxiren.class);
                        intent.putExtra("isWode",true);
                        startActivity(intent);
                        break;
                    case 1:
                        if(!NetUtils.check(getActivity())){
                            Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_LONG).show();
                            return;
                        }
                        new Thread(){
                            @Override
                            public void run() {
                                try{
                                    URL url=new URL(CONSTANT.HOST+"/otn/Account");
                                    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                                    connection.setRequestMethod("POST");//设置请求方法
                                    connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                                    SharedPreferences sp=getActivity().getSharedPreferences("info",MODE_PRIVATE);
                                    String val=sp.getString("cookie","");
                                    connection.setRequestProperty("Cookie",val);//设置头部

                                    PrintWriter writer=new PrintWriter(connection.getOutputStream());
                                    String params="&action=query";
                                    writer.write(params);
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
                                        reader.close();
                                        JSONObject obj=new JSONObject(result);
                                        Map<String,Object>map=new HashMap<>();
                                        map.put("name",obj.getString("name"));
                                        map.put("type_sfz",obj.getString("idType"));
                                        map.put("idcard",obj.getString("id"));
                                        map.put("type_ck",obj.getString("type"));
                                        map.put("tel",obj.getString("tel"));
                                        map.put("username",obj.getString("username"));
                                        map.put("isAdd",3);
                                        map.put("w",-1);
                                        MapSerializable m=new MapSerializable();
                                        m.setMap(map);
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable("map",m);
                                        Intent intent1=new Intent(getActivity(),Dispaly_wdlxr.class);
                                        intent1.putExtras(bundle);
                                        startActivity(intent1);
                                }
                            } catch (ProtocolException e) {
                                    e.printStackTrace();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            }.start();
                        break;
                    case 2:
                        View lins_view=getLayoutInflater().inflate(R.layout.dialog_password,null);
                        final EditText et=lins_view.findViewById(R.id.oldPassword);
                        new AlertDialog.Builder(getActivity())
                                .setTitle("验证")
                                .setView(lins_view)
                                .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!NetUtils.check(getActivity())){
                                            Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        new Thread(){
                                            @Override
                                            public void run() {
                                                try{
                                                    URL url=new URL(CONSTANT.HOST+"/otn/AccountPassword");
                                                    HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                                                    connection.setRequestMethod("POST");//设置请求方法
                                                    connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                                                    SharedPreferences sp=getActivity().getSharedPreferences("info",MODE_PRIVATE);
                                                    String val=sp.getString("cookie","");
                                                    connection.setRequestProperty("Cookie",val);//设置头部

                                                    PrintWriter writer=new PrintWriter(connection.getOutputStream());
                                                    String params="oldPassword="+et.getText().toString()+"&action=query";
                                                    writer.write(params);
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
                                                        Message msg=new Message();
                                                        msg.what=1;
                                                        if("\"1\"".equals(result)){
                                                            msg.arg1=1;
                                                        }else msg.arg1=0;
                                                        handler.sendMessage(msg);
                                                    }
                                                } catch (ProtocolException e) {
                                                    e.printStackTrace();
                                                } catch (MalformedURLException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }.start();
                                    }
                                })
                                .setPositiveButton("取消",null)
                                .create()
                                .show();

                        break;
                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetUtils.check(getActivity())){
                    Toast.makeText(getActivity(),"网络异常",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(new MyRunnable()).start();
            }
        });

        return view;
    }

    class MyRunnable implements Runnable{
        @Override
        public void run() {
            try {
                URL url=new URL(CONSTANT.HOST+"/otn/Logout");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//设置请求方法
                connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                SharedPreferences sp=getActivity().getSharedPreferences("info",MODE_PRIVATE);
                SharedPreferences.Editor edit=sp.edit();
                String val=sp.getString("cookie","");
                connection.setRequestProperty("Cookie",val);//设置头部

                int responseCode=connection.getResponseCode();
                String result="";
                if(responseCode==HttpURLConnection.HTTP_OK){
                    String str;
                    BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while((str=reader.readLine())!=null){
                        result+=str;
                    }
                    if("\"1\"".equals(result)){
                        Intent intent3=new Intent(getActivity(),MainActivity.class);
                        startActivity(intent3);
                        edit.putBoolean("remannbe",false);
                        edit.commit();
                        getActivity().finish();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
