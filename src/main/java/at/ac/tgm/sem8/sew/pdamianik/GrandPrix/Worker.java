package at.ac.tgm.sem8.sew.pdamianik.GrandPrix;

import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations.Task;
import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.messages.RoundUpdateMessage;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

public class Worker implements Runnable {
	private final int threadNumber;
	private final int roundCount;
	private final List<Task> tasks;
	private final CountDownLatch readyLatch;
	private final Lock startLock;
	private final BlockingQueue<RoundUpdateMessage> roundUpdateQueue;

	public Worker(int threadNumber, int roundCount, List<Task> tasks, CountDownLatch readyLatch, Lock startLock, BlockingQueue<RoundUpdateMessage> roundUpdateQueue) {
		this.threadNumber = threadNumber;
		this.roundCount = roundCount;
		this.tasks = tasks;
		this.readyLatch = readyLatch;
		this.startLock = startLock;
		this.roundUpdateQueue = roundUpdateQueue;
	}

	@Override
	public void run() {
		long startTime;
		long endTime;

		readyLatch.countDown();

		startLock.lock();
		startLock.unlock();

		for (int i = 0; i < roundCount; i++) {
			startTime = System.currentTimeMillis();
			tasks.get(i).execute();
			endTime = System.currentTimeMillis();

			RoundUpdateMessage message = new RoundUpdateMessage(threadNumber, i + 1, endTime - startTime);
			try {
				roundUpdateQueue.put(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
