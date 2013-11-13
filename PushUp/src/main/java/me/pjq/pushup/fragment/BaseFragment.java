package me.pjq.pushup.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.otto.Bus;
import me.pjq.pushup.EFLogger;
import me.pjq.pushup.MyApplication;
import me.pjq.pushup.ServiceProvider;
import me.pjq.pushup.utils.Utils;

/**
 * Created by pengjianqing on 5/24/13.
 */
public abstract class BaseFragment extends Fragment {
    private String TAG;

    private View mFragmentView;
    //protected UserData userData;
    private Bus bus;
    private boolean reuseFragmentView = false;
    ViewGroup container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EFLogger.i(TAG, "onCreate");

        TAG = this.getClass().getSimpleName();

        bus = ServiceProvider.getBus();
        bus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EFLogger.i(TAG, "onCreateView");
        this.container = container;

        if (reuseFragmentView) {
            if (null == mFragmentView) {
                mFragmentView = onGetFragmentView(inflater);

                ensureUi();
            }
        } else {
            mFragmentView = onGetFragmentView(inflater);

            ensureUi();
        }

        return mFragmentView;
    }

    protected View getContainerView() {
        return mFragmentView;
    }

    /**
     * Return the FragmentView created by
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     *
     * @return
     */
    protected abstract View onGetFragmentView(LayoutInflater inflater);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Init the ui,such as retriev the view id.
     */
    protected abstract void ensureUi();

    @Override
    public void onPause() {
        super.onPause();

        EFLogger.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

        EFLogger.i(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();

        EFLogger.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EFLogger.i(TAG, "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EFLogger.i(TAG, "onDestroyView");

        try {
            bus.unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Utils.unBindDrawables(mFragmentView);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        EFLogger.i(TAG, "onDestroyView");
    }

    protected Context getApplicationContext() {
        return MyApplication.getContext().getApplicationContext();
    }

}
