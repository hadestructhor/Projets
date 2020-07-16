package com.example.contact;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Contact> listeContact;
    private final static int ADD_CONTACT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listeContact= new ArrayList<>();

        File directory = this.getFilesDir();
        File file = new File(directory, "contacts");
        if(file.exists()){
            FileInputStream fis = null;
            ObjectInputStream in = null;

            try {
                fis = openFileInput("contacts");
                in = new ObjectInputStream(fis);
                listeContact = (ArrayList<Contact>) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        ListView listView = findViewById(R.id.listView);
        final ContactAdapter adapter = new ContactAdapter(MainActivity.this,listeContact);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                afficheContact(listeContact.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                File file = new File(listeContact.get(position).getImagePath());
                file.delete();
                if(file.exists()){
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file.exists()){
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
                listeContact.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });


        Button addContact = findViewById(R.id.btnAddContact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ajoutContact();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        if (resultCode == Activity.RESULT_OK && requestCode==ADD_CONTACT) {
            Contact c = (Contact) data.getSerializableExtra("Contact");
            listeContact.add(c);
        }
    }

    public void ajoutContact(){
        Intent intent = new Intent(this, AjoutContactActivity.class);
        startActivityForResult(intent, ADD_CONTACT);
    }

    public void afficheContact(Contact c){
        Intent intent = new Intent(this, AfficheContactActivity.class);
        intent.putExtra("Contact", c);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        ListView listView = findViewById(R.id.listView);
        final ContactAdapter adapter = new ContactAdapter(MainActivity.this,listeContact);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                afficheContact(listeContact.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                File file = new File(listeContact.get(position).getImagePath());
                file.delete();
                if(file.exists()){
                    try {
                        file.getCanonicalFile().delete();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(file.exists()){
                        getApplicationContext().deleteFile(file.getName());
                    }
                }
                listeContact.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onPause() {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            // save the object to file
            fos = openFileOutput("contacts", Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            System.out.println(listeContact.size());
            out.writeObject(listeContact);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }
}
