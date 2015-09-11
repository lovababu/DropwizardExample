package org.avol.tweet.conf;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.Setter;
import org.avol.tweet.dao.TweetDao;
import org.avol.tweet.dao.impl.TweetDaoImpl;
import org.avol.tweet.service.TweetBusinessService;
import org.avol.tweet.service.impl.TweetBusinessServiceImpl;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by Durga on 9/11/2015.
 * <p/>
 * Google Guice Bindings class, It defines the DI graph.
 */
public class TweetModule extends AbstractModule {

    @Setter
    private DbConfig dbConfig;

    @Override
    protected void configure() {
        //Linked Bindings.
        bind(TweetDao.class).to(TweetDaoImpl.class);
        bind(TweetBusinessService.class).to(TweetBusinessServiceImpl.class);
    }

    @Provides
    @Singleton
    public Connection connection() throws Exception {
        Preconditions.checkNotNull(dbConfig);
        Class.forName(dbConfig.getDriverClass());
        Connection con = DriverManager.getConnection(dbConfig.getConnURL(),
                dbConfig.getUserName(), dbConfig.getPassword());
        return con;
    }

    /*public static void main(String[] args) {
        Injector injector = Guice.createInjector(new TweetModule());
        TweetDao tweetDao = injector.getInstance(TweetDao.class);
        tweetDao.create(new Tweet());
    }*/

    /*public static void main(String[] args) {
        try
        {
            Class.forName("org.h2.Driver");
            Connection con = DriverManager.getConnection("jdbc:h2:~/test", "test", "");
            Statement stmt = con.createStatement();
            //stmt.executeUpdate( "DROP TABLE table1" );
            stmt.executeUpdate( "CREATE TABLE table1 ( user varchar(50) )" );
            stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Claudio' )" );
            stmt.executeUpdate( "INSERT INTO table1 ( user ) VALUES ( 'Bernasconi' )" );

            ResultSet rs = stmt.executeQuery("SELECT * FROM table1");
            while( rs.next() )
            {
                String name = rs.getString("user");
                System.out.println( name );
            }
            stmt.close();
            con.close();
        }
        catch( Exception e )
        {
            System.out.println( e.getMessage() );
        }
    }*/
}
