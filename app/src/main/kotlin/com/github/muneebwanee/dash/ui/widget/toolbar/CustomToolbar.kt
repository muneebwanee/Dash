package com.github.muneebwanee.dash.ui.widget.toolbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Animatable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*

import com.github.muneebwanee.dash.R
import com.github.muneebwanee.dash.ui.widget.toolbar.adapter.DefaultSuggestionsAdapter
import com.github.muneebwanee.dash.ui.widget.toolbar.adapter.SuggestionsAdapter
import com.github.muneebwanee.dash.utils.ConstFun.isAndroidM
import com.github.muneebwanee.dash.utils.ConstFun.setImageUrl
import com.github.muneebwanee.dash.utils.ConstFun.showKeyboard
import com.github.muneebwanee.dash.utils.ConstFun.isShow
import com.github.muneebwanee.dash.ui.animation.AnimationUtils.animateAlpha
import com.pawegio.kandroid.hide
import com.pawegio.kandroid.runDelayedOnUiThread
import com.pawegio.kandroid.show
import de.hdodenhof.circleimageview.CircleImageView
import kotterknife.bindView
import kotlin.math.ceil

/**
 * Created by muneebwanee on 15/12/20.
 */
class CustomToolbar : FrameLayout, View.OnClickListener, Animation.AnimationListener, SuggestionsAdapter.OnItemViewClickListener, View.OnFocusChangeListener, TextView.OnEditorActionListener, TextWatcher {

    private val inputContainer: LinearLayout by bindView(R.id.inputContainer)
    private val navIcon: ImageView  by bindView(R.id.tb_nav)
    private val menuIcon: ImageView  by bindView(R.id.tb_menu)
    private val clearIcon: ImageView  by bindView(R.id.tb_clear)
    private val childPhoto: CircleImageView  by bindView(R.id.tb_child_photo)
    private val searchEditText: EditText  by bindView(R.id.tb_editText)
    private val placeHolderView: TextView  by bindView(R.id.tb_placeholder)
    private val stateView : ImageView by bindView(R.id.tb_state)
    private val progress : ProgressBar by bindView(R.id.tb_progress)
    private val actionDelete : ImageView by bindView(R.id.tb_action_delete)
    private val last : RelativeLayout by bindView(R.id.last)
    private val recyclerView : RecyclerView by bindView(R.id.tb_recycler)
    private val linearRecord : LinearLayout by bindView(R.id.tb_record)
    private val viewRecord : ImageView by bindView(R.id.tb_view_record)
    private val timerRecord : TextView by bindView(R.id.tb_timer_record)

    private var onToolbarActionListener: OnToolbarActionListener? = null
    private var alphaRecord : AlphaAnimation?=null

    var enableSearch:Boolean = true

    var enableStatePermission:Boolean = false

    var statePermission : Boolean = false
        set(state) {
            stateView.show()
            stateView.setImageResource(if (state) R.drawable.ic_status_key_enable_24dp else R.drawable.ic_status_key_disable_24dp)
            field = state
        }

    var showProgress : Boolean = false
        set(state) {
            progress.isShow(state)
            field = state
        }

    var isActionEnabled:Boolean = false

    var isSearchEnabled:Boolean = false

    private var isSearchDisabledForAction : Boolean = false

    private var clickClearText : Boolean = false

    private var isSuggestionsVisible: Boolean = false

    private var adapter: DefaultSuggestionsAdapter? = null

    private var density: Float = 0.toFloat()

    var menu: PopupMenu? = null
        private set

    private var navIconShown = true

    var setTitle : String
        get() = placeHolderView.text.toString()
        set(title) {
            placeHolderView.text = title
            hint = title
        }

    var hint: String
        get() = searchEditText.hint.toString()
        set(hint) {
            searchEditText.hint = hint
        }

    var text: String
        get() = searchEditText.text.toString()
        set(text) = searchEditText.append(text)

