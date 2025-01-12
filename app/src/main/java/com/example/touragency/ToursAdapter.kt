package com.example.touragency

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.touragency.R
import com.example.touragency.serverConnection.Tour

class TourAdapter(private val tours: List<Tour>) : RecyclerView.Adapter<TourAdapter.TourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tour, parent, false)
        return TourViewHolder(view)
    }

    override fun onBindViewHolder(holder: TourViewHolder, position: Int) {
        val tour = tours[position]

        holder.tvTourName.text = tour.tourName
        holder.tvCountry.text = tour.country
        holder.tvDescription.text = tour.description
        holder.tvDateStart.text = "Дата начала: ${tour.dateStart}"

        if (tour.hotTour) {
            val discountedPrice = (tour.price * 0.8).toInt() // Скидка 20%
            holder.tvPrice.text = "${tour.price}₽"
            holder.tvDiscountedPrice.text = "Горящий тур: ${discountedPrice}₽"
            holder.tvDiscountedPrice.visibility = View.VISIBLE
        } else {
            holder.tvPrice.text = "${tour.price}₽"
            holder.tvDiscountedPrice.visibility = View.GONE
        }

        holder.btnOrder.setOnClickListener {
            // Здесь можно реализовать логику нажатия на кнопку
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
    }
}
