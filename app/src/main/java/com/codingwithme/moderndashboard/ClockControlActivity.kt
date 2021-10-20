package com.codingwithme.moderndashboard

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import kotlinx.android.synthetic.main.activity_clock_control.*
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

private const val CAMERA_REQUEST_CODE = 102

class ClockControlActivity : AppCompatActivity(),ItemClickListener {
    private lateinit var codeScanner: CodeScanner
    private lateinit var scanResult : String
    private lateinit var whichBlock : String
    private var isScanned : Boolean = false
    private lateinit var reguPengamanan : String
    private var phonestr : String = "6281229722400"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock_control)

        var mApp = MyApplication()
        reguPengamanan = mApp.globalVar

        setupPermissions()
        codeScanner()
    }

    private fun codeScanner() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        val textView: TextView = findViewById(R.id.tv_textView)

        val actionbar = supportActionBar
        actionbar!!.title = "SCAN QRCODE"
        actionbar.setDisplayHomeAsUpEnabled(true)

        codeScanner =  CodeScanner( this, scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            codeScanner.decodeCallback = DecodeCallback {
                runOnUiThread {
                    tv_textView.text = it.text

                    whichBlock = it.text.toString()

                    if (!isScanned
                        && (whichBlock.equals("BLOK A", ignoreCase = true)
                        || whichBlock.equals("BLOK B", ignoreCase = true)
                        || whichBlock.equals("BLOK C", ignoreCase = true)
                        || whichBlock.equals("BLOK D", ignoreCase = true)
                        || whichBlock.equals("BLOK E", ignoreCase = true)
                        || whichBlock.equals("BLOK F", ignoreCase = true)
                        || whichBlock.equals("POS ATAS 1", ignoreCase = true)
                        || whichBlock.equals("POS ATAS 2", ignoreCase = true)
                        || whichBlock.equals("POS ATAS 3", ignoreCase = true)
                        || whichBlock.equals("POS ATAS 4", ignoreCase = true)
                        || whichBlock.equals("RD 01", ignoreCase = true))
                    ) {
                        isScanned = true
                        openBottomSheet()
                    }
                }

            }

            codeScanner.errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e ("Main", "Camera initialization error : ${it.message}")
                }
            }
        }

        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    fun openBottomSheet() {
        val addPhotoBottomDialogFragment = ActionBottom.newInstance()
        addPhotoBottomDialogFragment.show(supportFragmentManager, ActionBottom.TAG)
    }

    override fun onItemClick(item: String?) {
//        scanResult = "REGU: $reguPengamanan\n"
        scanResult = ""
        scanResult = scanResult + "$whichBlock\n"
        scanResult = scanResult + "*KONDISI: $item*\n"
        scanResult = scanResult + "WAKTU PATROLI: " + SimpleDateFormat("dd-MM-yyyy HH:mm").format(Date())

        tv_textView.text = scanResult
        isScanned = false
        submitBtn.setEnabled(true)
        submitBtn.setBackgroundColor(R.color.teal_200)
        sendWAMsg()
    }
//
//    fun sendMessage(message:String){
//
//        // Creating intent with action send
//        val intent = Intent(Intent.ACTION_SEND)
//
//        // Setting Intent type
//        intent.type = "text/plain"
//
//        // Setting whatsapp package name
//        intent.setPackage("com.whatsapp")
//
//
//        // Give your message here
//        intent.putExtra(Intent.EXTRA_TEXT, message)
//
//        // Checking whether whatsapp is installed or not
//        if (intent.resolveActivity(packageManager) == null) {
//            Toast.makeText(this,
//                "Please install whatsapp first.",
//                Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // Starting Whatsapp
//        startActivity(intent)
//    }

    fun sendWAMsg() {
        submitBtn.setOnClickListener {

            submitBtn.setEnabled(false)

            val packageManager: PackageManager = packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url =
                "http://api.whatsapp.com/send?phone=" + phonestr + "&text=" + URLEncoder.encode(
                    scanResult,
                    "UTF-8"
                )
            i.setPackage("com.whatsapp")
            i.setData(Uri.parse(url))

            if (i.resolveActivity(packageManager) != null) {
                startActivity(i)
            } else {
                Toast.makeText(this,
                    "Please install whatsapp first.",
                    Toast.LENGTH_SHORT).show()
            }

//            Thread.sleep(1_000)
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            android.Manifest.permission.CAMERA)

        if (permission == PackageManager.PERMISSION_DENIED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }
}