package cn.spark.study.core;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;

import java.util.Arrays;
import java.util.List;

/**
 * 广播变量
 * Created by CTWLPC on 2018/3/22.
 */
public class BroadcastVariable {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("BroadcastVariable")
                .setMaster("local");

        JavaSparkContext sc = new JavaSparkContext(conf);

        // 在java中，创建共享变量，就是调用SparkContext的broadcast()方法
        // 获取的返回结果是Broadcast<T>类型
        final int factor = 3;
        // 定义共享变量
        final Broadcast<Integer> factorBroadcast = sc.broadcast(factor);

        List<Integer> numberList = Arrays.asList(1, 2, 3, 4, 5);
        // 序列化成RDD
        JavaRDD<Integer> numbers = sc.parallelize(numberList);

        JavaRDD<Integer> multipleNumbers = numbers.map(new Function<Integer, Integer>() {
            @Override
            public Integer call(Integer v1) throws Exception {
                // 使用共享变量时，调用其value()方法，即可获取其内部封装的值
                int factor = factorBroadcast.value();
                return v1 * factor;

            }

        });

        // foreach算子，打印
        multipleNumbers.foreach(new VoidFunction<Integer>() {
            @Override
            public void call(Integer t) throws Exception {
                System.out.println(t);
            }
        });

        sc.close();
    }

}
