package tetris.logic.core;

/**
 * 테트로미노가 바닥에 닿은 후 잠기기(Lock)까지의 지연 시간을 관리하는 타이머.
 * 정해진 시간(0.5초) 동안만 실행되고, 플레이어의 조작으로 리셋될 수 있음.
 */
public class LockDelayTimer implements Runnable {

    // --- Constants ---
    private static final long LOCK_DELAY_NANOS = 500_000_000L; // 0.5초

    // --- Instance Fields ---
    private final Runnable task;            // 지연 시간 후 실행될 작업 (블록을 필드에 고정)
    private final Object lock = new Object(); // 스레드 동기화를 위한 락 객체

    private volatile boolean running = false;         // 타이머 실행 상태
    private volatile boolean forceRunRequested = false; // 즉시 실행 요청 플래그 (하드 드롭용)

    private Thread thread; // 타이머 로직을 실행할 스레드

    // --- Constructor ---
    public LockDelayTimer(Runnable task) {
        this.task = task;
    }

    // --- Public Methods ---
    /** 락 딜레이 타이머를 시작. */
    public synchronized void start() {
        if (running) return;
        
        forceRunRequested = false;
        running = true;
        
        thread = new Thread(this, "LockDelayTimerThread");
        thread.setDaemon(true);
        thread.start();
    }

    /** 락 딜레이 타이머를 중지. */
    public synchronized void stop() {
        running = false;
        // wait() 상태의 스레드를 깨워 즉시 종료
        synchronized (lock) {
            lock.notifyAll();
        }
    }
    
    /**
     * 타이머를 리셋 (중지 후 다시 시작).
     * 블록이 바닥에 닿은 상태에서 이동/회전 시 호출되어 락 시간을 초기화.
     */
    public void reset() {
        stop();
        try {
            if (thread != null) {
                thread.join(); // 기존 스레드가 완전히 종료될 때까지 대기
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        start();
    }
    
    /** 타이머가 실행 중인지 여부를 반환. */
    public boolean isRunning() {
        return running;
    }

    /** 락을 즉시 실행하도록 요청 (하드 드롭 시 사용). */
    public void requestImmediateRun() {
        forceRunRequested = true;
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    /** 타이머의 메인 로직. 스레드에 의해 실행됨. */
    @Override
    public void run() {
        // 이 타이머는 한 번만 실행되는 구조이므로, while(running) 바깥 로직이 더 적합할 수 있음.
        // 현재 구조는 reset()을 통해 재시작하는 방식에 맞춰져 있음.
        try {
            long deadline = System.nanoTime() + LOCK_DELAY_NANOS;

            // 데드라인까지 대기
            while (running) {
                if (forceRunRequested) {
                    forceRunRequested = false;
                    break; // 즉시 실행 요청
                }

                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) break; // 시간 종료

                if (remaining > 2_000_000) {
                    synchronized (lock) {
                        lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
                    }
                } else {
                    Thread.onSpinWait();
                }
            }

            if (!running) return; // 대기 중에 stop()이 호출된 경우 작업 실행 안함
            
            task.run(); // 락 작업 실행

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[LockDelayTimer] Task threw exception:");
            e.printStackTrace();
        } finally {
            // 작업이 끝나면 타이머는 자동으로 멈춤
            running = false;
        }
    }
}