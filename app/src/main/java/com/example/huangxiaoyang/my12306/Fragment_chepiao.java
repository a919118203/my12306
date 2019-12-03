package com.example.huangxiaoyang.my12306;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/7/3.
 */

public class Fragment_chepiao extends Fragment implements View.OnClickListener{
    private ImageView iv_start_r,iv_end_r,iv_jiaohuan;
    private TextView tv_start_l,tv_end_l,tv_riqi;
    private Button bt_chaxun;
    private ListView lv_lscx;
    private int zf=1;
    private final float[] floats={0,630,0,-630};
    private ArrayList<String> datalist=new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Calendar cl=Calendar.getInstance();//记录日期，getInstance获取一个cl对象并附上本日日期

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_chepiao,null);
        findId(view);
        init();
        return view;
    }

    private void init() {
        iv_jiaohuan.setOnClickListener(this);
        tv_riqi.setOnClickListener(this);
        tv_start_l.setOnClickListener(this);
        iv_start_r.setOnClickListener(this);
        tv_end_l.setOnClickListener(this);
        iv_end_r.setOnClickListener(this);
        bt_chaxun.setOnClickListener(this);

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,datalist);
        lv_lscx.setAdapter(adapter);
        lv_lscx.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] sArray=datalist.get(position).split(" - ");
                if(zf==1){
                    tv_start_l.setText(sArray[0]);
                    tv_end_l.setText(sArray[1]);
                }
                else {
                    tv_start_l.setText(sArray[1]);
                    tv_end_l.setText(sArray[0]);
                }
            }
        });

        datalist.clear();
        SqlOpen open=new SqlOpen(getActivity(),"My12306",null,1);
        SQLiteDatabase database=open.getReadableDatabase();
        Cursor cursor=database.query("lscx",null,null,null,null,null,"cnt");
        String str1=null,str2=null;
        if(cursor.moveToFirst()){
            do {
                String s=cursor.getString(cursor.getColumnIndex("s"));
                String e=cursor.getString(cursor.getColumnIndex("e"));
                str1=str2;
                str2=s+" - "+e;
            }while(cursor.moveToNext());
        }
        datalist.add(str1);
        datalist.add(str2);

        tv_riqi.setText(cl.get(Calendar.YEAR)+"-"+(cl.get(Calendar.MONTH)+1)+"-"+cl.get(Calendar.DAY_OF_MONTH));
    }


    class AnimationThread extends Thread{
        float s,e;
        TextView tv;
        public AnimationThread(float s,float e,TextView tv){
            this.s=s;
            this.e=e;
            this.tv=tv;
        }

        @Override
        public void run() {
            Animation animation=new TranslateAnimation(s,e,0,0);
            animation.setFillAfter(true);
            animation.setDuration(1000);
            tv.startAnimation(animation);
        }
    }
    private void findId(View view) {
        iv_start_r=view.findViewById(R.id.start_r);
        iv_end_r=view.findViewById(R.id.end_r);
        iv_jiaohuan=view.findViewById(R.id.jiaohuan);
        tv_end_l=view.findViewById(R.id.end_l);
        tv_start_l=view.findViewById(R.id.start_l);
        tv_riqi=view.findViewById(R.id.tv_riqi);
        lv_lscx=view.findViewById(R.id.lscx_list);
        bt_chaxun=view.findViewById(R.id.bt_chaxun);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jiaohuan:
                new AnimationThread(floats[0],floats[1],tv_start_l).start();
                float lins=floats[0];
                floats[0]=floats[1];
                floats[1]=lins;

                new AnimationThread(floats[2],floats[3],tv_end_l).start();
                lins=floats[2];
                floats[2]=floats[3];
                floats[3]=lins;

                zf*=-1;
                break;
            case R.id.tv_riqi:
                final View view=getLayoutInflater().inflate(R.layout.dialog_riqi,null);
                CalendarView cv=view.findViewById(R.id.calendarView);
                cv.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                        cl.set(year,month,dayOfMonth);
                    }
                });
                new AlertDialog.Builder(getActivity())
                        .setTitle("选择日期")
                        .setView(view)
                        .setNegativeButton("完成", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tv_riqi.setText(cl.get(Calendar.YEAR)+"-"+(cl.get(Calendar.MONTH)+1)+
                                        "-"+cl.get(Calendar.DATE));
                            }
                        })
                        .create().show();
                break;
            case R.id.start_l:
            case R.id.start_r:
                Intent intent=new Intent(getActivity(),Cshi_select.class);
                startActivityForResult(intent,1);
                break;
            case R.id.end_l:
            case R.id.end_r:
                Intent intent2=new Intent(getActivity(),Cshi_select.class);
                startActivityForResult(intent2,2);
                break;
            case R.id.bt_chaxun:
                String start_l=tv_start_l.getText().toString()
                        ,end_l=tv_end_l.getText().toString();
                String s=zf==1?start_l:end_l,
                        e=zf==1?end_l:start_l;
                if(datalist.size()==2) {
                    String str=datalist.get(1);
                    datalist.clear();
                    datalist.add(str);
                }
                datalist.add(s + " - " + e);
                adapter.notifyDataSetChanged();
                SqlOpen open=new SqlOpen(getActivity(),"My12306",null,1);
                SQLiteDatabase database=open.getReadableDatabase();
                ContentValues values=new ContentValues();
                values.put("s",s);
                values.put("e",e);
                database.insert("lscx",null,values);
                database.close();

                Intent intent3=new Intent(getActivity(),Activity_cpyd1.class);
                intent3.putExtra("from",s);
                intent3.putExtra("to",e);
                MapSerializable map=new MapSerializable();
                Map<String,Object>m=new HashMap<>();
                m.put("riqi",cl);
                map.setMap(m);
                Bundle bundle=new Bundle();
                bundle.putSerializable("riqi",map);
                intent3.putExtras(bundle);
                startActivity(intent3);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode==1)
                if(zf==1)
                    tv_start_l.setText(data.getStringExtra("city"));
                else tv_end_l.setText(data.getStringExtra("city"));
                break;
            case 2:
                if(resultCode==1)
                if(zf==-1)
                    tv_start_l.setText(data.getStringExtra("city"));
                else tv_end_l.setText(data.getStringExtra("city"));
                break;
        }
    }
}
