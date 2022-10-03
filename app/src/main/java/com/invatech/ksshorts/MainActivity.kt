package com.invatech.ksshorts

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.invatech.ksshorts.databinding.ActivityMainBinding
import org.apache.commons.lang3.StringUtils


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var URL = "Null"
    private lateinit var mediaCt: MediaController
    var reelUrl: String = "1"
    private lateinit var uri2: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        requestRuntimePermission()

        mediaCt = MediaController(this)
        mediaCt.setAnchorView(binding.videoView)

        binding.getVideo.setOnClickListener {
            URL = binding.link.text.toString().trim()
            it.hideKeyboard()


            if (binding.link.text.toString().isEmpty()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.wait))
                    .setMessage(resources.getString(R.string.nourl))
                    .setCancelable(false)
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            } else if (binding.link.text.toString().contains("https://www.instagram.com/p/")) {
                binding.imageLayout.visibility = VISIBLE
                binding.card.visibility = INVISIBLE
                binding.relativeLayout.visibility = INVISIBLE

                val result2 = StringUtils.substringBefore(URL, "/?")
                URL = "$result2/?__a=1&__d=dis"
                processDataPhoto()

            } else {
                binding.relativeLayout.visibility = VISIBLE
                binding.imageLayout.visibility = INVISIBLE

                binding.card.visibility = INVISIBLE

                val result2 = StringUtils.substringBefore(URL, "/?")
                URL = "$result2/?__a=1&__d=dis"
                processData()
            }
        }



        binding.download.setOnClickListener {

            if (binding.link.text.toString().contains("instagram.com/p/")) {
                if (reelUrl != "1") {
                    val request: DownloadManager.Request = DownloadManager.Request(uri2)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    )
                    request.setTitle("Downloading...")
                    request.setDescription(".......")
                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "" + System.currentTimeMillis() + ".jpg"
                    )
                    val manager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    manager.enqueue(request)
                    MaterialAlertDialogBuilder(this)
                        .setTitle(resources.getString(R.string.downloaded))
                        .setMessage(resources.getString(R.string.msg2))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.Thanks)) { dialog, which ->
                            binding.link.text?.clear()
                            binding.relativeLayout.visibility = INVISIBLE
                            binding.card.visibility = VISIBLE
                            dialog.dismiss()
                            binding.videoView.stopPlayback()
                        }
                        .show()

                } else {
                    Toast.makeText(this, "No video available", Toast.LENGTH_SHORT).show()
                }

            } else if (binding.link.text.toString().contains("instagram.com/reel/")) {
                if (reelUrl != "1") {
                    val request: DownloadManager.Request = DownloadManager.Request(uri2)
                    request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
                    )
                    request.setTitle("Downloading...")
                    request.setDescription(".......")
                    request.allowScanningByMediaScanner()
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "" + System.currentTimeMillis() + ".mp4"
                    )
                    val manager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    manager.enqueue(request)
                    MaterialAlertDialogBuilder(this)
                        .setTitle(resources.getString(R.string.downloaded))
                        .setMessage(resources.getString(R.string.msg))
                        .setCancelable(false)
                        .setPositiveButton(resources.getString(R.string.Thanks)) { dialog, which ->
                            binding.link.text?.clear()
                            binding.relativeLayout.visibility = INVISIBLE
                            binding.card.visibility = VISIBLE
                            dialog.dismiss()
                            binding.videoView.stopPlayback()
                        }
                        .show()

                } else {
                    Toast.makeText(this, "Please enter correct Ig Url", Toast.LENGTH_SHORT).show()
                }

            }

        }


    }

    fun View.hideKeyboard() {
        val inputManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun processDataPhoto() {
        val request = StringRequest(URL, { response ->
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            val mainURL: MainURL = gson.fromJson(response, MainURL::class.java)
            reelUrl = mainURL.graphql.shortcode_media.display_url

            uri2 = Uri.parse(reelUrl)
            Glide.with(this).load(uri2).into(binding.photoView)

        }) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun processData() {
        val request = StringRequest(URL, { response ->
            val gsonBuilder = GsonBuilder()
            val gson = gsonBuilder.create()
            val mainURL: MainURL = gson.fromJson(response, MainURL::class.java)
            reelUrl = mainURL.graphql.shortcode_media.video_url

            uri2 = Uri.parse(reelUrl)
            binding.videoView.setMediaController(mediaCt)
            binding.videoView.setVideoURI(uri2)
            binding.videoView.requestFocus()
            binding.videoView.start()
            binding.progressCircular.visibility = VISIBLE

            binding.videoView.setOnPreparedListener { mp ->
                mp.setOnBufferingUpdateListener { mp, percent ->
                    if (percent == 100) {
                        //video have completed buffering
                        binding.progressCircular.visibility = INVISIBLE
                    }
                }
            }
        }) {
            Toast.makeText(this, "Erroorr", Toast.LENGTH_SHORT).show()
        }
        val queue: RequestQueue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun requestRuntimePermission(): Boolean{

        //requesting storage permission for only devices less than api 28
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
            if(ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),13)
                return false
            }
        }else{
            //read external storage permission for devices higher than android 10 i.e. api 29
            if(ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE),14)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

            }
            else Snackbar.make(binding.root, "Storage Permission Needed!!", 5000)
                .setAction("OK"){
                    ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),13)
                }
                .show()
//                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE),13)
        }

        //for read external storage permission
        if(requestCode == 14) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()

            }
            else Snackbar.make(binding.root, "Storage Permission Needed!!", 5000)
                .setAction("OK"){
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE),14)
                }
                .show()
//            else
//                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE),14)
        }
    }


}