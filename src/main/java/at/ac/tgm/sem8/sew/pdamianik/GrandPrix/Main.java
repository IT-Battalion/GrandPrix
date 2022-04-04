package at.ac.tgm.sem8.sew.pdamianik.GrandPrix;

import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations.Babylonian;
import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations.FibonacciRecursive;
import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations.Pi;
import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.calculations.Task;
import at.ac.tgm.sem8.sew.pdamianik.GrandPrix.messages.RoundUpdateMessage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
	private static final int THREAD_COUNT = 3;
	private static final int THREAD_ROUND_COUNT = 3;
	private static final Class[] possibleTasks = {
			Pi.class,
			Babylonian.class,
			FibonacciRecursive.class,
	};

	public static void main(String[] args) {
		final CountDownLatch readyLatch = new CountDownLatch(THREAD_COUNT);
		final BlockingQueue<RoundUpdateMessage> roundUpdateQueue = new LinkedBlockingQueue<>();

		final Lock lock = new ReentrantLock();

		final List<List<Task>> workerTasks = new ArrayList<>();
		final Integer[] workerIterations = new Integer[THREAD_COUNT];
		for (int i = 0; i < THREAD_COUNT; i++) {
			workerIterations[i] = 0;
		}

		final Random random = new Random();

		for (int i = 0; i < THREAD_COUNT; i++) {
			List<Task> singleWorkerTasks = new ArrayList<>();

			for (int j = 0; j < THREAD_ROUND_COUNT; j++) {
				try {
					Class<Task> randomTaskClass = possibleTasks[random.nextInt(possibleTasks.length)];
					singleWorkerTasks.add(randomTaskClass.getConstructor().newInstance());
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					e.printStackTrace();
				}
			}

			workerTasks.add(singleWorkerTasks);
		}

		lock.lock();

		for (int i = 0; i < THREAD_COUNT; i++) {
			Thread thread = new Thread(new Worker(i + 1, THREAD_ROUND_COUNT, workerTasks.get(i), readyLatch, lock, roundUpdateQueue));
			thread.start();
		}

		try {
			readyLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("The runners are ready...");
		System.out.println("Start!");
		lock.unlock();

		int finishedCount = 0;
		while (finishedCount < THREAD_COUNT) {
			RoundUpdateMessage message;
			try {
				message = roundUpdateQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}

			int threadNumber = message.threadNumber() - 1;
			workerIterations[threadNumber] += 1;
			String position = "";
			if (workerIterations[threadNumber] == THREAD_ROUND_COUNT) {
				++finishedCount;
				String suffix = switch (finishedCount % 10) {
					case 1 -> "st";
					case 2 -> "nd";
					case 3 -> "rd";
					default -> "th";
				};
				position = " " + finishedCount + suffix + " place!";
			}

			System.out.printf("Thread %d has completed round %d after %dms!%s\n", message.threadNumber(), message.iteration(), message.executionTime(), position);
		}
	}
}
