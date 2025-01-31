package com.example.touragency

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPasswordConfirm = findViewById<EditText>(R.id.etPasswordConfirm)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnCancel = findViewById<TextView>(R.id.btnCancel)
        val tvPasswordError = findViewById<TextView>(R.id.tvPasswordError)

        btnCancel.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val passwordConfirm = etPasswordConfirm.text.toString().trim()

            // Проверка на пустые поля
            if (email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на совпадение паролей
            if (password != passwordConfirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Создаем запрос на регистрацию
            val registerRequest = RegisterRequest(email, password, passwordConfirm)

            // Отправка данных на сервер
            RetrofitClient.api.registerClient(registerRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Registration successful", Toast.LENGTH_SHORT).show()
                        finish() // Закрываем текущую активность после успешной регистрации
                    } else {
                        // Если сервер возвращает ошибку, проверяем её
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val errorMessage = when {
                                errorBody.contains("PasswordRequiresUpper") -> "Password must contain at least one uppercase letter."
                                errorBody.contains("PasswordRequiresNonAlphanumeric") -> "Passwords must have at least one non alphanumeric character."
                                errorBody.contains("PasswordTooShort") -> "Passwords must be at least 6 characters."
                                errorBody.contains("PasswordRequiresLower") -> "Passwords must have at least one lowercase ('a'-'z')."
                                errorBody.contains("PasswordRequiresDigit") -> "Passwords must have at least one digit ('0'-'9')."
                                else -> "An error occurred. Please try again."
                            }
                            // Отображаем сообщение об ошибке в TextView
                            tvPasswordError.text = errorMessage
                        }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}