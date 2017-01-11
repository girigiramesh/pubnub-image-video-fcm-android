package com.pubnub_fcm_example.adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pubnub_fcm_example.R;
import com.pubnub_fcm_example.activity.ImageActivity;
import com.pubnub_fcm_example.activity.VideoActivity;
import com.pubnub_fcm_example.manager.SharedPreferenceManager;
import com.pubnub_fcm_example.model.Message;
import com.pubnub_fcm_example.util.Util;

import java.util.List;

/**
 * Created by Ramesh on 1/9/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private class TextMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView his_message_tv, my_message_tv;

        public TextMessageViewHolder(View itemView) {
            super(itemView);
            his_message_tv = (TextView) itemView.findViewById(R.id.his_message_tv);
            my_message_tv = (TextView) itemView.findViewById(R.id.my_message_tv);

        }

        public void populateData(Message message) {
            boolean isMine = message.getFromId().equals(myUserId);
            if (isMine) {
                my_message_tv.setVisibility(View.VISIBLE);
                his_message_tv.setVisibility(View.GONE);
                my_message_tv.setText(message.getText());
            } else {
                my_message_tv.setVisibility(View.GONE);
                his_message_tv.setVisibility(View.VISIBLE);
                his_message_tv.setText(message.getText());
            }
        }
    }

    private class MediaMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView iv_his_message, iv_my_message, iv_his_play, iv_my_play;
        private RelativeLayout ll_his_message, ll_my_message;

        public MediaMessageViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            iv_his_message = (ImageView) view.findViewById(R.id.iv_his_message);
            iv_my_message = (ImageView) view.findViewById(R.id.iv_my_message);

            iv_his_play = (ImageView) view.findViewById(R.id.iv_his_play);
            iv_my_play = (ImageView) view.findViewById(R.id.iv_my_play);

            ll_his_message = (RelativeLayout) view.findViewById(R.id.ll_his_message);
            ll_my_message = (RelativeLayout) view.findViewById(R.id.ll_my_message);
        }

        public void populateData(Message message) {

            boolean isMine = message.getFromId().equals(myUserId);

            if (isMine) {
                ll_my_message.setVisibility(View.VISIBLE);
                ll_his_message.setVisibility(View.GONE);
            } else {
                ll_my_message.setVisibility(View.GONE);
                ll_his_message.setVisibility(View.VISIBLE);
            }

            //hide play icons
            iv_my_play.setVisibility(View.GONE);
            iv_his_play.setVisibility(View.GONE);

            switch (message.getType()) {
                case Message.IMAGE:
                    if (isMine) {
                        iv_my_play.setVisibility(View.GONE);
                        if (Util.isNotNullAndNotEmpty(message.getUrl())) {
                            Glide.with(context)
                                    .load("file://" + message.getFilePath())
                                    .centerCrop()
                                    .into(iv_my_message);
                        }
                    }
                    break;
                case Message.VIDEO:
                    if (isMine) {
                        iv_my_play.setVisibility(View.VISIBLE);
                        if (Util.isNotNullAndNotEmpty(message.getThumbnailUrl())) {
                            iv_my_message.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getFilePath(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));
                        }
                    } else {
                        iv_his_play.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (MessageModels.get(getLayoutPosition()).getType()) {
                case Message.IMAGE:
                    ImageActivity.start(context, MessageModels.get(getLayoutPosition()).getEncryptedUrl(), MessageModels.get(getLayoutPosition()).getFilePath());
                    break;
                case Message.VIDEO:
                    VideoActivity.start(context,
                            MessageModels.get(getLayoutPosition()).getEncryptedVideoUrl(),
                            MessageModels.get(getLayoutPosition()).getEncryptedThumbnailUrl(),
                            MessageModels.get(getLayoutPosition()).getFilePath());
                    break;
            }
        }
    }

    private class UnknownMessageViewHolder extends RecyclerView.ViewHolder {
        public UnknownMessageViewHolder(View itemView) {
            super(itemView);
        }

        public void populateData(Message message) {

        }
    }

    private Context context;
    private List<Message> MessageModels;
    private String myUserId;

    public ChatAdapter(Context context, List<Message> MessageModels) {
        this.context = context;
        this.MessageModels = MessageModels;
        this.myUserId = SharedPreferenceManager.getInstance().getAccountId();
    }

    public static void build(Context context, RecyclerView recyclerView, List<Message> MessageModels) {
        if (recyclerView.getAdapter() == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            ChatAdapter sikkaChatAdapter = new ChatAdapter(context, MessageModels);
            recyclerView.setAdapter(sikkaChatAdapter);
            linearLayoutManager.scrollToPosition(MessageModels.size() - 1);
        } else {
            ((ChatAdapter) recyclerView.getAdapter()).rebuild(MessageModels);
            recyclerView.scrollToPosition(MessageModels.size() - 1);
        }
    }

    public void rebuild(List<Message> MessageModels) {
        this.MessageModels = MessageModels;
        notifyDataSetChanged();
    }

    public int getItemViewType(int position) {
        if (MessageModels.get(position) != null && MessageModels.get(position).getType() != null) {
            switch (MessageModels.get(position).getType()) {
                case Message.TEXT:
                    return R.layout.row_chat_text_message;
                case Message.VIDEO:
                case Message.IMAGE:
                    return R.layout.row_chat_media_message;
                default:
                    return R.layout.row_chat_unknown;
            }
        } else {
            return R.layout.row_chat_unknown;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == R.layout.row_chat_text_message) {
            view = inflater.inflate(R.layout.row_chat_text_message, parent, false);
            viewHolder = new TextMessageViewHolder(view);
        } else if (viewType == R.layout.row_chat_media_message) {
            view = inflater.inflate(R.layout.row_chat_media_message, parent, false);
            viewHolder = new MediaMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.row_chat_unknown, parent, false);
            viewHolder = new UnknownMessageViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == R.layout.row_chat_text_message) {
            ((TextMessageViewHolder) holder).populateData(MessageModels.get(position));
        } else if (holder.getItemViewType() == R.layout.row_chat_media_message) {
            ((MediaMessageViewHolder) holder).populateData(MessageModels.get(position));
        } else {
            ((UnknownMessageViewHolder) holder).populateData(MessageModels.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return MessageModels.size();
    }
}
