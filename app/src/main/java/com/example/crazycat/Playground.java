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

import java.security.KeyPairGeneratorSpi;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by jack on 2016/2/18.
 */
public class Playground extends SurfaceView implements View.OnTouchListener{

    int k = 1;

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
                return getDot(dot.getX()-1,dot.getY());

            case 2:
                if (dot.getY()%2==0){
                    return getDot(dot.getX()-1,dot.getY()-1);
                }else {
                    return getDot(dot.getX(),dot.getY()-1);
                }

            case 3:
                if (dot.getY()%2==0){
                    return getDot(dot.getX()-1,dot.getY());
                }else {
                    return getDot(dot.getX()+1,dot.getY()-1);
                }

            case 4:
                return getDot(dot.getX()+1,dot.getY());

            case 5:
                if (dot.getY()%2==0){
                    return getDot(dot.getX(),dot.getY()+1);
                }else {
                    return getDot(dot.getX()+1,dot.getY()+1);
                }

            case 6:
                if (dot.getY()%2==0){
                    return getDot(dot.getX()-1,dot.getY()+1);
                }else {
                    return getDot(dot.getX(),dot.getY()+1);
                }

            default:
                break;
        }
        return null;
    }

    //通过返回值得到各个方向到边界的距离
    //正数代表该方向上没路障，负值代表有路障，数字代表距离
    private int getDistance(Dot dot,int dir){
        int distance = 0;
        //如果当前点已经到屏幕边缘就不用判断了
        if (isAtEdge(dot)){
            return 1;
        }
        //是否一直走到场景边缘
        Dot ori = dot,next;
        while (true){
            next = getNrighbour(ori,dir);
            //如果下一个点碰到路障
            if (next.getStatus() == Dot.STATUS_ON){
                return distance*-1;
            }
            //是否到了场景边缘
            if (isAtEdge(next)){
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }

    }

    //猫移动的方法
    private void MoveTo(Dot one){
        one.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(one.getX(), one.getY());
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
            }else if (getDot(x,y).getStatus() == Dot.STATUS_OFF){
                //点可用则设置成路障
                getDot(x,y).setStatus(Dot.STATUS_ON);
                move();
            }
            redraw();
        }

        return true;
    }

    private void move() {
        //判断是否在场景边界
        if (isAtEdge(cat)){
            gameOver();
            return;
        }

        //记录器,泛型指定为Dot
        Vector<Dot> avaliable = new Vector<>();
        //记录是否有可以直接到达边缘的
        Vector<Dot> positive = new Vector<>();
        //需要一个容器记录方向
        HashMap<Dot,Integer> pl = new HashMap<Dot,Integer>();
        for (int i=1;i<7;i++){
            Dot n = getNrighbour(cat,i);
            if (n.getStatus()==Dot.STATUS_OFF){
                //如果当前点可用，则添加进去
                avaliable.add(n);
                pl.put(n,i);
                if (getDistance(n,i)>0){
                    positive.add(n);
                }
            }
        }
        if (avaliable.size()==0){
            win();
        }else if (avaliable.size()==1){
            MoveTo(avaliable.get(0));
        }
//        else if (justinit){
//            //第一次移动
//            int d = (int)(Math.random()*1000)%avaliable.size();
//            MoveTo(avaliable.get(d));
//        }
        else {
            //最优路径点
            Dot best= null;
            if (positive.size() !=0){
                //存在可以直接到达边缘的走向
                int min=999;
                for (int i= 0;i<positive.size();i++){
                    int a = getDistance(positive.get(i),pl.get(positive.get(i)));
                    if (a<min){
                        min = a;
                        best = positive.get(i);
                    }
                }
            }
            else {
                //所有方向都存在路障
                int max=0;
                for (int i=0;i<avaliable.size();i++){
                    int k = getDistance(avaliable.get(i),pl.get(avaliable.get(i)));
                    if (k<max){
                        max = k;
                        best = avaliable.get(i);
                    }
                }
            }
            MoveTo(best);
        }

    }

    private void gameOver() {
        Toast.makeText(getContext(),"CrazyCat跑了",Toast.LENGTH_SHORT).show();
    }private void win() {
        Toast.makeText(getContext(),"成功围住了神经猫",Toast.LENGTH_SHORT).show();
    }
}
