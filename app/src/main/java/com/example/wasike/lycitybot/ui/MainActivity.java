package com.example.wasike.lycitybot.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wasike.lycitybot.Constants;
import com.example.wasike.lycitybot.R;
import com.example.wasike.lycitybot.adapters.MessageAdapter;
import com.example.wasike.lycitybot.models.ChatMessage;
import com.example.wasike.lycitybot.services.WatsonService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import org.parceler.Parcels;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseListAdapter<ChatMessage> adapter;
    private ChatMessage chatMessage;
    private MessageAdapter messageAdapter;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Bind(R.id.activity_main) RelativeLayout mActivityMain;
    @Bind(R.id.fab) FloatingActionButton fab;
    @Bind(R.id.input) EditText userInput;
    @Bind(R.id.list_of_messages) ListView messageListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        fab.setOnClickListener(this);
        //check if not signed in then navigate to SignIn Page
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), Constants.SIGN_IN_REQUEST_CODE );
        } else {
            Snackbar.make(mActivityMain, "Welcome" + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Snackbar.LENGTH_SHORT)
                    .show();
        }
        //Load content
        displayChatMessage();
        // End of onCreate()


    }

    private void displayChatMessage() {
        String uid = user.getUid();
        Query query = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_CHAT)
                .child(uid);
        /* MessageAdapter is the custom adapter which makes it easier to set the ListView*/
        messageAdapter = new MessageAdapter(MainActivity.this, ChatMessage.class, R.layout.list_item, query, this);
        messageListView.setAdapter(messageAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.SIGN_IN_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                Snackbar.make(mActivityMain, "Successfully signed in. Welcome!", Snackbar.LENGTH_SHORT)
                        .show();
                displayChatMessage();
            } else {
                Snackbar.make(mActivityMain, "We couldn't sign in. Please try again later.", Snackbar.LENGTH_SHORT)
                        .show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            MenuItem item = menu.findItem(R.id.menu_sign_out);
            item.setVisible(false);
        } else {
            MenuItem item = menu.findItem(R.id.menu_sign_in);
            item.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_sign_out){
            AuthUI.getInstance().signOut(this).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MainActivity.this, "You have been signed out.", Toast.LENGTH_LONG).show();

                    //close activity
                    finish();
                }
            });
        }
        if (id == R.id.menu_sign_in) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String message = userInput.getText().toString();
        if (!message.equals("")) { // If statement ensures a message doesn't go blank
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                ChatMessage mChatMessage = new ChatMessage(message, String.valueOf(user.getEmail()));
                String uid = user.getUid();
                DatabaseReference chatRef = FirebaseDatabase
                        .getInstance()
                        .getReference(Constants.FIREBASE_CHILD_CHAT)
                        .child(uid);
                DatabaseReference pushRef = chatRef.push();
                String pushId = pushRef.getKey();
                mChatMessage.setPushId(pushId);
                mChatMessage.setSend(true);
                pushRef.setValue(mChatMessage);
            }
            watsonConversation(message); // Method to call on the Watson Service
            userInput.setText("");
        }
    }

    //Method that prevent the activity fro changing upon change in orientation of screen
    /*No worries already solved it */


    /** Watson Service method which sends the message
     * to the Watson AI(Lexy) */
    private void watsonConversation(String conversation) {
        if (!conversation.equals("")) {
            MessageRequest request = new MessageRequest.Builder()
                    .inputText(conversation)
                    .build();
            final WatsonService watsonService = new WatsonService();
            final TextView messageText = (BubbleTextView) findViewById(R.id.message_text);
            watsonService.watsonConversationService.message(Constants.BLUEMIX_WORK_SPACEID, request)
            .enqueue(new ServiceCallback<MessageResponse>() {
                @Override
                public void onResponse(MessageResponse response) {
                    final String outputText = response.getText().get(0);
                    /* Code to store the response on Firebase */
                    ChatMessage mChatMessage = new ChatMessage(outputText, "Lexy"); // Instantiating the model in order to store details onto Firebase.
                    String uid = user.getUid();
                    DatabaseReference chatRef = FirebaseDatabase
                            .getInstance()
                            .getReference(Constants.FIREBASE_CHILD_CHAT)
                            .child(uid);
                    DatabaseReference pushRef = chatRef.push();
                    String pushId = pushRef.getKey();
                    mChatMessage.setPushId(pushId);
                    mChatMessage.setSend(false);
                    pushRef.setValue(mChatMessage);
                    Log.d("Bot:", String.valueOf(outputText));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageText.setText(outputText);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }
}
