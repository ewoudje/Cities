package com.ewoudje.townskings.remote.faktory;

import com.ewoudje.townskings.TK;
import com.ewoudje.townskings.api.TKPlugin;
import com.github.sikandar.faktory.FaktoryClient;
import com.github.sikandar.faktory.FaktoryJob;
import io.sentry.Sentry;

import java.io.IOException;

public class FaktoryWrapper {

    private FaktoryClient client;

    public FaktoryWrapper(TKPlugin plugin) {
        client = new FaktoryClient(

        ); //TODO config
    }

    public void push(FaktoryJob job) {
        if (TK.REDIS.pfadd("faktory-id", job.prepare(true).toString()).intValue() != 0) {
            try {
                client.push(job);
            } catch (IOException e) {
                Sentry.captureException(e);
                e.printStackTrace();
            }
        } else { //Should i throw this?
            Exception e = new FaktoryDoubleException();
            Sentry.captureException(e);
            e.printStackTrace();
        }
    }
}
