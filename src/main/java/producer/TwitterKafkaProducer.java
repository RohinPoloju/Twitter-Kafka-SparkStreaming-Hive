package producer;

import com.google.gson.Gson;
import com.google.gson.LongSerializationPolicy;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import configurations.KafkaProducerConfiguration;
import configurations.TwitterConfig;
import model.Tweet;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import producer.callback.BasicCallback;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TwitterKafkaProducer {
    private Client client;
    private BlockingQueue<String> queue;
    private Gson gson;
    private Callback callback;

    public TwitterKafkaProducer(){
        Authentication authentication = new OAuth1(
                TwitterConfig.CONSUMER_KEY,
                TwitterConfig.CONSUMER_SECRET_KEY,
                TwitterConfig.ACCESS_TOKEN,
                TwitterConfig.ACCESS_TOKEN_SECRET
        );

        System.out.println("Authentication Passed");

        StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
        endpoint.trackTerms(Collections.singletonList(TwitterConfig.HASHTAG));

        queue = new LinkedBlockingQueue<String>(10000);

        client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .authentication(authentication)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        System.out.println("Client Created");

        gson = new Gson();
        callback = new BasicCallback();
    }

    private Producer<Long, String> getProducer() {

        System.out.println("Setting Producer Properties");

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProducerConfiguration.SEVERS);
        properties.put(ProducerConfig.ACKS_CONFIG, "1");
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 500);
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return new KafkaProducer<Long, String>(properties);
    }

    public void run(){

        client.connect();

        try (Producer<Long, String> producer = getProducer()){
            while(true) {
                Tweet tweet = gson.fromJson(queue.take(), Tweet.class);
                System.out.printf("Fetched tweet id %d\n", tweet.getId());
                long key = tweet.getId();
                String msg = tweet.toString();
                ProducerRecord<Long, String>record = new ProducerRecord<>(KafkaProducerConfiguration.TOPIC, key, msg);
                producer.send(record, callback);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            client.stop();
        }
    }

}
