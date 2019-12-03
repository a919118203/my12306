package com.example.huangxiaoyang.my12306;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinHelper;

class CharacterUtils {
    public static int getCnAscii(char firstWord) {
        int num = firstWord;
        return num;
    }

    public static String getFirstSpell(String data_cs) {
        String First_name = null;
        try {
            String str = PinyinHelper.getShortPinyin(data_cs);
            First_name = String.valueOf(str.charAt(0));
        } catch (PinyinException e) {
            e.printStackTrace();
        }
        return First_name;
    }
}
