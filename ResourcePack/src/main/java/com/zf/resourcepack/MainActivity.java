package com.zf.resourcepack;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    GridView clockBlock;
    BaseAdapter adapter;
    MediaPlayer mediaPlayer1;
    MediaPlayer mediaPlayer2;
    AssetManager am;
    //字符串资源id
//    int[] textIds = new int[]{
//            R.string.red, R.string.green, R.string.blue,
//            R.string.light_blue, R.string.pink, R.string.yello,
//            R.string.gray, R.string.purple, R.string.orange
//    };
//    int[] colorIds = new int[]{
//            R.color.red, R.color.green, R.color.blue,
//            R.color.light_blue, R.color.pink, R.color.yello,
//            R.color.gray, R.color.purple, R.color.orange
//    };
    String[] texts;

    Context mContext;
    TypedArray colors;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initWidget();
    }

    private void initData(){
        mContext = this;
        res = mContext.getResources();
        texts = res.getStringArray(R.array.str_array);
        colors = res.obtainTypedArray(R.array.color_array);
    }

    private void initWidget(){
        //引用res/raw目录下的原生资源，SDK会处理这些原生资源，在R清单列表中生成索引
        mediaPlayer1 = MediaPlayer.create(mContext, R.raw.winter_secret);
        mediaPlayer1.seekTo(60*1000);
        mediaPlayer1.start();
        //引用assets目录下的资源，更为原生的资源，需要通过AssetsManager来管理资源
        am = getAssets();
        try{
            AssetFileDescriptor afd = am.openFd("xuanyuanjian.mp3");
            mediaPlayer2 = new MediaPlayer();
            mediaPlayer2.setDataSource(afd.getFileDescriptor());//加载资源文件
            mediaPlayer2.prepare();
        }catch(Exception e){
            e.printStackTrace();
        }
        clockBlock = (GridView)findViewById(R.id.color_block);
        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return texts.length;
            }

            @Override
            public Object getItem(int position) {
                return texts[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView text = new TextView(mContext);

                text.setHeight((int)res.getDimension(R.dimen.cell_height));
                text.setWidth((int) res.getDimension(R.dimen.cell_width));
                text.setText(texts[position]);
                text.setBackgroundDrawable(colors.getDrawable(position));
                int fontSize = (int)res.getDimension(R.dimen.title_font_size);
                text.setTextSize(fontSize);
                return text;
            }
        };
        clockBlock.setAdapter(adapter);

        final ImageView image = (ImageView)findViewById(R.id.image);
        final ClipDrawable clipDrawable = (ClipDrawable)image.getBackground();
        final Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.my_animation);
        //设置动画结束后保留结束状态
        anim.setFillAfter(true);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //如果消息是本程序发送的
                if(msg.what == 0x1233){
                    //修改clipDrawable的level值
                    clipDrawable.setLevel(clipDrawable.getLevel() + 40);
                }else if(msg.what == 0x1234){
                    //开始动画
                    image.startAnimation(anim);
                    mediaPlayer1.stop();
                    mediaPlayer2.start();
                }
            }
        };
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                if (clipDrawable.getLevel() < 10000) {
                    msg.what = 0x1233;
                    handler.sendMessage(msg);
                } else {//加载完图片，进行动画变换
                    msg.what = 0x1234;
                    handler.sendMessage(msg);
                    timer.cancel();
                }
            }
        }, 0, 8);
    }
}

