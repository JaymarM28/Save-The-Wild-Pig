package com.example.savethewildpig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends View {

    Bitmap background, ground, character;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    float TEXT_SIZE = 120;
    int points = 0;
    int life = 3;
    static int dWidth, dHeight;
    Random random;
    float characterX, characterY;
    float oldX;
    float oldCharacterX;
    ArrayList<Rock> rocks;
    ArrayList<Explosion> explosions;

    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        character = BitmapFactory.decodeResource(getResources(), R.drawable.character);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        rectBackground = new Rect(0, 0, dWidth, dHeight);
        rectGround = new Rect(0, dHeight - ground.getHeight(), dWidth, dHeight);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        textPaint.setColor(Color.rgb(255, 165, 0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.morden));
        healthPaint.setColor(Color.GREEN);
        random = new Random();
        characterX = dWidth / 2 - character.getWidth() / 2;
        characterY = dHeight - ground.getHeight() - character.getHeight();
        rocks = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Rock rock = new Rock(context);
            rocks.add(rock);
        }
        explosions = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground, null, rectGround, null);
        canvas.drawBitmap(character, characterX, characterY, null);
        for (int i = 0; i < rocks.size(); i++) {
            canvas.drawBitmap(rocks.get(i).getRock(rocks.get(i).rockFrame), rocks.get(i).rockX, rocks.get(i).rockY, null);
            rocks.get(i).rockFrame++;
            if (rocks.get(i).rockFrame > 2) {
                rocks.get(i).rockFrame = 0;
            }
            rocks.get(i).rockY += rocks.get(i).rockVelocity;
            if (rocks.get(i).rockY + rocks.get(i).getRockHeight() >= dHeight - ground.getHeight()) {
                points++;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = rocks.get(i).rockX;
                explosion.explosionY = rocks.get(i).rockY;
                explosions.add(explosion);
                rocks.get(i).resetPosition();
            }
        }
        for (int i = 0; i < rocks.size(); i++) {
            if (rocks.get(i).rockX + rocks.get(i).getRockWidth() >= characterX
                    && rocks.get(i).rockX <= characterX + character.getWidth()
                    && rocks.get(i).rockY + rocks.get(i).getRockHeight() >= characterY
                    && rocks.get(i).rockY <= characterY + character.getHeight()) {
                life--;
                rocks.get(i).resetPosition();
                if (life == 0) {
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points", points);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX,
                    explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 2) {
                explosions.remove(i);
            }
        }

        if (life == 2) {
            healthPaint.setColor(Color.YELLOW);
        } else if (life == 1) {
            healthPaint.setColor(Color.RED);
        }
        canvas.drawRect(dWidth - 200, 30, dWidth - 200 + 60 * life, 80, healthPaint);
        canvas.drawText("" + points, 20, TEXT_SIZE, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if (touchY >= characterY) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                oldX = event.getX();
                oldCharacterX = characterX;
            }
            if (action == MotionEvent.ACTION_MOVE) {
                float shift = oldX - touchX;
                float newCharacterX = oldCharacterX - shift;
                if (newCharacterX <= 0)
                    characterX = 0;
                else if (newCharacterX >= dWidth - character.getWidth())
                    characterX = dWidth - character.getWidth();
                else
                    characterX = newCharacterX;
            }
        }
        return true;
    }
}
