package edu.university.examinator.domain;

import edu.university.examinator.service.TimerService;
import lombok.Getter;

import java.util.Timer;
import java.util.TimerTask;

public class ConsoleTimer {
    @Getter
    private final int totalMilliseconds = 5000;  // Загальний час в мілісекундах
    private final TimerService timerService;
    private Timer timer;

    public ConsoleTimer(TimerService timerService) {
        this.timerService = timerService;
    }

    public int getTotalSeconds() {
        return totalMilliseconds / 1000;
    }

    public void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int tempMilliseconds = totalMilliseconds;

            @Override
            public void run() {
                if (tempMilliseconds > 0) {
                    tempMilliseconds -= 10;
                    notifyTimeChanged(tempMilliseconds);
                } else {
                    handleTimerFinish();
                }
            }
        }, 0, 10);  // Оновлення кожну мілісекунду (1 мілісекунда)

    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void resetTimer() {
        notifyTimeChanged(totalMilliseconds);
    }

    private void notifyTimeChanged(int milliseconds) {
        if (timerService != null) {
            timerService.onTimeChanged(milliseconds);
        }
    }

    private void handleTimerFinish() {
        try {
            stopTimer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
