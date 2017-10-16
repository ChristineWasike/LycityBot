package com.example.wasike.lycitybot.ui;

import android.content.Intent;
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
import com.example.wasike.lycitybot.models.ChatMessage;
import com.example.wasike.lycitybot.services.WatsonService;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.library.bubbleview.BubbleTextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseListAdapter<ChatMessage> adapter;
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
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                R.layout.list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get references to the views of list_item.xml
                TextView messageText = (BubbleTextView)v.findViewById(R.id.message_text); // The actual message sent
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);

                //Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());

                //Format te data before showing it

                messageTime.setText(DateFormat.format("dd-mm-yyyy (HH:mm:ss)",model.getMessageTime()));
            }
        };
        listOfMessages.setAdapter(adapter);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out){
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
        return true;
    }

    @Override
    public void onClick(View view) {
//        EditText input = (EditText) findViewById(R.id.input);
        String message = userInput.getText().toString();

        if (!message.equals("")) { // If statement ensures a message doesn't go blank
            FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(message,
                    FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            watsonConversation(message); // Method to call on the Watson Service
            userInput.setText("");
        }

        //Clear message when send
//        BubbleTextView bubbleTextView = (BubbleTextView) findViewById(R.id.message_text);
//        chatText.setText("");
    }

    /** Watson Service method which sends the message
     * to the Watson AI(Lexy) */
    private void watsonConversation(String conversation) {
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
                        Log.d("Bot:", String.valueOf(outputText));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                messageText.setText(Html.fromHtml("<p><b>Lexy:</b> " + outputText + "</p>"));
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
