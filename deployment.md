# Synopsis:

There are three Quarkus applications contained in this solution - tweet-poller, tweet-aggregator and hashtag-dashboard

 1. ***tweet-poller***- This application connects to `Twitter` and gets the latest set of tweets for a given search term. It provides the capabilities to extract the hashtags from those tweets and push the hashtags to a Kafka topic. This application exposes a REST API that helps to specify the search term for which it has to fetch the tweets and extract associated hashtags. 
 2.  ***tweet-aggregator***- This contains a comprehensive Quarkus Kafka streams based implementation that takes all the hashtags provided by the `tweet-poller` application to a Kafka topic and applies a tumbling window of 10s and calculates the popularity of the haghtags based on their count/ number of occurrences with in the given window of 10s and creates a weightage for each hashtag that's available in the  10s tumbling window and feeds it the `hashtag,popularity/weightage` pair to another Kafka topic. 
 3. ***hashtag-dashboard*** - A Quarkus application that picks up the 10s tumbling `hashtag,popularity` details from the Kafka topic and renders the output in a dynamic tag cloud using JQWCloud API. The application uses server sent events to dyncamically change the tag cloud based on an in-memory stream.


# For local testing
```
docker-compose -f twitter-stream-app.yaml up -d
cd tweet-aggregator && mvn compile quarkus:dev
cd ../tweet-poller && mvn compile quarkus:dev
cd ../tag-dashboard && mvn compile quarkus:dev
```

Specify the search term for which the `tweet-poller` has to fetch the latest tweets from Twitter. 

```
export URL="http://localhost:8090/twitter/timeline?topic=covid&sample=20"
while sleep 5; do curl $URL ; done
```

## Open the hashtag-dashboard application in a browser
Once the aforementioned 3 applications are up and running, access the tag cloud at http://localhost:8080/results.html, to see the twitter hashtags popping up in the hashtag cloud and getting changed at regular intervals (10s) based on realtime twitter feeds.

# For deploying and running the apps in Openshift
### Login to openshift
`oc login --insecure-skip-tls-verify --server=https://api.cluster-f65d.f65d.example.opentlc.com:6443 -u user2 -p XXXXXX`

### Compile & deploy the tweet-poller application
```
cd tweet-poller
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=tweet-poller
oc start-build tweet-poller --from-file ./target/*-runner.jar --follow
oc new-app tweet-poller
oc expose svc/tweet-poller
```

### Compile & deploy the tweet-aggregator application
```
cd tweet-aggregator
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=tweet-aggregator
oc start-build tweet-aggregator --from-file ./target/*-runner.jar --follow
oc new-app tweet-aggregator 
```

### Compile & deploy the hashtag-dashboard application
```
cd tag-dashboard
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=hashtag-dashboard
oc start-build hashtag-dashboard --from-file ./target/*-runner.jar --follow
oc new-app hashtag-dashboard 
oc expose svc/hashtag-dashboard 
```

### Invoke `tweet-poller` every 10s to get the latest tweets from twitter that corresponds to a given search term 
```
export URL="http://tweet-poller-sentiment.apps.cluster-f65d.f65d.example.opentlc.com/twitter/timeline?topic=covid&sample=20" 
while sleep 10; do curl $URL ; done 
```

Once the 3 quarkus applications are running in OpenShift, access the URL `http://tweet-poller-sentiment.apps.cluster-f65d.f65d.example.opentlc.com/twitter/timeline?topic=covid&sample=20`, to see the twitter hashtags popping up in the tag cloud and getting changed at regular intervals (10s) based on realtime twitter feeds.


<ins>*For troubleshooting*</ins>:
On one of the kafka pods, use the following commands to check if the topics are getting populated correctly. 
```
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic agg-feeds --property print.key=true --property key.separator="-" --from-beginning
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic twitter-feeds
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic twitter-feeds --property "parse.key=true" --property "key.separator=:"
```
