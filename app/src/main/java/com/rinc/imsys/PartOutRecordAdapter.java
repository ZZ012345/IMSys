package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZhouZhi on 2017/9/12.
 */

public class PartOutRecordAdapter extends RecyclerView.Adapter<PartOutRecordAdapter.ViewHolder> implements View.OnClickListener {

    public static final int TYPE_HEADER = 0;

    public static final int TYPE_FOOTER = 1;

    public static final int TYPE_NORMAL = 2;

    private List<PartOutRecord> mlist;

    private PartOutRecordAdapter.OnItemClickListener mOnItemClickListener = null;

    private View mHeaderView;

    private View mFooterView;

    public PartOutRecordAdapter(List<PartOutRecord> mlist) {
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
        TextView textId;
        TextView textNum;
        TextView textLeftnum;
        TextView textUser;
        ImageView more;

        public ViewHolder(View view) {
            super(view);
            if (view != mHeaderView && view != mFooterView) {
                textTime = (TextView) view.findViewById(R.id.text_datatime_partoutrec);
                textOperator = (TextView) view.findViewById(R.id.text_operator_partoutrec);
                textId = (TextView) view.findViewById(R.id.text_id_partoutrec);
                textNum = (TextView) view.findViewById(R.id.text_num_partoutrec);
                textLeftnum = (TextView) view.findViewById(R.id.text_leftnum_partoutrec);
                textUser = (TextView) view.findViewById(R.id.text_user_partoutrec);
                more = (ImageView) view.findViewById(R.id.image_more_partoutrec);
            }
        }
    }

    @Override
    public PartOutRecordAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            mHeaderView.setOnClickListener(this);
            return new PartOutRecordAdapter.ViewHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            mFooterView.setOnClickListener(this);
            return new PartOutRecordAdapter.ViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partoutrecord_item, parent, false);
        PartOutRecordAdapter.ViewHolder viewHolder = new PartOutRecordAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PartOutRecordAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (holder != null) {
                PartOutRecord partOutRecord;
                if (mHeaderView != null) {
                    partOutRecord = mlist.get(position - 1);
                } else {
                    partOutRecord = mlist.get(position);
                }
                holder.textTime.setText(partOutRecord.getOutputDateTime());
                holder.textOperator.setText(partOutRecord.getOperator());
                holder.textId.setText(partOutRecord.getPartStock().getId());
                holder.textNum.setText(partOutRecord.getOutputNum());
                holder.textLeftnum.setText(partOutRecord.getLeftNum());
                holder.textUser.setText(partOutRecord.getUser());
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

    public void setOnItemClickListener(PartOutRecordAdapter.OnItemClickListener listener) {
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
