package com.example.huangxiaoyang.my12306;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HuangXiaoyang on 2018/7/4.
 */

public class MapSerializable implements Serializable {
    Map<String,Object> map;
    ArrayList<Map<String,Object>> list;
    public void setMap(Map<String,Object> map){
        this.map=map;
    }

    public Map<String,Object> getMap(){
        return map;
    }

    public void setList(ArrayList<Map<String,Object>> list){
        this.list=list;
    }

    public ArrayList<Map<String,Object>> getList(){
        return list;
    }
}
