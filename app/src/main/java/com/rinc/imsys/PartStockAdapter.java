package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZhouZhi on 2017/9/8.
 */

public class PartStockAdapter extends RecyclerView.Adapter<PartStockAdapter.ViewHolder> implements View.OnClickListener {

    public static final int TYPE_HEADER = 0;

    public static final int TYPE_FOOTER = 1;

    public static final int TYPE_NORMAL = 2;

    private List<PartStock> mlist;

    private PartStockAdapter.OnItemClickListener mOnItemClickListener = null;

    private View mHeaderView;

    private View mFooterView;

    public PartStockAdapter(List<PartStock> mlist) {
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
        TextView textId;
        TextView textType;
        TextView textNum;
        ImageView more;

        public ViewHolder(View view) {
            super(view);
            if (view != mHeaderView && view != mFooterView) {
                textId = (TextView) view.findViewById(R.id.partstock_text_id);
                textType = (TextView) view.findViewById(R.id.partstock_text_type);
                textNum = (TextView) view.findViewById(R.id.partstock_text_num);
                more = (ImageView) view.findViewById(R.id.partstock_image_more);
            }
        }
    }

    @Override
    public PartStockAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (mHeaderView != null && viewType == TYPE_HEADER) {
            mHeaderView.setOnClickListener(this);
            return new PartStockAdapter.ViewHolder(mHeaderView);
        }
        if (mFooterView != null && viewType == TYPE_FOOTER) {
            mFooterView.setOnClickListener(this);
            return new PartStockAdapter.ViewHolder(mFooterView);
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partstock_item, parent, false);
        PartStockAdapter.ViewHolder viewHolder = new PartStockAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PartStockAdapter.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_NORMAL) {
            if (holder != null) {
                PartStock partStock;
                if (mHeaderView != null) {
                    partStock = mlist.get(position - 1);
                } else {
                    partStock = mlist.get(position);
                }
                holder.textId.setText(partStock.getId());
                holder.textType.setText(partStock.getType());
                holder.textNum.setText(partStock.getNum());
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

    public void setOnItemClickListener(PartStockAdapter.OnItemClickListener listener) {
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
