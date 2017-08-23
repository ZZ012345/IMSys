package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhouzhi on 2017/8/17.
 */

public class MaterialStockAdapter extends RecyclerView.Adapter<MaterialStockAdapter.ViewHolder> implements View.OnClickListener {

    private List<MaterialStock> mlist;

    private OnItemClickListener mOnItemClickListener = null;

    public MaterialStockAdapter(List<MaterialStock> mlist) {
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
            textId = (TextView) view.findViewById(R.id.matstock_text_id);
            textType = (TextView) view.findViewById(R.id.matstock_text_type);
            textNum = (TextView) view.findViewById(R.id.matstock_text_num);
            more = (ImageView) view.findViewById(R.id.matstock_image_more);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materialstock_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MaterialStock materialStock = mlist.get(position);
        holder.textId.setText(materialStock.getId());
        holder.textType.setText(materialStock.getType());
        holder.textNum.setText(materialStock.getNum());
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
