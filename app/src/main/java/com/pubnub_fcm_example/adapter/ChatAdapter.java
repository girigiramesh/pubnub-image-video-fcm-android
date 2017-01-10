package com.pubnub_fcm_example.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.util.Util;

import java.util.List;

/**
 * Created by Ramesh on 1/9/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public class MyTextHolder extends RecyclerView.ViewHolder {
        private TextView tv_text;

        public MyTextHolder(View itemView) {
            super(itemView);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
        }
    }

    public class OthersTextHolder extends RecyclerView.ViewHolder {
        private TextView tv_text, tv_name;
        private ImageView iv_profile;

        public OthersTextHolder(View itemView) {
            super(itemView);
            tv_text = (TextView) itemView.findViewById(R.id.tv_text);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_profile= (ImageView) itemView.findViewById(R.id.iv_profile);
        }
    }

    private List<Message> list;
    Context context;
    private String udid;

    public ChatAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
        udid = Util.getUdid(context);
    }

    public void notifyList(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (udid.equals(list.get(position).getDeviseToken())) {
            return R.layout.row_my_text_message;
        } else {
            return R.layout.row_other_text_message;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case R.layout.row_my_text_message:
                view = (LayoutInflater.from(parent.getContext())).inflate(R.layout.row_my_text_message, parent, false);
                return new MyTextHolder(view);
            default:
                view = (LayoutInflater.from(parent.getContext())).inflate(R.layout.row_other_text_message, parent, false);
                return new OthersTextHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (udid.equals(list.get(position).getDeviseToken())) {
            ((MyTextHolder) holder).tv_text.setText(list.get(position).getText());
        } else {
            ((OthersTextHolder) holder).tv_text.setText(list.get(position).getText());
            ((OthersTextHolder) holder).tv_name.setText(list.get(position).getSenderName() + " :");
            ((OthersTextHolder) holder).iv_profile.getDrawable();
        }
    }
}
