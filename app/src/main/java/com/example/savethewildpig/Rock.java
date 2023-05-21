package com.example.savethewildpig;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Rock {
    Bitmap rock[] = new Bitmap[3];
    int rockFrame = 0;
    int rockX, rockY, rockVelocity;
    Random random;

    public Rock(Context context) {
        rock[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock0);
        rock[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock1);
        rock[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.rock2);
        random = new Random();
        resetPosition();
    }

    public Bitmap getRock(int rockFrame) {
        return rock[rockFrame];
    }

    public int getRockWidth() {
        return rock[0].getWidth();
    }

    public int getRockHeight() {
        return rock[0].getHeight();
    }

    public void resetPosition() {
        rockX = random.nextInt(GameView.dWidth - getRockWidth());
        rockY = -200 - random.nextInt(600);
        rockVelocity = 35 + random.nextInt(16);
    }
}
