package org.avol.tweet.healthchecks;

import com.codahale.metrics.health.HealthCheck;

/**
 * Created by Durga on 9/11/2015.
 */
public class DBHealthCheck extends HealthCheck {

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
