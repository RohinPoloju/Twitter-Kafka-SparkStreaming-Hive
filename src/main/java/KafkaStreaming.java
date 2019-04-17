import producer.TwitterKafkaProducer;

public class KafkaStreaming {
    public static void main(String[] args){
        System.out.println("The Twitter Streaming Started");
        TwitterKafkaProducer producer = new TwitterKafkaProducer();
        producer.run();
    }
}
