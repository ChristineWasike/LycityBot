package com.wasike.lycitybot.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.wasike.lycitybot.R;
import com.wasike.lycitybot.models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class MessageAdapter extends FirebaseListAdapter<ChatMessage> {
    private DatabaseReference mRef;
    private Context mContext;

    public MessageAdapter(Activity activity, Class<ChatMessage> modelClass, int modelLayout, Query ref, Context context) {
        super(activity, modelClass, modelLayout, ref);
        mRef = ref.getRef();
        mContext = context;
    }

//    public class SongViewHolder extends FirebaseListAdapter.ViewHolder{
//
//    }

    @Override
    protected void populateView(View v, ChatMessage model, int position) {
        //Get references to the views of list_item.xml
        TextView messageText = (BubbleTextView)v.findViewById(R.id.message_text); // The actual message sent
        TextView messageUser = v.findViewById(R.id.message_user);
        TextView messageTime = v.findViewById(R.id.message_time);

        messageText.setClickable(true);
        messageText.setMovementMethod(LinkMovementMethod.getInstance());
//        String text = model.getMessageText();
//        messageText.setText(Html.fromHtml(text));
        //Set their text
        messageText.setText(model.getMessageText());
        messageUser.setText(model.getMessageUser());
        //Format the data before showing it
        messageTime.setText(DateFormat.format("dd-mm-yyyy (hh:mm)",model.getMessageTime()));
    }
}
