package com.xxx;

import com.xxx.entity.MyEventCountKey;
import com.xxx.entity.MyEvent;
import io.vertx.core.json.JsonObject;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.shaded.guava30.com.google.common.collect.Iterables;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.PrintSinkFunction;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;

public class MyEventCountJob {
    public MyEventCountJob() {
    }

    public static void main(String[] args) throws Exception {
        String jobName = "Throws Key Exception";

        // Sleep time in milliseconds to throttle records from the source.
        // The lower this value is the more records are produced the more likely the exception is thrown.
        // The actual job the source is Kafka,
        // so we would presume that ingesting even a couple thousand records per second would be no problem.
        // A value from 0ms to 500ms will definitely cause the error.
        // Setting higher value to lower the throughput you will see the actual aggregations.
        final long sleepTimeMs = 100;



        final int windowSizeMins = 1;

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<MyEvent> slStream = env.
                addSource(new MyEventSource(sleepTimeMs))
                .assignTimestampsAndWatermarks(WatermarkStrategy.noWatermarks())
                .uid("source").name("source")
                ;

                slStream.
                        keyBy(new MinutesKeySelector(windowSizeMins))
                .window(TumblingProcessingTimeWindows.of(Time.minutes(windowSizeMins)))

                .apply(new WindowFunction<MyEvent, JsonObject, MyEventCountKey, TimeWindow>() {
                    @Override
                    public void apply(MyEventCountKey myEventCountKey, TimeWindow window, Iterable<MyEvent> input, Collector<JsonObject> out) throws Exception {
                        JsonObject count = new JsonObject()
                                .put("myEventCountId", myEventCountKey.toString())
                                .put("countDateTime", myEventCountKey.getCountDateTime())
                                .put("val1", myEventCountKey.getVal1())
                                .put("val2", myEventCountKey.getVal2())
                                .put("count", Iterables.size(input));


                        out.collect(count);

                    }
                })
                .uid("process").name("process")
                .addSink(new PrintSinkFunction<>())
                ;

        env.execute(jobName);
    }

    public static ZonedDateTime roundFloor(ZonedDateTime input, TemporalField roundTo, int roundIncrement) {
        /* Extract the field being rounded. */
        int field = input.get(roundTo);

        /* Distance from previous floor. */
        int r = field % roundIncrement;

        return input.plus(-r, roundTo.getBaseUnit())
                        .truncatedTo(roundTo.getBaseUnit());
    }

    public static class MinutesKeySelector implements KeySelector<MyEvent, MyEventCountKey> {

        private final int windowSizeMins;

        public MinutesKeySelector(int windowSizeMins) {
            this.windowSizeMins = windowSizeMins;
        }

        @Override
        public MyEventCountKey getKey(final MyEvent click) throws Exception
        {
            MyEventCountKey key = new MyEventCountKey(
                    Instant.from(roundFloor(Instant.now().atZone(ZoneId.of("UTC")), ChronoField.MINUTE_OF_HOUR, windowSizeMins)).toString(),
                    click.getVal1(),
                    click.getVal2()
            );

            return key;
        }
    }


    public static class MapToMyEvent implements FlatMapFunction<JsonObject, MyEvent> {
        @Override
        public void flatMap(JsonObject value, Collector<MyEvent> out) {
            out.collect(new MyEvent("EVENT", "foo", "bar", Instant.now()));

        }
    }
}
