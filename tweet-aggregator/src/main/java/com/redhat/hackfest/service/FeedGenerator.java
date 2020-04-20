package com.redhat.hackfest.service;

// @ApplicationScoped
public class FeedGenerator {
    /**
     * Test method that generates feeds and sends to the topic "twitter-feeds".
     * 
     * Uncomment the lines below to use custom feeds to be sent to the topic instead of the ones
     * from twitter
     */
    /*
     * @Outgoing("twitter-feeds") public Flowable<Feed> generate() { return Flowable.interval(1,
     * TimeUnit.SECONDS).map(tick -> { Feed f = new Feed(TagGenerator.generate(),
     * LocalDateTime.now() + ""); System.out.println(f.toString()); return f; }).take(20); }
     */

    // @Outgoing("twitter-feeds")
    // public Flowable<KafkaRecord<String, String>> generate() {
    // return Flowable.interval(1, TimeUnit.SECONDS).map(tick -> {
    // return KafkaRecord.of(TagGenerator.generate(), Instant.now() + "");
    // });
    // }
}
