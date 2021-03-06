package org.avol.tweet.dao.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import lombok.extern.slf4j.Slf4j;
import org.avol.tweet.api.Tweet;
import org.avol.tweet.dao.TweetDao;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Durga on 9/11/2015.
 *
 *
 */
@Slf4j
public class TweetDaoImpl implements TweetDao , Managed {

    private static Integer tweetId = 1;

    @Inject
    private Connection connection; //Assuming this will be injected by Guice.

    @Override
    public Serializable create(Tweet tweet) throws Exception {
        Preconditions.checkNotNull(connection);
        int id = 0;
        PreparedStatement preparedStatement =
                connection.prepareStatement("INSERT INTO TWEETS(MESSAGE, POSTED_ON) VALUES (?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, tweet.getMessage());
        preparedStatement.setTimestamp(2, getTimeStamp(tweet.getDate()));
        preparedStatement.execute();
        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
        while (generatedKeys.next()) {
            id = generatedKeys.getInt(1);
        }
        preparedStatement.close();
        return id;
    }

    @Override
    public Tweet find(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT *FROM TWEETS WHERE ID = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        Tweet tweet = null;
        while (resultSet.next()) {
            tweet = new Tweet();
            tweet.setId(resultSet.getInt("ID"));
            tweet.setMessage(resultSet.getString("MESSAGE"));
            tweet.setDate(resultSet.getTimestamp("POSTED_ON").toString());
        }
        log.info("Tweet fetched from DB is: " + tweet);
        preparedStatement.close();
        return tweet;
    }

    @Override
    public List<Tweet> findAll() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT *FROM TWEETS");
        ResultSet resultSet = preparedStatement.executeQuery();
        List<Tweet> tweets = new ArrayList<Tweet>();
        while (resultSet.next()) {
            Tweet tweet = new Tweet();
            tweet.setId(resultSet.getInt("ID"));
            tweet.setMessage(resultSet.getString("MESSAGE"));
            tweet.setDate(resultSet.getTimestamp("POSTED_ON").toString());
            tweets.add(tweet);
        }
        log.info("No of Tweets returning: " + tweets.size());
        preparedStatement.close();
        return tweets;
    }

    @Override
    public Tweet update(Tweet tweet) throws Exception {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE TWEETS SET MESSAGE = ?, POSTED_ON = ? WHERE ID = ?");
        preparedStatement.setString(1, tweet.getMessage());
        preparedStatement.setTimestamp(2, getTimeStamp(tweet.getDate()));
        preparedStatement.setInt(3, tweet.getId());
        int i = preparedStatement.executeUpdate();
        log.info("Tweet updated successfully..");
        preparedStatement.close();
        return tweet;
    }

    @Override
    public void delete(int id) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM TWEETS WHERE ID = ?");
        preparedStatement.setInt(1, id);
        int i = preparedStatement.executeUpdate();
        log.info("Tweet deleted..");
        preparedStatement.close();
    }

    @Override
    public void start() throws Exception {
        Statement stmt = connection.createStatement();
        //stmt.executeUpdate( "DROP TABLE table1" );
        int i = stmt.executeUpdate("CREATE TABLE TWEETS (ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, " +
                "MESSAGE LONGVARCHAR, POSTED_ON timestamp default CURRENT_TIMESTAMP);");
        log.info("Start up script executed, and db ready to use.");
        stmt.close();
    }

    @Override
    public void stop() throws Exception {
        connection.close();
        log.info("Shut down script executed, and connection has been closed.");
    }

    private Timestamp getTimeStamp(String dateAsString) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date birthDate = sdf.parse(dateAsString);
        return new Timestamp(birthDate.getTime());
    }
}
