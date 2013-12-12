
package me.pjq.pushup.widget;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomerViewPager extends ViewPager {

    protected int childId;

    public CustomerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int currentItem = getCurrentItem();
//        if (0 == currentItem) {
//            if (childId == R.id.page_yuncheng_recommend_gallery_id) {
//                View scroll = findViewById(childId);
//                if (scroll != null) {
//                    Rect rect = new Rect();
//                    scroll.getHitRect(rect);
//                    if (rect.contains((int) event.getX(), (int) event.getY())) {
//                        return false;
//                    }
//                }
//            }
//        }

        return super.onInterceptTouchEvent(event);
    }

    public void setChildId(int id) {
        this.childId = id;
    }
}
