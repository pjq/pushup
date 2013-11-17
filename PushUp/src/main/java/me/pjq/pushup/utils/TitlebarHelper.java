package me.pjq.pushup.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import me.pjq.pushup.R;

/**
 * Created by pjq on 11/13/13.
 */
public class TitlebarHelper {

    private View titlebarIcon;
    private TextView titlebarText;
    private OnTitlebarClickListener listener;

    public TitlebarHelper(Context activity, final OnTitlebarClickListener listener) {
        titlebarIcon = (ImageView) ((Activity) activity).findViewById(R.id.icon);
        titlebarText = (TextView) ((Activity) activity).findViewById(R.id.title);
        this.listener = listener;

        init();
    }

    public void setTitlebarText(String text) {
        if (null!=titlebarText){
            titlebarText.setText(text);
            titlebarText.setBackground(null);
        }
    }

    private void init() {
        if (null == titlebarIcon || null == titlebarText) {
            return;
        }


        titlebarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickIcon();
                }
            }
        });

        titlebarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClickTitle();
                }
            }
        });
    }


    public TitlebarHelper(View container, final OnTitlebarClickListener listener) {
        titlebarIcon = (ImageView) container.findViewById(R.id.icon);
        titlebarText = (TextView) container.findViewById(R.id.title);

        this.listener = listener;

        init();
    }

    public interface OnTitlebarClickListener {
        void onClickIcon();

        void onClickTitle();
    }
}
