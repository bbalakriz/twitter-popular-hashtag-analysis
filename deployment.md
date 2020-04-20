## For local testing
cd tweet-aggregator && mvn compile quarkus:dev
cd ../tweet-poller && mvn compile quarkus:dev
cd ../tag-dashboard && mvn compile quarkus:dev

export URL="http://localhost:8090/twitter/timeline?topic=covid&sample=20"
while sleep 5; do curl $URL ; done

## check the app in browser
http://localhost:8080/results.html

## For deploying and running in Openshift
### login to openshift
oc login --insecure-skip-tls-verify --server=https://api.cluster-f65d.f65d.example.opentlc.com:6443 -u user2 -p XXXXXX

### compile & deploy tweet-aggregator 
cd tweet-aggregator
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=tweet-aggregator
oc start-build tweet-aggregator --from-file ./target/*-runner.jar --follow
oc new-app tweet-aggregator 

### compile & deploy hashtag-dashboard
cd tag-dashboard
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=hashtag-dashboard
oc start-build hashtag-dashboard --from-file ./target/*-runner.jar --follow
oc new-app hashtag-dashboard 
oc expose svc/hashtag-dashboard 

### compile & deploy tweet-poller
cd tweet-poller
mvn clean compile package -DuberJar=true
oc new-build registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift:1.6 --binary --name=tweet-poller
oc start-build tweet-poller --from-file ./target/*-runner.jar --follow
oc new-app tweet-poller
oc expose svc/tweet-poller

### check kafka topic
bin/kafka-console-consumer.sh --bootstrap-server my-cluster-kafka-bootstrap:9092 --topic agg-feeds --property print.key=true --property key.separator="-" --from-beginning
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic twitter-feeds
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic twitter-feeds --property "parse.key=true" --property "key.separator=:"

### Poll the URL from a laptop for every 10s to get the hashtags on to the tag cloud in the browser session
export URL="http://localhost:8090/twitter/timeline?topic=covid&sample=20"
export URL="http://tweet-poller-sentiment.apps.cluster-f65d.f65d.example.opentlc.com/twitter/timeline?topic=covid&sample=20"
while sleep 5; do curl $URL ; done
