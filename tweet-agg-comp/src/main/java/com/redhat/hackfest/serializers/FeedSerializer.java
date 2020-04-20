package com.redhat.hackfest.serializers;

import java.nio.charset.Charset;
import java.util.Map;
import com.google.gson.Gson;
import com.redhat.hackfest.model.Feed;
import org.apache.kafka.common.serialization.Serializer;

public class FeedSerializer implements Serializer<Feed> {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    static private Gson gson = new Gson();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, Feed feed) {
        return gson.toJson(feed).getBytes(CHARSET);
    }

    @Override
    public void close() {
    }
}
