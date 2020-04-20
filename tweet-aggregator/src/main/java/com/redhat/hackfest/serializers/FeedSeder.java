package com.redhat.hackfest.serializers;

import java.util.Map;
import com.redhat.hackfest.model.Feed;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class FeedSeder implements Serde<Feed> {
    private FeedSerializer serializer = new FeedSerializer();
    private FeedDeserializer deserializer = new FeedDeserializer();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        serializer.configure(configs, isKey);
        deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        serializer.close();
        deserializer.close();
    }

    @Override
    public Serializer<Feed> serializer() {
        return serializer;
    }

    @Override
    public Deserializer<Feed> deserializer() {
        return deserializer;
    }
}
