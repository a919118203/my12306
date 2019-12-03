package com.example.huangxiaoyang.my12306;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.Hashtable;

public class Activity_cpyd5 extends AppCompatActivity {

    private Button bt;
    private ImageView iv;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chepiaoyuding5);
        bt=findViewById(R.id.cpyd5_bt_fh);
        iv=findViewById(R.id.cpyd5_image_erwm);
        tv=findViewById(R.id.cpyd5_tv_ddbh);
        tv.setText("您的订单"+getIntent().getStringExtra("ddph")+"支付成功，可以凭此二维码办理取票业务，也可以在订单中查看相关信息及二维码");
        createQRImage(getIntent().getStringExtra("erwmdata"),iv,300,300);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Activityglq.stopAll();
        super.onDestroy();
    }

    public static void createQRImage(String data, ImageView ivOrcode,int QR_WIDTH,int QR_HEIGHT){
        Hashtable<EncodeHintType,String>hints=new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");
        try {
            BitMatrix bitMatrix=new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE,QR_WIDTH,QR_HEIGHT,hints);
            int[] pixels=new int[QR_HEIGHT*QR_WIDTH];
            for(int y=0;y<QR_HEIGHT;y++)
                for(int x=0;x<QR_WIDTH;x++){
                    if(bitMatrix.get(x,y)){
                        pixels[y*QR_WIDTH+x]=0xff000000;
                    }else pixels[y*QR_WIDTH+x]=0xffffffff;
                }
            Bitmap bitmap=Bitmap.createBitmap(QR_WIDTH,QR_HEIGHT,Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,QR_WIDTH,0,0,QR_WIDTH,QR_HEIGHT);
            ivOrcode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
