package tetris.logic.core;

public class GameTimer implements Runnable {
	private volatile boolean running = false;
	private volatile long intervalNanos;
	private final Runnable task;
	private Thread thread;

	private final Object lock = new Object();
	private volatile boolean forceRunRequested = false;

	public GameTimer(long intervalNanos, Runnable task) {
		if (intervalNanos <= 0) {
			throw new IllegalArgumentException("intervalNanos must be > 0");
		}
		this.intervalNanos = intervalNanos;
		this.task = task;
	}

	public synchronized void start() {
		if (running)
			return;

		running = true;
		forceRunRequested = false;

		thread = new Thread(this, "TetrisTimerThread");
		thread.setDaemon(true);
		thread.start();
	}

	public synchronized void stop() {
		running = false;

		// 인터럽트 호출로 즉시 대기 상태 깨우기
		if (thread != null && thread.isAlive()) {
			thread.interrupt();
		}

		synchronized (lock) {
			lock.notifyAll();
		}
	}

	public boolean isRunning() {
		return running;
	}

	public void setIntervalNanos(long newIntervalNanos) {
		if (newIntervalNanos <= 0) {
			throw new IllegalArgumentException("intervalNanos must be > 0");
		}
		intervalNanos = newIntervalNanos;
		requestImmediateRun();
	}

	public long getIntervalNanos() {
		return intervalNanos;
	}

	private void requestImmediateRun() {
		forceRunRequested = true;
		synchronized (lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void run() {
		try {
			while (running) {
				task.run();

				long sleepNanos = intervalNanos;
				long deadline = System.nanoTime() + sleepNanos;

				while (running) {
					// forceRunRequested가 true면 즉시 task 실행
					// 호출 후 플래그를 false로 초기화하여 다음 대기 사이클로 넘어감
					if (forceRunRequested) {
						forceRunRequested = false;
						break;
					}

					long now = System.nanoTime();
					long remaining = deadline - now;

					if (remaining <= 0)
						break;
					
					// 2ms 이상 남았으면 대기 상태로 들어가 CPU 낭비 최소화
					// 2ms 이하일 때는 spin wait으로 짧은 시간 동안 대기
					if (remaining > 2_000_000) {
						synchronized (lock) {
							lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
						}
					} else {
						Thread.onSpinWait();
					}
				}
			}
		} catch (InterruptedException e) {
			// 인터럽트 시 정상 종료 처리
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			System.err.println("[GameTimer] Task threw exception:");
			e.printStackTrace();
		}
	}
}
