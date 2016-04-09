package com.kitware.charleslaw.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by charles.law on 1/16/2015.
 */
public class Ball {
    protected double X = -1;
    protected double Y = -1;
    protected double XVelocity = 20;
    protected double YVelocity = 10;
    private double Radius = -1;
    protected double Mass = Radius*Radius;
    protected Ball Spring;

    public Ball(int x, int y, int vx, int vy) {
        this.X = x;
        this.Y = y;
        this.XVelocity = vx;
        this.YVelocity = vy;
        this.Spring = null;
    }

    public void addSpring(Ball otherBall){
        this.Spring = otherBall;
    }

    /*
    protected void Collide(Ball otherBall) {
        double dx = this.X - otherBall.X;
        double dy = this.Y - otherBall.Y;
        double dist = Math.sqrt(dx*dx + dy*dy);
        double penetration = (2*this.Radius - dist) / 2;
        if (penetration < 0) {
            return;
        }
        // Normalize
        dx = dx / dist;
        dy = dy / dist;
        // Remove any interpenetration.
        this.X += dx*penetration;
        this.Y += dy*penetration;
        otherBall.X -= dx*penetration;
        otherBall.Y -= dy*penetration;
        // Now for the bounce.
        double momentum1 = this.XVelocity*dx + this.YVelocity*dy;
        double momentum2 = otherBall.XVelocity*dx + otherBall.YVelocity*dy;
        this.XVelocity += (momentum2-momentum1) * dx;
        this.YVelocity += (momentum2-momentum1) * dy;
        // now for the other ball.
        otherBall.XVelocity += (momentum1-momentum2) * dx;
        otherBall.YVelocity += (momentum1-momentum2) * dy;
    }
    */

    protected void Draw(Canvas c, int width, int height, Context ctx) {
        BitmapDrawable ball = (BitmapDrawable) ctx.getResources().getDrawable(R.drawable.ball);
        if (this.Radius < 0) {
            this.Radius = ball.getBitmap().getWidth()/2.5;
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
            //this.YVelocity += 0.5;

            //How long is the spring??
            double dX = Spring.X - this.X;
            double dY = Spring.Y - this.Y;
            double distance = Math.sqrt(dX*dX + dY*dY);

            //dX dY change to normal
            dX /= distance;
            dY /= distance;

            //turn dx and dy into acceleration
            double forceMag = distance - 100;
            dX *= forceMag;
            dY *= forceMag;

            dX /= (this.Mass * 100);
            dY /= (this.Mass * 100);

            this.XVelocity += dX;
            this.YVelocity += dY;

            this.XVelocity *= .99;
            this.YVelocity *= .99;
        }
        c.drawBitmap(
            ball.getBitmap(),
                (int)(this.X)-(ball.getBitmap().getWidth()/2),
                (int)(this.Y)-(ball.getBitmap().getWidth()/2),
                null);
    }
}