    var timer : String
        get() = timerRecord.text.toString()
        set(timer) {
            timerRecord.text = timer
        }

    /*private var lastSuggestions: MutableList<String>?
        get() = adapter!!.getSuggestions()
        set(suggestions) = adapter!!.setSuggestions(suggestions!!)*/

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        View.inflate(context, R.layout.toolbar, this)

        density = resources.displayMetrics.density

        adapter = DefaultSuggestionsAdapter()
        adapter!!.setListener(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        setOnClickListener(this)
        clearIcon.setOnClickListener(this)
        childPhoto.setOnClickListener(this)
        stateView.setOnClickListener(this)
        actionDelete.setOnClickListener(this)
        searchEditText.onFocusChangeListener = this
        searchEditText.setOnEditorActionListener(this)
        searchEditText.addTextChangedListener(this)
        navIcon.setOnClickListener(this)

        navIcon.layoutParams.width = (50 * density).toInt()
        (inputContainer.layoutParams as RelativeLayout.LayoutParams).leftMargin = (50 * density).toInt()
        navIcon.requestLayout()
        placeHolderView.requestLayout()

        setupIconRippleStyle()
        inflateMenu()

        topView()

    }

    private fun topView(){
        val top :Int
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        top = if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
        else ceil(((if (isAndroidM())  24 else 25) * density).toDouble()).toInt()
        setPadding(0,top,0,0)
    }

    fun setChildPhoto(url:String){
        childPhoto.setImageUrl(url,R.drawable.ic_placeholder_profile)
    }

