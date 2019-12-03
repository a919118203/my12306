package com.example.huangxiaoyang.my12306;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class Activity_cpyd3 extends AppCompatActivity {
    private TextView tv_tijiao,tv_tianjia,tv_zonge;
    private TextView tv_startcity,tv_startt,tv_checi,tv_endcity,
    tv_riqi,tv_endt,tv_zw,tv_jiaqian;
    private int syzs;//剩余车票张数
    private ListView lv;
    private Adapter_cpyd3 adapter;
    private ArrayList<Map<String,Object>>data=new ArrayList<>();
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    String s=tv_riqi.getText().toString();
                    Intent intent=new Intent(Activity_cpyd3.this,Activity_cpyd4.class);
                    intent.putExtra("checi",tv_checi.getText().toString());
                    intent.putExtra("riqi",s.substring(0,s.length()-3));
                    intent.putExtra("order",(String) msg.obj);
                    intent.putExtra("zonge",tv_zonge.getText().toString().split("¥")[1]);
                    intent.putExtra("s_c",tv_startcity.getText().toString());
                    intent.putExtra("e_c",tv_endcity.getText().toString());
                    intent.putExtra("s_t",tv_startt.getText().toString());
                    MapSerializable list=new MapSerializable();
                    list.setList(data);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("data", list);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case 2:
                    Toast.makeText(Activity_cpyd3.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chepiaoyuding3);
        Activityglq.addActivity(this);

        init();
        tv_tianjia=findViewById(R.id.cpyd3_bt_tianjia);
        tv_tijiao=findViewById(R.id.bt_tijiao);
        tv_zonge=findViewById(R.id.tv_zonge);
        lv=findViewById(R.id.cpyd3_lv);

        adapter=new Adapter_cpyd3(data,this);
        lv.setAdapter(adapter);

        tv_tianjia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Activity_cpyd3.this,Wodelianxiren.class);
                startActivityForResult(intent,1);
            }
        });

        tv_tijiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.size()<=0){
                    Toast.makeText(Activity_cpyd3.this,"还没添加联系人！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(data.size()>syzs){
                    Toast.makeText(Activity_cpyd3.this,"没有这么多票！",Toast.LENGTH_SHORT).show();
                    return;
                }
                final String s=tv_riqi.getText().toString();
                if(!NetUtils.check(Activity_cpyd3.this)){
                    Toast.makeText(Activity_cpyd3.this,"网络异常",Toast.LENGTH_LONG).show();
                    return;
                }
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            URL url=new URL(CONSTANT.HOST+"/otn/Order");
                            HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                            SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                            connection.setRequestProperty("Cookie",sp.getString("cookie",""));

                            String lins=tv_zw.getText().toString();//辅助用的字符串
                            PrintWriter writer=new PrintWriter(connection.getOutputStream());
                            String qingqiu="trainNo="+tv_checi.getText().toString()+"&startTrainDate="+s.substring(0,s.length()-3)+
                                    "&seatName="+lins.substring(0,lins.indexOf("("));
                            int len=data.size();
                            for(int i=0;i<len;i++){
                                qingqiu+="&id="+data.get(i).get("idcard");
                            }
                            for(int i=0;i<len;i++){
                                qingqiu+="&idType="+data.get(i).get("type_sfz");
                            }
                            System.out.println(qingqiu);
                            writer.write(qingqiu);
                            writer.flush();
                            writer.close();

                            int responseCode=connection.getResponseCode();
                            String result="";
                            Message msg=new Message();
                            if(responseCode==HttpURLConnection.HTTP_OK){
                                String str;
                                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while((str=reader.readLine())!=null){
                                    result+=str;
                                }
                                JSONObject object=new JSONObject(result);
                                if(!object.isNull("id")){
                                    msg.what=1;
                                    msg.obj=object.getString("id");
                                    JSONArray array=object.getJSONArray("passengerList");
                                    for(int i=0;i<array.length();i++){
                                        JSONObject obj= ((JSONObject) array.get(i)).getJSONObject("seat");
                                        data.get(i).put("座位号",obj.getString("seatNo"));
                                    }
                                }
                            }else{
                                msg.what=2;
                                msg.obj=result;
                            }
                            handler.sendMessage(msg);
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
        });
    }

    private void init() {
        tv_startcity=findViewById(R.id.tv_startcity);
        tv_startt=findViewById(R.id.tv_starttime);
        tv_checi=findViewById(R.id.tv_cci);
        tv_endcity=findViewById(R.id.tv_endcity);
        tv_riqi=findViewById(R.id.tv_endriqi);
        tv_endt=findViewById(R.id.tv_endtime);
        tv_zw=findViewById(R.id.tv_zw);
        tv_jiaqian=findViewById(R.id.tv_jiaqian);

        Intent intent=getIntent();
        Map<String,Object>map=((MapSerializable)intent.getExtras().getSerializable("data")).getMap();
        tv_startcity.setText((String)map.get("from"));
        tv_endcity.setText((String)map.get("to"));
        tv_startt.setText((String)map.get("startt"));
        tv_endt.setText((String)map.get("endt"));
        tv_checi.setText((String)map.get("checi"));
        tv_riqi.setText((String)map.get("riqi")+"("+(String)map.get("kua")+")");
        tv_zw.setText((String)map.get("zwlx")+"("+(String)map.get("syzs")+")");
        tv_jiaqian.setText((String)map.get("qian"));
        syzs=Integer.valueOf((String) map.get("syzs"));
    }

    @Override
    protected void onDestroy() {
        Activityglq.removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==1){
                    this.data.clear();
                    int len=data.getIntExtra("datalen",0);
                    MapSerializable[] map= (MapSerializable[]) data.getExtras().getSerializable("data");
                    for(int i=0;i<len;i++){
                        this.data.add(map[i].getMap());
                    }
                    tv_zonge.setText("订单总额：¥"+(double)this.data.size()*Double.valueOf(tv_jiaqian.getText().toString())+"元");
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    public class Adapter_cpyd3 extends BaseAdapter {

        private ArrayList<Map<String,Object>> data;
        private Context context;
        public Adapter_cpyd3(ArrayList<Map<String,Object>> data,Context context){
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
        public View getView(final int position, View convertView, final ViewGroup parent) {
            ViewHolder viewHolder=new ViewHolder();
            if(convertView==null){
                convertView= LayoutInflater.from(context).inflate(R.layout.item_cpyd3,null);
                viewHolder.name=convertView.findViewById(R.id.cpyd3_name);
                viewHolder.idcard=convertView.findViewById(R.id.cpyd3_idcard);
                viewHolder.tel=convertView.findViewById(R.id.cpyd3_tel);
                viewHolder.shanchu=convertView.findViewById(R.id.cpyd3_image);
                convertView.setTag(viewHolder);
            }
            else viewHolder= (ViewHolder) convertView.getTag();
            viewHolder.name.setText((String) data.get(position).get("name"));
            viewHolder.idcard.setText((String) data.get(position).get("idcard"));
            viewHolder.tel.setText((String) data.get(position).get("tel"));
            viewHolder.shanchu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(position);
                    String str=tv_zonge.getText().toString();
                    double a=Double.valueOf(str.substring(6,str.length()-1));//总额
                    double b=Double.valueOf(tv_jiaqian.getText().toString());//单价
                    tv_zonge.setText("订单总额：¥"+(a-b)+"元");
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder{
            TextView name,idcard,tel;
            ImageView shanchu;
        }
    }
}
