package com.rinc.imsys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by ZhouZhi on 2017/8/30.
 */

public class QRMakeFragment extends Fragment {

    private Button buttonMake;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qrmake, container, false);

        buttonMake = (Button) view.findViewById(R.id.button_qrmake);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_main);
        toolbar.setTitle("生成二维码");

        buttonMake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.rinc.imsys.FORCE_OFFLINE");
                getActivity().sendBroadcast(intent);
            }
        });

        return view;
    }
}
