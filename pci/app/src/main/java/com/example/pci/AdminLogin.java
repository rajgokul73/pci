package com.example.pci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
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

public class AdminLogin extends AppCompatActivity {
    Button button;
    EditText username = null, password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_admin_login);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        username = (EditText)findViewById(R.id.admusername);
        password = (EditText)findViewById(R.id.admpass);
        button = (Button)findViewById(R.id.admbut);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnline()) {

                    final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                    db.child("Admin").child("admin").setValue("1234");
                    db.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(username.getText().toString())) {
                                String pass = (String) dataSnapshot.child(username.getText().toString()).getValue();
                                if (pass.equals(password.getText().toString())) {

                                    Toast.makeText(getApplicationContext(), "Logging In", Toast.LENGTH_SHORT).show();
                                    Intent l = new Intent(AdminLogin.this, Admin.class);
                                    Bundle b = new Bundle();
                                    b.putInt("iter",0);
                                    l.putExtras(b);
                                    startActivity(l);
                                    finish();
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
        });
    }
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
