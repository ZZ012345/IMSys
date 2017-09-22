package com.rinc.imsys;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by ZhouZhi on 2017/9/18.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d("BaseFragment Create", getClass().getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.d("BaseFragment CreateView", getClass().getSimpleName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d("BaseFragment DestroyView", getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("BaseFragment Destroy", getClass().getSimpleName());
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        transaction.commit();
    }

    public void hideKeyboard() { //隐藏虚拟键盘
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
