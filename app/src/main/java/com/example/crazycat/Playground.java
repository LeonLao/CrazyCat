package com.example.crazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by jack on 2016/2/18.
 */
public class Playground extends SurfaceView {

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
        initGame();
    }
    //坐标转换
    private Dot getDot(int x,int y){
        return matrix[y][x];
    }


    private void redraw(){
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.CYAN);
        getHolder().unlockCanvasAndPost(canvas);

    }
    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

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

}
