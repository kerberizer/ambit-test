package ambit2.rest.task;

import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.data.Reference;

import ambit2.rest.SimpleTaskResource;

public class TaskStorage<USERID> implements ITaskStorage<USERID> {
	protected Logger logger;
	protected String name;
	protected TimeUnit taskCleanupUnit = TimeUnit.MINUTES;
	protected long taskCleanupRate = 60; //30 min
	protected double cpuutilisation = 0.75;
	protected double waittime=1;
	protected double cputime=1;
	
	protected ExecutorCompletionService<Reference> completionService_internal;
	protected ExecutorCompletionService<Reference> completionService_external;
	protected ExecutorService pool_internal;
	protected ExecutorService pool_external;
	protected ScheduledThreadPoolExecutor cleanupTimer;
	protected ScheduledThreadPoolExecutor cleanupCompletedTasks;

	protected ConcurrentMap<UUID,Task<Reference,USERID>> tasks;
	
	public TaskStorage(String name, Logger logger) {
		this.name = name;
		this.logger = logger;
		pool_internal = createExecutorService(5);
			//	(int)Math.ceil(Runtime.getRuntime().availableProcessors()*cpuutilisation*(1+waittime/cputime)));
		pool_external = createExecutorService(1);

		completionService_internal = new ExecutorCompletionService<Reference>(pool_internal);
		completionService_external = new ExecutorCompletionService<Reference>(pool_external);

		tasks = new ConcurrentHashMap<UUID,Task<Reference,USERID>>();
		

		TimerTask cleanUpTasks  = new TimerTask() {
			
			@Override
			public void run() {
				cleanUpTasks();
				
			}
		};
		
		TimerTask completedTasks  = new TimerTask() {
			
			@Override
			public void run() {
				Future<Reference> f = null;
				while ((f = completionService_internal.poll()) != null) {
					System.out.println(f);
					f= null;
				}
				while ((f = completionService_external.poll()) != null) {
					System.out.println(f);
					f= null;
				}
			}
		};
		

		cleanupTimer = new ScheduledThreadPoolExecutor(1);
		cleanupTimer.scheduleWithFixedDelay(cleanUpTasks, taskCleanupRate, taskCleanupRate,taskCleanupUnit);

		cleanupCompletedTasks = new ScheduledThreadPoolExecutor(1);
		cleanupCompletedTasks.scheduleWithFixedDelay(completedTasks, 100,100,TimeUnit.MILLISECONDS);
	}
	
	public void cleanUpTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			Task<Reference,USERID> task = tasks.get(key);
			try {
				//task.update();
				if (task.isDone() && (task.isExpired(taskCleanupRate))) tasks.remove(key);
			} catch (Exception x) {Context.getCurrentLogger().warning(x.getMessage());}
		}
	}	
	protected ExecutorService createExecutorService(int maxThreads) {
		
		return Executors.newFixedThreadPool(maxThreads,new ThreadFactory() {
		//return Executors.newCachedThreadPool(new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(true);
				thread.setName(String.format("%s task executor",name));
				thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					public void uncaughtException(Thread t, Throwable e) {
			            java.io.StringWriter stackTraceWriter = new java.io.StringWriter();
			            e.printStackTrace(new PrintWriter(stackTraceWriter));
						logger.severe(stackTraceWriter.toString());
					}
				});
				return thread;
			}
		});
	}
	
	public Task<Reference,USERID> addTask(String taskName, 
			Callable<Reference> callable, 
			Reference baseReference,
			USERID user,boolean internal) {
		if (callable == null) return null;
		Task<Reference,USERID> task = new Task<Reference,USERID>(user);
		task.setName(taskName);
		task.setInternal(internal);
		
		ExecutableTask<USERID> xtask = new ExecutableTask<USERID>(callable,task);
		
		
		Reference ref =	new Reference(
				String.format("%s%s/%s", baseReference.toString(),SimpleTaskResource.resource,Reference.encode(task.getUuid().toString())));
		task.setUri(ref);

		if (tasks.get(task.getUuid())==null) {
			Task<Reference,USERID> theTask = tasks.putIfAbsent(task.getUuid(),task);
	
			if (theTask==null) {
				theTask = task;
			} else {

			}

			try {
				Future future = task.isInternal()?
						completionService_internal.submit(xtask,null):
						completionService_external.submit(xtask,null);
						System.out.println(pool_internal.toString());
						return theTask;
			} catch (RejectedExecutionException x) {
				return null;
			} catch (Exception x) {
				return null;
			}

		}
		else return null;
	}	
	public synchronized Task<Reference,USERID> findTask(String id) {
		try {
			return tasks.get(UUID.fromString(id));
		} catch (Exception x) {
			return null;
		}
	}
	public synchronized Task<Reference,USERID> findTask(UUID id) {
		try {
			return tasks.get(id);
		} catch (Exception x) {

			return null;
		}
	}	
	public synchronized void removeTask(String id) {
		try {
			tasks.remove(UUID.fromString(id));
		} catch (Exception x) {
			return;
		}
	}
	@Override
	public Iterator<UUID> getTasks() {
		return tasks.keySet().iterator();
	}
	/*
	public Iterator<UUID> getTasks() {
		ArrayList<UUID> lists = new ArrayList<UUID>();
		Iterator<UUID> i = tasks.keySet().iterator();
		while (i.hasNext())
			lists.add(i.next());
		return lists.iterator();
	} 
	 */
	
	@Override
	public void removeTasks() {
		cancelTasks();
		tasks.clear();
		
	}
	public void cancelTasks() {
		Iterator<UUID> keys = tasks.keySet().iterator();
		while (keys.hasNext()) {
			UUID key = keys.next();
			Task<Reference,USERID> task = tasks.get(key);
			try {
				if (!task.isDone()) task.cancel(true);
				} catch (Exception x) {logger.warning(x.getMessage());}
		}
	}
	public synchronized void shutdown(long timeout,TimeUnit unit) throws Exception {
		
		if (!pool_internal.isShutdown()) {

			pool_internal.awaitTermination(timeout, unit);
			pool_internal.shutdown();
		}
		if (!pool_external.isShutdown()) {
			pool_external.awaitTermination(timeout, unit);
			pool_external.shutdown();
		}
		if (!cleanupTimer.isShutdown()) {
			cleanupTimer.awaitTermination(timeout, unit);
			cleanupTimer.shutdown();
		}
		if (!cleanupCompletedTasks.isShutdown()) {
			cleanupCompletedTasks.awaitTermination(timeout, unit);
			cleanupCompletedTasks.shutdown();
		}		
	}	
	public Iterator<Task<Reference,USERID>> filterTasks() {
		return null;
	}
}
