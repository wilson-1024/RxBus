package com.chemanman.rxbus;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;


import com.chemanman.rxbus.inject.Inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * 消息总线
 * Created by zhangzhang on 2017/1/9.
 *
 * @How-to： 1）建立用来表达事件的JavaBean，通常POJO即可；
 * 2）接受页面的监听：
 * listener：需要实现的OnEventListener侦听器
 * msg type：参见@MsgType字段说明
 * events：需要监听的时间类型列表，一般是POJO的class类
 * RxBus.getDefault().register(listener, msg type, events...);
 * 3）发送页面的发送消息：
 * RxBus.getDefault().post/postSticky();
 * 4）事件监听：
 * 方法一：
 * a）类继承或者实现 RxBus.OnEventListener 接口
 * b）调用register时，第二个入参传入需要监听的event class列表
 * c）在 onEvent 中对传入的 object 判断是否是需要监听的数据类型
 * 方法二：
 * 类方法中增加注解 EventMethod 注解方法，方法参数只支持一个如：
 * @EventMethodBind(type = RxBus.MSG_DEFAULT)
 * public void test(Event e) {}
 * 在类的初始化方法加  RxBus.getDefault().inject(this);
 * 在类销毁方法加 RxBus.getDefault().unInject(this);
 * 与ButterKnife使用方法类似
 */
public final class RxBus {

    public interface OnEventListener {

        void onEvent(Object o);
    }

    public static final int MSG_DEFAULT = 0;  // 默认通知类型，注册后有新状态才会收到更新
    public static final int MSG_STICKY = 1;  // 注册后立即收到当前消息的状态值
    private static final int DELAY_MSG = 0;//延时发送消息

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MSG_DEFAULT, MSG_STICKY})
    @interface MsgType {
    }

    private static volatile RxBus mDefaultInstance;

    private final Subject<Object, Object> mBus;

    private final Map<Class<?>, Object> mEventSet;  // ket -> event type, value -> event objects

    private final Map<Class<?>, List<Object>> mEventListenerSet;  // key -> event, value -> listeners
    private final Map<Object, List<Class<?>>> mListenerEventSet;  // key -> listeners, value -> event
    private Map<String, Inject> injectHashMap;

    Handler mHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DELAY_MSG:
                    mBus.onNext(msg.obj);
                    break;
            }
            return false;
        }
    });

    private RxBus() {
        mBus = new SerializedSubject<>(PublishSubject.create());

        mEventListenerSet = new ConcurrentHashMap<>();
        mListenerEventSet = new ConcurrentHashMap<>();
        mEventSet = new ConcurrentHashMap<>();
        injectHashMap = new ConcurrentHashMap<>();
    }

    public static RxBus getDefault() {
        if (mDefaultInstance == null) {
            synchronized (RxBus.class) {
                if (mDefaultInstance == null) {
                    mDefaultInstance = new RxBus();
                }
            }
        }
        return mDefaultInstance;
    }

    /**
     * 发送事件
     *
     * @param event 事件类
     */
    public final void post(Object event) {
        mBus.onNext(event);
    }

    /**
     * 延时发送事件
     *
     * @param event       事件类
     * @param delayMillis 延时时间（ms）
     */
    public final void postDelayed(Object event, long delayMillis) {
        Message message = new Message();
        message.what = DELAY_MSG;
        message.obj = event;
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    /**
     * 发送缓存事件（事件的每个状态都会被记录下来）
     *
     * @param event 事件类
     */
    public final void postSticky(Object event) {
        synchronized (mEventSet) {
            mEventSet.put(event.getClass(), event);
        }
        post(event);
    }

    /**
     * 注册监听普通事件
     *
     * @param listener     侦听器
     * @param eventClasses 事件class类列表
     */
    public final <T extends OnEventListener> void register(@NonNull T listener, @NonNull Class<?>... eventClasses) {
        this.register(listener, MSG_DEFAULT, eventClasses);
    }

    /**
     * 注册监听指定状态类型的事件
     *
     * @param listener     侦听器
     * @param arg          消息类型，参见@MsgType字段说明
     * @param eventClasses 事件类class列表
     */
    public final <T extends OnEventListener> void register(@NonNull T listener,
                                                           @MsgType int arg,
                                                           @NonNull Class<?>... eventClasses) {
        int type = arg == MSG_STICKY ? MSG_STICKY : MSG_DEFAULT;

        for (Class<?> eventClass : eventClasses) {
            if (!mEventListenerSet.containsKey(eventClass)) {
                mEventListenerSet.put(eventClass, new ArrayList<>());
            }

            if (!mEventListenerSet.get(eventClass).contains(listener)) {
                mEventListenerSet.get(eventClass).add(listener);
            }
        }

        List<Class<?>> ets = null;
        if (mListenerEventSet.containsKey(listener)) {
            ets = mListenerEventSet.get(listener);
        }
        if (ets != null) {
            Collections.addAll(ets, eventClasses);
        } else {
            ets = new ArrayList<>();
            Collections.addAll(ets, eventClasses);
        }
        mListenerEventSet.put(listener, ets);

        for (Class<?> eventClass : eventClasses) {
            subscribeEvent(eventClass, false);
        }

        if (type == MSG_STICKY) {
            for (Class<?> eventClass : eventClasses) {
                if (mEventSet.containsKey(eventClass)) {
                    post(mEventSet.get(eventClass));
                }
            }
        }
    }

    /**
     * 解注册
     *
     * @param t register时的侦听器实例
     */
    public final <T extends OnEventListener> void unregister(T t) {
        if (mListenerEventSet.containsKey(t)) {
            List<Class<?>> l = mListenerEventSet.get(t);
            for (Class<?> cls : l) {
                if (mEventListenerSet.containsKey(cls)) {
                    mEventListenerSet.get(cls).remove(t);

                    if (mEventListenerSet.get(cls).isEmpty()) {
                        mEventListenerSet.remove(cls);
                    }
                }
            }

            mListenerEventSet.remove(t);
        }
    }

    /**
     * 事件分发逻辑
     *
     * @param eventClass 事件类型
     * @param redo       循环标记（RxJava事件分发后需要重新注册）
     */
    private synchronized void subscribeEvent(final Class<?> eventClass, boolean redo) {
        if (!mEventSet.containsKey(eventClass) || redo) {
            mBus.ofType(eventClass)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Object>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            subscribeEvent(eventClass, true);
                        }

                        @Override
                        public void onNext(Object o) {
                            if (mEventListenerSet.containsKey(o.getClass())) {
                                for (Object obj : mEventListenerSet.get(o.getClass())) {
                                    if (obj instanceof OnEventListener) {
                                        ((OnEventListener) obj).onEvent(o);
                                    }
                                }
                            }
                        }
                    });

            mEventSet.put(eventClass, new Object());
        }
    }


    public void inject(Object host) {
        String className = host.getClass().getName();
        Inject inject = injectHashMap.get(className);

        if (inject == null) {
            try {
                Class<?> aClass = Class.forName(className + "_BindInject");
                inject = (Inject) aClass.newInstance();
                inject.inject(host);
                injectHashMap.put(className, inject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            inject.inject(host);
        }
    }

    public void unInject(Object host) {
        String className = host.getClass().getName();
        Inject inject = injectHashMap.get(className);
        if (inject != null) {
            inject.unInject(host);
        }
    }
}
