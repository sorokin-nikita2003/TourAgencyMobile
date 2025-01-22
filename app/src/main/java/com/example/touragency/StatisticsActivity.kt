package com.example.touragency

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.touragency.serverConnection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatisticsActivity : AppCompatActivity() {
    private lateinit var token: String
    private var roles: List<String>? = null // Для ролей

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        token = intent.getStringExtra("TOKEN") ?: ""
        roles = intent.getStringArrayListExtra("ROLES")

        val btnViewTours = findViewById<Button>(R.id.btnViewTours)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders)
        val btnExit = findViewById<Button>(R.id.btnExit)

        btnViewTours.setOnClickListener {
            val intent = Intent(this@StatisticsActivity, ToursActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }
        btnViewOrders.setOnClickListener {
            val intent = Intent(this@StatisticsActivity, OrdersActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }
        btnExit.setOnClickListener{
            val intent = Intent(this@StatisticsActivity, MainActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivity(intent)
            finish()
        }

        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        // Выполняем GET-запрос
        RetrofitClient.api.getStatistics().enqueue(object : Callback<Map<String, Int>> {
            override fun onResponse(call: Call<Map<String, Int>>, response: Response<Map<String, Int>>) {
                if (response.isSuccessful) {
                    val stats = response.body()
                    if (stats != null) {
                        populateTable(stats, tableLayout)
                    } else {
                        Toast.makeText(this@StatisticsActivity, "Данные отсутствуют", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@StatisticsActivity, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Int>>, t: Throwable) {
                Toast.makeText(this@StatisticsActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Заполнение таблицы данными
    private fun populateTable(stats: Map<String, Int>, tableLayout: TableLayout) {
        // Добавляем шапку таблицы
        val headerRow = TableRow(this).apply {
            setPadding(8, 8, 8, 8)
        }

        val headerCountry = TextView(this).apply {
            text = "Страна"
            textSize = 24f
            setPadding(16, 8, 16, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val headerCount = TextView(this).apply {
            text = "Количество человек"
            textSize = 24f
            setPadding(16, 8, 16, 8)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        headerRow.addView(headerCountry)
        headerRow.addView(headerCount)

        // Добавляем шапку в таблицу
        tableLayout.addView(headerRow)

        // Добавляем данные в таблицу
        for ((country, count) in stats) {
            val row = TableRow(this).apply {
                setPadding(8, 8, 8, 8)
            }

            val countryTextView = TextView(this).apply {
                text = country.trim()
                textSize = 24f
                setPadding(16, 8, 16, 8)
            }

            val countTextView = TextView(this).apply {
                text = count.toString()
                textSize = 24f
                setPadding(16, 8, 16, 8)
            }

            row.addView(countryTextView)
            row.addView(countTextView)

            tableLayout.addView(row)
        }
    }
}