package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;

public class AfficheContactActivity extends AppCompatActivity {

    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_contact);
        Contact c = (Contact) getIntent().getSerializableExtra("Contact");

        ImageView ivContact = findViewById(R.id.ivContact);
        TextView txtGender = findViewById(R.id.txtGender);
        TextView txtFirstName = findViewById(R.id.txtFirstName);
        TextView txtLastName = findViewById(R.id.txtLastName);
        TextView txtBirth = findViewById(R.id.txtBirth);
        TextView txtNumber = findViewById(R.id.txtNumber);
        TextView txtMail = findViewById(R.id.txtMail);
        TextView txtAddress = findViewById(R.id.txtAddress);
        TextView txtPostalCode = findViewById(R.id.txtPostalCode);

        txtGender.setText(c.getSexe());
        txtFirstName.setText(c.getPrenom());
        txtLastName.setText(c.getNom());
        txtBirth.setText(c.getAnniversaire());
        txtNumber.setText(c.getTel());
        txtMail.setText(c.getMail());
        txtAddress.setText(c.getAddresse());
        txtPostalCode.setText(c.getCodePostal());

        resources = getResources();

        String filename = c.getImagePath();
        Bitmap bmp = null;
        FileInputStream is = null;
        if(!filename.equals("")){
            try {
                is = this.getApplicationContext().openFileInput(filename);
                bmp = BitmapFactory.decodeStream(is);
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ivContact.setImageBitmap(bmp);
        }else{
            if(c.getSexe()!=null){
                if(c.getSexe().equals(resources.getString(R.string.btnMale)))ivContact.setImageResource(resources.getIdentifier("man","drawable", getPackageName()));
                else if(c.getSexe().equals(resources.getString(R.string.btnFemale)))ivContact.setImageResource(resources.getIdentifier("woman","drawable", getPackageName()));
            }
            else ivContact.setImageResource(resources.getIdentifier("person","drawable", getPackageName()));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
