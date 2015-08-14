package com.layoutstry.android.trythisloyout;

import android.database.MatrixCursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by User on 06-08-2015.
 */
public class RecyclerContactsAdapter extends RecyclerView.Adapter<RecyclerContactsAdapter.ViewHolder> {
MatrixCursor mDataset;

    //create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lv_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = mDataset.getString(position);
        holder.txtHeader.setText(mDataset.getString(position));
        holder.txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(name);
            }
        });

        holder.txtFooter.setText("Footer: " + mDataset.getString(position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.getCount();
    }

    public void add(int position, String item) {
        //mDataset.addRow(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        //int position = mDataset.indexOf(item);
        //mDataset.remove(position);
        //notifyItemRemoved(position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerContactsAdapter(MatrixCursor myDataset) {
        mDataset = myDataset;
    }





    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtHeader;
        public TextView txtFooter;

        public ViewHolder(View itemView) {
            super(itemView);
            txtHeader = (TextView) itemView.findViewById(R.id.tv_name);
            txtFooter = (TextView) itemView.findViewById(R.id.tv_details);
        }
    }
}
