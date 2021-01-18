package com.chilik1020.customviews.avatarimage

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.chilik1020.customviews.R
import com.chilik1020.customviews.extensions.dpToPx
import com.chilik1020.customviews.utils.LOG_TAG

class AvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    @Px
    var borderWidth: Float = context.dpToPx(DEFAULT_BORDER_WIDTH)

    @ColorInt
    private var borderColor: Int = Color.WHITE
    private var initials: String = "??"

    private val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val viewRect = Rect()
    private lateinit var resultBm: Bitmap
    private lateinit var maskBm: Bitmap
    private lateinit var srcBm: Bitmap

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.AvatarImageView)
            borderWidth = ta.getDimension(
                R.styleable.AvatarImageView_aiv_borderWidth,
                context.dpToPx(DEFAULT_BORDER_WIDTH)
            )

            borderColor =
                ta.getColor(R.styleable.AvatarImageView_aiv_borderColor, DEFAULT_BORDER_COLOR)

            initials =
                ta.getString(R.styleable.AvatarImageView_aiv_initials) ?: "??"
            ta.recycle()
        }
        scaleType = ScaleType.CENTER_CROP
        setup()
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(
            LOG_TAG, """ 
            onMeasure
            width: ${MeasureSpec.toString(widthMeasureSpec)}
            heignt: ${MeasureSpec.toString(heightMeasureSpec)}
        """.trimIndent()
        )
        val initSize = resolveDefaultSize(widthMeasureSpec)
        setMeasuredDimension(initSize, initSize)
        Log.d(LOG_TAG, "onMeasure after set size: $measuredWidth $measuredHeight")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(LOG_TAG, "onSizeChanged")
        if (w == 0) return
        with(viewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }
        prepareBitmaps(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d(LOG_TAG, "onDraw")

        canvas.drawBitmap(resultBm, viewRect, viewRect, null)

        val half = (borderWidth / 2).toInt()
        viewRect.inset(half, half)
        canvas.drawOval(viewRect.toRectF(), borderPaint)
    }

    private fun setup() {
        with(maskPaint) {
            color = Color.RED
            style = Paint.Style.FILL
        }

        with(borderPaint) {
            style = Paint.Style.STROKE
            strokeWidth = borderWidth
            color = borderColor
        }
    }

    private fun resolveDefaultSize(spec: Int): Int {
        return when (MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpToPx(DEFAULT_SIZE).toInt() /// ResolveDefaultSize()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec) // from spec
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec) // from spec
            else -> MeasureSpec.getSize(spec)
        }
    }

    private fun prepareBitmaps(w: Int, h: Int) {
        maskBm = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        resultBm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val maskCanvas = Canvas(maskBm)
        maskCanvas.drawOval(viewRect.toRectF(), maskPaint)

        srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)

        val resultCanvas = Canvas(resultBm)
        resultCanvas.drawBitmap(maskBm.extractAlpha(), viewRect, viewRect, null)
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        resultCanvas.drawBitmap(srcBm, viewRect, viewRect, maskPaint)
    }

    companion object {
        private const val DEFAULT_SIZE = 100
        private const val DEFAULT_BORDER_WIDTH = 2
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
    }
}