package com.kitware.charleslaw.ball;

/**
 * Created by charles.law on 1/14/2015.
 */

//package com.kitware.charleslaw.dot;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.drawable.BitmapDrawable;
        import android.os.Handler;
        import android.util.AttributeSet;
        import android.widget.ImageView;

        import java.util.ArrayList;

public class AnimatedView extends ImageView{
    private ArrayList<Dot> Dots = new ArrayList<Dot>();
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
        this.Dots.add(new Dot(500, 200, 0, 10));
        this.Dots.add(new Dot(400, 900, 0, -10));
        this.Dots.add(new Dot(350, 800, 5, 0));


        for(int i = 0; i < this.Dots.size(); i++)
        {
            this.Dots.get(i).initializeSprings(this.Dots);
        }


    }
    private Runnable Rn = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };
    protected void onDraw(Canvas c) {
        for (int i = 0; i < this.Dots.size(); ++i) {
            Dots.get(i).Draw(c, this.getWidth(), this.getHeight(), this.MContext);
            for (int j=0; j < i; ++j) {
                //Dots[i].Collide(Dots[j]);
            }
        }

        this.H.postDelayed(this.Rn, FRAME_RATE);
    }
}




