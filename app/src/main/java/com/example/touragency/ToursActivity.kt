package com.example.touragency

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.serverConnection.OnTourClickListener
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.Tour
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ToursActivity : AppCompatActivity(), OnTourClickListener {
    private lateinit var recyclerView: RecyclerView
    private var username: String? = null // Добавляем глобальную переменную username

    private lateinit var token: String
    private var roles: List<String>? = null // Для ролей

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tours)

        val dbHelper = DatabaseHelper(this)
        val user = dbHelper.getCurrentUser()

        if (user != null) {
            // Отображаем имя и роль пользователя
            val tvUserName = findViewById<TextView>(R.id.tvUserName)
            val tvUserRole = findViewById<TextView>(R.id.tvUserRole)

            tvUserName.text = "Имя: ${user.username}"
            tvUserRole.text = "Роль: ${user.roles}"
        } else {
            // Если пользователь не авторизован
            Toast.makeText(this, "Пользователь не авторизован", Toast.LENGTH_SHORT).show()
        }

        token = intent.getStringExtra("TOKEN") ?: run {
            Log.e("ToursActivity", "Token is null or missing")
            finish() // Завершаем Activity, если токен отсутствует
            return
        }

        username = intent.getStringExtra("USERNAME") // Инициализируем глобальную переменную username

        // Извлекаем токен и роли
        roles = intent.getStringArrayListExtra("ROLES")
//        val roles = arrayOf("TourOperator")

        recyclerView = findViewById(R.id.recyclerViewTours)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnAddTour = findViewById<Button>(R.id.btnAddTour)
        val btnViewOrders = findViewById<Button>(R.id.btnViewOrders) // Кнопка для открытия заказов
        val btnStat = findViewById<Button>(R.id.btnStat) // Кнопка для открытия заказов
        val btnExit = findViewById<Button>(R.id.btnExit) // Кнопка для открытия заказов

        btnAddTour.visibility = View.GONE
        btnStat.visibility = View.GONE

        if (roles != null && (roles as ArrayList<String>).contains("TourOperator")) {
            // Если пользователь с ролью TourOperator
            btnAddTour.visibility = View.VISIBLE
            btnStat.visibility = View.VISIBLE
            btnViewOrders.setText("Заказы клиентов")
            // Дополнительная логика для TourOperator
        } else {
            btnViewOrders.setText("Мои заказы")
        }

        // Загружаем туры
        loadTours(roles as ArrayList<String>?)

        btnExit.setOnClickListener{
//            val dbHelper = DatabaseHelper(this)
            dbHelper.deleteUser()
            val intent = Intent(this@ToursActivity, MainActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivity(intent)

            finish()
        }

        btnAddTour.setOnClickListener {
            val intent = Intent(this@ToursActivity, AddTourActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivityForResult(intent, REQUEST_ADD_TOUR)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
        }

        btnStat.setOnClickListener {
            val intent = Intent(this@ToursActivity, StatisticsActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putExtra("USERNAME", username) // Передаем имя пользователя
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }

        // Обработчик для открытия страницы заказов
        btnViewOrders.setOnClickListener {
            val intent = Intent(this@ToursActivity, OrdersActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_TOUR && resultCode == RESULT_OK) {
            // Если тур был успешно добавлен, перезагружаем список
            val roles = intent.getStringArrayListExtra("ROLES")
            loadTours(roles)
        }
    }

    private fun loadTours(roles: ArrayList<String>?) {
        RetrofitClient.api.getAllTours().enqueue(object : Callback<List<Tour>> {
            override fun onResponse(call: Call<List<Tour>>, response: Response<List<Tour>>) {
                if (response.isSuccessful) {
                    val tours = response.body()
                    if (!tours.isNullOrEmpty()) {
                        recyclerView.adapter = TourAdapter(tours, roles, this@ToursActivity)
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

    override fun onTourSubmit(tourName: String, pricePerPerson: Int, tourId: Int) {
        Log.e("ccccccccc", "ccccccccccc")
        val intent = Intent(this, BasketActivity::class.java)
        intent.putExtra("TOUR_NAME", tourName)
        intent.putExtra("PRICE_PER_PERSON", pricePerPerson)
        intent.putExtra("USERNAME", username) // Передаем имя пользователя
        intent.putExtra("TOUR_ID", tourId) // Передаем имя пользователя
        intent.putStringArrayListExtra("ROLES", ArrayList(roles))
        startActivity(intent)
//        finish()
    }
    override fun onTourDelete(tourId: Int) {
        // Создание диалога подтверждения удаления
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Вы действительно хотите удалить тур?")
            .setCancelable(false)
            .setPositiveButton("Да") { dialog, id ->
                // Если "Да", выполняем удаление тура
                RetrofitClient.api.deleteTour(tourId).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            recreate() // Обновляем Activity после успешного удаления
                        } else {
                            Toast.makeText(
                                this@ToursActivity,
                                "Ошибка удаления тура: ${response.message()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("ToursError", t.message ?: "Неизвестная ошибка")
                        Toast.makeText(
                            this@ToursActivity,
                            "Ошибка: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
            .setNegativeButton("Нет") { dialog, id ->
                // Если "Нет", закрываем диалог
                dialog.cancel()
            }

        val alert = dialogBuilder.create()
        // Устанавливаем размеры для диалога
        alert.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        alert.show()
    }

    override fun onTourEdit(tour: Tour) {
        val intent = Intent(this, AddTourActivity::class.java)
        startActivityForResult(intent, ADD_TOUR_REQUEST_CODE)
        intent.putExtra("TOUR_ID", tour.id.toString()) // Tour должен быть Parcelable или Serializable
        intent.putExtra("TOUR_PRICE", tour.price.toString()) // Tour должен быть Parcelable или Serializable
        intent.putExtra("EDIT_NAME", tour.tourName) // Tour должен быть Parcelable или Serializable
        intent.putExtra("EDIT_COUNTRY", tour.country) // Tour должен быть Parcelable или Serializable
        intent.putExtra("EDIT_DESCRIPTION", tour.description) // Tour должен быть Parcelable или Serializable
        intent.putExtra("EDIT_HOTTOUR", tour.hotTour) // Tour должен быть Parcelable или Serializable
        intent.putExtra("EDIT_DATESTART", tour.dateStart) // Tour должен быть Parcelable или Serializable
        intent.putExtra("TOKEN", token)
        intent.putStringArrayListExtra("ROLES", ArrayList(roles))
        startActivity(intent)
    }

    companion object {
        private const val REQUEST_ADD_TOUR = 1
        const val ADD_TOUR_REQUEST_CODE = 1001
    }
}
