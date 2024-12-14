package com.example.loms;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<String> messages;
    private EditText messageInput;
    private Button sendButton;

    private FirebaseUser currentUser;
    private DatabaseReference chatDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Firebase components
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatDatabase = FirebaseDatabase.getInstance().getReference("chats");

        // Initialize UI components
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        // Initialize chat data
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // Send button click listener
        sendButton.setOnClickListener(v -> sendMessage());
    }

    /**
     * Sends a message to the Firebase database and updates the UI.
     */
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (message.isEmpty()) {
            Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save message to Firebase
        String messageId = chatDatabase.push().getKey();
        if (messageId != null) {
            chatDatabase.child(messageId).setValue(message)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            messages.add(message);
                            chatAdapter.notifyItemInserted(messages.size() - 1);
                            chatRecyclerView.scrollToPosition(messages.size() - 1);
                            messageInput.setText(""); // Clear input field
                        } else {
                            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}