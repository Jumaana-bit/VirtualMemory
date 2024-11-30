package com.example.virtualmemory;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private List<Contact> contactList;
    private OnItemClickListener onItemClickListener;
    private ContactDatabaseHelper dbHelper;

    public interface OnItemClickListener {
        void onItemClick(Contact contact);
    }

    public ContactAdapter(Context context, List<Contact> contactList, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.contactList = contactList;
        this.onItemClickListener = onItemClickListener;
        this.dbHelper = new ContactDatabaseHelper(context);
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // Set contact details
        holder.name.setText(contact.getName());
        holder.relationship.setText(contact.getRelationship());
        holder.email.setText(contact.getEmail());
        holder.phoneNumber.setText(contact.getPhoneNumber());

        // Handle item click
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(contact));

        // Handle call button click
        holder.callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + contact.getPhoneNumber()));
            context.startActivity(intent);
        });

        // Set up the play button
        holder.playButton.setOnClickListener(v -> playAudio(contact.getVoiceMemoPath()));

        holder.deleteButton.setOnClickListener(v -> {
            deleteContact(contact.getId()); // Delete contact by its ID
            contactList.remove(position); // Remove from the list
            notifyItemRemoved(position); // Notify adapter of the item removal
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    private void playAudio(String voiceMemoPath) {
        if (voiceMemoPath != null && !voiceMemoPath.isEmpty()) {
            try {
                AssetFileDescriptor afd = context.getAssets().openFd(voiceMemoPath); // Open the audio file from assets
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "No audio available", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteContact(long contactId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("contacts", "id = ?", new String[]{String.valueOf(contactId)});
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView name, relationship, email, phoneNumber;
        ImageView callButton, playButton,deleteButton; // Add call button reference

        public ContactViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contactName);
            relationship = itemView.findViewById(R.id.contactRelationship);
            email = itemView.findViewById(R.id.contactEmail);
            phoneNumber = itemView.findViewById(R.id.contactPhone);
            callButton = itemView.findViewById(R.id.callButton); // Initialize call button
            playButton = itemView.findViewById(R.id.playButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}