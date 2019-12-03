package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Activity_cpyd1 extends AppCompatActivity implements View.OnClickListener{

    private ListView lv;
    private Adapter_cpyd1 adapter;
    private TextView cpyd1_tv_riqi,cpyd1_tv_city
            ,cpyd1_bt_qyt,cpyd1_bt_hyt;
    private ArrayList<Map<String,Object>> data=new ArrayList<>();
    private String from,to;
    private Calendar cl;
    private String riqi;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    adapter.notifyDataSetChanged();
                    break;
                case 2:
                    Toast.makeText(Activity_cpyd1.this,"没有信息",Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chepiaoyuding1);
        Activityglq.addActivity(this);

        lv=findViewById(R.id.cpyd1_lv);
        cpyd1_tv_city=findViewById(R.id.cpyd1_tv_city);
        cpyd1_tv_riqi=findViewById(R.id.cpyd1_tv_riqi);
        cpyd1_bt_qyt=findViewById(R.id.cpyd1_bt_qyt);
        cpyd1_bt_hyt=findViewById(R.id.cpud1_bt_hyt);
        cpyd1_bt_qyt.setOnClickListener(this);
        cpyd1_bt_hyt.setOnClickListener(this);

        Intent intent=getIntent();
        from=intent.getStringExtra("from");
        to=intent.getStringExtra("to");
        Bundle bundle=intent.getExtras();
        MapSerializable map= (MapSerializable) bundle.getSerializable("riqi");
        cl= (Calendar) map.getMap().get("riqi");

        cpyd1_tv_city.setText(from+"->"+to);

        adapter=new Adapter_cpyd1(data,this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(Activity_cpyd1.this,Activity_cpyd2.class);
                intent.putExtra("from",from);
                intent.putExtra("to",to);
                intent.putExtra("checi",String.valueOf(data.get(position).get("checi")));
                MapSerializable map=new MapSerializable();
                Map<String,Object>m=new HashMap<>();
                m.put("riqi",cl);
                map.setMap(m);
                Bundle bundle=new Bundle();
                bundle.putSerializable("riqi",map);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        createData();
    }

    private void createData() {
        data.clear();
        riqi=cl.get(Calendar.YEAR)+"-"+(cl.get(Calendar.MONTH)+1)+"-"+cl.get(Calendar.DAY_OF_MONTH);
        final String qingqiu="fromStation="+from+"&toStation="+to+"&startTrainDate="+riqi;
        System.out.println(qingqiu);


        cpyd1_tv_riqi.setText(riqi);
        if(!NetUtils.check(this)){
            Toast.makeText(this,"网络异常",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try{
                    URL url=new URL(CONSTANT.HOST+"/otn/TrainList");
                    HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                    SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                    connection.setRequestProperty("Cookie",sp.getString("cookie",""));

                    PrintWriter pw=new PrintWriter(connection.getOutputStream());
                    pw.write(qingqiu);
                    pw.flush();
                    pw.close();

                    int responseCode=connection.getResponseCode();
                    StringBuilder result=new StringBuilder();
                    if(responseCode==HttpURLConnection.HTTP_OK){
                        String lins;
                        BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while((lins=reader.readLine())!=null){
                            result.append(lins);
                        }

                        if(result.toString().equals("[]")){
                            handler.sendEmptyMessage(2);
                            return;
                        }
                        JSONArray array=new JSONArray(result.toString());
                        System.out.println(result.toString());
                        for(int i=0;i<array.length();i++){
                            JSONObject train= (JSONObject) array.get(i);
                            Map<String,Object> map=new HashMap<>();
                            map.put("checi",train.getString("trainNo"));
                            map.put("startt",train.getString("startTime"));
                            map.put("endt",train.getString("arriveTime")+
                                    "("+train.getString("dayDifference")+ "日)");
                            map.put("shang",R.drawable.flg_shi);
                            map.put("xia",R.drawable.flg_zhong);
                            JSONObject seats=train.getJSONObject("seats");
                            Iterator<String> it=seats.keys();
                            int cnt=0;
                            while(it.hasNext()){
                                JSONObject obj=seats.getJSONObject(it.next());
                                map.put("zw"+(++cnt),obj.getString("seatName")+":"+obj.getString("seatNum"));
                            }
                            data.add(map);
                            handler.sendEmptyMessage(1);
                        }

                    }
                    else handler.sendEmptyMessage(2);
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
    }

    @Override
    protected void onDestroy() {
        Activityglq.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cpyd1_bt_qyt:
                cl.add(Calendar.DAY_OF_MONTH,-1);
                createData();
                break;
            case R.id.cpud1_bt_hyt:
                cl.add(Calendar.DAY_OF_MONTH,1);
                createData();
                break;
        }
    }
}
