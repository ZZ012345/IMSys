package com.rinc.imsys;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by zhouzhi on 2017/8/20.
 */

public class MaterialOutRecordAdapter extends RecyclerView.Adapter<MaterialOutRecordAdapter.ViewHolder> implements View.OnClickListener {

    private List<MaterialOutRecord> mrlist;

    private MaterialOutRecordAdapter.OnItemClickListener mOnItemClickListener = null;

    public MaterialOutRecordAdapter(List<MaterialOutRecord> mrlist) {
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
        TextView textLeftnum;
        TextView textUser;
        ImageView more;

        public ViewHolder(View view) {
            super(view);
            textTime = (TextView) view.findViewById(R.id.text_datatime_matoutrec);
            textOperator = (TextView) view.findViewById(R.id.text_operator_matoutrec);
            textId = (TextView) view.findViewById(R.id.text_id_matoutrec);
            textNum = (TextView) view.findViewById(R.id.text_num_matoutrec);
            textLeftnum = (TextView) view.findViewById(R.id.text_leftnum_matoutrec);
            textUser = (TextView) view.findViewById(R.id.text_user_matoutrec);
            more = (ImageView) view.findViewById(R.id.image_more_matoutrec);
        }
    }

    @Override
    public MaterialOutRecordAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materialoutrecord_item, parent, false);
        MaterialOutRecordAdapter.ViewHolder viewHolder = new MaterialOutRecordAdapter.ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MaterialOutRecordAdapter.ViewHolder holder, int position) {
        MaterialOutRecord materialOutRecord = mrlist.get(position);
        holder.textTime.setText(materialOutRecord.getOutputDateTime());
        holder.textOperator.setText(materialOutRecord.getOperator());
        holder.textId.setText(materialOutRecord.getMaterialStock().getId());
        holder.textNum.setText(materialOutRecord.getOutputNum());
        holder.textLeftnum.setText(materialOutRecord.getLeftNum());
        holder.textUser.setText(materialOutRecord.getUser());
        holder.itemView.setTag(position);
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int)view.getTag());
        }
    }

    public void setOnItemClickListener(MaterialOutRecordAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mrlist.size();
    }

}
