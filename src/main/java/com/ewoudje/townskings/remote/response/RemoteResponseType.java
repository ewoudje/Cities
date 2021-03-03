package com.ewoudje.townskings.remote.response;

import com.ewoudje.townskings.api.Named;

public interface RemoteResponseType<T> extends Named {

    boolean sync();

    T requestValues(String loc);

    void execute(T input);

}
