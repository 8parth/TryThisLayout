package com.layoutstry.android.trythisloyout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by User on 28-04-2015.
 */
public class MyDrawerAdaper extends BaseAdapter{

    private Context context;
    private String[] titles;
    private int[] images;
    private LayoutInflater inflater;
    private int[] selectedposition;

   public MyDrawerAdaper(Context context, String[] titles, int[] images, int[] selectedposition){
       this.context = context;
       this.images = images;
       this.inflater = LayoutInflater.from(this.context);
       this.titles = titles;
       this.selectedposition = selectedposition;
   }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder mViewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.drawer_list, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tvTitle = (TextView) convertView
                .findViewById(R.id.textView1);
        mViewHolder.ivIcon = (ImageView) convertView
                .findViewById(R.id.imageView1);

        mViewHolder.tvTitle.setText(titles[position]);
        mViewHolder.ivIcon.setImageResource(images[position]);

        //Highlight the selected list item
        /*if (position == selectedposition[0]) {
            convertView.setBackgroundColor(Color.WHITE);
            mViewHolder.tvTitle.setTextColor(Color.BLUE);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            mViewHolder.tvTitle.setTextColor(Color.WHITE);
        }*/

        return convertView;
    }

    private class ViewHolder{
        TextView tvTitle;
        ImageView ivIcon;
    }
}
