package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhouzhi on 2017/8/19.
 */

public class MaterialInRecordAdapter extends RecyclerView.Adapter<MaterialInRecordAdapter.ViewHolder> implements View.OnClickListener {

    private List<MaterialInRecord> mrlist;

    private OnItemClickListener mOnItemClickListener = null;

    public MaterialInRecordAdapter(List<MaterialInRecord> mrlist) {
        this.mrlist = mrlist;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTime;
        TextView textOperator;
        TextView textId;
        TextView textNum;
        ImageView more;

        public ViewHolder(View view) {
            super(view);
            textTime = (TextView) view.findViewById(R.id.text_datatime_matinrec);
            textOperator = (TextView) view.findViewById(R.id.text_operator_matinrec);
            textId = (TextView) view.findViewById(R.id.text_id_matinrec);
            textNum = (TextView) view.findViewById(R.id.text_num_matinrec);
            more = (ImageView) view.findViewById(R.id.image_more_matinrec);
        }
    }

    @Override
    public MaterialInRecordAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materialinrecord_item, parent, false);
        MaterialInRecordAdapter.ViewHolder viewHolder = new MaterialInRecordAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MaterialInRecordAdapter.ViewHolder holder, int position) {
        MaterialInRecord materialInRecord = mrlist.get(position);
        holder.textTime.setText(materialInRecord.getInputDateTime());
        holder.textOperator.setText(materialInRecord.getOperator());
        holder.textId.setText(materialInRecord.getMaterialStock().getId());
        holder.textNum.setText(materialInRecord.getInputNum());
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setOnItemClickListener(MaterialInRecordAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mrlist.size();
    }

}
