package com.grapexpectations.prototype1.ui.detail

import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.grapexpectations.prototype1.data.model.ReadingPoint
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun FermentationChart(
    readings: List<ReadingPoint>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.textColor = Color.LTGRAY
                xAxis.valueFormatter = object : ValueFormatter() {
                    private val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        return try {
                            dateFormat.format(java.util.Date(value.toLong()))
                        } catch (e: Exception) {
                            ""
                        }
                    }
                }
                
                axisLeft.textColor = Color.LTGRAY
                axisRight.isEnabled = false
                legend.textColor = Color.LTGRAY
            }
        },
        update = { chart ->
            if (readings.isEmpty()) {
                chart.clear()
                return@AndroidView
            }

            val sgEntries = readings.mapNotNull { point ->
                point.sg?.let { Entry(point.timestamp.time.toFloat(), it.toFloat()) }
            }

            // For now, let's just plot SG. We can add Temp on a second axis later.
            
            if (sgEntries.isNotEmpty()) {
                val sgDataSet = LineDataSet(sgEntries, "Specific Gravity").apply {
                    color = Color.parseColor("#9C27B0") // WinePrimary
                    setCircleColor(Color.parseColor("#9C27B0"))
                    lineWidth = 2f
                    circleRadius = 3f
                    setDrawCircleHole(false)
                    valueTextColor = Color.LTGRAY
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = Color.parseColor("#9C27B0")
                    fillAlpha = 50
                }

                val lineData = LineData(sgDataSet)
                chart.data = lineData
                chart.invalidate() // Refresh
            }
        }
    )
}
