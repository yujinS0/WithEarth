package com.example.withearth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class StoreActivityConfirmOrder extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;
    private Button confirmOrderBtn;
    private FirebaseAuth auth;
    private String point;
    private int orderNum;
    private TextView totalPricetv;
    String stOrderNum;

    double realPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_store_confirm_order);

        String totalPrice = getIntent().getStringExtra("total");
        stOrderNum = getIntent().getStringExtra("ordernum");
        orderNum = Integer.parseInt(stOrderNum);




        confirmOrderBtn = (Button) findViewById(R.id.confirm_final_order_btn);
        nameEditText = (EditText) findViewById(R.id.shippment_name);
        phoneEditText = (EditText) findViewById(R.id.shippment_phone_number);
        addressEditText = (EditText) findViewById(R.id.shippment_address);

        totalPricetv = (TextView) findViewById(R.id.total_price_tv);
        totalPricetv.setText(totalPrice);

        // 사용자 ordernum 가져오기


        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();

                //구매 금액의 5% 포인트 적립
                DatabaseReference totalRef = FirebaseDatabase.getInstance().getReference().child("Orders");
                totalRef = totalRef.child(auth.getCurrentUser().getUid()).child(String.valueOf(orderNum)).child("total");
                totalRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        point = snapshot.getValue(String.class);
                        int totalint = Integer.parseInt(point);
                        double finalpoint = Math.floor(totalint * 0.05);


                        DatabaseReference pointRef = FirebaseDatabase.getInstance().getReference().child("Point")
                                .child(auth.getCurrentUser().getUid());
                        pointRef.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (snapshot.hasChild("point")){
                                    String totalPoint = snapshot.child("point").getValue(String.class);
                                    double doubletotalPoint = Double.parseDouble(totalPoint);

                                    realPoint = doubletotalPoint + finalpoint;
                                    point = String.valueOf(realPoint);

                                    HashMap<String, Object> pointMap = new HashMap<>();
                                    pointMap.put("point", point);
                                    pointRef.updateChildren(pointMap);


                                }

                                else {
                                    point = String.valueOf(finalpoint);
                                    HashMap<String, Object> pointMap = new HashMap<>();
                                    pointMap.put("point", point);
                                    pointRef.updateChildren(pointMap);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });



            }
        });

    }

    //배송지 정보 입력, 빈칸일 경우 toast 출력
    private void Check() {
        if(TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phoneEditText.getText().toString())){
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(this, "주소를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        else
            ConfirmOrder();


    }

    //주문 정보 저장 realtime database 이용, Orders 밑에 회원 ID로 저장
    private void ConfirmOrder() {

        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(auth.getCurrentUser().getUid()).child(String.valueOf(orderNum));
        HashMap<String, Object> ordersMap = new HashMap<>();
        ordersMap.put("name", nameEditText.getText().toString());
        ordersMap.put("phone", phoneEditText.getText().toString());
        ordersMap.put("address", addressEditText.getText().toString());

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){

                    Intent intent = new Intent(StoreActivityConfirmOrder.this, StoreActivityOrderSuccess.class);
                    intent.putExtra("ordernum", orderNum);
                    startActivity(intent);
                    finish();


                }
            }
        });


    }
}