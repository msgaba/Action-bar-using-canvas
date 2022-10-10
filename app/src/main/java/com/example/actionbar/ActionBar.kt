package com.example.actionbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat


/**
 * Created by Ankita
 */
class ActionBar : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    private var x = 0       // screen width
    private var animXCoord = 100f
    private var touchXCoord = -1
    private var prevPosition = 1
    private var selectedPosition = -1
    private var width = 0f      // width of each horizontal arc
    private var start = 0f      // starting X point for horizontal line
    private var end = 0f        // ending X point for horizontal line
    private var paint = Paint().apply {
        color = Color.RED
        strokeWidth = 5F
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private var path = Path()
    private val home = ContextCompat.getDrawable(context, R.drawable.ic_home)
    private val graph = ContextCompat.getDrawable(context, R.drawable.ic_bar_chart)
    private val notif = ContextCompat.getDrawable(context, R.drawable.ic_notifications)
    private val user = ContextCompat.getDrawable(context, R.drawable.ic_user)
    private var posMap = hashMapOf<Int,Float>()     // position hashmap for starting point of each arc

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        // checking which arc area has been touched by the user
        if (touchXCoord == -1 || (touchXCoord > start && touchXCoord < start + width))
            drawArc(canvas, 0)
        else if (touchXCoord > start + width && touchXCoord < start + (2 * width))
            drawArc(canvas, 1)
        else if (touchXCoord > start + (2 * width) && touchXCoord < start + (3 * width))
            drawArc(canvas, 2)
        else if (touchXCoord > start + (3 * width) && touchXCoord < start + (4 * width))
            drawArc(canvas, 3)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        x = w
        width = (x - 200f) / 4f
        start = 100f
        end = x - 200f
        // hashmap for start left X coordinate for arc as per position
        posMap = hashMapOf(
            0 to start,
            1 to start + width,
            2 to start + (2 * width),
            3 to start + (3 * width),
        )
    }

    private fun drawArc(canvas: Canvas?, pos: Int = 0) {        // pos = {0,1,2,3}
        /** drawing path as per required position **/
        path.apply {
            moveTo(start, 300f)
            // left vertical arc
            addArc(RectF(0f, 100f, 200f, 300f), 90f, 180f)
            // top horizontal line joining left vertical line with horizontal arc
            lineTo(start + (pos * width), 100f)
            // horizontal arc
            addArc(RectF( animXCoord, 0f,  width + animXCoord, 225f), 185f, -190f)
            // top horizontal line joining horizontal arc with right vertical arc
            lineTo(end + 100f, 100f)
            // right vertical arc
            addArc(RectF(end, 100f, x.toFloat(), 300f), 270f, 180f)
            moveTo(end + 100f, 300f)
            // bottom horizontal line
            lineTo(100f, 300f)
            close()
            canvas?.drawPath(this, paint)
        }

        /** drawing circle as per required position **/
        // circle for selected icon
        canvas?.drawCircle(  width / 2 + animXCoord, 100f, (width - 40f) / 2, paint)

        /** setting icons as per required position **/
        // first icon i.e. home
        home?.bounds = Rect((start + width / 2 - 50f).toInt(), if(pos == 0) 50 else 150, (start + width / 2 + 50f).toInt(), if(pos == 0) 150 else 250)
        home?.draw(canvas!!)
        // second icon i.e. graph
        graph?.bounds = Rect((start + (3 * width) / 2 - 50f).toInt(), if(pos == 1) 50 else 150, (start + (3 * width) / 2 + 50f).toInt(), if(pos == 1) 150 else 250)
        graph?.draw(canvas!!)
        // third icon i.e. notif
        notif?.bounds = Rect((start + (5 * width) / 2 - 50f).toInt(), if(pos == 2) 50 else 150, (start + (5 * width) / 2 + 50f).toInt(), if(pos == 2) 150 else 250)
        notif?.draw(canvas!!)
        // forth icon i.e. user
        user?.bounds = Rect((start + (7 * width) / 2 - 50f).toInt(), if(pos == 3) 50 else 150, (start + (7 * width) / 2 + 50f).toInt(), if(pos == 3) 150 else 250)
        user?.draw(canvas!!)
    }

    fun changeView(x: Int) {
        touchXCoord = x
        prevPosition = selectedPosition
        if (touchXCoord == -1 || (x > start && touchXCoord < start + width))
            selectedPosition = 1
        else if (touchXCoord > start + width && touchXCoord < start + (2 * width))
            selectedPosition = 2
        else if (touchXCoord > start + (2 * width) && touchXCoord < start + (3 * width))
            selectedPosition = 3
        else if (touchXCoord > start + (3 * width) && touchXCoord < start + (4 * width))
            selectedPosition = 4
        if (selectedPosition != prevPosition) move()
    }

    private fun move() {
        val va = ValueAnimator.ofFloat(posMap[prevPosition - 1] ?: start,
            posMap[selectedPosition - 1] ?: (start + (3 * width))
        )
        va.duration = 50 //in millis
        va.addUpdateListener { animation ->
            animXCoord = animation.animatedValue as Float
            invalidate()
        }
        va.start()
    }
}