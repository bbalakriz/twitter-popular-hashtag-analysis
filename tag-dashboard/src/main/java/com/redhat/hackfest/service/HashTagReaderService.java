package com.redhat.hackfest.service;

import javax.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import io.smallrye.reactive.messaging.annotations.Broadcast;

@ApplicationScoped
public class HashTagReaderService {

    @Incoming("agg-feeds")
    @Outgoing("hash-stream")
    @Broadcast
    public String stream(String hashStream) {
        return hashStream;
    }
}
