package com.example.pci;

import java.io.*;
import java.util.*;
import java.lang.Object;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

import static com.google.common.reflect.Reflection.initialize;


public class Activity2 extends AppCompatActivity{
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    /**
     * Permissions that need to be explicitly requested from end user.
     */
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE ,Manifest.permission.SEND_SMS};
    DatabaseReference db;
    int flag=0,counter;
    Intent disp;
    OutputStream fout;
    String picturePath;
    String path;
    File folder,file;
    Bitmap selectedImage,greyscale;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_2);
        disp = new Intent(Activity2.this,display.class);
        Button b1 = (Button)findViewById(R.id.btn);
        path = Environment.getExternalStorageDirectory().toString()+"/PCI";
        folder = new File(path);
        if(!folder.exists())
        {
            folder.mkdir();
        }
        fout = null;
        db = FirebaseDatabase.getInstance().getReference();
        db.child("Counter").setValue(0);
        db.child("Counter").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long  pass = (long) dataSnapshot.getValue();
                counter = (int)pass;
                Log.e("HERE", String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        try {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()) {

                        selectImage(Activity2.this);
                    }

                    else {
                        Toast.makeText(getApplicationContext(),"No Internet Connection",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }
    private void selectImage (Context context)
    {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your image");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takepicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                    startActivityForResult(takepicture, 0);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent pickphoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(pickphoto, 1);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        selectedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                        if (selectedImage!=null)
                        greyscale = toGrayscale(selectedImage);
                        counter = counter+1;
                        file = new File(path,"Road"+counter+".jpg");
                        try {
                            fout = new FileOutputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        greyscale.compress(Bitmap.CompressFormat.JPEG,85,fout);
                        Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                        picturePath = String.valueOf(savedImageURI);
                        flag = 1;
                        update(flag);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();

                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String n = String.valueOf(filePathColumn[0]);
                                picturePath = cursor.getString(columnIndex);
                                Bitmap image = BitmapFactory.decodeFile(picturePath);
                                greyscale = toGrayscale(image);
                                counter = counter+1;
                                file = new File(path,"Road"+counter+".jpg");
                                try {
                                    fout = new FileOutputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                greyscale.compress(Bitmap.CompressFormat.JPEG,85,fout);
                                Uri savedImageURI = Uri.parse(file.getAbsolutePath());
                                picturePath = String.valueOf(savedImageURI);
                                cursor.close();
                                flag = 1;
                                update(flag);

                            } else {
                                Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "No image", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
            }

            Bundle b = new Bundle();
            b.putString("picturepath", picturePath);
            b.putInt("counter",counter);
            disp.putExtras(b);
            startActivity(disp);
            finish();

        }
    }
    public void update(int flag)
    {
        if(flag ==1)
        {
            db.child("Counter").setValue(counter);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
    public void enableStrictMode()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /*public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }*/

    /*
    IamOptions options = new IamOptions.Builder().apiKey("{apikey}").build();
    VisualRecognition service = new VisualRecognition("2018-03-19", options);
    InputStream imagesStream = new FileInputStream(picturePath);
    ClassifyOptions classifyOptions = new ClassifyOptions.Builder()
	.imagesFile(imagesStream)
	.imagesFilename("fruitbowl.jpg")
	.threshold((float) 0.6)
	.classifierIds(Arrays.asList("Testmodel_1933497769"))
	.build();
    ClassifiedImages result = service.classify(classifyOptions).execute();
    System.out.println(result);
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int index = permissions.length - 1; index >= 0; --index) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        // exit the app if one permission is not granted
                        Toast.makeText(this, "Required permission '" + permissions[index]
                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                }
                // all permissions were granted
                initialize();
                break;
        }
    }
}