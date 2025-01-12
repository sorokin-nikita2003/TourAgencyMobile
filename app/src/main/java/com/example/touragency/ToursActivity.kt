package com.example.touragency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.Tour
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToursActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tours)

        token = intent.getStringExtra("TOKEN") ?: ""
        recyclerView = findViewById(R.id.recyclerViewTours)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAddTour = findViewById<Button>(R.id.btnAddTour)

        // Загружаем туры
        loadTours()

        btnAddTour.setOnClickListener {
            val intent = Intent(this@ToursActivity, AddTourActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivityForResult(intent, REQUEST_ADD_TOUR)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_TOUR && resultCode == RESULT_OK) {
            // Если тур был успешно добавлен, перезагружаем список
            loadTours()
        }
    }

    private fun loadTours() {
        RetrofitClient.api.getAllTours("Bearer $token").enqueue(object : Callback<List<Tour>> {
            override fun onResponse(call: Call<List<Tour>>, response: Response<List<Tour>>) {
                if (response.isSuccessful) {
                    val tours = response.body()
                    if (!tours.isNullOrEmpty()) {
                        recyclerView.adapter = TourAdapter(tours)
                    } else {
                        Toast.makeText(this@ToursActivity, "Нет доступных туров", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ToursActivity, "Ошибка загрузки туров: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Tour>>, t: Throwable) {
                Log.e("ToursError", t.message ?: "Неизвестная ошибка")
                Toast.makeText(this@ToursActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val REQUEST_ADD_TOUR = 1
    }
}
