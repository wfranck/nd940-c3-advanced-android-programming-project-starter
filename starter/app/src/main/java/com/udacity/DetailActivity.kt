package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import com.udacity.databinding.ActivityDetailBinding
import androidx.core.content.ContextCompat
import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color

class DetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        val url: String? = intent.getStringExtra("URL")
        val status: String? = intent.getStringExtra("STATUS")

        binding.contentDetail.apply {
            returnButton.setOnClickListener{
                startActivity(Intent(this@DetailActivity, MainActivity::class.java))
            }

            binding.contentDetail.fileNameDetail.text = url
            binding.contentDetail.statusDetail.text = status
            if (status == "Succeed"){
                binding.contentDetail.statusDetail.setTextColor(Color.rgb(191,194,87))
            }
            else { binding.contentDetail.statusDetail.setTextColor(Color.rgb(232,63,51)) }
        }

        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelAll()
    }
}
