package tetris.logic.core;

/**
 * 주기적으로 특정 작업을 실행하는 정밀 타이머.
 * 게임의 중력(블록 자동 하강)을 구현하는 데 사용.
 * nanoseconds 단위로 동작하며, 간격 변경 및 즉시 실행 기능을 지원.
 */
public class GameTimer implements Runnable {

    // --- Instance Fields ---
    private final Runnable task;            // 타이머가 주기적으로 실행할 작업
    private final Object lock = new Object(); // 스레드 동기화를 위한 락 객체

    private volatile boolean running = false;         // 타이머의 실행 상태
    private volatile long intervalNanos;              // 작업 실행 간격 (나노초)
    private volatile boolean forceRunRequested = false; // 작업 즉시 실행 요청 플래그
    
    private Thread thread; // 타이머 로직을 실행할 스레드

    // --- Constructor ---
    public GameTimer(long intervalNanos, Runnable task) {
        if (intervalNanos <= 0) {
            throw new IllegalArgumentException("intervalNanos must be > 0");
        }
        this.intervalNanos = intervalNanos;
        this.task = task;
    }

    // --- Public Methods ---
    /** 타이머를 시작. 이미 실행 중이면 아무 작업도 하지 않음. */
    public synchronized void start() {
        if (running) return;

        running = true;
        forceRunRequested = false;

        thread = new Thread(this, "TetrisTimerThread");
        thread.setDaemon(true); // 주 스레드 종료 시 함께 종료되도록 데몬 스레드로 설정
        thread.start();
    }

    /** 타이머를 정지. 스레드를 중단시키고 대기 상태의 스레드를 깨움. */
    public synchronized void stop() {
        running = false;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        // wait() 상태에 있는 스레드를 즉시 깨워 루프를 종료시키기 위함
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /** 타이머가 현재 실행 중인지 여부를 반환. */
    public boolean isRunning() {
        return running;
    }

    /** 
     * 타이머의 실행 간격을 동적으로 변경.
     * 간격 변경 후에는 다음 작업을 즉시 실행하도록 요청.
     */
    public void setIntervalNanos(long newIntervalNanos) {
        if (newIntervalNanos <= 0) {
            throw new IllegalArgumentException("intervalNanos must be > 0");
        }
        this.intervalNanos = newIntervalNanos;
        requestImmediateRun(); // 간격 변경 시 즉시 반영
    }

    /** 현재 설정된 실행 간격(nanos)을 반환. */
    public long getIntervalNanos() {
        return intervalNanos;
    }

    /** 타이머의 메인 로직. 스레드에 의해 실행됨. */
    @Override
    public void run() {
        try {
            while (running) {
                // 다음 실행 시간 계산
                long sleepNanos = intervalNanos;
                long deadline = System.nanoTime() + sleepNanos;

                // 데드라인까지 대기 (정밀한 대기를 위한 루프)
                while (running) {
                    if (forceRunRequested) {
                        forceRunRequested = false;
                        break; // 즉시 실행 요청이 들어오면 대기 중단
                    }

                    long now = System.nanoTime();
                    long remaining = deadline - now;

                    if (remaining <= 0) break; // 대기 시간 종료

                    // 대기 시간이 길면(2ms 초과) wait()으로 CPU 자원 절약
                    // 짧으면 Spin-Wait으로 정밀도 확보
                    if (remaining > 2_000_000) {
                        synchronized (lock) {
                            lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
                        }
                    } else {
                        Thread.onSpinWait(); // Busy-waiting. 짧은 시간에 효과적
                    }
                }

                if (running) {
                    task.run(); // 실제 작업 실행
                }
            }
        } catch (InterruptedException e) {
            // 스레드가 interrupt되면 여기서 종료
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[GameTimer] Task threw exception:");
            e.printStackTrace();
        }
    }
    
    // --- Private Methods ---
    /** 다음 작업을 즉시 실행하도록 요청. */
    private void requestImmediateRun() {
        forceRunRequested = true;
        // wait() 상태에 있는 스레드를 깨워서 즉시 실행 루프로 진입시킴
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}