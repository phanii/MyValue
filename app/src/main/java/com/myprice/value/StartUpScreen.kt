package com.myprice.value

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start_up_screen.*

class StartUpScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_up_screen)
        img_seller.setOnClickListener {
            goToMain("I am a Seller")
        }
        img_buyer.setOnClickListener {
            goToMain("I am a Buyer")
        }
    }

    private fun goToMain(s: String? = null) {
        finish()
        startActivity(Intent(this, VerificationActivity::class.java))
    }
}
