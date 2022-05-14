package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    EditText edtUser,edtPass; //Biến điều khiển EditText
    Button   btnLogin, btnRegister; //Biến điều khiển Button
    static   String userNameLogined;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Khởi tạo các biến điều khiển tương ứng trong layout
        edtUser = (EditText)findViewById(R.id.edtUser);
        edtPass = (EditText)findViewById(R.id.edtPass);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button)findViewById(R.id.btnRegister);

        // Cài đặt sự kiện Click cho Button Login
        btnLogin.setOnClickListener(new CButtonLogin());
        // Cài đặt sự kiện Click cho Button Register
        btnRegister.setOnClickListener(new CButtonRegister());
    }

    public class CButtonLogin  implements View.OnClickListener {


        @Override
        public void onClick(View v) {//Hàm sử lý sự kiện click button login
            //Nhận nội dung từ biến điều khiển
            String szUser = edtUser.getText().toString();
            String szPass = edtPass.getText().toString();

            //Kiểm tra tính hợp lệ của tài khoản, mật khẩu
            if (szUser.length() <= 3 || szPass.length() < 6){
                Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu không hợp lệ!",Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                //Gọi hàm dịch vụ Login
                apiLogin(szUser,szPass);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class CButtonRegister implements View.OnClickListener {
        @Override
        public void onClick(View v) {//Hàm sử lý sự kiện click button register
            Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(i);
        }
    }


    //Hàm dịch vụ Login
    void apiLogin(String user, String pass) throws IOException {
        OkHttpClient client = new OkHttpClient();
        userNameLogined = user;
        String json = "{\"username\":\"" + user + "\",\"password\":\"" + pass +"\"}";
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url("http://192.168.1.5:3080/login")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("K40",response.body().string());
                if (!response.isSuccessful()){
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Tài khoản hoặc mật khẩu không chính xác.",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                Intent intent = new Intent(getApplicationContext(),UserActivity.class);
                startActivity(intent);

            }
        });
    }
}
