package com.example.huangxiaoyang.my12306;

import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Cshi_select extends AppCompatActivity{
    private ListView lv_cs;
    private TextView tv_search;
    private NawView nv;
    private List<Cs_Name> list;
    private CsAdapter adapter;
    private String[] data_cs = {"北京","天津","上海","重庆","兰州","成都","杭州","青海","广州",
        "乌鲁木齐","哈尔滨","大连","沈阳","阿坝","天水"};
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cshi_select);
        tv_search = findViewById(R.id.tv_search);
        lv_cs = findViewById(R.id.lv_cs);
        nv = findViewById(R.id.nv);
        nv.setTextView(tv_search);
        list = new ArrayList();
        for(int i = 0; i < data_cs.length; i++){
            list.add(new Cs_Name(data_cs[i], CharacterUtils.getFirstSpell(data_cs[i]).toUpperCase()));
        }
        Collections.sort(list, new Comparator<Cs_Name>() {
            @Override
            public int compare(Cs_Name o1, Cs_Name o2) {
                return o1.getFirstWord().compareTo(o2.getFirstWord());
            }
        });
        adapter = new CsAdapter(this, list);
        lv_cs.setAdapter(adapter);
        nv.setListener(new NawView.onTouchCharacterListener() {
            @Override
            public void touchCharacterListener(String s) {
                int position = adapter.getSelectPosition(s);
                if(position != -1){
                    lv_cs.setSelection(position);
                }
            }
        });
        lv_cs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("city",list.get(position).getCs_Name());
                setResult(1,intent);
                Cshi_select.this.finish();
            }
        });
    }
}
//    private String[] data_szm = {"A","B","C","D","E","F","G","H","I",
//                                    "G","K","L","M","N","O","P","Q","R",
//                                    "S","T","U","V","W","X","Y","Z"};
//    private String[] data_cs = {"北京","天津","上海","重庆","兰州","成都","杭州","青海","广州",
//                                    "乌鲁木齐","哈尔滨","大连","沈阳","阿坝","天水"};
//    private int[] number_cs = new int[data_cs.length];
//    private String[] data_cs_FirstWord = new String[data_cs.length];
//    private String[] data_cs_sort_FirstWord = new String[data_cs.length];
//    private String Test = "";
//    private ListView lv_cs,lv_szm;
//    private TextView tv_test;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cshi_select);
//        lv_cs = findViewById(R.id.lv_cs);
//        lv_szm = findViewById(R.id.lv_szm);
//        tv_test = findViewById(R.id.tv_test);
//        ArrayAdapter<String> szmAdapt = new ArrayAdapter<>(Cshi_select.this,android.R.layout.simple_list_item_1,data_szm);
//        ArrayAdapter<String> csAdapt = new ArrayAdapter<>(Cshi_select.this,android.R.layout.simple_list_item_1,data_cs);
//        lv_cs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String word = data_cs[position];
//                Intent intent = new Intent();
//            }
//        });
//        lv_szm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String word = data_szm[position];
//                Toast.makeText(Cshi_select.this,"您选取了首字母为" + word + "的城市!",Toast.LENGTH_LONG).show();
//            }
//        });
//        for (int i = 0; i < data_cs.length; i++){
//            number_cs[i] = i;
//        }
//        for(int i = 0; i < data_cs.length; i++){
//            try {
//                data_cs_FirstWord[i] = PinyinHelper.getShortPinyin(data_cs[i]);
//            } catch (PinyinException e) {
//                e.printStackTrace();
//            }
//        }
//        String str = "";
//        for(int i = 0; i < data_cs_FirstWord.length; i++){
//            for (int j = data_cs_FirstWord.length - 1; j > i; j--){
//                if(data_cs_FirstWord[i].compareTo(data_cs_FirstWord[j]) > 0){
//                    str = data_cs_FirstWord[j];
//                    data_cs_FirstWord[j] = data_cs_FirstWord[i];
//                    data_cs_FirstWord[i] = str;
//                    str = data_cs[j];
//                    data_cs[j] = data_cs[i];
//                    data_cs[i] = str;
//                }
//            }
//            Test += data_cs[i] +";    ";
//        }
//        tv_test.setText(Test);
//        lv_cs.setAdapter(csAdapt);
//        lv_szm.setAdapter(szmAdapt);
//    }