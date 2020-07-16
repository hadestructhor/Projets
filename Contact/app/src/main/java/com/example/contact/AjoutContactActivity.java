package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AjoutContactActivity extends AppCompatActivity {

    private Resources resources;
    private static final int PICK_IMAGE = 1;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        if (requestCode == PICK_IMAGE  && resultCode == Activity.RESULT_OK) {
            ImageButton imageButton = findViewById(R.id.imageBtnAvatar);
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                imageButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_contact);

        Button btnValidate = findViewById(R.id.btnValidate);
        resources = getResources();

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etFirstName = findViewById(R.id.etFirstName);
                EditText etLastName = findViewById(R.id.etLastName);
                EditText etPhoneNumber = findViewById(R.id.etPhoneNumber);
                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                EditText etDateBirth = findViewById(R.id.etDateBirth);
                EditText etMail = findViewById(R.id.etMail);
                EditText etAddress = findViewById(R.id.etAddress);
                EditText etPostalCode = findViewById(R.id.etPostalCode);
                ImageButton imageButton = findViewById(R.id.imageBtnAvatar);

                Boolean contactValide = true;
                int selectedId = radioGroup.getCheckedRadioButtonId();
                String fName = etFirstName.getText().toString();
                String lName = etLastName.getText().toString();
                String phoneNumber = etPhoneNumber.getText().toString();

                String message = "";

                if(fName.equals("")){
                    contactValide=false;
                    String noFirstName = resources.getString(R.string.no_first_name);
                    message += noFirstName + "\n";
                }else{
                    String firstName = resources.getString(R.string.first_name);
                    message += firstName + " "  + fName + "\n";
                }


                if(lName.equals("")){
                    contactValide=false;
                    String noLasttName = resources.getString(R.string.no_last_name);
                    message += noLasttName + "\n";
                }else{
                    String lasttName = resources.getString(R.string.last_name);
                    message += lasttName + " "  + lName + "\n";
                }


                if(phoneNumber.equals("")){
                    contactValide=false;
                    String noPhoneNumber = resources.getString(R.string.no_phone_number);
                    message += noPhoneNumber;
                }else {
                    String phoneNumberTxt = resources.getString(R.string.phone_number);
                    message += phoneNumberTxt + " "  + phoneNumber;
                }

                if(contactValide){
                    BitmapDrawable drawable = (BitmapDrawable) imageButton.getDrawable();
                    Bitmap bitmap = null;
                    if(drawable != null) bitmap = drawable.getBitmap();
                    String sexe = null;
                    if(selectedId!=-1) {
                        RadioButton btn = findViewById(selectedId);
                        sexe  = btn.getText().toString();
                    }
                    String filename = "";
                    if (bitmap!=null) {
                        filename = "contact_"+Contact.getNbContact()+".png";
                        FileOutputStream stream = null;
                        try {
                            stream = openFileOutput(filename, Context.MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                stream.close();
                                bitmap.recycle();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    Contact obj = new Contact(sexe, fName, lName,phoneNumber,etDateBirth.getText().toString(),etMail.getText().toString(),etAddress.getText().toString(),etPostalCode.getText().toString(),filename);
                    Intent intent = new Intent();
                    intent.putExtra("Contact", obj);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(AjoutContactActivity.this, message, Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageButton imageButton = findViewById(R.id.imageBtnAvatar);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMAGE);

            }

        });

    }
}
