package com.redhat.hackfest.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.google.gson.Gson;
import com.redhat.hackfest.model.Feed;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

/**
 * FeedTimestampExtractor
 */
public class FeedTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long previousTimestamp) {
        // final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        String eventTime = ((Feed) record.value()).getTime();

        try {
            return sdf.parse(eventTime).getTime();
        } catch (ParseException e) {
            System.out.println("Error in timestamp parsing");
            return 0;
        }
    }

    public void test1() {
        String eventTime = "2020-04-14T05:24:34.588Z";
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            System.out.println("parsedTime: " + sdf.parse(eventTime).getTime());
        } catch (ParseException e) {
            System.out.println("Error in timestamp parsing");
        }
    }

    public void test2() {
        String eventTime = "Wed Apr 15 00:54:49 SGT 2020";
        final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        try {
            System.out.println("parsedTime: " + sdf.parse(eventTime).getTime());
        } catch (ParseException e) {
            System.out.println("Error in timestamp parsing");
        }
    }
}

