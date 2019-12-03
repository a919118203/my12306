package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.Md5Utils;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView tv_wangji;
    private EditText et_user,et_pass;
    private Button bt;
    private CheckBox cb;
    private SharedPreferences sp;
    private SharedPreferences.Editor edit;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(msg.arg1==1){
                        if(cb.isChecked()){
                            edit.putString("user",et_user.getText().toString());
                            edit.putString("pass",et_pass.getText().toString());
                            edit.putBoolean("remannbe",true);
                        }
                        else edit.putBoolean("remannbe",false);
                        edit.putString("cookie",(String)msg.obj);
                        edit.commit();
                        Intent intent=new Intent(MainActivity.this,User_main.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }else Toast.makeText(MainActivity.this,"用户名或者密码错误",Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this,"服务器错误，请重试",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tv_wangji=findViewById(R.id.textView);
        bt=findViewById(R.id.button);
        et_user=findViewById(R.id.editText);
        et_pass=findViewById(R.id.editText2);
        cb=findViewById(R.id.checkBox);

        sp=getSharedPreferences("info",MODE_PRIVATE);
        edit=sp.edit();
        Boolean f=sp.getBoolean("remannbe",false);
        if(f){
            et_user.setText(sp.getString("user",null));
            et_pass.setText(sp.getString("pass",null));
            if(!NetUtils.check(MainActivity.this)){
                Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_LONG).show();
                return;
            }
            new Thread(new MyRunnable()).start();
        }

        tv_wangji.setText(Html.fromHtml("<a href=\"https://www.baidu.com/\">忘记密码？</a>"));
        tv_wangji.setMovementMethod(LinkMovementMethod.getInstance());

        et_pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(et_pass.hasFocus()){
                    if(et_user.getText().toString().length()<1){
                        Drawable drawable=getResources().getDrawable(R.drawable.chijing);
                        drawable.setBounds(new Rect(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight()));
                        et_user.setError("请输入用户名",drawable);
                    }
                }
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_pass.getText().toString().length()<1)
                {
                    Drawable drawable=getResources().getDrawable(R.drawable.chijing);
                    drawable.setBounds(new Rect(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight()));
                    et_pass.setError("请输入密码",drawable);
                }
                else {
                    if(!NetUtils.check(MainActivity.this)){
                        Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_LONG).show();
                        return;
                    }
                    new Thread(new MyRunnable()).start();
                }
            }
        });
    }
    class MyRunnable implements Runnable{
        @Override
        public void run() {
            Message msg=new Message();
            try {
                URL url=new URL(CONSTANT.HOST+"/Login");
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");//设置请求方法
                connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                PrintWriter writer=new PrintWriter(connection.getOutputStream());
                String params="username="+et_user.getText().toString()+"&password="+
                        Md5Utils.MD5(et_pass.getText().toString());
                writer.write(params);
                writer.flush();
                writer.close();


                int responseCode=connection.getResponseCode();
                String result=null;
                if(responseCode==connection.HTTP_OK){
                    XmlPullParser parser= Xml.newPullParser();
                    parser.setInput(new InputStreamReader(connection.getInputStream()));
                    int type=parser.getEventType();
                    while(type!=XmlPullParser.END_DOCUMENT){
                        switch (type){
                            case XmlPullParser.START_TAG:
                                if(parser.getName().equals("result")){
                                    result=parser.nextText();
                                }
                                break;
                        }
                        type=parser.next();
                    }
                    String[] str=connection.getHeaderField("Set-Cookie").split(";");
                    String header=str[0];
                    msg.what=1;
                    msg.arg1=Integer.valueOf(result);
                    msg.obj=header;
                }
                else msg.what=2;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                msg.what=2;
            } catch (IOException e) {
                e.printStackTrace();
                msg.what=2;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                msg.what=2;
            }
            handler.sendMessage(msg);
        }
    }
}
