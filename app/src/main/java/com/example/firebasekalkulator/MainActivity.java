package com.example.firebasekalkulator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "MainActivity";
    private List<Hitung> hitungList = new ArrayList<>();
    private Adapter mAdapter;

    android.widget.RadioButton RadioButton;
    TextView Hasil;
    EditText Input1, Input2;
    Button BtnHtg,btn_clear;
    RadioGroup Group;
    RadioButton btn_tambah, btn_kurang, btn_kali, btn_bagi;

    private RecyclerView.LayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String MsgDel;
        MsgDel = intent.getStringExtra("MsgDel");
        if(MsgDel != null){
            if(MsgDel == "0") {
                Toast.makeText(MainActivity.this, "Data Gagal Dihapus", Toast.LENGTH_SHORT).show();
                MsgDel = null;
            }else{
                Toast.makeText(MainActivity.this, "Data Id " + MsgDel + " Berhasil Dihapus", Toast.LENGTH_SHORT).show();
                MsgDel = null;
            }
        }

        db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.MyRecyclerview);
        mAdapter = new Adapter(this,hitungList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        DataHitung();

        Input1 = findViewById(R.id.Input1);
        Input2 = findViewById(R.id.Input2);
        btn_tambah = findViewById(R.id.btn_tambah);
        btn_kurang = findViewById(R.id.btn_kurang);
        btn_kali = findViewById(R.id.btn_kali);
        btn_bagi = findViewById(R.id.btn_bagi);
        BtnHtg = findViewById(R.id.btn_hitung);
        Hasil = findViewById(R.id.Hasil);
        btn_clear = findViewById(R.id.btn_clear);
        Group = findViewById(R.id.radioGroup);
        btn_clear.setOnClickListener(v -> {

            Input1.setText("");
            Input2.setText("");

            DataHitung();
                });
        BtnHtg.setOnClickListener(v -> {

            if(btn_tambah.isChecked())
            {
                double pertama = Double.parseDouble(Input1.getText().toString());
                double kedua = Double.parseDouble(Input2.getText().toString());
                double res = pertama+kedua;

                Hasil.setText(String.valueOf(res));
            }
            else if(btn_kurang.isChecked())
            {
                double pertama = Double.parseDouble(Input1.getText().toString());
                double kedua = Double.parseDouble(Input2.getText().toString());
                double res = pertama-kedua;

                Hasil.setText(String.valueOf(res));
            }
            else if(btn_kali.isChecked())
            {
                double pertama = Double.parseDouble(Input1.getText().toString());
                double kedua = Double.parseDouble(Input2.getText().toString());
                double res = pertama*kedua;

                Hasil.setText(String.valueOf(res));
            }
            else if(btn_bagi.isChecked())
            {
                double pertama = Double.parseDouble(Input1.getText().toString());
                double kedua = Double.parseDouble(Input2.getText().toString());
                double res = pertama/kedua;

                Hasil.setText(String.valueOf(res));
            }
            else
            {
                Log.w(TAG,"salah");
            }

            Double Result;
            String Operand;
            int RadioId = Group.getCheckedRadioButtonId();
            RadioButton = findViewById(RadioId);
            Toast.makeText(MainActivity.this, "Data Sudah Masuk", Toast.LENGTH_SHORT).show();

            if (RadioButton.getText().equals("Bagi")) {
                Result = Double.parseDouble(Input1.getText().toString()) / Double.parseDouble(Input2.getText().toString());
                Operand = "/";
            } else if (RadioButton.getText().equals("Kurang")) {
                Result = Double.parseDouble(Input1.getText().toString()) - Double.parseDouble(Input2.getText().toString());
                Operand = "-";
            } else if (RadioButton.getText().equals("Kali")) {
                Result = Double.parseDouble(Input1.getText().toString()) * Double.parseDouble(Input2.getText().toString());
                Operand = "*";
            } else {
                Result = Double.parseDouble(Input1.getText().toString()) + Double.parseDouble(Input2.getText().toString());
                Operand = "+";
            }

            Map<String, Object> hitung = new HashMap<>();
            hitung.put("Var1", Input1.getText().toString());
            hitung.put("Var2", Input2.getText().toString());
            hitung.put("Operator", Operand);
            hitung.put("Result", Result.toString());

            db.collection("data_hitung")
                    .add(hitung)
                    .addOnSuccessListener(OnSuccessListener -> {
                        Toast.makeText(getBaseContext(),
                                "Input Data Berhasil", //documentReference.getId()
                                Toast.LENGTH_SHORT).show();
                        // Lihat Data (setelah sukses push data)
                        Input1.setText("0");
                        Input2.setText("0");
                        //Group.clearCheck();
                        DataHitung();

                    })
                    .addOnFailureListener(e ->
                            Log.e("firebase-error", e.getMessage()));
            DataHitung();
        });

    }

    private void DataHitung() {
        db.collection("data_hitung")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        hitungList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Hitung hitung = new Hitung(document.getId(), document.getData().get("Var1").toString(), document.getData().get("Var2").toString(), document.getData().get("Operator").toString(), document.getData().get("Result").toString());
                            hitungList.add(hitung);
                            mAdapter.notifyDataSetChanged();
                        }


                    } else {
                        Log.w(TAG, "Error", task.getException());
                    }
                });
    }
}