package com.example.breathgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GameView extends View implements View.OnTouchListener {
    // Игровые объекты
    private float ballX, ballY;
    private float ballRadius = 40;
    private float targetY;
    private int score = 0;
    private int combo = 0;
    private boolean isGameOver = false;
    
    // Кольца
    private ArrayList<Ring> rings = new ArrayList<>();
    private Random random = new Random();
    private Handler handler = new Handler();
    
    // Кисти
    private Paint ballPaint = new Paint();
    private Paint ringPaint = new Paint();
    private Paint ringGlowPaint = new Paint();
    private Paint breathIndicatorPaint = new Paint();
    
    // Параметры дыхания
    private float currentPressure = 0.5f;
    private float targetPressure = 0.5f;
    private String breathState = "ВДОХ";
    
    // Визуальные эффекты
    private float glowSize = 0;
    private int shakeX = 0, shakeY = 0;
    private boolean isSuccess = false;
    private long successTime = 0;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnTouchListener(this);
        initGame();
    }

    private void initGame() {
        // Размеры экрана
        ballX = 400;
        ballY = 800;
        targetY = ballY;
        
        // Настройка кистей
        ballPaint.setColor(Color.CYAN);
        ballPaint.setAntiAlias(true);
        ballPaint.setShadowLayer(20, 0, 0, Color.CYAN);
        
        ringPaint.setColor(Color.WHITE);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(8);
        ringPaint.setAntiAlias(true);
        
        ringGlowPaint.setColor(Color.parseColor("#FF6B6B"));
        ringGlowPaint.setStyle(Paint.Style.FILL);
        ringGlowPaint.setAlpha(30);
        ringGlowPaint.setAntiAlias(true);
        
        breathIndicatorPaint.setColor(Color.WHITE);
        breathIndicatorPaint.setTextSize(40);
        breathIndicatorPaint.setAntiAlias(true);
        
        // Генерация колец
        generateRings();
        
        // Запуск обновления
        startGameLoop();
    }

    private void generateRings() {
        rings.clear();
        for (int i = 0; i < 8; i++) {
            float x = 100 + i * 200;
            float y = 200 + random.nextInt(1200);
            float size = 0.5f + random.nextFloat() * 1.0f;
            Ring ring = new Ring(x, y, size);
            ring.passed = false;
            rings.add(ring);
        }
    }

    private void startGameLoop() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    updateGame();
                    invalidate(); // Перерисовка
                    handler.postDelayed(this, 16); // ~60 FPS
                }
            }
        }, 16);
    }

    private void updateGame() {
        // Движение шарика к цели
        ballY += (targetY - ballY) * 0.15f;
        
        // Проверка колец
        Iterator<Ring> iterator = rings.iterator();
        while (iterator.hasNext()) {
            Ring ring = iterator.next();
            
            // Проверка прохождения через кольцо
            if (!ring.passed && ballX > ring.x - 50 && ballX < ring.x + 150) {
                if (Math.abs(ballY - ring.y) < 80) {
                    // Успех! Проверяем глубину дыхания
                    float requiredPressure = ring.size;
                    float tolerance = 0.15f;
                    
                    if (Math.abs(currentPressure - requiredPressure) < tolerance) {
                        // Идеальное попадание!
                        ring.passed = true;
                        score += 10 + combo * 2;
                        combo++;
                        isSuccess = true;
                        successTime = System.currentTimeMillis();
                        
                        // Виброотклик
                        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null) {
                            vibrator.vibrate(50);
                        }
                        
                        // Эффект свечения
                        glowSize = 100;
                        
                    } else {
                        // Ошибка дыхания - штраф
                        combo = 0;
                        shakeX = 20;
                        shakeY = 20;
                        score -= 5;
                        
                        // Красный цвет ошибки
                        ballPaint.setColor(Color.RED);
                        handler.postDelayed(() -> {
                            ballPaint.setColor(Color.CYAN);
                        }, 300);
                    }
                }
            }
        }
        
        // Анимация свечения
        if (glowSize > 0) {
            glowSize *= 0.9f;
            if (glowSize < 1) glowSize = 0;
        }
        
        // Анимация тряски
        if (shakeX > 0) {
            shakeX *= 0.9f;
            shakeY *= 0.9f;
            if (shakeX < 1) shakeX = 0;
            if (shakeY < 1) shakeY = 0;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Задний фон с градиентом
        canvas.drawColor(Color.parseColor("#0f0c29"));
        
        // Тряска камеры
        if (shakeX > 1 || shakeY > 1) {
            canvas.translate(shakeX * (random.nextFloat() - 0.5f) * 2, 
                           shakeY * (random.nextFloat() - 0.5f) * 2);
        }
        
        // Рисуем кольца
        for (Ring ring : rings) {
            if (!ring.passed) {
                float radius = 60 + ring.size * 60;
                
                // Свечение кольца
                canvas.drawCircle(ring.x, ring.y, radius + 30, ringGlowPaint);
                
                // Само кольцо
                canvas.drawCircle(ring.x, ring.y, radius, ringPaint);
                
                // Показываем нужную глубину внутри кольца
                Paint textPaint = new Paint();
                textPaint.setColor(Color.YELLOW);
                textPaint.setTextSize(30);
                textPaint.setAntiAlias(true);
                String depthText = String.format("%.0f%%", ring.size * 100);
                canvas.drawText(depthText, ring.x - 30, ring.y + 10, textPaint);
            }
        }
        
        // Рисуем шарик
        canvas.drawCircle(ballX, ballY, ballRadius + glowSize / 4, ballPaint);
        canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
        
        // Индикатор дыхания (шкала)
        float indicatorX = getWidth() - 80;
        float indicatorY = 200;
        float indicatorHeight = 600;
        
        breathIndicatorPaint.setColor(Color.WHITE);
        breathIndicatorPaint.setStyle(Paint.Style.STROKE);
        breathIndicatorPaint.setStrokeWidth(10);
        canvas.drawRect(indicatorX - 20, indicatorY, indicatorX + 20, indicatorY + indicatorHeight, breathIndicatorPaint);
        
        // Заполнение шкалы (глубина дыхания)
        float fillHeight = currentPressure * indicatorHeight;
        breathIndicatorPaint.setStyle(Paint.Style.FILL);
        breathIndicatorPaint.setColor(currentPressure > 0.6 ? Color.RED : Color.GREEN);
        canvas.drawRect(indicatorX - 15, indicatorY + indicatorHeight - fillHeight, 
                       indicatorX + 15, indicatorY + indicatorHeight, breathIndicatorPaint);
        
        // Подпись к шкале
        breathIndicatorPaint.setStyle(Paint.Style.FILL);
        breathIndicatorPaint.setColor(Color.WHITE);
        breathIndicatorPaint.setTextSize(20);
        canvas.drawText("ВДОХ", indicatorX - 30, indicatorY + indicatorHeight + 40);
        canvas.drawText("ВЫДОХ", indicatorX - 30, indicatorY - 20);
        
        // Статус дыхания
        breathIndicatorPaint.setColor(Color.WHITE);
        breathIndicatorPaint.setTextSize(60);
        breathIndicatorPaint.setStyle(Paint.Style.FILL);
        String stateText = currentPressure > 0.5 ? "ВЫДОХ" : "ВДОХ";
        canvas.drawText(stateText, 100, 300, breathIndicatorPaint);
        
        // Комбо
        if (combo > 0) {
            breathIndicatorPaint.setColor(Color.YELLOW);
            breathIndicatorPaint.setTextSize(50);
            canvas.drawText("x" + combo, 100, 400, breathIndicatorPaint);
        }
        
        // Счет
        breathIndicatorPaint.setColor(Color.WHITE);
        breathIndicatorPaint.setTextSize(60);
        canvas.drawText("" + score, getWidth() - 200, 150, breathIndicatorPaint);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isGameOver) return true;
        
        int action = event.getActionMasked();
        
        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) {
            // ГЛУБИНА ДЫХАНИЯ = сила нажатия
            float pressure = event.getSize(); // 0..1
            currentPressure = Math.min(1, pressure * 2);
            
            // Визуальный отклик на силу нажатия
            targetY = 1800 - (currentPressure * 1400);
            
            // Проверка, не улетел ли шарик за экран
            if (targetY < 100) targetY = 100;
            if (targetY > 1800) targetY = 1800;
            
            // Обновляем цвет шарика в зависимости от глубины
            if (currentPressure > 0.7) {
                ballPaint.setColor(Color.RED);
            } else if (currentPressure < 0.3) {
                ballPaint.setColor(Color.GREEN);
            } else {
                ballPaint.setColor(Color.CYAN);
            }
            
            return true;
        }
        
        if (action == MotionEvent.ACTION_UP) {
            // Отпустили палец - возвращаемся в нейтраль
            currentPressure = 0.5f;
            targetY = 900;
            ballPaint.setColor(Color.CYAN);
        }
        
        return true;
    }
    
    // Вспомогательный класс для колец
    private class Ring {
        float x, y;
        float size; // 0.3 - 0.8 (глубина дыхания для прохождения)
        boolean passed;
        
        Ring(float x, float y, float size) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.passed = false;
        }
    }
}