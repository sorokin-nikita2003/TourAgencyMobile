package com.example.touragency

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.touragency.serverConnection.BankCard
import com.example.touragency.serverConnection.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class PaymentActivity : AppCompatActivity() {
    private var orderId = 0 // Добавляем глобальную переменную username
    private var roles: List<String>? = null // Для ролей


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        orderId = intent.getIntExtra("ORDER_ID",0)
        roles = intent.getStringArrayListExtra("ROLES")
        Log.e("", roles.toString())


        val etCardNum = findViewById<EditText>(R.id.etCardNum)
        val etCVV = findViewById<EditText>(R.id.etCVV)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        // Форматирование CardNum
        etCardNum.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var cursorPosition = 0

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                cursorPosition = etCardNum.selectionStart
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val unformattedText = s.toString().replace(" ", "")
                val formattedText = StringBuilder()

                for (i in unformattedText.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formattedText.append(" ")
                    }
                    formattedText.append(unformattedText[i])
                }

                etCardNum.removeTextChangedListener(this)
                etCardNum.setText(formattedText.toString())
                etCardNum.setSelection(minOf(cursorPosition + 1, formattedText.length))
                etCardNum.addTextChangedListener(this)

                isFormatting = false
            }
        })

        // Валидация CVV (только цифры, максимум 3)
        etCVV.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                if (input.length > 3) {
                    etCVV.setText(input.substring(0, 3))
                    etCVV.setSelection(3)
                }
            }
        })

        // Обработка кнопки Submit
        btnSubmit.setOnClickListener {
            val cardNum = etCardNum.text.toString().replace(" ", "")
            val cvv = etCVV.text.toString()

            if (cardNum.length != 16) {
                Toast.makeText(this, "Card number must be 16 digits!", Toast.LENGTH_SHORT).show()
            } else if (cvv.length != 3) {
                Toast.makeText(this, "CVV must be 3 digits!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Payment submitted!", Toast.LENGTH_SHORT).show()

                val bankCard = BankCard(
                    id = orderId,
                    CardNum = cardNum,
                    CVV = cvv
                )
                RetrofitClient.api.Payment(bankCard).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.e("", bankCard.id.toString())
                            Log.e("", bankCard.CardNum.toString())
                            Log.e("", bankCard.CVV.toString())
                            val intent = Intent(this@PaymentActivity, OrdersActivity::class.java)
//                            intent.putExtra("ORDER_ID", orderId)
                            intent.putStringArrayListExtra("ROLES", ArrayList(roles))
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@PaymentActivity, "Ошибка при оплате заказа: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@PaymentActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }
}
