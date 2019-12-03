package com.example.huangxiaoyang.my12306;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

class Cs_Name {
    private String Cs_Name;
    private String cs_name;
    public Cs_Name(String Cs_Name, String cs_name){
        this.Cs_Name = Cs_Name;
        this.cs_name = cs_name;
    }
    public String getFirstWord(){
        String str = null;
        try {
            str = PinyinHelper.getShortPinyin(cs_name);
        } catch (PinyinException e) {
            e.printStackTrace();
        }
        return str;
    }

    public String getCs_Name() {
        return Cs_Name;
    }
}
