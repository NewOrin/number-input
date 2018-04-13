package com.neworin.numberinputview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        main_niv.setInputCompleteListener(object : NumberInputView.InputCompleteListener {
            override fun inputComplete(content: String) {
                applicationContext.showToast(content)
            }
        })
    }
}
