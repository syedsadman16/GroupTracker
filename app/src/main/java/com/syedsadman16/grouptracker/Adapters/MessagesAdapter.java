package com.syedsadman16.grouptracker.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.syedsadman16.grouptracker.Models.Message;
import com.syedsadman16.grouptracker.Models.User;
import com.syedsadman16.grouptracker.R;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    Context context;
    ArrayList<Message> messages;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }


    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImage;
        public TextView messageTextView;
        public TextView nameTextView;



        public ViewHolder(View itemView) {
            super(itemView);

        }


        public void populateView(final Message message) {
            messageTextView.setText(message.getMessage());
            if(!message.getUserid().equals(User.uid)) {
                nameTextView.setText(message.getSenderName());
            }

        }

    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) messages.get(position);

        if (message.getUserid().equals(User.uid)) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflating the layout file and return it to view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.my_message, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(context).inflate(R.layout.other_message, parent, false);
            return new MessagesAdapter.ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.populateView(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
