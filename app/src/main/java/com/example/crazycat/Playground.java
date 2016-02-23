package com.example.crazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

/**
 * Created by jack on 2016/2/18.
 */
public class Playground extends SurfaceView implements View.OnTouchListener{

    private static int WIDTH = 40;
    private static final int COL = 10;
    private static final int ROW = 10;
    private static final int RBLOCKS = 10;//默认的路障数量

    private Dot matrix[][];
    // 猫
    private Dot cat;


    public Playground(Context context) {
        super(context);
        getHolder().addCallback(callback);
        //创建二位数组,赋值
         matrix = new Dot[COL][ROW];
        //用2个for循环逐行嵌套
        for (int i = 0;i<ROW;i++){
            for (int j = 0;j<COL;j++){
                matrix[i][j]  = new Dot(j,i);
            }
        }
        setOnTouchListener(this);
        initGame();
    }
    //坐标转换
    private Dot getDot(int x,int y){
        return matrix[y][x];
    }

    //判断点是否到了游戏的边界
    private boolean isAtEdge(Dot dot){
        if (dot.getX()*dot.getY() == 0||dot.getX()+1==COL||dot.getY()+1==ROW){
            return true;
        }
        return false;
    }

    //获取相邻点
    private Dot getNrighbour(Dot dot,int dir){
        switch (dir){
            case 1:
                getDot(dot.getX()-1,dot.getY());
                break;
            case 2:
                if (dot.getY()%2==0){
                    getDot(dot.getX(),dot.getY()-1);
                }else {
                    getDot(dot.getX()-1,dot.getY()-1);
                }
                break;
            case 3:
                if (dot.getY()%2==0){
                    getDot(dot.getX()+1,dot.getY()-1);
                }else {
                    getDot(dot.getX()-1,dot.getY());
                }
                break;
            case 4:
                getDot(dot.getX()+1,dot.getY());
                break;
            case 5:
                if (dot.getY()%2==0){
                    getDot(dot.getX(),dot.getY());
                }else {
                    getDot(dot.getX(),dot.getY());
                }
                break;
            case 6:
                if (dot.getY()%2==0){
                    getDot(dot.getX(),dot.getY()+1);
                }else {
                    getDot(dot.getX(),dot.getY());
                }
                break;
        }
    }



    private void redraw(){
        Canvas canvas = getHolder().lockCanvas();
            canvas.drawColor(Color.LTGRAY);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0;i<ROW;i++){
            //错位排列每行元素
            int offset = 0;
            if (i%2!=0){
                offset = WIDTH/2;
            }
            for (int j = 0 ;j<COL;j++){
                Dot one  = getDot(j,i);
                switch (one.getStatus()){
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;
                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;
                }
                canvas.drawOval(new RectF(one.getX()*WIDTH+offset,one.getY()*WIDTH,
                        (one.getX()+1)*WIDTH+offset,(one.getY()+1)*WIDTH),paint);//画一个椭圆
            }
        }
        getHolder().unlockCanvasAndPost(canvas);

    }
    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            WIDTH = width/(COL+1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };
    //游戏初始化,全部为可用
    private void initGame(){
        for (int i =0;i<ROW;i++){
            for (int j=0;j<COL;j++){
                matrix[i][j].setStatus(Dot.STATUS_OFF);

            }
        }
        cat = new Dot(4,5);
        getDot(4,5).setStatus(Dot.STATUS_IN);

        //随机路障坐标
        for (int i =0;i<RBLOCKS;){
            int x = (int)(Math.random()*1000)%COL;
            int y = (int)(Math.random()*1000)%ROW;
            if (getDot(x,y).getStatus()==Dot.STATUS_OFF){
                getDot(x,y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP){
           // Toast.makeText(getContext(),event.getX()+":"+event.getY(),Toast.LENGTH_SHORT).show();
            int x,y;
            y = (int)(event.getY()/WIDTH);
            if (y%2==0){
                x=(int)(event.getX()/WIDTH);
            }else {
                x=(int)(event.getX()-WIDTH/2)/WIDTH;
            }
            if (x+1>COL||y+1>ROW){
                initGame();
            }else {
                getDot(x,y).setStatus(Dot.STATUS_ON);
            }
            redraw();
        }

        return true;
    }
}
