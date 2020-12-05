package com.example.firebasekalkulator;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;


public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context Ctx;
    private FirebaseFirestore db;

    private List<Hitung> hitungList;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView IDHide, Var1, Var2, Operator, Result, Smd;
        Button Hapus;
        RelativeLayout index_hitung;
        MyViewHolder(View view) {
            super(view);
            Var1 = view.findViewById(R.id.Var1);
            Var2 = view.findViewById(R.id.Var2);
            Operator = view.findViewById(R.id.Operator);
            Result = view.findViewById(R.id.Result);
            Smd = view.findViewById(R.id.hsl);
            IDHide = view.findViewById(R.id.IDHide);
            index_hitung = view.findViewById(R.id.index_hitung);
            IDHide.setVisibility(View.INVISIBLE);
            Hapus = view.findViewById(R.id.Hapus);
        }
    }
    public Adapter(Context ctx, List<Hitung> hitungList) {
        Ctx = ctx;
        this.hitungList = hitungList;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        db = FirebaseFirestore.getInstance();
        final Hitung hitung = hitungList.get(position);
        holder.Var1.setText(hitung.getVar1());
        holder.Operator.setText(hitung.getOperator());
        holder.Var2.setText(hitung.getVar2());
        holder.Smd.setText("=");
        holder.Result.setText(hitung.getResult());
        holder.IDHide.setText(hitung.getID());
        holder.Hapus.setOnClickListener(v ->

                db.collection("data_hitung").document(hitung.getID())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(Ctx,MainActivity.class);
                            intent.putExtra("MsgDel", hitung.getID());
                            Ctx.startActivity(intent);


                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Intent intent = new Intent(Ctx,MainActivity.class);
                                intent.putExtra("MsgDel", "0");
                                Ctx.startActivity(intent);
                            }
                        })

                        );


    }


    @Override
    public int getItemCount() {
        return hitungList.size();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewrecycle_history, parent, false);
        return new MyViewHolder(itemView);
    }
}
