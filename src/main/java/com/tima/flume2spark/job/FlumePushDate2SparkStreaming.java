package com.tima.flume2spark.job;
import java.util.Arrays;
import java.util.Iterator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.flume.FlumeUtils;
import org.apache.spark.streaming.flume.SparkFlumeEvent;
import scala.Tuple2;

public class FlumePushDate2SparkStreaming {

    public static void main(String[] args) throws InterruptedException {

        SparkConf conf = new SparkConf().setMaster("local[2]").
                setAppName("FlumePushDate2SparkStreaming");

        JavaStreamingContext jsc = new JavaStreamingContext(conf, Durations.seconds(30));

        JavaReceiverInputDStream<SparkFlumeEvent> lines = FlumeUtils.createStream(jsc,"DataWorks.Master", 9999);

        //如果是Scala，由于SAM转换，所以可以写成val words = lines.flatMap { line => line.split(" ")}
        JavaDStream<String> words = lines.flatMap(new FlatMapFunction<SparkFlumeEvent, String>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Iterator<String> call(SparkFlumeEvent event) throws Exception {
                String line = new String(event.event().getBody().array());
                return Arrays.asList(line.split(" ")).iterator();
            }
        });
        JavaPairDStream<String, Integer> pairs = words.mapToPair(new PairFunction<String, String, Integer>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Tuple2<String, Integer> call(String word) throws Exception {
                return new Tuple2<String, Integer>(word, 1);
            }
        });
        //对相同的Key，进行Value的累计（包括Local和Reducer级别同时Reduce）
        JavaPairDStream<String, Integer> wordsCount = pairs.reduceByKey(new Function2<Integer, Integer, Integer>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                return v1 + v2;
            }
        });
        wordsCount.print();
        jsc.start();
        jsc.awaitTermination();
        jsc.close();
    }

}

