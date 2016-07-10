package com.kitware.charleslaw.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

/**
 * Created by gwenda.law on 1/16/2015.
 */
public class Dot {
    protected double X = -1;
    protected double Y = -1;
    protected double XVelocity = 20;
    protected double YVelocity = 10;
    private double Radius = -1;
    protected double Mass = Radius*Radius;
    protected ArrayList<Dot> Springs;
    protected boolean beingTouched;

    public Dot(int x, int y, int vx, int vy) {
        this.X = x;
        this.Y = y;
        this.XVelocity = vx;
        this.YVelocity = vy;
        this.Springs = new ArrayList<Dot>();
        this.beingTouched = false;
    }

    public ArrayList<Dot> getSprings(){
        return Springs;
    }

    public void initializeSprings(ArrayList<Dot> otherDots){
        this.Springs.clear();
        for(int i = 0; i < otherDots.size(); i++)
        {
            if(otherDots.get(i) != this) {
                this.Springs.add(otherDots.get(i));
            }
        }
    }

    public boolean Touch(double x, double y) {
        double dx = x - this.X;
        double dy = y - this.Y;
        if (dx*dx + dy*dy < this.Radius * this.Radius*2) {
            this.XVelocity = 0.0;
            this.YVelocity = 0.0;
            this.beingTouched = true;
            return true;
        }
        return false;
    }

    public void setBeingTouched(boolean value){
        this.beingTouched = value;
    }

    public void Move(float dx, float dy) {
        this.XVelocity = 0;
        this.YVelocity = 0;
        this.X += dx;
        this.Y += dy;
    }

    public double GetXPosition(){
        return this.X;
    }

    public double GetYPosition(){
        return this.Y;
    }

    public void addSpring(Dot otherDot){
        this.Springs.add(otherDot);
    }

    public double Distance2(Dot otherDot) {
        double dx = this.X - otherDot.X;
        double dy = this.Y - otherDot.Y;
        return dx*dx + dy*dy;
    }

    protected boolean Collide(Dot otherDot) {
        double dx = this.X - otherDot.X;
        double dy = this.Y - otherDot.Y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double penetration = (2*this.Radius - dist) / 2;
        if (penetration < 0) {
            return false;
        }
        else{
            if(dist < 50){
                return true;
            }
            else{
                return false;
            }
        }
    }


    protected void Draw(Canvas c, int width, int height, Context ctx) {
        BitmapDrawable dot = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.smalldot);
        if (this.Radius < 0) {
            this.Radius = dot.getBitmap().getWidth()/2.5;
        }

        if (this.X < 0 && this.Y < 0) {
            this.X = width / 2;
            this.Y = height / 2;
        } else {
            this.X += this.XVelocity;
            this.Y += this.YVelocity;
            // Cannot simply reverse velocity because it can get trapped.
            if (this.X > width - this.Radius) {
                this.X = width - this.Radius;
                this.XVelocity = -Math.abs(this.XVelocity);
            } else if (this.X < this.Radius) {
                this.X = this.Radius;
                this.XVelocity = Math.abs(this.XVelocity);
            }
            if (this.Y > height - this.Radius) {
                this.Y = height - this.Radius;
                this.YVelocity = -Math.abs(this.YVelocity);
            } else if (this.Y < this.Radius) {
                this.Y = this.Radius;
                this.YVelocity = Math.abs(this.YVelocity);
            }
            // Gravity

            if(!this.beingTouched) {
                int totalDX = 0;
                int totalDY = 0;
                for (int i = 0; i < Springs.size(); i++) {
                    //How long is the spring??
                    double dX = Springs.get(i).X - this.X + Math.random();
                    double dY = Springs.get(i).Y - this.Y + Math.random();
                    double distance = Math.sqrt(dX * dX + dY * dY);

                    //dX dY change to normal
                    if (distance == 0) {
                        continue;
                    }
                    dX /= distance;
                    dY /= distance;

                    distance -= 100;
                    dX = dX * 0.2 * distance;
                    dY = dY * 0.2 * distance;

                    totalDX += dX;
                    totalDY += dY;
                }
                this.XVelocity = totalDX;
                this.YVelocity = totalDY;
            }

        }
        c.drawBitmap(
            dot.getBitmap(),
                (int)(this.X)-(dot.getBitmap().getWidth()/2),
                (int)(this.Y)-(dot.getBitmap().getWidth()/2),
                null);
    }
}
