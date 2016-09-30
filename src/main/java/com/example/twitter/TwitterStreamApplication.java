package com.example.twitter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.MessageChannel;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.twitter.api.SearchParameters;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.impl.TwitterTemplate;


@IntegrationComponentScan
@EnableBinding(Source.class)
@SpringBootApplication
@EnableSocial
public class TwitterStreamApplication {

	@Value("${twitter.credentials.consumer-key}")
	private String consumerKey;
	@Value("${twitter.credentials.consumer-secret}")
	private String consumerSecret;
	@Value("${twitter.credentials.access-token}")
	private String accessToken;
	@Value("${twitter.credentials.access-token-secret}")
	private String accessTokenSecret;

	private TwitterTemplate twitterTemplate;

	@Bean
	CommandLineRunner runner(TwitterStreamWriteOutput twitterStreamWriteOutput) {
		return args -> {
			twitterTemplate = new TwitterTemplate(consumerKey, consumerSecret,accessToken,accessTokenSecret);

			while (true) {
				this.searchTweet(twitterStreamWriteOutput);
				Thread.sleep(900000);
			}





//			SearchResults results = twitterTemplate.searchOperations().search(
//					new SearchParameters("Paytm")
//							.lang("en")
//							.resultType(SearchParameters.ResultType.MIXED)
//							.includeEntities(false));
//
//			System.out.println("Total Tweets received : - " + results.getTweets().size());
//
//			results.getTweets().stream().forEach(tweet -> {
//
//				System.out.println(tweet.getId() + "----- " + tweet.getText() + "------" + tweet.getSource());
//				twitterStreamWriteOutput.write(tweet.getText());
//			});



//			StreamListener streamListener = new StreamListener() {
//				@Override
//				public void onTweet(Tweet tweet) {
//					String tweetText = tweet.getText();
//					System.out.println("================ :- " + tweet.getText());
//					twitterStreamWriteOutput.write(tweetText);
//
//				}
//
//				@Override
//				public void onDelete(StreamDeleteEvent deleteEvent) {
//
//				}
//
//				@Override
//				public void onLimit(int numberOfLimitedTweets) {
//					System.out.println("================ :- " + numberOfLimitedTweets);
//				}
//
//				@Override
//				public void onWarning(StreamWarningEvent warningEvent) {
//
//				}
//			};
//
//			StreamingOperations streamingOperations = twitterTemplate.streamingOperations();
//			List<StreamListener> listeners = Collections.singletonList(streamListener);
//			Stream stream = streamingOperations.filter("Paytm,Lendingkart", listeners);
////			Stream stream = streamingOperations.sample(listeners);
		};
	}

	public void searchTweet(TwitterStreamWriteOutput twitterStreamWriteOutput) {
		SearchResults results = this.twitterTemplate.searchOperations().search(
				new SearchParameters("Paytm")
						.lang("en")
						.resultType(SearchParameters.ResultType.MIXED)
						.includeEntities(false));

		System.out.println("Total Tweets received : - " + results.getTweets().size());

		results.getTweets().stream().forEach(tweet -> {

			System.out.println(tweet.getId() + "----- " + tweet.getText() + "------" + tweet.getSource());
			twitterStreamWriteOutput.write(tweet.getText());
		});
	}


	public static void main(String[] args) {
		SpringApplication.run(TwitterStreamApplication.class, args);


	}
}

interface TwitterStreamWrite {
	@Output
	MessageChannel output();
}

@MessagingGateway
interface TwitterStreamWriteOutput {

	@Gateway(requestChannel = "output")
	void write(String Text);

}
