package com.chemanman.rxbus.inject;

/**
 * Created by huilin on 2017/7/14.
 */

public interface Inject<T> {
    void inject(T host);

    void unInject(T host);
}
