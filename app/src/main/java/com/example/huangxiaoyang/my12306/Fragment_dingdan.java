package com.example.huangxiaoyang.my12306;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by HuangXiaoyang on 2018/7/3.
 */

public class Fragment_dingdan extends Fragment implements View.OnClickListener{
    private TextView tv_dzf,tv_qbdd;
    private ArrayList<Fragment> fragmentList=new ArrayList<>();
    private ViewPager viewPager;
    private int dqwz=1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_dingdan,null);
        fragmentList.add(new Fragment_wzf());
        fragmentList.add(new Fragment_qbdd());
        viewPager=view.findViewById(R.id.dd_vp);
        viewPager.setAdapter(new FragmentAdapter(getActivity().getSupportFragmentManager(),fragmentList));
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new MyPageListener());

        tv_dzf=view.findViewById(R.id.dd_tv_dzf);
        tv_qbdd=view.findViewById(R.id.dd_tv_qbdd);
        tv_dzf.setOnClickListener(this);
        tv_qbdd.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dd_tv_dzf:
                viewPager.setCurrentItem(0);
                break;
            case R.id.dd_tv_qbdd:
                viewPager.setCurrentItem(1);
                break;
        }
    }

    public class MyPageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    tv_dzf.setTextColor(android.graphics.Color.parseColor("#000000"));
                    tv_dzf.setBackgroundColor(android.graphics.Color.parseColor("#00aacc"));
                    tv_qbdd.setTextColor(android.graphics.Color.parseColor("#ffffff"));
                    tv_qbdd.setBackgroundColor(android.graphics.Color.parseColor("#b9d1ed"));
                    break;
                case 1:
                    tv_dzf.setTextColor(android.graphics.Color.parseColor("#ffffff"));
                    tv_dzf.setBackgroundColor(android.graphics.Color.parseColor("#b9d1ed"));
                    tv_qbdd.setTextColor(android.graphics.Color.parseColor("#000000"));
                    tv_qbdd.setBackgroundColor(android.graphics.Color.parseColor("#00aacc"));
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void gengXinQbdd(){
        ((Fragment_qbdd)fragmentList.get(1)).createData();
    }
}
