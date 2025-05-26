package tetris.logic;

/**
 * TetrisTimer는 일정 간격으로 지정된 작업을 실행하는 타이머 스레드입니다.
 * 나노초 단위로 정확한 간격을 유지하며, CPU 효율과 정확도를 위해
 * 일정 시간 이상 대기 시 Thread.sleep을 사용하고, 짧은 시간 대기는 spin wait을 활용합니다.
 */
public class GameTimer implements Runnable {
    // 실행 상태 플래그, volatile로 멀티스레드 간 가시성 보장
    private volatile boolean running = false;

    // 작업 실행 간격 (나노초 단위, 1초 = 1_000_000_000 나노초)
    private final long intervalNanos;

    // 주기적으로 실행할 작업
    private final Runnable task;

    /**
     * 타이머 생성자
     * @param intervalNanos 실행 간격 (나노초 단위), 0 이하 값은 IllegalArgumentException 발생
     * @param task 주기적으로 실행할 Runnable 작업
     */
    public GameTimer(long intervalNanos, Runnable task) {
        if (intervalNanos <= 0) {
            throw new IllegalArgumentException("intervalNanos must be > 0");
        }
        this.intervalNanos = intervalNanos;
        this.task = task;
    }

    /**
     * 타이머 시작 (새 스레드 생성 및 실행)
     * 이미 실행 중이면 중복 실행하지 않음
     */
    public void start() {
        if (running) return;
        running = true;
        Thread thread = new Thread(this, "TetrisTimerThread");
        thread.setDaemon(true); // 데몬 스레드로 설정 (메인 종료 시 자동 종료)
        thread.start();
    }

    /**
     * 타이머 정지 (실행 상태 플래그 false로 변경)
     * run() 메서드의 while문 종료 유도
     */
    public void stop() {
        running = false;
    }

    /**
     * 타이머 스레드 메인 루프
     * 현재 시간과 다음 실행 예정 시간을 비교해 작업 실행 여부 판단
     * - 예정 시간이 도래하면 task.run() 실행 후 다음 실행 시간 갱신
     * - 너무 많이 밀렸을 경우 보정 수행
     * - 예정 시간까지 남은 시간이 2ms 이상이면 Thread.sleep 사용하여 CPU 부담 최소화
     * - 2ms 이하면 spin wait(바쁜 대기)하여 정확도 향상
     */
    @Override
    public void run() {
        long nextRun = System.nanoTime(); // 다음 작업 실행 예정 시간 (초기값 현재 시각)
        while (running) {
            long now = System.nanoTime();

            if (now >= nextRun) {
                // 실행 예정 시간 도달 시 task 수행
                try {
                    task.run();
                } catch (Exception e) {
                    e.printStackTrace(); // 예외 발생 시 스택 트레이스 출력 (로깅으로 대체 가능)
                }

                // 다음 실행 예정 시간 갱신
                nextRun += intervalNanos;

                // 만약 작업 실행이 너무 밀려서 다음 실행 시간도 지났다면
                // 현재 시각 + intervalNanos로 보정 (스케줄 지연 방지)
                if (now > nextRun + intervalNanos) {
                    nextRun = now + intervalNanos;
                }
            } else {
                // 아직 실행 시간이 안 된 경우 남은 시간 계산
                long sleepNanos = nextRun - now;

                if (sleepNanos > 2_000_000) {  // 2ms 이상 남았으면 sleep
                    try {
                        // 나노초 단위로 sleep (밀리초, 나노초 분리)
                        Thread.sleep(sleepNanos / 1_000_000, (int)(sleepNanos % 1_000_000));
                    } catch (InterruptedException e) {
                        // 인터럽트 발생 시 현재 스레드 인터럽트 상태 복원
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // 2ms 이하면 spin wait: 짧은 시간 동안 바쁜 대기, 정확도 높임
                    while (System.nanoTime() < nextRun) {
                        Thread.onSpinWait(); // Java 9 이상에서 CPU 효율적으로 대기
                    }
                }
            }
        }
    }
}
