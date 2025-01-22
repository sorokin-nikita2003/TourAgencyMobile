package com.example.touragency

import android.app.AlertDialog
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.serverConnection.OnTourClickListener
import com.example.touragency.serverConnection.Tour
import java.util.ArrayList

class TourAdapter(private val tours: List<Tour>, private val roles: ArrayList<String>?, private val listener: OnTourClickListener) : RecyclerView.Adapter<TourAdapter.TourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = tours[position]

        holder.tvTourName.text = tour.tourName
        holder.tvCountry.text = "Страна: ${tour.country}"
        holder.tvDescription.text = tour.description
        holder.tvDateStart.text = "Дата начала: ${tour.dateStart.split("T")[0]}"
        holder.tvPrice.text = "Цена: ${tour.price}₽"

        var discountedPrice: Int? = null
        if (tour.hotTour) {
            discountedPrice = tour.price * 4/5 // Скидка 20%
            // Зачеркиваем цену
            holder.tvPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvDiscountedPrice.text = "Горящий тур: ${discountedPrice}₽"
            holder.tvDiscountedPrice.visibility = View.VISIBLE
        } else {
            // Убираем зачеркивание цены
//            holder.tvPrice.paintFlags = holder.tvPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvDiscountedPrice.visibility = View.GONE
        }

        holder.btnOrder.setVisibility(INVISIBLE)
        holder.btnDelete.setVisibility(INVISIBLE)
        holder.btnEdit.setVisibility(INVISIBLE)

        if (roles != null && !roles.contains("TourOperator")) {
            // Если пользователь с ролью TourOperator
            holder.btnOrder.setVisibility(VISIBLE)
            // Дополнительная логика для TourOperator
        } else {
            holder.btnDelete.setVisibility(VISIBLE)
            holder.btnEdit.setVisibility(VISIBLE)
        }

        holder.btnOrder.setOnClickListener {
            val tourId = tour.id
            val tourName = holder.tvTourName
            var pricePerPerson: Int? = null
            if (tour.hotTour) {
                pricePerPerson = tour.price
            }
            else {
                pricePerPerson = tour.price * 4/5
            }
//            val priceWithoutCurrency = pricePerPerson.dropLast(1) // Удаляем символ "₽"
//            val priceAsInt = priceWithoutCurrency.toInt() // Преобразуем строку в Int
            listener.onTourSubmit(holder.tvTourName.text.toString(), pricePerPerson, tourId)
        }

        holder.btnDelete.setOnClickListener {
            val tourId = tour.id
            listener.onTourDelete(tourId)
        }
        holder.btnEdit.setOnClickListener {
            val tourId = tour.id
            listener.onTourEdit(tour)
        }
    }

    override fun getItemCount(): Int = tours.size

    class TourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTourName: TextView = itemView.findViewById(R.id.tvTourName)
        val tvCountry: TextView = itemView.findViewById(R.id.tvCountry)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        val tvDateStart: TextView = itemView.findViewById(R.id.tvDateStart)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvDiscountedPrice: TextView = itemView.findViewById(R.id.tvDiscountedPrice)
        val btnOrder: Button = itemView.findViewById(R.id.btnOrder)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
    }
}
