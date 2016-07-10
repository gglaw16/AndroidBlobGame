package com.kitware.charleslaw.ball;

/**
 * Created by charles.law on 1/14/2015.
 */

//package com.kitware.charleslaw.dot;
        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.drawable.BitmapDrawable;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorManager;
        import android.os.Handler;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.widget.ImageView;

        import java.util.ArrayList;

public class AnimatedView extends ImageView{
    private SensorManager mSensorManager;

    private ArrayList<Dot> Dots = new ArrayList<Dot>();
    private ArrayList<Dot> SeparateDots = new ArrayList<Dot>();
    private Context MContext;
    int X = -1;
    int Y = -1;
    private int XVelocity = 20;
    private int YVelocity = 10;
    private Handler H;
    private final int FRAME_RATE = 30;
    float LastTouchX;
    float LastTouchY;
    private Dot TouchedDot = null;
    private ArrayList<Dot> TouchedDots = new ArrayList<Dot>();
    private int numRandC = 3;

    public AnimatedView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        this.MContext = context;
        this.H = new Handler();
        for(int i = 0; i < numRandC*numRandC; i++){
            this.Dots.add(new Dot(350, 200, 0, 0));
        }
        for (int x = 100; x <= 1000; x += 500) {
            for (int y = 100; y <= 3000; y += 500) {
                this.SeparateDots.add(new Dot(x, y, 0, 0));
            }
        }
        initializeAllSprings();

        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);


        this.LastTouchX = 0;
        this.LastTouchY = 0;

    }
    private Runnable Rn = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };


    protected void onDraw(Canvas c) {
        for (int j=0; j < SeparateDots.size(); ++j) {
            this.SeparateDots.get(j).Draw(c, this.getWidth(), this.getHeight(), this.MContext);
        }

        ArrayList<Dot> collectedDots = new ArrayList<Dot>();
        for (int i = 0; i < this.Dots.size(); ++i) {
            Dot dot = this.Dots.get(i);
            ArrayList<Dot> tempSprings = dot.getSprings();
            double springLength;
            double shortestSpring = 0;
            for (int j = 0; j < tempSprings.size(); ++j) {
                springLength = Math.sqrt((this.Dots.get(i).GetXPosition() - tempSprings.get(j).GetXPosition()) *
                        (this.Dots.get(i).GetXPosition() - tempSprings.get(j).GetXPosition()) +
                        (this.Dots.get(i).GetYPosition() - tempSprings.get(j).GetXPosition()) *
                                (this.Dots.get(i).GetYPosition() - tempSprings.get(j).GetYPosition()));

                if (j == 0 || springLength < shortestSpring) {
                    shortestSpring = springLength;
                }
            }
            if (shortestSpring > 500) {
                //this.SeparateDots.add(this.Dots.get(i));
                //this.Dots.remove(i);
                //if((numRandC - 1)*(numRandC - 1) >= Dots.size()){
                //    numRandC--;
                //}
                //initializeAllSprings();
            } else {
                for (int j = 0; j < this.SeparateDots.size(); ++j) {
                    Dot checkDot = this.SeparateDots.get(j);
                    if (dot.Collide(checkDot)) {
                        collectedDots.add(checkDot);
                        this.SeparateDots.remove(j);
                    }
                }
            }

            this.Dots.get(i).Draw(c, this.getWidth(), this.getHeight(), this.MContext);
        }

        if (collectedDots.size() > 0) {
            for (int j = 0; j < collectedDots.size(); j++) {
                this.Dots.add(collectedDots.get(j));
            }
            if((numRandC)*(numRandC) < Dots.size()){
                numRandC++;
            }
            this.initializeAllSprings();
        }

        this.H.postDelayed(this.Rn, FRAME_RATE);
    }

        protected void addSpring(int idx1, int idx2){
            if (idx1 == idx2) return;
            if (idx1 < 0 || idx2 < 0 ) return;
            if (idx1 >= this.Dots.size()) return;
            if (idx2 >= this.Dots.size()) return;
            this.Dots.get(idx1).getSprings().add(this.Dots.get(idx2));
        }

        protected void initializeAllSprings(){

        // Get rid of old springs.
        for(int i = 0; i < this.Dots.size(); i++) {
            this.Dots.get(i).getSprings().clear();
        }

        for(int i = 0; i < this.Dots.size(); i++)
        {
            int x = i%numRandC;
            int y = (i-x)/numRandC;
            if(x != 0) {
                this.addSpring(i, i-1);
                if(y != 0) {
                    this.addSpring(i, i - numRandC - 1);
                }
                if(y != numRandC - 1) {
                    if(i + numRandC < this.Dots.size()) {
                        this.addSpring(i, i + numRandC - 1);
                    }
                }
            }
            if(x != numRandC - 1) {
                if(i + 1 < this.Dots.size()) {
                    this.addSpring(i, i + 1);
                }
                if(y != 0) {
                    this.addSpring(i, i - numRandC + 1);
                }
                if(y != numRandC - 1) {
                    if(i + numRandC < this.Dots.size()) {
                        this.addSpring(i,i + numRandC + 1);
                    }
                }
            }
            if(y != 0) {
                this.addSpring(i, i - numRandC);
            }
            if(y != numRandC - 1) {
                if(i + numRandC < this.Dots.size()) {
                    this.addSpring(i,i + numRandC);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Find what dots we are touching.
                for (int i = 0; i < this.Dots.size(); ++i) {
                    if (this.Dots.get(i).Touch(x, y)) {
                        this.TouchedDots.add(this.Dots.get(i));
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.TouchedDots.size() != 0) {
                    for(int i = 0; i < TouchedDots.size(); i++) {
                        this.TouchedDots.get(i).Move(x - LastTouchX, y - LastTouchY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < this.Dots.size(); ++i) {
                    Dots.get(i).setBeingTouched(false);
                }
                this.TouchedDots.clear();
        }

        this.LastTouchX = x;
        this.LastTouchY = y;

        return true;
    }

}




