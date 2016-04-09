package com.kitware.charleslaw.ball;

/**
 * Created by charles.law on 1/14/2015.
 */

//package com.kitware.charleslaw.ball;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.drawable.BitmapDrawable;
        import android.os.Handler;
        import android.util.AttributeSet;
        import android.widget.ImageView;
public class AnimatedView extends ImageView{
    private Ball[] Balls = new Ball[2];
    private Context MContext;
    int X = -1;
    int Y = -1;
    private int XVelocity = 20;
    private int YVelocity = 10;
    private Handler H;
    private final int FRAME_RATE = 30;

    public AnimatedView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        this.MContext = context;
        this.H = new Handler();
        this.Balls[0] = new Ball(500, 200, 0, 10);
        this.Balls[1] = new Ball(400, 900, 0, -10);

        this.Balls[0].addSpring(this.Balls[1]);
        this.Balls[1].addSpring(this.Balls[0]);

    }
    private Runnable Rn = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    protected void onDraw(Canvas c) {
        for (int i = 0; i < this.Balls.length; ++i) {
            Balls[i].Draw(c, this.getWidth(), this.getHeight(), this.MContext);
            for (int j=0; j < i; ++j) {
                //Balls[i].Collide(Balls[j]);
            }
        }

        this.H.postDelayed(this.Rn, FRAME_RATE);
    }
}




