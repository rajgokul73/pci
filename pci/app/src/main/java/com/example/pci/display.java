package com.example.pci;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.automl.FirebaseAutoMLLocalModel;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfWriter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifiedImages;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyOptions;
import com.itextpdf.text.pdf.qrcode.ByteArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

import static com.example.pci.R.drawable.logo;

public class display extends AppCompatActivity implements LocationListener{
    Document document;
    TextView createpdf,openpdf;
    int counter,pci;
    float sum = 0;
    String loc;
   FusedLocationProviderClient mFusedLocationClient;
    StringBuilder builder = new StringBuilder();
   String picturePath;
    PdfDocument doc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);
        setContentView(R.layout.activity_display);
        final View v = null;
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        openpdf = (TextView) findViewById(R.id.pdfopen);
        document = new Document();
        final ImageView image = (ImageView) findViewById(R.id.imgview);
        Bundle b = getIntent().getExtras();
        picturePath = null;
        doc = new PdfDocument();
        if (b != null) {
            picturePath = b.getString("picturepath");
            Log.e("HERE1", picturePath);
            counter = b.getInt("counter");
        }
        final Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e("HERE",width+","+height);
        image.requestLayout();
        image.getLayoutParams().height = height;
        image.getLayoutParams().width = width;
        image.setImageBitmap(bitmap);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastlocation();

        /*try {
            enableStrictMode();
            IamOptions options = new IamOptions.Builder().apiKey("9Qe1qDNmjnyFQXnlJMEvdU2DPmCj_tZJjTmLmA6Gt0cM").build();
            VisualRecognition service = new VisualRecognition("2018-03-19", options);
            InputStream imagesStream = null;
            try
            {
                imagesStream = new FileInputStream(picturePath);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            ClassifyOptions classifyOptions = new ClassifyOptions.Builder().imagesFile(imagesStream).imagesFilename("fruitbowl.jpg").threshold((float) 0.0).classifierIds(Arrays.asList("PCImodel_353078942")).build();
            ClassifiedImages result = service.classify(classifyOptions).execute();
            String resultJson = String.valueOf(result);
            JSONObject res = new JSONObject(resultJson);
            JSONArray img = res.getJSONArray("images");
            JSONObject obj = img.getJSONObject(0);
            JSONArray classifiers = obj.getJSONArray(("classifiers"));
            obj = classifiers.getJSONObject(0);
            JSONArray classes = obj.getJSONArray("classes");

            double max=0.0;
            double wi[] = new double[9];
            wi[0] = 3.0;
            wi[1] = 3.0;
            wi[2] = 3.0;
            wi[4] = 3.0;
            wi[5] = 3.0;
            wi[6] = 3.0;
            wi[7] = 3.0;
            wi[8] = 3.0;
            int count = 0;
            double sum =0.0,good = 0.0;
            for (int iter = 0; iter < classes.length(); iter++) {
                obj = classes.getJSONObject(iter);
                String cla = obj.getString("class");
                Double score = obj.getDouble("score");
                if(iter == 3)
                {
                    good = score;
                }
                if(score>max && iter!=3)
                {
                    max=score;
                }
                String sco = String.valueOf(score);
                String answer = cla + " : " + sco;
                builder.append(answer + "\n");
            }


            Log.e("HERE","Good:"+good+"Max:"+max);
            if(good > max)
            {
                pci = (int) (good*100);
            }
            else {

                pci = (int) (100-(max*100));
            }*/
        FirebaseAutoMLLocalModel localModel = new FirebaseAutoMLLocalModel.Builder()
                .setAssetFilePath("manifest.json")
                .build();
        FirebaseVisionImageLabeler labeler = null;
        try {
            FirebaseVisionOnDeviceAutoMLImageLabelerOptions option =
                    new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder(localModel)
                            .setConfidenceThreshold(0.0f)  // Evaluate your model in the Firebase console
                            // to determine an appropriate value.
                            .build();
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(option);
        } catch (FirebaseMLException e) {
            // ...
        }
        FirebaseVisionImage result = FirebaseVisionImage.fromBitmap(bitmap);
        labeler.processImage(result)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        for (FirebaseVisionImageLabel label : labels) {
                            String text = label.getText();
                            float confidence = label.getConfidence();
                            sum += (confidence * 100);
                            String bui = text + " : " + confidence;
                            builder.append(bui + "\n");
                        }
                        TextView txt = (TextView) findViewById(R.id.txt);
                        txt.setText(builder.toString());
                        System.out.println(builder.toString());
                        TextView Res = (TextView) findViewById(R.id.result);
                        pci = (int) (sum / 9);
                        Log.e("Result", "PCI: " + pci);
                        Res.setText("PCI : " + pci);

                        final int finalCounter = counter;
                        try {
                            String path = Environment.getExternalStorageDirectory().toString() + "/PCI";
                            File pdf = new File(path, "PDF" + counter + ".pdf");
                            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
                            document.open();
                            Drawable d = getResources().getDrawable(R.drawable.logo);
                            BitmapDrawable bitdw = ((BitmapDrawable) d);
                            Bitmap bmp = bitdw.getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            Image image = Image.getInstance(stream.toByteArray());
                            image.setAlignment(1);
                            image.scaleAbsolute(50, 50);
                            document.add(image);
                            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 35, Font.UNDERLINE);
                            Paragraph title = new Paragraph("Pradhan Mantri Gram Sadak Yojana\n\n", font1);
                            title.setAlignment(1);
                            title.setSpacingAfter(10);
                            document.add(title);
                            font1.setStyle(Font.BOLD);
                            font1.setSize(20);
                            Anchor anchor = new Anchor(
                                    loc);
                            anchor.setReference("www.google.com");
                            Paragraph ima = new Paragraph("Location: " + anchor.toString() + "\nImage: ", font1);
                            ima.setAlignment(0);
                            ima.setSpacingAfter(10);
                            document.add(ima);
                            Image image1 = Image.getInstance(picturePath);
                            image1.scaleAbsolute(250, 175);
                            image1.setAlignment(1);
                            document.add(image1);
                            Font font2 = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
                            Paragraph details = new Paragraph("Details Of the Road:\n\n", font2);
                            details.setAlignment(1);
                            document.add(details);
                            Paragraph p = new Paragraph(builder.toString() + "\n");
                            p.setAlignment(0);
                            p.setIndentationLeft(300);
                            Paragraph pc = new Paragraph("PCI: " + pci, font2);
                            pc.setAlignment(0);
                            document.add(p);
                            document.add(pc);
                            document.addCreationDate();
                            document.addTitle("ROAD" + finalCounter);
                            document.addSubject("Road" + finalCounter);
                            Toast.makeText(getApplicationContext(), "Document created", Toast.LENGTH_SHORT).show();
                            document.close();
                            writer.close();
                            openpdf.setVisibility(v.VISIBLE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });
        openpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/PCI/PDF"+counter+".pdf");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        });


                        /*Uri file = Uri.fromFile(new File(path+"/PDF"+counter+".pdf"));
                        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                        StorageReference pdfref = firebaseStorage.getReference();
                        pdfref.child("doc/"+file.getLastPathSegment());
                        UploadTask uploadTask = pdfref.putFile(file);

// Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Log.e("HERE",exception.toString());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                String downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                Log.e("Down",downloadUrl);
                            }
                        });*/

    }
    @SuppressLint("MissingPermission")
    private void getLastlocation()
    {
        if(checkPermissions())
        {
            if(isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        TextView txtLat = (TextView) findViewById(R.id.loc);
                        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
                        loc = location.getLatitude() +"," + location.getLongitude();
                    }
                });
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("HERE","if");
        TextView txtLat = (TextView) findViewById(R.id.loc);
        txtLat.setText("Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
        Log.e("HERE","!Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
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
    private boolean checkPermissions()
    {
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
          return false;
    }
    private boolean isLocationEnabled()
    {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER);
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}