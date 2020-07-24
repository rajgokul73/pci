package com.example.pci;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin extends AppCompatActivity {
    String user,pass,firstname,lastname,mobile,emailid;
    int iter ,i=0,count = 0;
    long no ;
    Context context;
    TextView username ,first,last,mob,ema;
    Bundle b ;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    DatabaseReference register = FirebaseDatabase.getInstance().getReference();
    StringBuilder builder = new StringBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_admin);
        username = (TextView)findViewById(R.id.user);
        first = (TextView)findViewById(R.id.first);
        last = (TextView)findViewById(R.id.last);
        mob = (TextView)findViewById(R.id.mob);
        ema = (TextView)findViewById(R.id.ema);
        context = this;
        b = getIntent().getExtras();
        if(b != null)
        {
            iter  = b.getInt("iter");
            Log.e("HERE2",String.valueOf(iter));
        }

        db.child("Account Details").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                no = dataSnapshot.getChildrenCount();
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            user = snapshot.getKey();
                            long ver = (long) snapshot.child("Verified").getValue();
                            if(ver!=1) {
                                if (i < iter)
                                {
                                    i++;
                                }
                                if(i == iter) {
                                    db.child("Account Details").child(user).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            user = snapshot.getKey();
                                            username.setText("UserName: "+user);
                                            pass = (String) dataSnapshot.child("Password").getValue();
                                            firstname = (String) dataSnapshot.child("FirstName").getValue();
                                            first.setText("FirstName: " + firstname);
                                            lastname = (String) dataSnapshot.child("LastName").getValue();
                                            last.setText("LastName: " + lastname);
                                            mobile = (String) dataSnapshot.child("Mobile").getValue();
                                            mob.setText("Mobile: " + mobile);
                                            emailid = (String) dataSnapshot.child("EmailId").getValue();
                                            ema.setText("EmailId: " + emailid);
                                            //builder.append("FirstName: " + firstname + "\nLastName: " + lastname + "\nMobile: " + mobile + "\nEmailId: " + emailid);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }

                            }
                            else
                            {
                                count++;
                            }

                    iter = i;


                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
        if(count >= no-1)
        {
            username.setText("All Users are verified");
            Toast.makeText(getApplicationContext(),"All Users are verified",Toast.LENGTH_SHORT).show();
        }
        System.out.println(builder.toString());
        Button next = (Button)findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("HERE5",String.valueOf(count));
                if(count < no)
                {
                    Intent same = new Intent(Admin.this,Admin.class);
                    Bundle b = new Bundle();
                    b.putInt("iter",++iter);
                    Log.e("HERE1",String.valueOf(iter));
                    same.putExtras(b);
                    startActivity(same);
                    finish();
                }
                else if(count >= no-1)
                {
                    username.setText("All Users are verified");
                    Toast.makeText(getApplicationContext(),"All Users are verified",Toast.LENGTH_SHORT).show();
                }

            }
        });
        Button verify = (Button)findViewById(R.id.verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("HERE3",user);
                db.child("Account Details").child(user).child("Verified").setValue(1);
                register.child("RegisteredUsers").child(user).setValue(pass);
                sendSMS(mobile,"Your registration is verified\nUsername: "+user+"\nPassword: "+pass);
                Log.e("HERE6",mobile);
            }
        });
    }
    private void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}
