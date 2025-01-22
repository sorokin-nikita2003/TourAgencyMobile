package com.example.touragency

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touragency.serverConnection.Basket
import com.example.touragency.serverConnection.OrderData
import com.example.touragency.serverConnection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class BasketActivity : AppCompatActivity() {

    private var peopleCount = 1 // Начальное количество человек
    private var pricePerPerson = 0 // Цена за одного человека
    private var tourId = 0 // Цена за одного человека
    private var username: String? = null // Добавляем глобальную переменную username
    private var roles: List<String>? = null // Для ролей

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)

        // Получение данных из Intent
        val tourName = intent.getStringExtra("TOUR_NAME") ?: "Неизвестный тур"
        pricePerPerson = intent.getIntExtra("PRICE_PER_PERSON", 0)
        tourId = intent.getIntExtra("TOUR_ID", 0)
        username = intent.getStringExtra("USERNAME") ?: "Неизвестный пользователь"
        roles = intent.getStringArrayListExtra("ROLES")


        // Найти элементы интерфейса
        val tvTourName = findViewById<TextView>(R.id.tvTourName)
        val tvPeopleCount = findViewById<TextView>(R.id.tvPeopleCount)
        val tvTotalPrice = findViewById<TextView>(R.id.tvTotalPrice)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnCancel = findViewById<Button>(R.id.btnCancel)

        // Установить начальные значения
        tvTourName.text = tourName
        updateTotalPrice(tvTotalPrice)

        btnCancel.setOnClickListener {
            finish()
        }

        // Обработчик кнопки "минус"
        btnMinus.setOnClickListener {
            if (peopleCount > 1) {
                peopleCount--
                tvPeopleCount.text = peopleCount.toString()
                updateTotalPrice(tvTotalPrice)
            }
        }

        // Обработчик кнопки "плюс"
        btnPlus.setOnClickListener {
            peopleCount++
            tvPeopleCount.text = peopleCount.toString()
            updateTotalPrice(tvTotalPrice)
        }

        // Обработчик кнопки "Отправить заказ"
        btnSubmit.setOnClickListener {
            val totalPrice = peopleCount * pricePerPerson

            // Форматируем текущую дату в формате yyyy-MM-dd
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = format.format(Date()) // Получаем текущую дату в строковом формате
            // Отправляем запрос на сервер
            sendOrder(username.toString(), currentDate, totalPrice)
        }
    }

    // Функция для обновления итоговой цены
    private fun updateTotalPrice(tvTotalPrice: TextView) {
        val totalPrice = peopleCount * pricePerPerson
        tvTotalPrice.text = "Итоговая цена - ${totalPrice}₽"
    }

    // Функция для отправки заказа на сервер
    private fun sendOrder(userId: String, buyTime: String, price: Int) {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val basket = Basket(
            id = tourId,
            UserId = username.toString(),
            PersonsCount = peopleCount,
        )

        RetrofitClient.api.createBasket(basket).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@BasketActivity, "Корзина успешно оформлен!", Toast.LENGTH_SHORT).show()

                    val orderData = OrderData(
                        UserId = userId,
                        buyTime = format.format(Date()),
                        Price = price,
                    )

                    RetrofitClient.api.createOrder(orderData).enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@BasketActivity, "Заказ успешно оформлен!", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Toast.makeText(this@BasketActivity, "Ошибка при оформлении заказа: ${response.message()}", Toast.LENGTH_SHORT).show()
                                Log.e("не формируется заказ", userId)
                                Log.e("не формируется заказ", buyTime.toString())
                                Log.e("не формируется заказ", price.toString())

                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Log.e("BasketActivity", "Ошибка при отправке заказа: ${t.message}")
                            Toast.makeText(this@BasketActivity, "Не удалось отправить заказ", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this@BasketActivity, "Ошибка при оформлении заказа: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("не формируется заказ", userId)
                    Log.e("не формируется заказ", buyTime.toString())
                    Log.e("не формируется заказ", price.toString())

                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BasketActivity", "Ошибка при отправке заказа: ${t.message}")
                Toast.makeText(this@BasketActivity, "Не удалось отправить заказ", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun closeWindow() {
//        val intent = Intent(this, ToursActivity::class.java)
//        intent.putExtra("USERNAME", username) // Передаем имя пользователя
//        intent.putStringArrayListExtra("ROLES", ArrayList(roles))
//        startActivity(intent)
//        finish()
    }
}