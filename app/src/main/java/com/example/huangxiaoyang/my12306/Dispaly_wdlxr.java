package com.example.huangxiaoyang.my12306;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dispaly_wdlxr extends AppCompatActivity {

    private ListView lv;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private List<Map<String,Object>> data;
    private SimpleAdapter adapter;
    private Intent intent;
    private Button bt_baocun;
    private int w;//这里显示的联系人是联系人列表中的第几个
    private int flag;//标志是否是添加联系人
    private String[] Title=new String[]{"姓名","证件类型","证件号码","乘客类型","电话"};
    private String[] Key=new String[]{"name","type_sfz","idcard","type_ck","tel"};
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(msg.arg1==1){
                        Intent intent=new Intent();
                        intent.putExtra("w",w);
                        setResult(-1,intent);
                        Dispaly_wdlxr.this.finish();
                        Toast.makeText(Dispaly_wdlxr.this,"成功删除",Toast.LENGTH_LONG).show();
                    }
                    break;
                case 2:
                    Toast.makeText(Dispaly_wdlxr.this,"还有未输入的地方！",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispaly_wdlxr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createData();
        lv=findViewById(R.id.listview_dispaly_wdlxr);
        bt_baocun=findViewById(R.id.button_dispaly_wdlxr_baocun);

        bt_baocun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i=0;i<5;i++){
                    System.out.println("wtf:"+data.get(i).get("data"));
                    if(data.get(i).get("data").equals("")){
                        handler.sendEmptyMessage(2);
                        return;
                    }
                }
                if(!NetUtils.check(Dispaly_wdlxr.this)){
                    Toast.makeText(Dispaly_wdlxr.this,"网络异常",Toast.LENGTH_LONG).show();
                    return;
                }

                new Thread(){
                    @Override
                    public void run() {
                        try{
                            String http;
                            if(flag!=3)
                            http=CONSTANT.HOST+"/otn/Passenger";
                            else http=CONSTANT.HOST+"/otn/Account";
                            URL url=new URL(http);
                            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");//设置请求方法
                            connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                            SharedPreferences.Editor edit=sp.edit();
                            String val=sp.getString("cookie","");
                            connection.setRequestProperty("Cookie",val);//设置头部

                            PrintWriter writer=new PrintWriter(connection.getOutputStream());
                            String str="姓名="+data.get(0).get("data")+"&证件类型="+data.get(1).get("data")+
                                    "&证件号码="+data.get(2).get("data")+"&乘客类型="+data.get(3).get("data")+
                                    "&电话="+data.get(4).get("data")+"&action=";
                            str+= flag==1 ?"new":"update";

                            if(flag==3)str="乘客类型="+data.get(4).get("data")+
                                    "&电话="+data.get(5).get("data")+"&action=update";
                            writer.write(str);
                            writer.flush();
                            writer.close();

                            int responseCode=connection.getResponseCode();
                            String result="";
                            if(responseCode==HttpURLConnection.HTTP_OK){
                                String strr;
                                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while((strr=reader.readLine())!=null){
                                    result+=strr;
                                }
                                reader.close();
                                if(result.equals("\"1\"")){
                                    Map<String ,Object> map=new HashMap<>();
                                    for(int i=0;i<5;i++){
                                        map.put(Key[i],data.get(i).get("data"));
                                    }
                                    map.put("w",w);
                                    Intent intent=new Intent();
                                    Bundle bundle=new Bundle();
                                    MapSerializable ser=new MapSerializable();
                                    ser.setMap(map);
                                    bundle.putSerializable("map",ser);
                                    intent.putExtras(bundle);
                                    setResult(1,intent);
                                    Dispaly_wdlxr.this.finish();
                                }else if(result.equals("\"0\"")){

                                }else{
                                    JSONObject obj=new JSONObject(result);
                                    if(obj.getString("tel").equals(data.get(5).get("data"))&&
                                            obj.getString("type").equals(data.get(4).get("data"))){
                                        Dispaly_wdlxr.this.finish();
                                    }
                                }
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
        });
        lv.setAdapter(adapter=new SimpleAdapter(this,data,R.layout.item_dispaly_wdlxr,
                new String[]{"title","data","image"},
                new int[]{R.id.Ltitle,R.id.Ldata,R.id.Ltu}));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if((int)data.get(position).get("image")==R.drawable.forward_25){
                    EditText et = null;
                    View v=null;
                    RadioGroup rg=null;
                    final String title= (String) data.get(position).get("title");
                    switch (title){
                        case "乘客类型":
                            v=getLayoutInflater().inflate(R.layout.dialog_cklx,null);
                            rg=v.findViewById(R.id.radiogroup);
                            int[] rg_btid=new int[]{R.id.chengren,R.id.ertong,R.id.xuesheng,R.id.qita};
                            String cklx=data.get(position).get("data").toString();
                            for(int i=0;i<4;i++){
                                RadioButton rb=v.findViewById(rg_btid[i]);
                                if(rb.getText().toString().equals(cklx)){
                                    rb.setChecked(true);
                                }
                            }
                            break;
                        case "证件类型":
                            v=getLayoutInflater().inflate(R.layout.dialog_sfzlx,null);
                            rg=v.findViewById(R.id.radiogroup_sfzlx);
                            break;
                        case "电话":
                            v=getLayoutInflater().inflate(R.layout.dialog_tel,null);
                            et=v.findViewById(R.id.dialog_tel);
                            et.setText((String)data.get(position).get("data"));
                            break;
                        default:
                            v=getLayoutInflater().inflate(R.layout.dialog_uncklx,null);
                            et=v.findViewById(R.id.dispaly_wdlxr_xiugai);
                            et.setText((String) data.get(position).get("data"));
                            break;

                    }
                    final EditText finalEt = et;
                    final View finalV = v;
                    final RadioGroup finalRg = rg;

                    new AlertDialog.Builder(Dispaly_wdlxr.this)
                            .setTitle("输入")
                            .setIcon(R.mipmap.ic_launcher)
                            .setView(v)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (title){
                                        case "乘客类型":
                                        case "证件类型":
                                            RadioButton linshi_rb=finalV.findViewById(finalRg.getCheckedRadioButtonId());
                                            data.get(position).put("data",linshi_rb.getText().toString());
                                            break;
                                        default:
                                            data.get(position).put("data", finalEt.getText().toString());
                                            break;
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            })
                            .setPositiveButton("取消",null)
                            .create()
                            .show();
                }
            }
        });

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.fanhui);
        }
    }

    private void createData() {
        intent=getIntent();
        data=new ArrayList<>();
        Boolean[] Editable=new Boolean[]{false,false,false,true,true};
        Map<String,Object>data_map=((MapSerializable)intent.getExtras().getSerializable("map")).getMap();
        w=(int)data_map.get("w");
        flag =(int)data_map.get("isAdd");
        if(flag==1){
            Editable[0]=true;
            Editable[1]=true;
            Editable[2]=true;
        }else if(flag==2){
            Editable[0]=true;
        }else if(flag==3){
            Map<String,Object>map=new HashMap<>();
            map.put("title","用户名");
            map.put("data",data_map.get("username"));
            map.put("image",R.drawable.flg_null);
            data.add(map);
        }
        for(int i=0;i<5;i++){
            Map<String,Object>map=new HashMap<>();
            map.put("title",Title[i]);
            map.put("data",data_map.get(Key[i]));
            map.put("image",Editable[i]?R.drawable.forward_25:R.drawable.flg_null);
            data.add(map);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(flag==1||flag==2)
        getMenuInflater().inflate(flag==1?R.menu.dispaly_wdlxr_menu_jia:R.menu.dispaly_wdlxr_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.dispaly_wdlxr_jian:
                if(!NetUtils.check(Dispaly_wdlxr.this)){
                    Toast.makeText(Dispaly_wdlxr.this,"网络异常",Toast.LENGTH_LONG).show();
                    break;
                }
                new Thread(){
                    @Override
                    public void run() {
                        try{
                            URL url=new URL(CONSTANT.HOST+"/otn/Passenger");
                            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");//设置请求方法
                            connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);
                            connection.setDoInput(true);
                            connection.setDoOutput(true);

                            SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                            SharedPreferences.Editor edit=sp.edit();
                            String val=sp.getString("cookie","");
                            connection.setRequestProperty("Cookie",val);//设置头部

                            PrintWriter writer=new PrintWriter(connection.getOutputStream());
                            String str="姓名="+data.get(0).get("data")+"&证件类型="+data.get(1).get("data")+
                                    "&证件号码="+data.get(2).get("data")+"&乘客类型="+data.get(3).get("data")+
                                    "&电话="+data.get(4).get("data")+"&action=remove";
                            writer.write(str);
                            writer.flush();
                            writer.close();

                            int responseCode=connection.getResponseCode();
                            String result="";
                            if(responseCode==HttpURLConnection.HTTP_OK){
                                String strr;
                                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                while((strr=reader.readLine())!=null){
                                    result+=strr;
                                }
                                reader.close();
                                Message msg=new Message();
                                msg.what=1;
                                if(result.equals("\"1\"")){
                                    msg.arg1=1;
                                }
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
                break;
            case android.R.id.home:
                setResult(-2);
                this.finish();
                break;
            case R.id.dispaly_wdlxr_jia:

                break;
        }
        return true;
    }

}
