package com.kitware.charleslaw.ball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

import java.util.ArrayList;

/**
 * Created by gwenda.law on 1/16/2015.
 */
public class Dot {
    public static final double SPRING_LENGTH= 80;
    public static final double DAMP = 0.98;
    public static final int MAX_NUM_NEIGHBORS = 8;
    public static final double MAX_DISTANCE = 160000;
    protected double X = -1;
    protected double Y = -1;
    protected double XVelocity = 20;
    protected double YVelocity = 10;
    private double Radius = -1;
    protected double Mass = Radius*Radius;
    protected boolean beingTouched;
    protected Paint paint = new Paint();
    protected Dot[] Neighbors;
    protected double[] NeighborDistances;
    int numNeighbors;

    public Dot(double x, double y, double vx, double vy) {
        this.X = x;
        this.Y = y;
        this.XVelocity = vx;
        this.YVelocity = vy;
        this.beingTouched = false;
        this.Neighbors = new Dot[MAX_NUM_NEIGHBORS];
        this.NeighborDistances = new double[MAX_NUM_NEIGHBORS];
        for (int i = 0; i < MAX_NUM_NEIGHBORS; i++) {
            this.Neighbors[i] = null;
            this.NeighborDistances[i] = MAX_DISTANCE;
        }
        this.numNeighbors = 0;
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

    // This also applies the springs.
    // TODO: Separate draw and apply.
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
            // Damp movement.
            this.XVelocity *= DAMP;
            this.YVelocity *= DAMP;

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
                for (int i = 0; i < MAX_NUM_NEIGHBORS; i++) {
                    Dot neighbor = this.Neighbors[i];
                    if (neighbor != null) {
                        //How long is the spring??
                        double dX = neighbor.X - this.X + Math.random();
                        double dY = neighbor.Y - this.Y + Math.random();
                        double distance = Math.sqrt(dX * dX + dY * dY);
                        //dX dY change to normal
                        if (distance == 0) {
                            continue;
                        }
                        dX /= distance;
                        dY /= distance;

                        distance -= SPRING_LENGTH;
                        dX = dX * 0.03 * distance;
                        dY = dY * 0.03 * distance;

                        this.XVelocity += dX;
                        this.YVelocity += dY;
                        neighbor.XVelocity -= dX;
                        neighbor.YVelocity -= dY;
                    }
                }
            }

        }
        c.drawBitmap(
            dot.getBitmap(),
                (int)(this.X)-(dot.getBitmap().getWidth()/2),
                (int)(this.Y)-(dot.getBitmap().getWidth()/2),
                null);
    }

    // Compute new neighbor distances. Make sure neighbors are the closest 6 dots
    public void updateNeighbors(ArrayList<Dot> otherDots) {
        // First, break neighbors when the get too far away.
        // Sort to make it easier to check if a dot is already a neighbor.
        Dot dot;
        this.numNeighbors = 0;
        for (int i = 0; i < MAX_NUM_NEIGHBORS; i++) {
            dot = this.Neighbors[i];
            if (dot != null) {
                double dx = dot.X - this.X;
                double dy = dot.Y - this.Y;
                this.NeighborDistances[i] = dx * dx + dy * dy;
                if (this.NeighborDistances[i] >= MAX_DISTANCE) {
                    this.Neighbors[i] = null;
                    this.NeighborDistances[i] = MAX_DISTANCE;
                } else {
                    ++this.numNeighbors;
                }
            } else {
                this.NeighborDistances[i] = MAX_DISTANCE;
            }
            // Sort.
            int j = i;
            while (j > 1 && this.NeighborDistances[j] < this.NeighborDistances[j - 1]) {
                // swap.
                Dot swapDot = this.Neighbors[j];
                this.Neighbors[j] = this.Neighbors[j - 1];
                this.Neighbors[j - 1] = swapDot;
                double swapDist = this.NeighborDistances[j];
                this.NeighborDistances[j] = this.NeighborDistances[j - 1];
                this.NeighborDistances[j - 1] = swapDist;
            }
        }

        // Note:  If we keep this strategry, we should make a spring object that keeps its length
        // and dots.  It would minimize the number of times distances is computed.

        // Replace springs with dots that are closer.
        for (int i = 0; i < otherDots.size(); ++i) {
            Dot potentialNeighbor = otherDots.get(i);
            if (potentialNeighbor != this) {
                // Compute the distance
                double dx = this.X - potentialNeighbor.X;
                double dy = this.Y - potentialNeighbor.Y;
                double potentialNeighborDist = dx * dx + dy * dy;
                if (potentialNeighborDist < MAX_DISTANCE) {
                    for (int j = 0; j < MAX_NUM_NEIGHBORS; j++) {
                        if (potentialNeighbor == this.Neighbors[j]) {
                            // Skip if the dot is already a neighbor.
                            // Sorting ensures that we detect this before
                            // the dot as added a second time.
                            potentialNeighbor = null;
                            potentialNeighborDist = MAX_DISTANCE;
                        }
                        if (potentialNeighbor != null && potentialNeighborDist < this.NeighborDistances[j]) {
                            // Replace / swap.
                            // Swap and continue loop keeps array sorted.
                            Dot swapDot = this.Neighbors[j];
                            this.Neighbors[j] = potentialNeighbor;
                            potentialNeighbor = swapDot;
                            double swapDist = this.NeighborDistances[j];
                            this.NeighborDistances[j] = potentialNeighborDist;
                            potentialNeighborDist = swapDist;
                            if (swapDot != null) {
                                ++this.numNeighbors;
                            }
                        }
                    }
                }
            }
        }
    }
    // Move the dot (given spring forces


    // Draw a line for each spring (for debugging)
    public void drawSprings(Canvas canvas) {
        paint.setColor(Color.WHITE);
        for (int i = 0; i < MAX_NUM_NEIGHBORS; i++) {
            if (this.Neighbors[i] != null) {
                canvas.drawLine((float) this.X, (float) this.Y,
                        (float) (this.Neighbors[i].X), (float) (this.Neighbors[i].Y), paint);
            }
        }
    }
}
