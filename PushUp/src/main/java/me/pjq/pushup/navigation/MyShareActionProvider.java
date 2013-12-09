package me.pjq.pushup.navigation;

import android.content.Context;
import android.support.v7.widget.ShareActionProvider;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import me.pjq.pushup.EFLogger;

/**
 * Created by pengjianqing on 12/9/13.
 */
public class MyShareActionProvider extends ShareActionProvider {
    private static final String TAG = MyShareActionProvider.class.getSimpleName();
    View view;
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EFLogger.i(TAG, "onClick");
        }
    };

    public MyShareActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        EFLogger.i(TAG, "onCreateActionView");
        view = super.onCreateActionView();

        view.setOnClickListener(onClickListener);

        return view;
    }

    @Override
    public void onPrepareSubMenu(SubMenu subMenu) {
        EFLogger.i(TAG, "onPrepareSubMenu");
        super.onPrepareSubMenu(subMenu);
    }

    @Override
    public boolean onPerformDefaultAction() {
        EFLogger.i(TAG, "onPerformDefaultAction");
        return true;
    }

    @Override
    public void setOnShareTargetSelectedListener(OnShareTargetSelectedListener listener) {
        EFLogger.i(TAG, "setOnShareTargetSelectedListenern");
        super.setOnShareTargetSelectedListener(listener);
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        EFLogger.i(TAG, "onCreateActionView, forItem="+forItem);
        View itemView = super.onCreateActionView(forItem);
        itemView.setOnClickListener(onClickListener);
        return itemView;
    }
}
