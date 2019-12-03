package com.example.huangxiaoyang.my12306;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huangxiaoyang.my12306.utils.CONSTANT;
import com.example.huangxiaoyang.my12306.utils.NetUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class mypassword extends AppCompatActivity {

    private EditText et_password,sure_password_again;
    private TextView tv_passwod,tv_password_again;
    private Button bt_sure;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(msg.arg1==1){
                        Toast.makeText(mypassword.this,"修改密码成功",Toast.LENGTH_LONG).show();
                        SharedPreferences sp=getSharedPreferences("info",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString("password",tv_passwod.getText().toString());
                        mypassword.this.finish();
                    }
                    else Toast.makeText(mypassword.this,"修改密码失败",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypassword);
        et_password = findViewById(R.id.et_password);
        sure_password_again = findViewById(R.id.sure_password_again);
        tv_passwod = findViewById(R.id.tv_password);
        tv_password_again = findViewById(R.id.sure_password_again);
        bt_sure = findViewById(R.id.bt_sure);

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s1=et_password.getText().toString(),
                        s2=sure_password_again.getText().toString();
                if(s1.equals(s2)){
                    if(!NetUtils.check(mypassword.this)){
                        Toast.makeText(mypassword.this,"网络异常",Toast.LENGTH_LONG).show();
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(CONSTANT.HOST + "/otn/AccountPassword");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");//设置请求方法
                                connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                                SharedPreferences sp = mypassword.this.getSharedPreferences("info", MODE_PRIVATE);
                                String val = sp.getString("cookie", "");
                                connection.setRequestProperty("Cookie", val);//设置头部

                                PrintWriter writer = new PrintWriter(connection.getOutputStream());
                                String params = "newPassword=" + s1 + "&action=update";
                                writer.write(params);
                                writer.flush();
                                writer.close();

                                int responseCode = connection.getResponseCode();
                                String result = "";
                                if (responseCode == HttpURLConnection.HTTP_OK) {
                                    String str;
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                                    while ((str = reader.readLine()) != null) {
                                        result += str;
                                    }
                                    Message msg = new Message();
                                    msg.what = 1;
                                    if ("\"1\"".equals(result)) {
                                        msg.arg1 = 1;
                                    } else msg.arg1 = 0;
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
                else Toast.makeText(mypassword.this,"两次输入的密码不一致",Toast.LENGTH_LONG).show();
            }
        });
    }
}
