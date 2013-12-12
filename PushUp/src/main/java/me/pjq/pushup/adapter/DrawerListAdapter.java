package me.pjq.pushup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import me.pjq.pushup.R;
import me.pjq.pushup.utils.Utils;

import java.util.ArrayList;

public class DrawerListAdapter extends MyBaseAdapter {
    ArrayList<Integer> colors;

    public DrawerListAdapter(Context context) {
        super(context);

        colors = Utils.randomColorDrawerList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        ViewHolder viewHolder;
        DrawerItem item = (DrawerItem) getItem(position);
        if (null == itemView) {
            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.drawer_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) itemView.findViewById(R.id.drawerItemTitle);
            viewHolder.icon = (TextView) itemView.findViewById(R.id.drawerItemIcon);

            itemView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) itemView.getTag();
        }

        viewHolder.setUnit(item,colors.get(position));

        return itemView;
    }

    static class ViewHolder {
        TextView title;
        TextView icon;

        public void setUnit(DrawerItem item ,Integer color) {
            String text = item.getTitle();

            title.setText(text);
            icon.setText(text.substring(0, 1));

            icon.setBackgroundResource(color);
        }
    }

    public static class DrawerItem extends Object{
        private int position;
        private String title;

        public DrawerItem(int position, String title) {
            this.position = position;
            this.title = title;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
