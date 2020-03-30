package com.mgl7130.avisshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import kotlinx.android.synthetic.main.activity_main.*

// home activity
class MainActivity : BaseActivity() {
    override val TAG = "MainActivity"

    // code bar value
    var mScannedResult: String = ""
    // code bar prosessor
    var mBarCodeProcessor: BarCodeProcessor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //adding the main view to the menu view
        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val contentView = inflater.inflate(R.layout.activity_main, null, false)
        mDrawerLayout.addView(contentView, 0)

        captureCodeBar.setOnClickListener {
            run {
                //set capture code bar activity
                val integrator = IntentIntegrator(this)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES)
                integrator.setPrompt("Scanner un code à barre")
                integrator.setBeepEnabled(false)
                integrator.setBarcodeImageEnabled(true)
                integrator.setOrientationLocked(false)
                integrator.initiateScan()
            }
        }
        //when the code bar is entred manually in the edit text
        codeBarNumber.setOnEditorActionListener{ _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                if (codeBarNumber.text.length == 12 || codeBarNumber.text.length == 8){
                    //it could be a valid codebar
                    mScannedResult = codeBarNumber.text.toString()
                    mBarCodeProcessor = BarCodeProcessor(this@MainActivity, mScannedResult)
                    mBarCodeProcessor!!.proces_bar_code()
                }
                true
            } else {
                false
            }
        }

    }

    //handling the result sent from the code bar activity (of the library)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if(result != null){

            if(result.contents != null){
                //scan done successufly call the barcode processor to handle the bar code
                mScannedResult = result.contents
                mBarCodeProcessor = BarCodeProcessor(this, mScannedResult)
                mBarCodeProcessor!!.proces_bar_code()

            } else {
                mScannedResult = "scan failed"
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("scannedResult", mScannedResult)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        savedInstanceState?.let {
            mScannedResult = it.getString("scannedResult").toString()
        }
    }

    override fun onResume() {
        codeBarNumber.text.clear()
        super.onResume()
    }
}

