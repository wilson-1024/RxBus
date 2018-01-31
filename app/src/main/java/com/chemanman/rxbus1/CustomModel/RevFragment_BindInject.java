package com.chemanman.rxbus1.CustomModel;

import com.chemanman.rxbus.RxBus;
import com.chemanman.rxbus.inject.Inject;
import com.chemanman.rxbus1.RevFragment;

/**
 * Created by huilin on 2017/8/11.
 */

public class RevFragment_BindInject implements Inject<RevFragment> {
    public RxBus.OnEventListener text_bind;

    @Override
    public void inject(final RevFragment host) {
        text_bind = new RxBus.OnEventListener() {
            @Override
            public void onEvent(final Object object) {
                ExpE o = (ExpE)object;
                host.text(o);
            }
        };
        RxBus.getDefault().register(text_bind,0,ExpE.class);
    }

    @Override
    public void unInject(final RevFragment host) {
        RxBus.getDefault().unregister(text_bind);
    }
}
