package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var padding = 0f

    private var currentText: String = ""

    private var valueAnimator = ValueAnimator()

    private var progress: Float = 0f

    private var loadingBackgroundRect = Rect()

    private val messageRect = Rect()

    private val backgroundRect = Rect()

    private val loadingBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    }


    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val loadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        Log.i("LoadingButton", buttonState.toString())
        when (new) {
            ButtonState.Loading -> {
                progress = 0f
                currentText = context.getString(R.string.download)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 2000 // same duration as toast short
                    repeatCount = ValueAnimator.INFINITE

                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    start()
                }
                this.isEnabled = false
            }
            ButtonState.Completed -> {
                currentText = context.getString(R.string.download)
                this.isEnabled = true
                progress = 0f

            }
            ButtonState.Clicked -> {
                progress = 0f
                currentText = context.getString(R.string.download)
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    duration = 2000 // same duration as toast short
                    repeatCount = 0

                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    this.doOnEnd {
                        Log.i(
                            "ButtonState",
                            "Button clicked animation ended, moving to complete status"
                        )
                        buttonState = ButtonState.Completed
                    }
                    start()
                }
                this.isEnabled = false

            }
        }
        invalidate()
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }


    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)
        // Draw background
        canvas?.drawRect(backgroundRect, backgroundPaint)
        if (buttonState == ButtonState.Loading || buttonState == ButtonState.Clicked) {
            // Draw loading background rectangle
            loadingBackgroundRect.set(0, 0, (widthSize * progress).toInt(), heightSize)
            canvas?.drawRect(loadingBackgroundRect, loadingBackgroundPaint)
            // Draw arc
            textPaint.getTextBounds(currentText, 0, currentText.length, messageRect)
            canvas?.drawArc(
                widthSize / 2f + messageRect.width() / 2f + padding,
                padding,
                widthSize / 2f + messageRect.width() / 2f + padding + min(widthSize, heightSize),
                heightSize.toFloat() - padding,
                0f,
                360 * progress,
                true,
                loadingArcPaint
            )
        }
        // Draw text
        canvas?.drawText(
            currentText,
            widthSize / 2f,
            ((heightSize + textPaint.textSize) / 2f),
            textPaint
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
        backgroundRect.set(0, 0, widthSize, heightSize)
        textPaint.textSize = heightSize * .4f
        padding = if (heightSize < widthSize) heightSize * 0.1f else widthSize * 0.1f

    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        invalidate()
        return true
    }

}
//package com.udacity
//
//import android.animation.ValueAnimator
//import android.content.Context
//import android.graphics.*
//import android.util.AttributeSet
//import android.view.View
//import androidx.core.animation.doOnEnd
//import androidx.core.content.res.ResourcesCompat
//import kotlin.math.min
//import kotlin.properties.Delegates
//
//class LoadingButton @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//    private var widthSize = 0
//    private var heightSize = 0
//    private var progress: Float = 0f
//    private var currentText: String = ""
//    private var padding = 0f
//    private var valueAnimator = ValueAnimator()
//
//    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
//        when(new) {
//            ButtonState.Clicked -> {
//                progress = 0f
//                currentText = context.getString(R.string.LoadingText)
//                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
//                    duration = 2000
//                    repeatCount = 0
//                    addUpdateListener {
//                        progress = animatedValue as Float
//                        invalidate()
//                    }
//                    this.doOnEnd { buttonState = ButtonState.Completed }
//                    start()
//                }
//                this.isEnabled = false
//            }
//            ButtonState.Loading -> {
//                progress = 0f
//                currentText = context.getString(R.string.LoadingText)
//                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
//                    duration = 2000
//                    repeatCount = 0
//                    addUpdateListener {
//                        progress = animatedValue as Float
//                        invalidate()
//                    }
//                    this.doOnEnd { buttonState = ButtonState.Completed }
//                    start()
//                }
//                this.isEnabled = false
//            }
//            ButtonState.Completed -> {
//                currentText = context.getString(R.string.download)
//                this.isEnabled = true
//                progress = 0f
//            }
//        }
//        invalidate()
//    }
//
//
//    fun setState(state: ButtonState) {
//        buttonState = state
//    }
//
//    init {
//        isClickable = true
//    }
//
//    override fun performClick(): Boolean {
//        if (super.performClick()) return true
//        invalidate()
//        return true
//    }
//
//
//    override fun onDraw(canvas: Canvas?) {
//        super.onDraw(canvas)
//
//        canvas?.drawRect(Rect(), backgroundPaint)
//        if (buttonState == ButtonState.Loading || buttonState == ButtonState.Clicked) {
//            Rect().set(0,0, (widthSize * progress).toInt(), heightSize)
//            canvas?.drawRect(Rect(), loadingBackgroundPaint)
//            textPaint.getTextBounds(currentText, 0, currentText.length, Rect())
//            canvas?.drawArc(
//                widthSize / 2f + Rect().width() / 2f  +  padding,
//                padding,
//                widthSize / 2f + Rect().width() / 2f + padding + min(widthSize, heightSize),
//                heightSize.toFloat() + padding,
//                0f,
//                360 * progress,
//                true,
//                loadingArcPaint
//            )
//        }
//        canvas?.drawText(
//            currentText,
//            widthSize / 2f,
//            ((widthSize + textPaint.textSize) / 2f),
//            textPaint
//        )
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
//        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
//        val h: Int = resolveSizeAndState(
//            MeasureSpec.getSize(w),
//            heightMeasureSpec,
//            0
//        )
//        widthSize = w
//        heightSize = h
//        setMeasuredDimension(w, h)
//        Rect().set(0, 0, widthSize, heightSize)
//        textPaint.textSize = heightSize * .4f
//        padding = if (heightSize < widthSize) heightSize * 0.1f else widthSize * 0.1f
//    }
//
//    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//        color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
//    }
//
//    private val loadingBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//        color = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
//    }
//
//    private val loadingArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//        color = Color.YELLOW
//        typeface = Typeface.create("", Typeface.BOLD)
//    }
//
//    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
//        style = Paint.Style.FILL
//        textAlign = Paint.Align.CENTER
//        color = Color.WHITE
//        typeface = Typeface.create("", Typeface.BOLD)
//    }
//
//}