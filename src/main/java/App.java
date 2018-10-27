// Spark test code
//YOU CAN DELETE ALL OF THIS

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import java.util.Arrays;


public class App {
    public static void main(String[] args){
        // Disable logging
        Logger.getLogger("org").setLevel(Level.OFF);
        Logger.getLogger("akka").setLevel(Level.OFF);

        // Create a Java Spark Context.
        SparkConf sparkConf = new SparkConf().setAppName("Hello Spark - WordCount").setMaster("local[*]");
        SparkContext sc = new SparkContext(sparkConf);

        // Load our input data.
        JavaRDD<String> lines = sc.textFile("/Users/anask/Desktop/input.txt",2).toJavaRDD();

        // Check read data
    }
}

