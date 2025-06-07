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
				long sleepNanos = intervalNanos;
				long deadline = System.nanoTime() + sleepNanos;

				while (running) {
					if (forceRunRequested) {
						forceRunRequested = false;
						break; // 즉시 실행
					}

					long now = System.nanoTime();
					long remaining = deadline - now;

					if (remaining <= 0)
						break;

					if (remaining > 2_000_000) {
						synchronized (lock) {
							lock.wait(remaining / 1_000_000, (int) (remaining % 1_000_000));
						}
					} else {
						Thread.onSpinWait();
					}
				}

				if (running) {
					task.run(); // 🔁 여기를 대기 후 실행으로 이동
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			System.err.println("[GameTimer] Task threw exception:");
			e.printStackTrace();
		}
	}
}
