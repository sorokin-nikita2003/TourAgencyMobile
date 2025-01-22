package com.example.touragency

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.serverConnection.OnOrderClickListener
import com.example.touragency.serverConnection.OnTourClickListener
import com.example.touragency.serverConnection.Order
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Locale

class OrderAdapter(private val context: Context, private val orders: List<Order>, private val listener: OnOrderClickListener, private val roles: ArrayList<String>?) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)

//        holder.orderNumber.text = order.id.toString()
//        holder.orderStatus.text = order.orderStatus
//        holder.paymentStatus.text = order.paymentStatus
//        holder.clientEmail.text = order.User.email
//        holder.purchaseDate.text = order.buyTime
//        holder.orderPrice.text = order.Price.toString()
//        holder.detailsButton.text = order

    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumber: TextView = itemView.findViewById(R.id.textOrderNumber)
        val orderStatus: TextView = itemView.findViewById(R.id.textOrderStatus)
        val paymentStatus: TextView = itemView.findViewById(R.id.textPaymentStatus)
        val clientEmail: TextView = itemView.findViewById(R.id.textClientEmail)
        val purchaseDate: TextView = itemView.findViewById(R.id.textPurchaseDate)
        val orderPrice: TextView = itemView.findViewById(R.id.textOrderPrice)
//        val detailsButton: TextView = itemView.findViewById(R.id.textDetailsButton)
        val confirmButton: TextView = itemView.findViewById(R.id.button2)
        val cancelButton: TextView = itemView.findViewById(R.id.button3)

        fun bind(order: Order) {
            if (roles != null && roles.contains("TourOperator")) {
                // Если пользователь с ролью TourOperator
                confirmButton.setText("Подтвердить")
                // Дополнительная логика для TourOperator
            } else {
                clientEmail.visibility = View.GONE
                confirmButton.setText("Оплатить")
            }

            // Установка данных для клиентского email
            if (order.user == null) {
                clientEmail.text = "Клиент: ${order.userId}" // Используем userId, если user == null
            } else {
                clientEmail.text = "Клиент: ${order.user.userName}" // Используем userName, если user != null
            }

            if (order.orderStatus != "Подтвержден") {
                paymentStatus.visibility = View.GONE
                purchaseDate.visibility = View.GONE
            }

            confirmButton.visibility = View.GONE
            cancelButton.visibility = View.GONE
            orderNumber.text = "Номер заказа: ${order.id}"
            orderStatus.text = "Статус заказа: ${order.orderStatus}"
            paymentStatus.text = "Статус оплаты: ${order.paymentStatus}"
            // Преобразуем buyTime в формат yyyy-MM-dd
            val formattedDate = formatDate(order.buyTime)
            if (order.orderStatus == "Подтвержден" && order.paymentStatus == "Ожидает оплаты") {
                purchaseDate.visibility = View.GONE
            }
            purchaseDate.text = "Дата покупки: $formattedDate"
            orderPrice.text = "Цена заказа: ${order.price}₽"

            if ((order.orderStatus == "Ожидает подтверждения" && roles != null && roles.contains("TourOperator")) || (order.paymentStatus == "Ожидает оплаты" && roles != null && roles.contains("User"))) {
                confirmButton.visibility = View.VISIBLE
                cancelButton.visibility = View.VISIBLE
            }
            if (order.orderStatus == "Отменён") {
                confirmButton.visibility = View.GONE
                cancelButton.visibility = View.GONE
            }

            confirmButton.setOnClickListener {
                val orderId = order.id
                listener.onOrderConfirm(orderId)
            }
            cancelButton.setOnClickListener {
                val orderId = order.id
                listener.onOrderCancel(orderId)
            }
        }
        // Функция для преобразования даты в формат yyyy-MM-dd
        private fun formatDate(dateString: String): String {
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault())
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                return outputFormat.format(date)
            } catch (e: Exception) {
                e.printStackTrace()
                return "Неизвестная дата"
            }
        }
    }
}