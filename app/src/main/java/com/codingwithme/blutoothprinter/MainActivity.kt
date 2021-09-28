package com.codingwithme.blutoothprinter

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.data.printable.ImagePrintable
import com.mazenrashed.printooth.data.printable.Printable
import com.mazenrashed.printooth.data.printable.RawPrintable
import com.mazenrashed.printooth.data.printable.TextPrintable
import com.mazenrashed.printooth.data.printer.DefaultPrinter
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import com.mazenrashed.printooth.utilities.PrintingCallback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class MainActivity : AppCompatActivity(), PrintingCallback {

    internal  var printing:Printing? =null;

    private val btn3: Button = findViewById<Button>(R.id.btn3)
    private val btn2: Button = findViewById<Button>(R.id.btn2)
    private val btn1: Button = findViewById<Button>(R.id.btn1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        if(printing!=null)
            printing!!.printingCallback = this



        btn3!!.setOnClickListener {
            if(Printooth.hasPairedPrinter()) {
                Printooth.removeCurrentPrinter()
            }else{
                startActivityForResult(Intent(this@MainActivity, ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
                changePairAndUnPair()

            }
        }

        btn2!!.setOnClickListener {
            if(!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java),
                ScanningActivity.SCANNING_FOR_PRINTER)
            else
                PrintImage()
        }

        btn1!!.setOnClickListener {
            if(!Printooth.hasPairedPrinter())
                startActivityForResult(Intent(this@MainActivity,ScanningActivity::class.java),
                    ScanningActivity.SCANNING_FOR_PRINTER)
            else
                PrintText()
        }

    }

    private fun PrintText() {
         val printables = ArrayList<Printable>()
        printables.add(RawPrintable.Builder(byteArrayOf(27,100,47)).build())


        //add text method
        printables.add(TextPrintable.Builder()
            .setText("Hello World ")
            .setCharacterCode(DefaultPrinter.CHARCODE_PC1252)
            .setNewLinesAfter(1)
            .build())

        //custom text method
        printables.add(TextPrintable.Builder()
            .setText("Hello world")
            .setLineSpacing(DefaultPrinter.LINE_SPACING_60)
            .setAlignment(DefaultPrinter.ALIGNMENT_CENTER)
            .setEmphasizedMode(DefaultPrinter.EMPHASIZED_MODE_BOLD)
            .setUnderlined(DefaultPrinter.UNDERLINED_MODE_ON)
            .setNewLinesAfter(1)
            .build())

        printing!!.print(printables)
    }

    private fun PrintImage() {
        val printables  = ArrayList<Printable>()

            //Load bitmap for internet
        Picasso.get().load("https://www.google.com/search?q=android+image+icon&tbm=isch&ved=2ahUKEwjCkdvqkKHzAhUUSHwKHZ6tACEQ2-cCegQIABAA&oq=android+image+icon&gs_lcp=CgNpbWcQAzIFCAAQgAQyBggAEAgQHjIGCAAQCBAeMgYIABAIEB4yBggAEAgQHjIGCAAQCBAeMgYIABAIEB4yBggAEAgQHjIECAAQGDoHCCMQ7wMQJ1DbM1iOR2DYSWgAcAB4AIABlwGIAbUKkgEEMC4xMZgBAKABAaoBC2d3cy13aXotaW1nwAEB&sclient=img&ei=TcJSYYK3LZSQ8QOe24KIAg&bih=1009&biw=1792&rlz=1C5CHFA_enIN920IN920#imgrc=TV2qeIb6KNpIBM")
            .into(object : Target{
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    printables.add(ImagePrintable.Builder(bitmap!!).build())
                    printing!!.print(printables)

                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {

                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

                }

            })

    }

    private fun changePairAndUnPair() {
        if (Printooth.hasPairedPrinter())
            btn3.text = "UnPair ${Printooth.getPairedPrinter()!!.name}"
        else
            btn3.text = "Paired with Printer .."
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ScanningActivity.SCANNING_FOR_PRINTER && resultCode== Activity.RESULT_OK)
            initPrinting()
        changePairAndUnPair()

    }

    private fun initPrinting() {
        if (Printooth.hasPairedPrinter())
            printing = Printooth.printer()
        if(printing!=null)
            printing!!.printingCallback = this
    }

    override fun connectingWithPrinter() {
        Toast.makeText(this,"Connecting to printer..",Toast.LENGTH_SHORT).show()
    }

    override fun connectionFailed(error: String) {
        Toast.makeText(this,"Failed: $error",Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this,"Error occurred..",Toast.LENGTH_SHORT).show()
    }

    override fun onMessage(message: String) {
        Toast.makeText(this,"Message: $message",Toast.LENGTH_SHORT).show()
    }

    override fun printingOrderSentSuccessfully() {
        Toast.makeText(this,"Order sent successfully",Toast.LENGTH_SHORT).show()
    }
}

