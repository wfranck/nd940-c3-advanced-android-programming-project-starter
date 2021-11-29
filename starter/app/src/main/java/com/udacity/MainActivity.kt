package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.udacity.databinding.ActivityMainBinding

data class Option(
    val name: String,
    val url: String = ""
)

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var optionSelected: Option? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(toolbar)

        binding.contentMain.apply {
            customButton.setOnClickListener {
                if (optionGroup.checkedRadioButtonId == -1) {
                    Toast.makeText(
                        applicationContext,
                        "Please, select the file to download",
                        Toast.LENGTH_SHORT
                    ).show()
                    customButton.setState(ButtonState.Clicked)
                } else {
                    notificationManager = ContextCompat.getSystemService(
                        applicationContext,
                        NotificationManager::class.java
                    ) as NotificationManager
                    notificationManager.cancelNotifications()
                    downloadFile()
                }
            }

            optionGroup.setOnCheckedChangeListener { _, id ->
                when (id) {
                    R.id.glide_radiobutton -> optionSelected = Option(
                        application.getString(R.string.glide_string),
                        "https://github.com/bumptech/glide"
                    )
                    R.id.loadapp_radiobutton -> optionSelected = Option(
                        application.getString(R.string.load_app_string),
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
                    )
                    R.id.retrofit_radiobutton -> optionSelected = Option(
                        application.getString(R.string.retrofit_string),
                        "https://github.com/square/retrofit",
                    )
                }
            }
        }

        binding.contentMain.customButton.setState(ButtonState.Completed)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            id?.let {
                if (id == downloadID) {
                    val downloadQuery = DownloadManager.Query().setFilterById(id)
                    val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    val downloadCursor: Cursor = downloadManager.query(downloadQuery)

                    if (downloadCursor.moveToFirst()) {
                        val status =
                            downloadCursor.getInt(downloadCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL){
                            val url = downloadCursor.getString(
                                downloadCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)
                            )
                            notificationManager.sendNotification(
                                applicationContext,
                                url,
                                "Succeed",
                                application.getString(R.string.notification_description)
                            )
                            binding.contentMain.customButton.setState(ButtonState.Completed)
                        } else {
                            notificationManager.sendNotification(
                                applicationContext,
                                downloadCursor.getString(
                                    downloadCursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE)
                                ),
                                "Fail",
                                application.getString(R.string.notification_description)
                            )
                            binding.contentMain.customButton.setState(ButtonState.Completed)
                        }
                    } else {
                        binding.contentMain.customButton.setState(ButtonState.Completed)
                    }
                }
            }
        }
    }

    private fun downloadFile() {
        if (isNetworkAvailable()) {
            binding.contentMain.customButton.setState(ButtonState.Loading)
            optionSelected?.let {
                val request =
                    DownloadManager.Request(Uri.parse(it.url + "/archive/master.zip"))
                        .setTitle(it.name)
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                downloadID = downloadManager.enqueue(request)
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Network is not available!",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
