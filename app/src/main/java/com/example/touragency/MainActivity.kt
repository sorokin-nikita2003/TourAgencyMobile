package com.example.touragency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touragency.serverConnection.LoginRequest
import com.example.touragency.serverConnection.LoginResponse
import com.example.touragency.serverConnection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val RegistrBtn = findViewById<TextView>(R.id.RegistrBtn)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)

        RegistrBtn.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()


            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loginRequest = LoginRequest(username, password)

            RetrofitClient.api.login(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val token = loginResponse?.token
                        val roles = loginResponse?.roles

                        if (!token.isNullOrEmpty() && !roles.isNullOrEmpty()) {
                            val dbHelper = DatabaseHelper(this@MainActivity)
                            dbHelper.saveUser(username, password, ArrayList(roles).get(0))
                            // Переход на новую Activity с передачей токена и ролей
                            val intent = Intent(this@MainActivity, ToursActivity::class.java)
                            intent.putExtra("TOKEN", token)
                            intent.putExtra("USERNAME", username) // Передаем имя пользователя
                            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@MainActivity, "Login failed: Missing token or roles", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (response.message() == "Unauthorized") {
                            tvPasswordError.text = "Неправильный логин или пароль"
                        }
                        Toast.makeText(this@MainActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    t.message?.let { Log.e("LoginError", it) }
                    Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
