package com.example.contact;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ContactAdapter extends BaseAdapter {

    private Context contexte;
    private ArrayList<Contact> liste;

    private class ViewHolder {
        public ImageView img;
        public TextView tvNomPrenom;
        public TextView tvMail;
        public TextView tvTel;
    }

    public ContactAdapter(Context contexte, ArrayList liste) {
        this.contexte = contexte;
        this.liste = liste;
    }

    public Context getContext() {
        return contexte;
    }

    public void setContext(Context contexte) {
        this.contexte = contexte;
    }

    public ArrayList getListe() {
        return liste;
    }

    public void setListe(ArrayList liste) {
        this.liste = liste;
    }

    @Override
    public int getCount() {
        return liste.size();
    }

    @Override
    public Object getItem(int position) {
        return liste.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ConstraintLayout layoutItem;
        LayoutInflater mInflater = LayoutInflater.from(contexte);
        if(convertView == null){
            layoutItem = (ConstraintLayout) mInflater.inflate(R.layout.contact_layout, parent, false);
        }else {
            layoutItem = (ConstraintLayout) convertView;
        }
        ViewHolder viewHolder = (ViewHolder) layoutItem.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder();
        }

        viewHolder.tvNomPrenom = layoutItem.findViewById(R.id.tvNameSurname);
        viewHolder.tvMail = layoutItem.findViewById(R.id.tvMail);
        viewHolder.tvTel = layoutItem.findViewById(R.id.tvPhoneNumber);
        viewHolder.img = layoutItem.findViewById(R.id.imgvPicture);

        viewHolder.tvNomPrenom.setText(liste.get(position).getNom());
        viewHolder.tvMail.setText(liste.get(position).getMail());
        viewHolder.tvTel.setText(liste.get(position).getTel());

        String filename = liste.get(position).getImagePath();
        Bitmap bmp = null;
        FileInputStream is = null;
        if(!filename.equals("")){
            try {
                is = contexte.getApplicationContext().openFileInput(filename);
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
            viewHolder.img.setImageBitmap(bmp);
        }else {
            if(liste.get(position).getSexe()!=null){
                if(liste.get(position).getSexe().equals(contexte.getResources().getString(R.string.btnMale)))viewHolder.img.setImageResource(contexte.getResources().getIdentifier("man","drawable", contexte.getPackageName()));
                else if(liste.get(position).getSexe().equals(contexte.getResources().getString(R.string.btnFemale)))viewHolder.img.setImageResource(contexte.getResources().getIdentifier("woman","drawable", contexte.getPackageName()));
            }
            else viewHolder.img.setImageResource(contexte.getResources().getIdentifier("person","drawable", contexte.getPackageName()));
        }
        return layoutItem;
    }
}
