package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wodelianxiren extends AppCompatActivity {

    private ListView lv;
    private ArrayList<Map<String,Object>> data=new ArrayList<>();
    private Adapter_wdlxr adapter;
    private Button bt;
    private boolean f;//区分是从“我的”来还是从订车票来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wodelianxiren);
        Toolbar toolbar = findViewById(R.id.toolbar);
        lv=findViewById(R.id.wdlxr_list);
        bt=findViewById(R.id.cpyd3_bt_tjlxr);
        setSupportActionBar(toolbar);

        adapter=new Adapter_wdlxr(data,this);
        Intent intent=getIntent();
        f=intent.getBooleanExtra("isWode",false);
        System.out.println("falg"+f);
        adapter.goneCheckBox(!f);
        if(f){
            bt.setVisibility(View.GONE);
        }
        createData();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Wodelianxiren.this,Dispaly_wdlxr.class);
                MapSerializable map=new MapSerializable();
                map.setMap(data.get(position));
                int isAdd;
                if(!f&&position==0)isAdd=4;
                else isAdd=2;
                map.getMap().put("isAdd",isAdd);
                map.getMap().put("w",position);
                Bundle bundle=new Bundle();
                bundle.putSerializable("map",map);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int len=data.size();
                Intent intent=new Intent();
                Bundle bundle=new Bundle();
                MapSerializable[] map=new MapSerializable[len+10];
                int mapcnt=0;
                for(int i=0;i<len;i++){
                    if(adapter.isChecked(i)){
                        MapSerializable m=new MapSerializable();
                        m.setMap(data.get(i));
                        map[mapcnt++]=m;
                    }
                }
                bundle.putSerializable("data",map);
                intent.putExtras(bundle);
                intent.putExtra("datalen",mapcnt);
                setResult(1,intent);
                Wodelianxiren.this.finish();
            }
        });

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.fanhui);
        }
    }

    private void createData() {

        data.clear();
        if(!NetUtils.check(Wodelianxiren.this)){
            Toast.makeText(Wodelianxiren.this,"网络异常",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    String http;
                    if(f)http=CONSTANT.HOST+"/otn/PassengerList";//标记是从我的联系人按钮来的
                    else http=CONSTANT.HOST+"/otn/TicketPassengerList";//标记是从车票预定来的
                    URL url=new URL(http);
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                    SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                    connection.setRequestProperty("Cookie",sp.getString("cookie",""));

                    int responseCode=connection.getResponseCode();
                    String result="";
                    if(responseCode==HttpURLConnection.HTTP_OK){
                        String str;
                        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while((str=reader.readLine())!=null){
                            result+=str;
                        }
                        JSONArray array=new JSONArray(result);
                        for(int i=0;i<array.length();i++){
                            Map<String,Object>map=new HashMap<>();
                            JSONObject ojb= (JSONObject) array.get(i);
                            map.put("name",ojb.getString("name"));
                            map.put("type_sfz",ojb.getString("idType"));
                            map.put("idcard",ojb.getString("id"));
                            map.put("type_ck",ojb.getString("type"));
                            map.put("tel",ojb.getString("tel"));
                            data.add(map);
                        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.wdlxr_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
            case R.id.add_wdlxr:
                Intent intent=new Intent(Wodelianxiren.this,Dispaly_wdlxr.class);
                Map<String,Object>map=new HashMap<>();
                map.put("name","");
                map.put("type_sfz","");
                map.put("idcard","");
                map.put("type_ck","");
                map.put("tel","");
                map.put("isAdd",1);//标记是添加联系人
                map.put("w",data.size());//标记修改的是第几个，用于更新列表
                Bundle bundle=new Bundle();
                MapSerializable serializable=new MapSerializable();
                serializable.setMap(map);
                bundle.putSerializable("map",serializable);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==1)
                {
                    Map<String,Object> data_map=((MapSerializable)data.getExtras().getSerializable("map")).getMap();
                    int w=(int)data_map.get("w");
                    this.data.remove(w);
                    this.data.add(w,data_map);
                    adapter.notifyDataSetChanged();
                }
                else if(resultCode==-1){
                    int w=data.getIntExtra("w",-1);
                    if(w!=-1)
                    this.data.remove(w);
                    adapter.notifyDataSetChanged();
                }
                break;
            case 2:
                if(resultCode==1){
                    Map<String,Object> data_map2=((MapSerializable)data.getExtras().getSerializable("map")).getMap();
                    this.data.add(data_map2);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
