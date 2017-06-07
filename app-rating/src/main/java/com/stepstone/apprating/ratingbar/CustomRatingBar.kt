/*
Copyright 2017 StepStone Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.stepstone.apprating.ratingbar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.stepstone.apprating.R
import com.stepstone.apprating.common.Preconditions
import com.stepstone.apprating.listener.OnRatingBarChangedListener
import java.util.*

/**
 * This class is a custom rating bar. It handles displaying of
 * stars, tinting etc.
 */
class CustomRatingBar(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val starList = ArrayList<StarButton>()

    private val container: LinearLayout

    private var numStars: Int = 0

    var rating: Float = 0.0f
        private set

    private var isIndicator: Boolean = false

    private var onRatingBarChangedListener: OnRatingBarChangedListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.component_custom_rating_bar, this)
        container = findViewById(R.id.rating_bar_container) as LinearLayout
    }

    private fun addStars(numberOfAll: Int, numberOfChecked: Int) {
        Preconditions.checkArgument(numberOfChecked <= numberOfAll, "wrong argument")

        starList.clear()
        container.removeAllViews()

        for (index in 0..numberOfAll - 1) {
            addStar()
                    .setCheckedWithoutAnimation(index < numberOfChecked)
                    .setColor(getThemeAccentColor(context))
                    .setOnClickListener(OnStarClickedHandler(index + 1))
        }
    }

    private fun addStar(): StarButton {
        val starButton = StarButton(context)
        starButton.layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        starList.add(starButton)
        container.addView(starButton)
        return starButton
    }

    fun setNumStars(numStars: Int) {
        this.numStars = numStars

        addStars(numStars, 0)
    }

    fun setRating(rating: Int, withAnimation : Boolean = false) {
        this.rating = rating.toFloat()

        if (rating <= starList.size) {
            for (index in starList.indices) {
                if (withAnimation) {
                    starList[index].setChecked(index < rating)
                } else {
                    starList[index].setCheckedWithoutAnimation(index < rating)
                }
            }
        }

        onRatingBarChangedListener!!.onRatingChanged(rating)
    }

    fun setIsIndicator(isIndicator: Boolean) {
        this.isIndicator = isIndicator
    }

    fun setOnRatingBarChangeListener(onRatingBarChangedListener: OnRatingBarChangedListener) {
        this.onRatingBarChangedListener = onRatingBarChangedListener
    }

    private fun getThemeAccentColor(context: Context): Int {
        val colorAttr: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorAccent
        } else {
            //Get colorAccent defined for AppCompat
            colorAttr = context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        }
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }

    private inner class OnStarClickedHandler(private val number: Int) : View.OnClickListener {

        override fun onClick(v: View) {
            setRating(number, true)
        }
    }
}