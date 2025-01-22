package com.example.touragency

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DatePickerDialog
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.Tour
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AddTourActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tour)

        val roles = intent.getStringArrayListExtra("ROLES")
        val token = intent.getStringExtra("TOKEN")

        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etTourName = findViewById<EditText>(R.id.etTourName)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etCountry = findViewById<EditText>(R.id.etCountry)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val etDateStart = findViewById<EditText>(R.id.etDateStart)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val cbIsHotTour = findViewById<CheckBox>(R.id.cbIsHotTour)

        // Месяц начиная с нуля. Для отображения добавляем 1.
        datePicker.init(2024, 2, 1) { view, year, monthOfYear, dayOfMonth ->
            // Отсчет месяцев начинается с нуля. Для отображения добавляем 1.
            etDateStart.setText("${year}-${monthOfYear + 1}-${dayOfMonth}")
        }

        val editTour_id: String? = intent.getStringExtra("TOUR_ID")
        editTour_id?.let {
            etTourName.setText(intent.getStringExtra("EDIT_NAME"))
            etPrice.setText(intent.getStringExtra("TOUR_PRICE"))
            etCountry.setText(intent.getStringExtra("EDIT_COUNTRY"))
            etDescription.setText(intent.getStringExtra("EDIT_DESCRIPTION"))
            val dateStart = intent.getStringExtra("EDIT_DATESTART")
            etDateStart.setText(dateStart)
            cbIsHotTour.isChecked = intent.getStringExtra("EDIT_HOTTOUR").toBoolean()

            // Устанавливаем дату в DatePicker
            val dateParts1 = dateStart?.split("T")?.get(0) ?: "Дата не указана"
            val dateParts = dateParts1.split("-")
            if (dateParts != null) {
                if (dateParts.size == 3) {
                    datePicker.updateDate(
                        dateParts[0].toInt(),
                        dateParts[1].toInt() - 1, // Месяцы начинаются с 0
                        dateParts[2].toInt()
                    )
                }
            }

            // Меняем текст кнопки
            btnSubmit.text = "Сохранить"
        }


        btnSubmit.setOnClickListener {
            val tour = Tour(
                id = editTour_id?.toInt() ?: 0, // Если редактируем, оставляем ID
                tourName = etTourName.text.toString(),
                price = etPrice.text.toString().toIntOrNull() ?: 0,
                country = etCountry.text.toString(),
                description = etDescription.text.toString(),
                dateStart = etDateStart.text.toString(),
                hotTour = cbIsHotTour.isChecked,
                deleted = false,
//                info = "rgreherh"
            )

            if (editTour_id == null) {
                RetrofitClient.api.addTour(tour).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@AddTourActivity,
                                "Тур успешно добавлен!",
                                Toast.LENGTH_SHORT
                            ).show()
                            setResult(RESULT_OK) // Устанавливаем результат успешного добавления
                            finish() // Закрываем Activity
                        } else {
                            Toast.makeText(
                                this@AddTourActivity,
                                "Ошибка при добавлении тура: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(
                            this@AddTourActivity,
                            "Ошибка сети: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            else {
                // Редактирование тура
                RetrofitClient.api.editTour(tour).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@AddTourActivity, "Тур успешно обновлен!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@AddTourActivity, ToursActivity::class.java)
                            intent.putExtra("TOKEN", token)
                            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@AddTourActivity, "Ошибка: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@AddTourActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
