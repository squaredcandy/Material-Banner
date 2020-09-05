package com.squaredcandy.materialbanner

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.withStyledAttributes
import com.google.android.material.button.MaterialButton

/**
 * Implementation of Material design's Banner for Android
 * https://material.io/components/banners
 */
class MaterialBannerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private var negativeClickListener: OnClickListener? = null
    private var positiveClickListener: OnClickListener? = null

    private val contextTextView: TextView
    private val negativeButton: MaterialButton
    private val positiveButton: MaterialButton
    private val iconImageView: ImageView

    var isVisible: Boolean = false
        private set

    init {
        val view = View.inflate(context, R.layout.view_banner, this)

        contextTextView = view.findViewById(R.id.banner_content)
        negativeButton = view.findViewById(R.id.banner_button_negative)
        positiveButton = view.findViewById(R.id.banner_button_positive)
        iconImageView = view.findViewById(R.id.banner_icon)

        negativeButton.setOnClickListener {
            negativeClickListener?.onClick(it)
        }

        positiveButton.setOnClickListener {
            positiveClickListener?.onClick(it)
        }

        context.withStyledAttributes(attrs, R.styleable.MaterialBannerView, 0, 0) {
            val contentText = getString(R.styleable.MaterialBannerView_contextText)
            val negativeButtonText = getString(R.styleable.MaterialBannerView_negativeButtonText)
            val positiveButtonText = getString(R.styleable.MaterialBannerView_positiveButtonText)
            val icon = getDrawable(R.styleable.MaterialBannerView_icon)

            contextTextView.text = contentText
            if(negativeButtonText != null) {
                negativeButton.visibility = View.VISIBLE
                negativeButton.text = negativeButtonText
            } else {
                negativeButton.visibility = View.GONE
            }
            if(positiveButtonText != null) {
                positiveButton.visibility = View.VISIBLE
                positiveButton.text = positiveButtonText
            } else {
                positiveButton.visibility = View.GONE
            }
            setIcon(icon)
        }
    }

    fun show() = this.expand()

    fun dismiss() = this.collapse()

    fun setNegativeButton(@StringRes stringRes: Int?, onClickListener: (View?) -> Unit) {
        setNegativeButton(stringRes, OnClickListener { onClickListener(it) })
    }

    fun setNegativeButton(string: String?, onClickListener: (View?) -> Unit) {
        setNegativeButton(string, OnClickListener { onClickListener(it) })
    }

    fun setNegativeButton(@StringRes stringRes: Int?, onClickListener: OnClickListener?) {
        val string = stringRes?.let { context.getString(it) }
        setNegativeButton(string, onClickListener)
    }

    fun setNegativeButton(string: String?, onClickListener: OnClickListener?) {
        negativeButton.text = string
        negativeClickListener = onClickListener
        if(string != null) {
            enableNegativeButton(true)
        }
    }

    fun enableNegativeButton(enabled: Boolean) {
        negativeButton.visibility = if(enabled) View.VISIBLE else View.GONE
    }

    fun setPositiveButton(@StringRes stringRes: Int?, onClickListener: (View?) -> Unit) {
        setPositiveButton(stringRes, OnClickListener { onClickListener(it) })
    }

    fun setPositiveButton(string: String?, onClickListener: (View?) -> Unit) {
        setPositiveButton(string, OnClickListener { onClickListener(it) })
    }

    fun setPositiveButton(@StringRes stringRes: Int?, onClickListener: OnClickListener?) {
        val string = stringRes?.let { context.getString(it) }
        setPositiveButton(string, onClickListener)
    }

    fun setPositiveButton(string: String?, onClickListener: OnClickListener?) {
        positiveButton.text = string
        positiveClickListener = onClickListener
        if(string != null) {
            enablePositiveButton(true)
        }
    }

    fun enablePositiveButton(enabled: Boolean) {
        positiveButton.visibility = if(enabled) View.VISIBLE else View.GONE
    }

    fun setIcon(@DrawableRes drawableRes: Int) {
        setIcon(context.getDrawable(drawableRes))
    }

    fun setIcon(drawable: Drawable?) {
        iconImageView.setImageDrawable(drawable)
        iconImageView.visibility = if(drawable != null) View.VISIBLE else View.GONE
    }

    fun setContextText(@StringRes stringRes: Int) {
        contextTextView.setText(stringRes)
    }

    fun setContextText(text: String) {
        contextTextView.text = text
    }

    private fun View.expand() {
        measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        // The height we want to get to
        val targetHeight = measuredHeight

        // Force the height to be 0
        layoutParams.height = 0
        // Set the visibility to VISIBLE as it is most likely GONE right now
        visibility = View.VISIBLE
        val expandAnimation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                layoutParams.height = if (interpolatedTime >= 1f) {
                    isVisible = true
                    // Set it at the correct height
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else (targetHeight * interpolatedTime).toInt()
                requestLayout()
            }
            override fun willChangeBounds(): Boolean = true
        }

        expandAnimation.duration = (targetHeight.toFloat() / context.resources.displayMetrics.density).toLong()
        startAnimation(expandAnimation)
    }

    private fun View.collapse() {
        // Current height
        val initialHeight = measuredHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    isVisible = false
                    // Make it disappear when done
                    visibility = View.GONE
                } else {
                    // otherwise get the approximate height
                    layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration = (initialHeight.toFloat() / context.resources.displayMetrics.density).toLong()
        startAnimation(animation)
    }
}