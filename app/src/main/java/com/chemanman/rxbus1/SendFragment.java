package com.chemanman.rxbus1;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chemanman.rxbus.RxBus;
import com.chemanman.rxbus1.CustomModel.ExpE;
import com.chemanman.rxbus1.CustomModel.ExpESticky;
import com.chemanman.rxbus1.CustomModel.ExpEvent;
import com.chemanman.rxbus1.CustomModel.ExpEventSticky;

public class SendFragment extends Fragment {

    int i = 0;
    int j = 5;
    int m = 10;
    int n = 15;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send, container, false);

        Button postBtn = (Button) view.findViewById(R.id.post);
        final TextView postTV = (TextView) view.findViewById(R.id.post_msg);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().post(new ExpEvent().setValue(i));
                postTV.setText(postTV.getText() + "," + i);
                i += 1;
            }
        });

        Button postStickyBtn = (Button) view.findViewById(R.id.post_sticky);
        final TextView postStickyTV = (TextView) view.findViewById(R.id.post_sticky_msg);
        postStickyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.getDefault().postSticky(new ExpEventSticky().setValue(j));
                postStickyTV.setText(postStickyTV.getText() + "," + j);
                j += 1;
            }
        });

        Button postMethodBtn = (Button) view.findViewById(R.id.post_method);
        final TextView postMethodTV = (TextView) view.findViewById(R.id.post_method_msg);
        postMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpE e = new ExpE();
                e.value = m;
                RxBus.getDefault().post(e);
                postMethodTV.setText(postMethodTV.getText() + "," + m);
                m += 1;
            }
        });

        Button postMethodStickyBtn = (Button) view.findViewById(R.id.post_method_sticky);
        final TextView postMethodStickyTV = (TextView) view.findViewById(R.id.post_method_sticky_msg);
        postMethodStickyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpESticky e = new ExpESticky();
                e.value = n;
                RxBus.getDefault().postSticky(e);
                postMethodStickyTV.setText(postMethodStickyTV.getText() + "," + n);
                n += 1;
            }
        });

        Button postMethodDelayBtn = (Button) view.findViewById(R.id.post_method_delay);
        final TextView postMethodDealyTV = (TextView) view.findViewById(R.id.post_method_delay_msg);
        postMethodDelayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpE e = new ExpE();
                e.value = n;
                RxBus.getDefault().postDelayed(e, 3000);
                postMethodStickyTV.setText(postMethodDealyTV.getText() + "," + n);
                n += 1;
            }
        });

        return view;
    }
}