    private fun setupIconRippleStyle() {
        val rippleStyle = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, rippleStyle, true)
        navIcon.setBackgroundResource(rippleStyle.resourceId)
        menuIcon.setBackgroundResource(rippleStyle.resourceId)
        clearIcon.setBackgroundResource(rippleStyle.resourceId)
    }

    private fun inflateMenu() {
        val params = childPhoto.layoutParams as RelativeLayout.LayoutParams
        params.rightMargin = (48 * density).toInt()
        childPhoto.layoutParams = params
        val paramsDelete = actionDelete.layoutParams as RelativeLayout.LayoutParams
        paramsDelete.rightMargin = (48 * density).toInt()
        actionDelete.layoutParams = paramsDelete
        menuIcon.show()
        menuIcon.setOnClickListener(this)
        menu = PopupMenu(context, menuIcon)
        menu!!.inflate(R.menu.menu_main)
        menu!!.gravity = Gravity.END
    }

    fun setOnToolbarActionListener(onToolbarActionListener: OnToolbarActionListener) {
        this.onToolbarActionListener = onToolbarActionListener
    }

    fun enableAction(){
        isActionEnabled = true
        enableSearch = false
        if (navIconShown) animateNavIcon()
        val leftIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_left)
        val leftOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_left)
        leftIn.setAnimationListener(this)
        if (isSearchEnabled){
            isSearchDisabledForAction = true
            isSearchEnabled = false
            val `in` = AnimationUtils.loadAnimation(context, R.anim.fade_in)
            val out = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            placeHolderView.show()
            placeHolderView.startAnimation(`in`)
            inputContainer.startAnimation(out)
            if (isSuggestionsVisible) animateSuggestions(getListHeight(false), 0)
        }else{
            if (enableStatePermission) stateView.startAnimation(leftOut)
            childPhoto.startAnimation(leftOut)
        }
        actionDelete.show()
        actionDelete.startAnimation(leftIn)
        if (listenerExists()) onToolbarActionListener!!.onActionStateChanged(true)
    }

    fun disableAction(){
        isActionEnabled = false
        animateNavIcon()
        val out = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        out.setAnimationListener(this)
        actionDelete.startAnimation(out)
        if (listenerExists()) {
            if (isSearchDisabledForAction && (searchEditText.text.toString() != "" || clickClearText)) {
                isSearchDisabledForAction = false
                clickClearText = false
                onToolbarActionListener!!.onButtonClicked(BUTTON_BACK)
                searchEditText.setText("")
            }
            onToolbarActionListener!!.onActionStateChanged(false)
        }
    }

    fun disableSearch() {
        animateNavIcon()
        isSearchEnabled = false
        val out = AnimationUtils.loadAnimation(context, R.anim.fade_out)
        val `in` = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        out.setAnimationListener(this)
        inputContainer.startAnimation(out)
        placeHolderView.show()
        placeHolderView.startAnimation(`in`)

        if (listenerExists()){
            if (searchEditText.text.toString() != "" || clickClearText){
                clickClearText = false
                onToolbarActionListener!!.onButtonClicked(BUTTON_BACK)
                searchEditText.setText("")
            }
            onToolbarActionListener!!.onSearchStateChanged(false)
        }
        if (isSuggestionsVisible) animateSuggestions(getListHeight(false), 0)
    }

    private fun enableSearch() {
        animateNavIcon()
        adapter!!.notifyDataSetChanged()
        isSearchEnabled = true
        val leftIn = AnimationUtils.loadAnimation(context, R.anim.fade_in_left)
        val leftOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_left)
        leftIn.setAnimationListener(this)
        placeHolderView.hide()
        inputContainer.show()
        inputContainer.startAnimation(leftIn)
        if (listenerExists()) {
            onToolbarActionListener!!.onSearchStateChanged(true)
        }
        if (enableStatePermission) stateView.startAnimation(leftOut)
        childPhoto.startAnimation(leftOut)
    }

    private fun animateNavIcon() {
        if (navIconShown) {
            this.navIcon.setImageResource(R.drawable.ic_menu_animated)
        } else {
            this.navIcon.setImageResource(R.drawable.ic_back_animated)
        }
        val mDrawable = navIcon.drawable
        if (mDrawable is Animatable) {
            (mDrawable as Animatable).start()
        }
        navIconShown = !navIconShown
    }

    override fun onAnimationEnd(animation: Animation) {
        if (enableSearch){
            if (!isSearchEnabled) {
                inputContainer.hide(true)
                val `in` = AnimationUtils.loadAnimation(context, R.anim.fade_in_right)
                if (enableStatePermission) { stateView.show() ; stateView.startAnimation(`in`) }
                childPhoto.show()
                childPhoto.startAnimation(`in`)
            } else {
                if (enableStatePermission) stateView.hide(true)
                childPhoto.hide(true)
                searchEditText.requestFocus()
                if (!isSuggestionsVisible)
                    showSuggestionsList()
            }
        }else{
            if (!isActionEnabled){
                actionDelete.hide(true)
                enableSearch = true
                val `in` = AnimationUtils.loadAnimation(context, R.anim.fade_in_right)
                if (enableStatePermission) { stateView.show() ; stateView.startAnimation(`in`) }
                childPhoto.show()
                childPhoto.startAnimation(`in`)
            }
            else{
                inputContainer.hide(true)
                if (enableStatePermission) stateView.hide(true)
                childPhoto.hide(true)
            }
        }
    }

    private fun animateSuggestions(from: Int, to: Int) {
        isSuggestionsVisible = to > 0
        val lp = last.layoutParams
        if (to == 0 && lp.height == 0)
            return
        val animator = ValueAnimator.ofInt(from, to)
        animator.duration = 200
        animator.addUpdateListener { animation ->
            lp.height = animation.animatedValue as Int
            last.layoutParams = lp
        }
        if (adapter!!.itemCount > 0) {
            animator.start()
            runDelayedOnUiThread(205){
                if (listenerExists()) onToolbarActionListener?.onChangeHeight()
            }
        }
    }

    fun animateRecord(record:Boolean){
        if (record) alphaRecord = viewRecord.animateAlpha()
        else { alphaRecord?.cancel() ; alphaRecord?.reset() }
        val lp = linearRecord.layoutParams
        val animator = if (record) ValueAnimator.ofInt(0,(35 * density).toInt())
        else ValueAnimator.ofInt((35 * density).toInt(),0)
        animator.duration = 200
        animator.addUpdateListener { animation ->
            lp.height = animation.animatedValue as Int
            linearRecord.layoutParams = lp
        }
        animator.start()
        runDelayedOnUiThread(205){
            if (listenerExists()) onToolbarActionListener?.onChangeHeight()
        }
    }

    private fun showSuggestionsList() {
        animateSuggestions(0, getListHeight(false))
    }

    private fun hideSuggestionsList() {
        animateSuggestions(getListHeight(false), 0)
    }

    private fun listenerExists(): Boolean {
        return onToolbarActionListener != null
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == getId()) {
            if (enableSearch) if (!isSearchEnabled) enableSearch()
        }else if (id == R.id.tb_state){
            if (listenerExists()) onToolbarActionListener!!.onButtonClicked(BUTTON_STATE)
        }else if (id == R.id.tb_child_photo) {
            if (listenerExists()) onToolbarActionListener!!.onButtonClicked(BUTTON_CHILD_USER)
        } else if (id == R.id.tb_clear) {
            clickClearText = true
            searchEditText.setText("")
        } else if (id == R.id.tb_menu) {
            menu!!.show()
        } else if (id == R.id.tb_action_delete) {
            val icon = actionDelete.drawable
            if (icon is Animatable) (icon as Animatable).start()
            if (listenerExists()) onToolbarActionListener!!.onButtonClicked(BUTTON_ACTION_DELETE)
        } else if (id == R.id.tb_nav)
            if (listenerExists()) {
                if (navIconShown) {
                    onToolbarActionListener!!.onButtonClicked(BUTTON_NAVIGATION)
                } else {
                    if (enableSearch) { if (isSearchEnabled) disableSearch() }
                    else {if (isActionEnabled) disableAction()}
                }
            }
    }

    override fun onAnimationStart(animation: Animation) {}

    override fun onAnimationRepeat(animation: Animation) {}

    override fun onFocusChange(v: View, hasFocus: Boolean) {
        searchEditText.showKeyboard(hasFocus)
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        val filter = searchEditText.text.toString()
        if (filter!=""){
            adapter!!.addSuggestion(searchEditText.text.toString())
        }
        searchEditText.showKeyboard(false)
        return true
    }

    private fun textSearchConfirmed(text: String){
        if (listenerExists()) onToolbarActionListener!!.onSearchConfirmed(text)
        if (isSuggestionsVisible) hideSuggestionsList()
    }

    private fun getListHeight(isSubtraction: Boolean): Int {
        return if (!isSubtraction) (adapter!!.listHeight * density).toInt() else ((adapter!!.itemCount - 1) * adapter!!.singleViewHeight * density).toInt()
    }

    override fun afterTextChanged(s: Editable) {
        if (s.isEmpty()) textSearchConfirmed(s.toString())
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isNotEmpty()) { textSearchConfirmed(s.toString()) ; clearIcon.show() }
        else clearIcon.hide()
    }

    override fun onItemClickListener(position: Int, text: String) {
        searchEditText.append(text)
        textSearchConfirmed(text)
    }

    override fun onItemDeleteListener(position: Int, text: String) {
        animateSuggestions(getListHeight(false), getListHeight(true))
        adapter!!.deleteSuggestion(position, text)
    }

    interface OnToolbarActionListener {
        fun onSearchStateChanged(enabled: Boolean)
        fun onSearchConfirmed(text: CharSequence)
        fun onButtonClicked(buttonCode: Int)
        fun onActionStateChanged(enabled: Boolean)
        fun onChangeHeight()
    }

    companion object {
        const val BUTTON_CHILD_USER = 1
        const val BUTTON_NAVIGATION = 2
        const val BUTTON_BACK = 3
        const val BUTTON_STATE = 4
        const val BUTTON_ACTION_DELETE = 5
    }
}
