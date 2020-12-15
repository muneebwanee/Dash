package com.github.muneebwanee.dash.ui.widget

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.github.muneebwanee.dash.R
import com.pawegio.kandroid.show
import kotterknife.bindView

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomEditText : FrameLayout, TextWatcher {

    private val editText : AppCompatEditText by bindView(R.id.id_edit_text)
    private val textView : TextView by bindView(R.id.id_text)

    private val passwordNumber = 1
    private val number = 2

    private var minCharacters = 0
    private var maxCharacters = 0
    private var showError = false

    var text :String
        get() = editText.text.toString()
        set(value) = editText.setText(value)

    var errorFocus :Int = 0
        set(value) {
            editText.error = context.getString(value)
            text=""
            editText.requestFocus()
            if (!showError){ showError = true; editText.background = ContextCompat.getDrawable(context,R.drawable.drawable_text_red) }
        }

    fun errorNotFocus(clear:Boolean){
        if (clear) text = ""
        if (!showError){ showError = true; editText.background = ContextCompat.getDrawable(context,R.drawable.drawable_text_red) }
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }


    private fun init(attrs: AttributeSet){

        inflate(context,R.layout.edittext,this)

        val typeArray = context.obtainStyledAttributes(attrs,R.styleable.CustomEditText)

        minCharacters = typeArray.getInt(R.styleable.CustomEditText_et_minCharacters,0)
        maxCharacters = typeArray.getInt(R.styleable.CustomEditText_et_maxCharacters,0)
        editText.hint = typeArray.getString(R.styleable.CustomEditText_et_hint)
        setInputType(typeArray.getInt(R.styleable.CustomEditText_et_inputType,0))

        typeArray.recycle()

        editText.addTextChangedListener(this)
        checkCharactersCount()
    }

    private fun setInputType(type:Int){
        when(type){
            passwordNumber ->{
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            number->editText.inputType =InputType.TYPE_CLASS_NUMBER
        }
    }

    override fun afterTextChanged(s: Editable) {
        checkCharactersCount()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    @SuppressLint("SetTextI18n")
    private fun checkCharactersCount(){
        if (showError) { showError = false;editText.background = ContextCompat.getDrawable(context,R.drawable.drawable_text_white) }
        if (hasCharactersCounter()) {
            textView.show()
            textView.text = getCharactersCounterText()
            textView.setTextColor(if (isCharactersValid()) ContextCompat.getColor(context,R.color.colorBlack) else ContextCompat.getColor(context,R.color.colorRed_01))
        }
    }

    private fun hasCharactersCounter():Boolean = minCharacters > 0 || maxCharacters > 0

    fun isCharactersValid() : Boolean  = (text.length >= minCharacters && (maxCharacters <=0 || text.length <= maxCharacters))

    private fun getCharactersCounterText () :String{
        return if (minCharacters> 0 && maxCharacters>0 ) {
            "${text.length}/$minCharacters-$maxCharacters"
        } else if (maxCharacters > 0 ) {
            "${text.length}/$maxCharacters"
        }else if (minCharacters > 0){
            "${text.length}/$minCharacters"
        }else ""
    }

}