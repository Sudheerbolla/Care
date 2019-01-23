package com.careapp.views

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import com.careapp.R

class AppTextView : AppCompatTextView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attributeSet: AttributeSet) {
        val fontPath: String
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.AppTextView)
        val font_val = typedArray.getInteger(R.styleable.AppTextView_txt_font_type, 1)
        when (font_val) {
            0 -> fontPath = "light.otf"
            1 -> fontPath = "regular.otf"
            2 -> fontPath = "bold.otf"
            3 -> fontPath = "italic.ttf"
            else -> fontPath = "regular.otf"
        }
        val tf = Typeface.createFromAsset(getContext().assets, fontPath)
        typeface = tf
        typedArray.recycle()
    }

}
