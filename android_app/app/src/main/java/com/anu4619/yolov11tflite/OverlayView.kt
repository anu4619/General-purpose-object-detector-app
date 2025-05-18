package com.anu4619.yolov11tflite // Or your actual package name

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.color
import java.util.Locale
import kotlin.math.max

class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var results = listOf<BoundingBox>()

    // textBackgroundPaint will now get its color dynamically
    private val textBackgroundPaint = Paint()
    private val textPaint = Paint() // This will be for the black text
    private val boxPaints = mutableMapOf<String, Paint>()

    private val classColors: Map<String, Int> by lazy {
        mapOf(
            "person" to ContextCompat.getColor(context, R.color.class_person_color),
            "car" to ContextCompat.getColor(context, R.color.class_car_color),
            "bicycle" to ContextCompat.getColor(context, R.color.class_bicycle_color),
            "dog" to ContextCompat.getColor(context, R.color.class_dog_color),
            "cat" to ContextCompat.getColor(context, R.color.class_cat_color),
            // Add all your class colors from colors.xml here
            "motorcycle" to ContextCompat.getColor(context, R.color.class_motorcycle_color),
            "airplane" to ContextCompat.getColor(context, R.color.class_airplane_color),
            "bus" to ContextCompat.getColor(context, R.color.class_bus_color),
            "train" to ContextCompat.getColor(context, R.color.class_train_color),
            "truck" to ContextCompat.getColor(context, R.color.class_truck_color),
            "boat" to ContextCompat.getColor(context, R.color.class_boat_color),
            "traffic_light" to ContextCompat.getColor(context, R.color.class_traffic_light_color),
            "fire_hydrant" to ContextCompat.getColor(context, R.color.class_fire_hydrant_color),
            "stop_sign" to ContextCompat.getColor(context, R.color.class_stop_sign_color),
            "parking_meter" to ContextCompat.getColor(context, R.color.class_parking_meter_color),
            "bench" to ContextCompat.getColor(context, R.color.class_bench_color),
            "bird" to ContextCompat.getColor(context, R.color.class_bird_color),
            // "cat" already there
            // "dog" already there
            "horse" to ContextCompat.getColor(context, R.color.class_horse_color),
            "sheep" to ContextCompat.getColor(context, R.color.class_sheep_color),
            "cow" to ContextCompat.getColor(context, R.color.class_cow_color),
            "elephant" to ContextCompat.getColor(context, R.color.class_elephant_color),
            "bear" to ContextCompat.getColor(context, R.color.class_bear_color),
            "zebra" to ContextCompat.getColor(context, R.color.class_zebra_color),
            "giraffe" to ContextCompat.getColor(context, R.color.class_giraffe_color),
            "backpack" to ContextCompat.getColor(context, R.color.class_backpack_color),
            "umbrella" to ContextCompat.getColor(context, R.color.class_umbrella_color),
            "handbag" to ContextCompat.getColor(context, R.color.class_handbag_color),
            "tie" to ContextCompat.getColor(context, R.color.class_tie_color),
            "suitcase" to ContextCompat.getColor(context, R.color.class_suitcase_color),
            "frisbee" to ContextCompat.getColor(context, R.color.class_frisbee_color),
            "skis" to ContextCompat.getColor(context, R.color.class_skis_color),
            "snowboard" to ContextCompat.getColor(context, R.color.class_snowboard_color),
            "sports_ball" to ContextCompat.getColor(context, R.color.class_sports_ball_color),
            "kite" to ContextCompat.getColor(context, R.color.class_kite_color),
            "baseball_bat" to ContextCompat.getColor(context, R.color.class_baseball_bat_color),
            "baseball_glove" to ContextCompat.getColor(context, R.color.class_baseball_glove_color),
            "skateboard" to ContextCompat.getColor(context, R.color.class_skateboard_color),
            "surfboard" to ContextCompat.getColor(context, R.color.class_surfboard_color),
            "tennis_racket" to ContextCompat.getColor(context, R.color.class_tennis_racket_color),
            "bottle" to ContextCompat.getColor(context, R.color.class_bottle_color),
            "wine_glass" to ContextCompat.getColor(context, R.color.class_wine_glass_color),
            "cup" to ContextCompat.getColor(context, R.color.class_cup_color),
            "fork" to ContextCompat.getColor(context, R.color.class_fork_color),
            "knife" to ContextCompat.getColor(context, R.color.class_knife_color),
            "spoon" to ContextCompat.getColor(context, R.color.class_spoon_color),
            "bowl" to ContextCompat.getColor(context, R.color.class_bowl_color),
            "banana" to ContextCompat.getColor(context, R.color.class_banana_color),
            "apple" to ContextCompat.getColor(context, R.color.class_apple_color),
            "sandwich" to ContextCompat.getColor(context, R.color.class_sandwich_color),
            "orange" to ContextCompat.getColor(context, R.color.class_orange_color),
            "broccoli" to ContextCompat.getColor(context, R.color.class_broccoli_color),
            "carrot" to ContextCompat.getColor(context, R.color.class_carrot_color),
            "hot_dog" to ContextCompat.getColor(context, R.color.class_hot_dog_color),
            "pizza" to ContextCompat.getColor(context, R.color.class_pizza_color),
            "donut" to ContextCompat.getColor(context, R.color.class_donut_color),
            "cake" to ContextCompat.getColor(context, R.color.class_cake_color),
            "chair" to ContextCompat.getColor(context, R.color.class_chair_color),
            "couch" to ContextCompat.getColor(context, R.color.class_couch_color),
            "potted_plant" to ContextCompat.getColor(context, R.color.class_potted_plant_color),
            "bed" to ContextCompat.getColor(context, R.color.class_bed_color),
            "dining_table" to ContextCompat.getColor(context, R.color.class_dining_table_color),
            "toilet" to ContextCompat.getColor(context, R.color.class_toilet_color),
            "tv" to ContextCompat.getColor(context, R.color.class_tv_color),
            "laptop" to ContextCompat.getColor(context, R.color.class_laptop_color),
            "mouse" to ContextCompat.getColor(context, R.color.class_mouse_color),
            "remote" to ContextCompat.getColor(context, R.color.class_remote_color),
            "keyboard" to ContextCompat.getColor(context, R.color.class_keyboard_color),
            "cell_phone" to ContextCompat.getColor(context, R.color.class_cell_phone_color),
            "microwave" to ContextCompat.getColor(context, R.color.class_microwave_color),
            "oven" to ContextCompat.getColor(context, R.color.class_oven_color),
            "toaster" to ContextCompat.getColor(context, R.color.class_toaster_color),
            "sink" to ContextCompat.getColor(context, R.color.class_sink_color),
            "refrigerator" to ContextCompat.getColor(context, R.color.class_refrigerator_color),
            "book" to ContextCompat.getColor(context, R.color.class_book_color),
            "clock" to ContextCompat.getColor(context, R.color.class_clock_color),
            "vase" to ContextCompat.getColor(context, R.color.class_vase_color),
            "scissors" to ContextCompat.getColor(context, R.color.class_scissors_color),
            "teddy_bear" to ContextCompat.getColor(context, R.color.class_teddy_bear_color),
            "hair_drier" to ContextCompat.getColor(context, R.color.class_hair_drier_color),
            "toothbrush" to ContextCompat.getColor(context, R.color.class_toothbrush_color)
        )
    }
    private val defaultBoxColor = ContextCompat.getColor(context, R.color.bounding_box_color) // Fallback for box
    private val defaultTextBackgroundColor = Color.DKGRAY // Fallback for text background if box color fails

    private var bounds = Rect()

    init {
        initPaints()
    }

    fun clear() {
        results = listOf()
        invalidate()
    }

    private fun initPaints() {
        // textBackgroundPaint color will be set dynamically in draw()
        textBackgroundPaint.style = Paint.Style.FILL
        // textBackgroundPaint.textSize is not needed as its bounds are determined by textPaint

        textPaint.color = Color.BLACK // Text will always be black
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = 40f // Adjusted for potentially smaller text area, tune as needed
        textPaint.textAlign = Paint.Align.LEFT
        textPaint.isAntiAlias = true
    }

    private fun getPaintForClass(className: String): Paint {
        return boxPaints.getOrPut(className.lowercase(Locale.ROOT)) {
            Paint().apply {
                color = classColors[className.lowercase(Locale.ROOT)] ?: defaultBoxColor
                strokeWidth = 8F // Box stroke width
                style = Paint.Style.STROKE
                isAntiAlias = true
            }
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        results.forEach { result ->
            val left = result.x1 * width
            val top = result.y1 * height
            val right = result.x2 * width
            val bottom = result.y2 * height

            val boxPaint = getPaintForClass(result.clsName)
            canvas.drawRect(left, top, right, bottom, boxPaint)

            // --- Text Drawing Modifications ---
            // Set the text background color to be the same as the bounding box
            textBackgroundPaint.color = boxPaint.color // Main change here!

            val confidencePercentage = String.format(Locale.US, "%.2f", result.conf)
            val drawableText = "${result.clsName} ($confidencePercentage)"

            // Use textPaint (for black text) to measure bounds
            textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
            val textWidth = bounds.width()
            val textHeight = bounds.height() // This is the actual height of the text glyphs

            // Calculate text background position (above the box)
            val textBgRectLeft = left
            // Position text background slightly above the box top.
            // The height of the background will be textHeight + padding.
            val textBgRectBottom = top
            val textBgRectTop = top - textHeight - (BOUNDING_RECT_TEXT_PADDING * 2) // Add padding above and below text

            val textBgRectRight = left + textWidth + (BOUNDING_RECT_TEXT_PADDING * 2) // Add padding left and right of text

            // Adjust if text background goes off screen (vertically)
            val adjustedTextBgRectTop = max(0f, textBgRectTop)
            // Ensure the bottom of the background rectangle doesn't exceed the top of the box
            // This also means if adjustedTextBgRectTop is 0f, the bgBottom will be textHeight + padding
            val finalBgBottom = if (adjustedTextBgRectTop == textBgRectTop) textBgRectBottom else adjustedTextBgRectTop + textHeight + (BOUNDING_RECT_TEXT_PADDING * 2)


            canvas.drawRect(
                textBgRectLeft,
                adjustedTextBgRectTop,
                textBgRectRight,
                finalBgBottom, // Use the calculated bottom for the background
                textBackgroundPaint // This now has the class color
            )

            // Draw the black text on top of the colored background
            // Text baseline Y needs to be calculated carefully.
            // It's the top of the background + padding + textHeight (as drawText uses baseline)
            canvas.drawText(
                drawableText,
                textBgRectLeft + BOUNDING_RECT_TEXT_PADDING, // Padding from left
                finalBgBottom - BOUNDING_RECT_TEXT_PADDING, // Position text baseline within the background
                textPaint // This paint is configured for black text
            )
        }
    }

    fun setResults(boundingBoxes: List<BoundingBox>) {
        results = boundingBoxes
        invalidate()
    }

    companion object {
        private const val BOUNDING_RECT_TEXT_PADDING = 10 // Increased padding a bit for aesthetics
    }
}