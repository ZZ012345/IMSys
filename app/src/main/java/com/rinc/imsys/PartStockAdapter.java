package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ZhouZhi on 2017/9/6.
 */

public class PartStockAdapter extends RecyclerView.Adapter<PartStockAdapter.ViewHolder> implements View.OnClickListener {

    private List<PartStock> mlist;

    private PartStockAdapter.OnItemClickListener mOnItemClickListener = null;

    public PartStockAdapter(List<PartStock> mlist) {
        this.mlist = mlist;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textId;
        TextView textType;
        TextView textNum;
        ImageView more;

        public ViewHolder(View view) {
            super(view);
            textId = (TextView) view.findViewById(R.id.partstock_text_id);
            textType = (TextView) view.findViewById(R.id.partstock_text_type);
            textNum = (TextView) view.findViewById(R.id.partstock_text_num);
            more = (ImageView) view.findViewById(R.id.partstock_image_more);
        }
    }

    @Override
    public PartStockAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partstock_item, parent, false);
        PartStockAdapter.ViewHolder viewHolder = new PartStockAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PartStockAdapter.ViewHolder holder, int position) {
        PartStock partStock = mlist.get(position);
        holder.textId.setText(partStock.getId());
        holder.textType.setText(partStock.getType());
        holder.textNum.setText(partStock.getNum());
        holder.itemView.setTag(position);
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
        return mlist.size();
    }
}
