package com.mykameal.planner.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class SpendingChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val totalPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var chartData = listOf<BarData>()
    private var totalSpent = 0f
    private var savings = 0f
    private var maxValue = 700f
    private var barWidth = 0f
    private var chartArea = RectF()

    // Colors matching the image
    private val orangeColor = Color.parseColor("#FFA500")
    private val greenColor = Color.parseColor("#32CD32")
    private val redColor = Color.parseColor("#FF4040")

    init {
        setupPaints()
    }

    private fun setupPaints() {
        gridPaint.apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 1f
            pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }

        textPaint.apply {
            color = Color.BLACK
            textSize = 35f
            textAlign = Paint.Align.CENTER
        }

        titlePaint.apply {
            color = Color.BLACK
            textSize = 45f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        totalPaint.apply {
            color = Color.BLACK
            textSize = 40f
        }

        barPaint.style = Paint.Style.FILL
    }

    fun setData(data: List<BarData>, total: Float, savingsAmount: Float) {
        chartData = data
        totalSpent = total
        savings = savingsAmount
        invalidate()
    }

 /*   override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val leftPadding = 120f
        val bottomPadding = 150f
        val topPadding = 200f  // Extra space for title and total

        chartArea.set(
            leftPadding,
            topPadding,
            width.toFloat() - 50f,
            height.toFloat() - bottomPadding
        )

        barWidth = (chartArea.width() - (chartData.size + 1) * 30f) / chartData.size
    }*/

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val leftPadding = 120f
        val bottomPadding = 150f
        val topPadding = 200f  // Extra space for title and total

        chartArea.set(
            leftPadding,
            topPadding,
            width.toFloat() - 50f,
            height.toFloat() - bottomPadding
        )

        // Calculate bar width and margin dynamically
        val totalSpacing = 30f * (chartData.size + 1)  // Total spacing between bars
        barWidth = (chartArea.width() - totalSpacing) / chartData.size
    }





    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawGrid(canvas)
        drawBars(canvas)
        drawAxisLabels(canvas)
    }

    private fun drawGrid(canvas: Canvas) {
        val gridLines = chartData.size
        val stepSize = chartArea.height() / gridLines
        val valueStep = maxValue / gridLines

        // Create a new Paint for the solid black vertical line
        val verticalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK // Set the color to black
            style = Paint.Style.STROKE // Solid line style
            strokeWidth = 2f // Set the desired width of the line
        }

        // Draw vertical border for price labels
        canvas.drawLine(
            chartArea.left - 0f, // A bit further left to leave space for labels
            chartArea.top,
            chartArea.left - 0f,
            chartArea.bottom,
            verticalLinePaint // Use the solid black paint
        )

        // Draw horizontal grid lines and labels
        for (i in 0..gridLines) {
            val y = chartArea.bottom - (stepSize * i)
            canvas.drawLine(chartArea.left, y, chartArea.right, y, gridPaint)

            if (i > 0) {
                val value = (valueStep * i).toInt()
                val label = "$$value"
                canvas.drawText(label, chartArea.left - 70f, y + 10f, textPaint) // Adjust position for price labels
            }
        }
    }

  /*  private fun drawBars(canvas: Canvas) {
        chartData.forEachIndexed { index, data ->
            // Adjust the margin between bars here
            val barMargin = 0f // Set a smaller margin (or 0f to remove it completely)

            val left = chartArea.left + index * (barWidth + barMargin)
            val top = chartArea.bottom - (data.value / maxValue * chartArea.height())
            val right = left + barWidth
            val bottom = chartArea.bottom
            val cornerRadius = 20f // Radius for the rounded top corners

            barPaint.color = data.color

            // Draw the top rounded corners using a custom path
            val path = Path()
            path.moveTo(left, bottom)
            path.lineTo(left, top + cornerRadius)  // Start from left, just below the rounded corner
            path.arcTo(left, top, left + cornerRadius * 2, top + cornerRadius * 2, 180f, 90f, false) // Top left corner
            path.lineTo(right - cornerRadius, top) // Draw line from the rounded corner to the right
            path.arcTo(right - cornerRadius * 2, top, right, top + cornerRadius * 2, 270f, 90f, false) // Top right corner
            path.lineTo(right, bottom) // Draw line down to the bottom
            path.close() // Close the path

            canvas.drawPath(path, barPaint)

            // Draw value above the bar
            canvas.drawText(
                "$${data.value.toInt()}",
                left + barWidth / 2,
                top - 20f,
                textPaint
            )
        }
    }*/

    private fun drawBars(canvas: Canvas) {
        chartData.forEachIndexed { index, data ->
            // Calculate the left position of the bar
            val left = chartArea.left + 30f * (index + 1) + index * barWidth
            val top = chartArea.bottom - (data.value / maxValue * chartArea.height())
            val right = left + barWidth
            val bottom = chartArea.bottom
            val cornerRadius = 20f

            barPaint.color = data.color

            // Draw rounded bars
            val path = Path()
            path.moveTo(left, bottom)
            path.lineTo(left, top + cornerRadius)
            path.arcTo(left, top, left + cornerRadius * 2, top + cornerRadius * 2, 180f, 90f, false)
            path.lineTo(right - cornerRadius, top)
            path.arcTo(right - cornerRadius * 2, top, right, top + cornerRadius * 2, 270f, 90f, false)
            path.lineTo(right, bottom)
            path.close()

            canvas.drawPath(path, barPaint)

            // Draw value above the bar
            canvas.drawText(
                "$${data.value.toInt()}",
                left + barWidth / 2,
                top - 20f,
                textPaint
            )
        }
    }


    /*private fun drawAxisLabels(canvas: Canvas) {
        // Define the Y position for the horizontal line (just below the graph area)
        val lineY = chartArea.bottom + 0f  // Adjust this to be just below the graph

        // Draw the horizontal black line just below the graph
        val horizontalLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK // Set the color to black
            style = Paint.Style.STROKE // Line style
            strokeWidth = 2f // Set the width of the line
        }

        // Draw the line across the entire chart
        canvas.drawLine(chartArea.left, lineY, chartArea.right, lineY, horizontalLinePaint)

        // Adjust Y position for the date labels
        val dateLabelY = lineY + 35f  // Slightly below the line for proper alignment
        val subLabelY =
            dateLabelY + 35f  // Position for the second line of the label (e.g., "June")

        // Loop through and draw the labels for each bar
        chartData.forEachIndexed { index, data ->
            // Calculate the center X position for the bar
            val barCenterX = chartArea.left + index * (barWidth + 30f) + barWidth / 2

            // Measure the width of the date text
            textPaint.textSize = 35f
            val dateTextWidth = textPaint.measureText(data.date)

            // Measure the width of the month text
            val monthTextWidth = textPaint.measureText(data.month)

            // Calculate the X positions to center the text
            val dateTextX = barCenterX - dateTextWidth / 2
            val monthTextX = barCenterX - monthTextWidth / 2

            // Draw the date label
            canvas.drawText(data.date, dateTextX, dateLabelY, textPaint)

            // Draw the month label under the date label
            canvas.drawText(data.month, monthTextX, subLabelY, textPaint)
        }
    }*/

    /*private fun drawAxisLabels(canvas: Canvas) {
        val lineY = chartArea.bottom + 10f  // Slightly below the graph
        val dateLabelY = lineY + 35f
        val subLabelY = dateLabelY + 35f

        chartData.forEachIndexed { index, data ->
            // Calculate the center of the bar
            val barCenterX = chartArea.left + 30f * (index + 1) + index * barWidth + barWidth / 2

            // Draw date and month labels
            canvas.drawText(data.date, barCenterX, dateLabelY, textPaint)
            canvas.drawText(data.month, barCenterX, subLabelY, textPaint)
        }
    }*/

    private fun drawAxisLabels(canvas: Canvas) {
        // Calculate positions
        val lineY = chartArea.bottom + 10f  // Slightly below the graph
        val dateLabelY = lineY + 35f
        val subLabelY = dateLabelY + 35f
        val blackLineY = dateLabelY - 15f  // Adjust the vertical position for the line

        // Draw the horizontal black line
        canvas.drawLine(
            chartArea.left,
            blackLineY,
            chartArea.right,
            blackLineY,
            textPaint.apply { color = Color.BLACK; strokeWidth = 5f } // Black color and thick line
        )

        // Draw the labels
        chartData.forEachIndexed { index, data ->
            // Calculate the center of the bar
            val barCenterX = chartArea.left + 30f * (index + 1) + index * barWidth + barWidth / 2

            // Draw date and month labels
            canvas.drawText(data.date, barCenterX, dateLabelY, textPaint)
            canvas.drawText(data.month, barCenterX, subLabelY, textPaint)
        }
    }



    data class BarData(
        val value: Float,
        val date: String,
        val month: String,
        val color: Int
    )
}