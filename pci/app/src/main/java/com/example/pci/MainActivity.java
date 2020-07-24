package com.example.pci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class
MainActivity extends AppCompatActivity {
    private Button button;
    DatabaseReference db;
    TextView admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_main);
        db = FirebaseDatabase.getInstance().getReference() ;
        Button button = findViewById(R.id.button);
        final TextView appear = (TextView)findViewById(R.id.appear);
        final EditText username = (EditText)findViewById(R.id.username);
        final EditText password = (EditText)findViewById(R.id.password);
        TextView create = findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent r = new Intent(MainActivity.this,Registration.class);
                startActivity(r);
            }
        });
        admin = (TextView)findViewById(R.id.adminlog);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adm = new Intent(MainActivity.this,AdminLogin.class);
                startActivity(adm);
            }
        });
        Log.e("HERE",username.getText().toString());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(isOnline()) {
                if((username.getText().toString().equals(""))||(password.getText().toString().equals("")))
                {
                    Log.e("Main","if");
                    appear.setVisibility(v.VISIBLE);
                }
                else {
                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("RegisteredUsers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(username.getText().toString())) {
                                String pass = (String) dataSnapshot.child(username.getText().toString()).getValue();
                                if (pass.equals(password.getText().toString())) {

                                    Toast.makeText(MainActivity.this, "Logging In", Toast.LENGTH_SHORT).show();
                                    Intent l = new Intent(MainActivity.this, Activity2.class);
                                    startActivity(l);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), "Invalid Username", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }


            }
            else
            {
                Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
            }
            }

        });
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
