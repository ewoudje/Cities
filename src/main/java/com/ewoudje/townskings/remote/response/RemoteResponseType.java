package com.ewoudje.townskings.remote.response;

public interface RemoteResponseType<T> {

    String getName();

    boolean sync();

    T requestValues(String loc);

    void execute(T input);

}
