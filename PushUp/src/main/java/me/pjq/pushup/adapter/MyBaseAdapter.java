package me.pjq.pushup.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class MyBaseAdapter extends BaseAdapter {
    Context context;
    private ArrayList<Object> arrayList;

    public MyBaseAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(ArrayList<Object> arrayList) {
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (null != observer) {
            super.registerDataSetObserver(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (null != observer) {
            super.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public int getCount() {
        if (null != arrayList) {
            return arrayList.size();
        }

        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != arrayList) {
            return arrayList.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

}