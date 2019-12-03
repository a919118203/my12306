package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

public class Activity_cpyd2 extends AppCompatActivity implements View.OnClickListener{

    private TextView cpyd2_bt_qyt,cpyd2_bt_hyt;
    private TextView tv_riqi,tv_city,cxing,shijian;
    private String from,to,checi,riqi;
    private String startt,endt,kua;
    private Calendar cl;
    private ArrayList<Map<String,Object>> data=new ArrayList<>();
    private Adapter_cpyd2 adapter;
    private ListView lv;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    shijian.setText(String.valueOf(msg.obj));
                    break;
                case 2:
                    shijian.setText("");
                    Toast.makeText(Activity_cpyd2.this,"没有！",Toast.LENGTH_SHORT).show();
                    break;
            }
            adapter.notifyDataSetChanged();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chepiaoyuding2);
        Activityglq.addActivity(this);

        Intent intent=getIntent();
        from=intent.getStringExtra("from");
        to=intent.getStringExtra("to");
        checi=intent.getStringExtra("checi");
        Bundle bundle=intent.getExtras();
        MapSerializable map= (MapSerializable) bundle.getSerializable("riqi");
        cl= (Calendar) map.getMap().get("riqi");

        tv_city=findViewById(R.id.cpyd2_tv_city);
        tv_riqi=findViewById(R.id.cpyd2_tv_riqiofcpyd);
        cxing=findViewById(R.id.cpyd2_tv_cxing);
        shijian=findViewById(R.id.cpyd2_tv_shijian);
        lv=findViewById(R.id.cpyd2_lv);
        cpyd2_bt_qyt=findViewById(R.id.cpyd2_bt_qyt);
        cpyd2_bt_hyt=findViewById(R.id.cpyd2_bt_hyt);
        cpyd2_bt_qyt.setOnClickListener(this);
        cpyd2_bt_hyt.setOnClickListener(this);

        tv_city.setText(from+"->"+to);
        cxing.setText(checi);

        adapter=new Adapter_cpyd2(data,this);
        lv.setAdapter(adapter);
        createData();
    }

    private void createData() {
        data.clear();
        riqi=cl.get(Calendar.YEAR)+"-"+(cl.get(Calendar.MONTH)+1)+"-"+cl.get(Calendar.DAY_OF_MONTH);
        final String qingqiu="fromStation="+from+"&toStation="+to+"&startTrainDate="+riqi+"&trainNo="+checi;

        tv_riqi.setText(riqi);

        if(!NetUtils.check(this)){
            Toast.makeText(this,"网络异常",Toast.LENGTH_LONG).show();
            return;
        }
        new Thread(){
            @Override
            public void run() {
                try{
                    URL url=new URL(CONSTANT.HOST+"/otn/Train");
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
                        System.out.println(result.toString());
                        if(result.toString().equals("null")){
                            handler.sendEmptyMessage(2);
                            return;
                        }
                        JSONObject train=new JSONObject(result.toString());
                        Message message=new Message();
                        message.what=1;
                        startt=train.getString("startTime");
                        endt=train.getString("arriveTime");
                        kua=train.getString("dayDifference");
                        message.obj=startt+" - "+endt+
                                ",历时"+train.getString("durationTime");
                        JSONObject obj=train.getJSONObject("seats");
                        Iterator<String> it=obj.keys();
                        while(it.hasNext()){
                            Map<String,Object>map=new HashMap<>();
                            JSONObject o=obj.getJSONObject(it.next());
                            map.put("zwlx",o.getString("seatName"));
                            map.put("syzs",o.getString("seatNum"));
                            map.put("qian",o.getString("seatPrice"));
                            map.put("checi",checi);
                            map.put("from",from);
                            map.put("to",to);
                            map.put("startt",startt);
                            map.put("endt",endt);
                            map.put("kua",kua);
                            map.put("riqi",riqi);
                            data.add(map);
                        }
                        handler.sendMessage(message);
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
    }


    @Override
    protected void onDestroy() {
        Activityglq.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cpyd2_bt_qyt:
                cl.add(Calendar.DAY_OF_MONTH,-1);
                createData();
                break;
            case R.id.cpyd2_bt_hyt:
                cl.add(Calendar.DAY_OF_MONTH,1);
                createData();
                break;
        }
    }
}
