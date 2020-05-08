package com.redhat.hackfest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.redhat.hackfest.model.Feed;
import org.apache.camel.ConsumerTemplate;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

@Path("/twitter")
@ApplicationScoped
public class CamelResource {

  final Logger LOG = Logger.getLogger(CamelResource.class);
  String twitterSearchUri = "twitter-search://#" + "$$TOPIC" + "?count=10&lang=en-us";

  @Inject
  @Channel("twitter-feeds")
  Emitter<Feed> emitter;

  @Inject
  ConsumerTemplate consumerTemplate;

  @Path("/timeline")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getTimeline(@QueryParam("topic") String topic,
      @QueryParam("sample") Integer sample) {

    final List<String> tags = new ArrayList<String>();
    final String searchQuery = String.format(twitterSearchUri.replace("$$TOPIC", topic));

    String tweet = null;
    String tweetTimeStamp = null;

    sample = (sample == null || sample <= 0 ? 1 : sample);

    for (int i = 0; i < sample; i++) {
      tweet = consumerTemplate.receiveBodyNoWait(searchQuery, String.class);
      if (null != tweet) {
        tweetTimeStamp = tweet.substring(0, tweet.indexOf("(")).trim();

        // Pattern match hashtag
        Matcher matcher = Pattern.compile("#[a-zA-Z0-9-_]*").matcher(tweet);
       
        // Iterate over matches
        while (matcher.find()) {
          String hashTag = matcher.group();
          tags.add(hashTag);
          if (hashTag.trim().length() > 1) {
            eventTag(hashTag, tweetTimeStamp);
          }
        }
      }
    }

    LOG.info(tags);
    return Response.Status.OK.name();
  }

  /**
   * Publish the hashtags to kafka topic - twitter-feeds
   */
  public void eventTag(String tag, String tweetTimeStamp) {
    LOG.info("Hashtag: " + tag);
    emitter.send(new Feed(formatTag(tag), tweetTimeStamp));
  }

  /**
   * Remove #, leading and trailing spaces and convert to lowercase
   */
  public static String formatTag(String tag) {
    tag = tag.substring(1).trim().toLowerCase();
    return tag;
  }
}
