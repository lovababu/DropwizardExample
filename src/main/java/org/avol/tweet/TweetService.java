package org.avol.tweet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.avol.tweet.conf.TweetModule;
import org.avol.tweet.dao.impl.TweetDaoImpl;
import org.avol.tweet.filters.RequestTraxnIdFilter;
import org.avol.tweet.healthchecks.DBHealthCheck;
import org.avol.tweet.resource.TweetResource;
import org.avol.tweet.service.impl.TweetBusinessServiceImpl;

import javax.servlet.DispatcherType;
import java.util.EnumSet;

/**
 * Created by Durga on 9/11/2015.
 *
 * Explains the Dropwizard framework with simple Guice injection and H2 database connectivity.
 */
public class TweetService extends Application<TweetConfiguration> {

    public static void main(String[] args) throws Exception {
        new TweetService().run(args);
    }

    @Override
    public String getName() {
        return "avol-tweet";
    }

    @Override
    public void initialize(Bootstrap<TweetConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(TweetConfiguration configuration, Environment environment) throws Exception {

        TweetModule tweetModule = new TweetModule();
        tweetModule.setDbConfig(configuration.getDbConfig());
        Injector injector = Guice.createInjector(tweetModule);

        final TweetResource tweetResource = new TweetResource(injector.getInstance(TweetBusinessServiceImpl.class));
        environment.jersey().register(tweetResource);
        environment.lifecycle().manage(injector.getInstance(TweetDaoImpl.class));
        //Adding servlet filter, you can also register jersey filter too.
        environment.servlets().addFilter("TxnFilter", new RequestTraxnIdFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
        environment.healthChecks().register("DbHealthCheck",injector.getInstance(DBHealthCheck.class));
    }
}
