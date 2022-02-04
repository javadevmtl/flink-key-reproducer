package com.xxx;

import com.xxx.entity.MyEvent;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.time.Instant;
import java.util.Random;

public class MyEventSource extends RichParallelSourceFunction<MyEvent> {

	private volatile boolean cancelled = false;
	private Random random;
	private long sleepTimeMs;

	public MyEventSource(long sleepTimeMs) {
		this.sleepTimeMs = sleepTimeMs;
	}

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
		random = new Random();
	}

	@Override
	public void run(SourceContext<MyEvent> ctx) throws Exception {
		while (!cancelled) {


			synchronized (ctx.getCheckpointLock()) {
				ctx.collect(new MyEvent("EVENT", "foo", "bar", Instant.now()));

				// The lower this is the more the issue seems to arise.
				Thread.sleep(sleepTimeMs);
			}
		}
	}

	@Override
	public void cancel() {
		cancelled = true;
	}
}