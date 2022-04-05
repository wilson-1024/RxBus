package com.chemanman.rxbus1;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chemanman.rxbus.RxBus;
import com.chemanman.rxbus.annotation.InjectMethodBind;
import com.chemanman.rxbus1.CustomModel.*;

import java.util.ArrayList;
import java.util.List;


public class RevFragment extends Fragment {

    TextView postTV;
    TextView postStickyTV;
    TextView postMethodTV;
    TextView postMethodStickyTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recv, container, false);

        postTV = (TextView) view.findViewById(R.id.post_msg_recv);
        postStickyTV = (TextView) view.findViewById(R.id.post_sticky_msg_recv);
        postMethodTV = (TextView) view.findViewById(R.id.post_msg_recv_method);
        postMethodStickyTV = (TextView) view.findViewById(R.id.post_sticky_msg_recv_method);
//        RxBus.getDefault().register(textEventListener, ExpEvent.class);
//        EventInject.inject(this);
        RxBus.getDefault().inject(this);
//
//        revFragment_bindInject.inject(this);
//        RxBus.getDefault().register(textEventListener,ExpEvent.class);
        List<Class> classes = new ArrayList<>();
        classes.add(ExpEvent.class);
        if (!classes.contains(ExpEvent.class)) {
            classes.add(ExpEvent.class);
        }
        return view;
    }

    @Override
    public void onDestroy() {
//        RxBus.getDefault().unregister(textEventListener);
//        EventInject.unInject(this);
        RxBus.getDefault().unInject(this);
        super.onDestroy();
    }


    @InjectMethodBind
    public void text(ExpE e) {
        postMethodTV.setText(postMethodTV.getText() + "," + (e.value));
    }

    public RxBus.OnEventListener textEventListener = new RxBus.OnEventListener() {
        @Override
        public void onEvent(Object o) {
            ExpEvent expEvent = (ExpEvent) o;
            RevFragment.this.postTV.setText(postTV.getText() + "," + expEvent.value);
//            RevFragment.this.postTV
        }
    };
}
