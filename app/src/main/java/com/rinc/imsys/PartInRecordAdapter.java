package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZhouZhi on 2017/9/11.
 */

public class PartInRecordAdapter extends RecyclerView.Adapter<PartInRecordAdapter.ViewHolder> implements View.OnClickListener {

    public static final int TYPE_HEADER = 0;

    public static final int TYPE_FOOTER = 1;

    public static final int TYPE_NORMAL = 2;

    private List<PartInRecord> mlist;

    private PartInRecordAdapter.OnItemClickListener mOnItemClickListener = null;

    private View mHeaderView;

    private View mFooterView;

    public PartInRecordAdapter(List<PartInRecord> mlist) {
        this.mlist = mlist;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getFooterView() {
        return mFooterView;
    }

    public void setFooterView(View footerView) {
        mFooterView = footerView;
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mHeaderView != null) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && mFooterView != null) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTime;
        TextView textOperator;
        TextView textNum;
        TextView textDescription;

        public ViewHolder(View view) {
            super(view);
            if (view != mHeaderView && view != mFooterView) {
                textTime = (TextView) view.findViewById(R.id.text_datatime_partinrec);
                textOperator = (TextView) view.findViewById(R.id.text_operator_partinrec);
                textNum = (TextView) view.findViewById(R.id.text_num_partinrec);
                textDescription = (TextView) view.findViewById(R.id.text_description_partinrec);
            }
        }
    }

    @Override
    public PartInRecordAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            mHeaderView.setOnClickListener(this);
            return new PartInRecordAdapter.ViewHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            mFooterView.setOnClickListener(this);
            return new PartInRecordAdapter.ViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partinrecord_item, parent, false);
        PartInRecordAdapter.ViewHolder viewHolder = new PartInRecordAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PartInRecordAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (holder != null) {
                PartInRecord partInRecord;
                if (mHeaderView != null) {
                    partInRecord = mlist.get(position - 1);
                } else {
                    partInRecord = mlist.get(position);
                }
                holder.textTime.setText(partInRecord.getInputDateTime());
                holder.textOperator.setText(partInRecord.getOperator());
                holder.textNum.setText(partInRecord.getInputNum());
                holder.textDescription.setText(partInRecord.getDescription());
                holder.itemView.setTag(position);
            }
        } else {
            if (holder != null) {
                holder.itemView.setTag(position);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setOnItemClickListener(PartInRecordAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (mHeaderView == null && mFooterView == null) {
            return mlist.size();
        } else if (mHeaderView == null) {
            return mlist.size() + 1;
        } else if (mFooterView == null) {
            return mlist.size() + 1;
        } else {
            return mlist.size() + 2;
        }
    }
}
