package com.example.touragency

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.Tour
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tour)

        val token = intent.getStringExtra("TOKEN")
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etTourName = findViewById<EditText>(R.id.etTourName)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etCountry = findViewById<EditText>(R.id.etCountry)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etDateStart = findViewById<EditText>(R.id.etDateStart)

        btnSubmit.setOnClickListener {
            val tour = Tour(
                id = 0, // ID генерируется сервером
                tourName = etTourName.text.toString(),
                price = etPrice.text.toString().toIntOrNull() ?: 0,
                country = etCountry.text.toString(),
                description = etDescription.text.toString(),
                dateStart = etDateStart.text.toString(),
                hotTour = true,
                deleted = false,
//                info = "rgreherh"
            )

            RetrofitClient.api.addTour(tour).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddTourActivity, "Тур успешно добавлен!", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Устанавливаем результат успешного добавления
                        finish() // Закрываем Activity
                    } else {
                        Toast.makeText(this@AddTourActivity, "Ошибка при добавлении тура: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@AddTourActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
