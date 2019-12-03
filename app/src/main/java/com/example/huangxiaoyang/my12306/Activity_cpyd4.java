package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_cpyd4 extends AppCompatActivity {

    private TextView tv_zbzhifu,tv_xzzhifu,tv_ddph;
    private ListView lv;
    private String order;
    private ArrayList<Map<String,Object>> data;
    private String erweimaData;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Intent intent=new Intent(Activity_cpyd4.this,Activity_cpyd5.class);
                    intent.putExtra("erwmdata",erweimaData);
                    intent.putExtra("ddph",order);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(Activity_cpyd4.this,"支付失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chepiaoyuding4);
        Activityglq.addActivity(this);

        tv_zbzhifu=findViewById(R.id.bt_zbzhifu);
        tv_xzzhifu=findViewById(R.id.bt_xzzhifu);
        tv_ddph=findViewById(R.id.cpyd4_ddph);
        Intent intent=getIntent();

        data=((MapSerializable)intent.getExtras().getSerializable("data")).getList();
        order=intent.getStringExtra("order");
        System.out.println("order="+order);
        tv_ddph.setText(tv_ddph.getText().toString().split("：")[0]+"："+order);
        erweimaData="订单编号："+order+"&总金额："+intent.getStringExtra("zonge")+"&乘客：";
        for(int i=0;i<data.size();i++){
            data.get(i).put("wz",data.get(i).get("座位号"));
            data.get(i).put("checi",intent.getStringExtra("checi"));
            data.get(i).put("riqi",intent.getStringExtra("riqi"));
            erweimaData+=data.get(i).get("name")+"、";
        }

        erweimaData+="&出发城市："+intent.getStringExtra("s_c")+"&到达城市："+intent.getStringExtra("e_c")
                +"&出发时间："+intent.getStringExtra("riqi")+","+intent.getStringExtra("s_t");

        lv=findViewById(R.id.cpyd4_lv);

        lv.setAdapter(new SimpleAdapter(this,data,R.layout.item_cpyd4,new String[]{"name","checi","riqi","wz"},
                new int[]{R.id.cpyd4_lv_name,R.id.cpyd4_lv_cci,R.id.cpyd4_lv_riqi,R.id.cpyd4_lv_wz}));


        tv_zbzhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activityglq.stopAll();
            }
        });
        tv_xzzhifu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!NetUtils.check(Activity_cpyd4.this)){
                    Toast.makeText(Activity_cpyd4.this,"网络异常",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            URL url=new URL(CONSTANT.HOST+"/otn/Pay");
                            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                            SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                            connection.setRequestProperty("Cookie",sp.getString("cookie",""));

                            String qingqiu="orderid="+order;
                            PrintWriter writer=new PrintWriter(connection.getOutputStream());
                            writer.write(qingqiu);
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
                            }
                            if(result.equals("\"1\""))
                                handler.sendEmptyMessage(1);
                            else handler.sendEmptyMessage(2);

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Activityglq.removeActivity(this);
        super.onDestroy();
    }
}
