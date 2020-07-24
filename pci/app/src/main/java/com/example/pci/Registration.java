package com.example.pci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static com.ibm.watson.developer_cloud.http.RequestBuilder.post;

public class Registration extends AppCompatActivity {
    public String user,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_registration);

        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password1 = (EditText)findViewById(R.id.password);
        final EditText password2 = (EditText)findViewById(R.id.password1);
        final EditText fname = (EditText)findViewById(R.id.firstname);
        final EditText lname = (EditText)findViewById(R.id.lastname);
        final EditText mail = (EditText)findViewById(R.id.email);
        final EditText mobile = (EditText)findViewById(R.id.mobile);
        Button create = (Button)findViewById(R.id.signup);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference acc = FirebaseDatabase.getInstance().getReference();
                db.child("RegisteredUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(username.getText().toString()))
                        {
                            Toast.makeText(getApplicationContext(), "Username Already Exist", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            if(password1.getText().toString().equals(password2.getText().toString()))
                            {
                                acc.child("Account Details").child(username.getText().toString()).child("Verified").setValue(0);
                                acc.child("Account Details").child(username.getText().toString()).child("Password").setValue(password1.getText().toString());
                                acc.child("Account Details").child(username.getText().toString()).child("Username").setValue(username.getText().toString());
                                acc.child("Account Details").child(username.getText().toString()).child("FirstName").setValue(fname.getText().toString());
                                acc.child("Account Details").child(username.getText().toString()).child("LastName").setValue(lname.getText().toString());
                                acc.child("Account Details").child(username.getText().toString()).child("EmailId").setValue(mail.getText().toString());
                                acc.child("Account Details").child(username.getText().toString()).child("Mobile").setValue(mobile.getText().toString());
                                Toast.makeText(getApplicationContext(),"Registered",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Passwords do not match",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
    }
}