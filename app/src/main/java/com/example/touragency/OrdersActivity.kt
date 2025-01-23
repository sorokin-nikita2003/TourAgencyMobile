package com.example.touragency

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.serverConnection.OnOrderClickListener
import com.example.touragency.serverConnection.Order
import com.example.touragency.serverConnection.OrderId
import com.example.touragency.serverConnection.RetrofitClient
import com.example.touragency.serverConnection.Tour
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class OrdersActivity : AppCompatActivity(), OnOrderClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var token: String
    private var roles: List<String>? = null // Для ролей

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

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

        token = intent.getStringExtra("TOKEN") ?: ""
        roles = intent.getStringArrayListExtra("ROLES")

        recyclerView = findViewById(R.id.recyclerViewOrders)
        val btnViewTours = findViewById<Button>(R.id.btnViewTours)
        val btnStat = findViewById<Button>(R.id.btnStat)
        val btnExit = findViewById<Button>(R.id.btnExit)
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnStat.visibility = View.GONE
        if (roles != null && (roles as ArrayList<String>).contains("TourOperator")) {
            // Если пользователь с ролью TourOperator
            btnStat.visibility = View.VISIBLE
            // Дополнительная логика для TourOperator
        }

        loadOrders(roles as ArrayList<String>?)

        btnViewTours.setOnClickListener {
            val intent = Intent(this@OrdersActivity, ToursActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }

        btnStat.setOnClickListener {
            val intent = Intent(this@OrdersActivity, StatisticsActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }

        btnExit.setOnClickListener{
            dbHelper.deleteUser()
            val intent = Intent(this@OrdersActivity, MainActivity::class.java)
            intent.putExtra("TOKEN", token)
            startActivity(intent)
            finish()
        }
    }

    private fun loadOrders(roles: ArrayList<String>?) {
        RetrofitClient.api.getAllOrders().enqueue(object : Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                if (response.isSuccessful) {
                    val orders = response.body()
                    if (!orders.isNullOrEmpty()) {
                        recyclerView.adapter = OrderAdapter(this@OrdersActivity, orders, this@OrdersActivity, roles)
                    } else {
                        Toast.makeText(this@OrdersActivity, "Нет доступных заказов", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@OrdersActivity, "Ошибка загрузки заказов: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                Log.e("OrdersError", t.message ?: "Неизвестная ошибка")
                Toast.makeText(this@OrdersActivity, "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onOrderConfirm(orderId: Int) {
        val order = OrderId(
            id = orderId
        )
        if (roles != null && (roles as ArrayList<String>).contains("TourOperator")) {
            RetrofitClient.api.submitOrder(order).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        recreate()
                    } else {
                        Toast.makeText(this@OrdersActivity, "Ошибка при подтверждении заказа: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        } else {
            val intent = Intent(this@OrdersActivity, PaymentActivity::class.java)
            intent.putExtra("ORDER_ID", orderId)
            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
            startActivity(intent)
            finish()
        }
    }

    override fun onOrderCancel(orderId: Int) {
        val order = OrderId(
            id = orderId
        )
        RetrofitClient.api.cancelOrder(order).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    recreate()
                } else {
                    Toast.makeText(this@OrdersActivity, "Ошибка при отмене заказа: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}
