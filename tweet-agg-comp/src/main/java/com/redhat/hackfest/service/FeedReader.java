package com.redhat.hackfest.service;

import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import com.redhat.hackfest.model.Feed;
import com.redhat.hackfest.serializers.FeedSeder;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.Windowed;


@ApplicationScoped
public class FeedReader {
    private static final String TWITTER_FEEDS_TOPIC = "twitter-feeds";
    private static final String FEEDS_AGGREGATED_TOPIC = "agg-feeds";

    /**
     * Build a topology that does the following:
     * 
     * a. create a new KV for the message with key as hashtag
     * 
     * b. group by the hashtag and apply a tumbling windows of 10 seconds to count the occurences of
     * the incoming hashtags
     * 
     * c. create a map with hashtag and its count
     */
    @Produces
    public Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        builder.<String, Feed>stream(TWITTER_FEEDS_TOPIC,
                Consumed.with(Serdes.String(), new FeedSeder()))
                // key in the incoming message may be anything, so, create a new KV for the message
                // using the hashtag (key) and message (value)
                .map((key, feed) -> new KeyValue<>(feed.getHashtag().toString(), feed)).groupByKey()
                // group by key and apply 10s tumbling window to get the no of occurences of
                // hashtags in 10s
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10))).count().toStream()
                // get a map to get the count of hashtags in the last 10s
                .map((Windowed<String> key, Long count) -> new KeyValue(
                        key.key() + "@" + key.window().start() + "-->" + key.window().end(),
                        key.key() + ":" + count.toString()))
                // send it to output topic
                .to(FEEDS_AGGREGATED_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();
    }
}
