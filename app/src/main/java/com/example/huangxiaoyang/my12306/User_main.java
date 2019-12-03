package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;


import com.example.huangxiaoyang.my12306.utils.CONSTANT;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class User_main extends AppCompatActivity implements View.OnClickListener{

    private List<Fragment> fragmentList=new ArrayList<>();
    private ViewPager vp;
    private TextView tv1,tv2,tv3;
    private int w=0;//当前的位置
    private View gdt;
    private int pyl;
    private long startTime =-1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main_layout);
        fragmentList.add(new Fragment_chepiao());
        fragmentList.add(new Fragment_dingdan());
        fragmentList.add(new Fragment_wode());
        tv1=findViewById(R.id.one);
        tv2=findViewById(R.id.two);
        tv3=findViewById(R.id.three);
        gdt=findViewById(R.id.view_gdt);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);

        vp=findViewById(R.id.viewp);
        vp.setAdapter(new FragmentAdapter(this.getSupportFragmentManager(),fragmentList));
        vp.addOnPageChangeListener(new MyPageListener());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                vp.setCurrentItem(0);
                break;
            case R.id.two:
                vp.setCurrentItem(1);
                break;
            case R.id.three:
                vp.setCurrentItem(2);
                break;
        }
    }

    public class MyPageListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    tv1.setTextColor(android.graphics.Color.parseColor("#00aacc"));
                    tv2.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    tv3.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    break;
                case 1:
                    tv1.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    tv2.setTextColor(android.graphics.Color.parseColor("#00aacc"));
                    tv3.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    //((Fragment_dingdan)fragmentList.get(1)).gengXinQbdd();
                    break;
                case 2:
                    tv1.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    tv2.setTextColor(android.graphics.Color.parseColor("#b7b9ba"));
                    tv3.setTextColor(android.graphics.Color.parseColor("#00aacc"));
                    break;
            }
            moveToW(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void moveToW(int tow){
        int gdt_width=gdt.getWidth();
        DisplayMetrics disp=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(disp);
        int ww=disp.widthPixels;//获取分辨率宽度
        pyl=(ww/3-gdt_width)/2;//间隔
        int one=gdt.getWidth()+pyl*2;
        int two=one*2;

        Animation animation=null;

        // Toast.makeText(MainActivity.this,tow+" "+w,Toast.LENGTH_SHORT).show();
        switch (tow){
            case 0:
                if(w==1)
                    animation=new TranslateAnimation(one,0,0,0);
                else if(w==2)
                    animation=new TranslateAnimation(two,0,0,0);
                break;
            case 1:
                if(w==0)
                    animation=new TranslateAnimation(0,one,0,0);
                else if(w==2) animation=new TranslateAnimation(two,one,0,0);
                break;
            case 2:
                if(w==0)
                    animation=new TranslateAnimation(0,two,0,0);
                else if(w==1)
                    animation=new TranslateAnimation(one,two,0,0);
                break;
        }
        if(animation!=null){
            w=tow;
            animation.setFillAfter(true);
            animation.setDuration(300);
            gdt.startAnimation(animation);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(startTime ==-1)
            {
                Toast.makeText(this,"再按一次退出",Toast.LENGTH_LONG).show();
                startTime =System.currentTimeMillis();
            }
            else if(System.currentTimeMillis()- startTime <(long)1500)
            {
                class MyRunnable implements Runnable{
                    @Override
                    public void run() {
                        try {
                            URL url=new URL(CONSTANT.HOST+"/otn/Logout");
                            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");//设置请求方法
                            connection.setConnectTimeout(CONSTANT.REQUEST_TIMEOUT);

                            SharedPreferences sp=User_main.this.getSharedPreferences("info",MODE_PRIVATE);
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
                                    Intent intent3=new Intent(User_main.this,MainActivity.class);
                                    startActivity(intent3);
                                    User_main.this.finish();
                                }
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                finish();
            }
        }
        return true;
    }
}
