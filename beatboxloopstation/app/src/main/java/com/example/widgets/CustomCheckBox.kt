package com.example.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.CheckBox
import com.example.beatboxloopstation.R

class CustomCheckBox @JvmOverloads constructor(context: Context, attributeSet: AttributeSet?, var select: Int = R.drawable.select_checkbox, var deselect: Int = R.drawable.deselect_checkbox): CheckBox(context, attributeSet){

    init {
        isChecked = true
        this.setTypeface(typeface, Typeface.BOLD)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)

        if (checked){
            this.setBackgroundResource(select) //R.drawable.select_checkbox
            this.setTextColor(Color.parseColor("#47b66a"))
        }else{
            this.setBackgroundResource(deselect) //R.drawable.deselect_checkbox
            this.setTextColor(Color.parseColor("#adb1bd"))
        }
    }
}