package com.app.gym

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ConfettiView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val particles = mutableListOf<Particle>()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val starPath = Path()

    private var animator: ValueAnimator? = null

    private val colors = intArrayOf(
        Color.parseColor("#FF5722"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#FFC107"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#8BC34A"),
        Color.parseColor("#CDDC39")
    )

    private enum class Shape {
        RIBBON,
        STAR,
        RECTANGLE
    }

    private data class Particle(
        var x: Float,
        var y: Float,

        var vx: Float,
        var vy: Float,

        var size: Float,

        var color: Int,

        var rotation: Float,
        var rotationSpeed: Float,

        val shape: Shape,

        var alpha: Float = 255f
    )

    /**
     * Starts a new celebration from both poppers.
     *
     * Coordinates MUST be in this ConfettiView's coordinate system.
     */
    fun burst(
        leftX: Float,
        leftY: Float,
        rightX: Float,
        rightY: Float
    ) {
        stopAnimation()
        clearParticles()

        /*
         * FIRST BURST:
         * Both poppers immediately emit their
         * ribbons/stars from their nozzles.
         */
        generateParticles(
            originX = leftX,
            originY = leftY,
            isLeft = true
        )

        generateParticles(
            originX = rightX,
            originY = rightY,
            isLeft = false
        )

        startAnimation()
    }

    private fun generateParticles(
        originX: Float,
        originY: Float,
        isLeft: Boolean
    ) {
        val count = 55
        val baseAngle = if (isLeft) 25f else -25f
        val spread = 60f

        repeat(count) {

            val angleDeg =
                baseAngle + (Random.nextFloat() * 2f - 1f) * spread

            val angleRad =
                Math.toRadians(angleDeg.toDouble())

            val speed =
                Random.nextFloat() * 9f + 7f

            val horizontalDirection =
                if (isLeft) 1f else -1f

            val vx =
                horizontalDirection *
                        Random.nextFloat() * 4.5f +
                        Random.nextFloat() * 2f *
                        horizontalDirection

            val vy =
                -(Random.nextFloat() * 12f + 14f)

            val shape =
                when (Random.nextInt(100)) {
                    in 0..59 -> Shape.RIBBON
                    in 60..89 -> Shape.STAR
                    else -> Shape.RECTANGLE
                }

            particles.add(
                Particle(
                    /*
                     * THIS IS THE IMPORTANT PART.
                     *
                     * Every ribbon/star is born at the
                     * exact popper nozzle.
                     */
                    x = originX,
                    y = originY,

                    vx = vx,
                    vy = vy,

                    /*
                     * Large visible party pieces.
                     */
                    size =
                        Random.nextFloat() * 16f + 18f,

                    color =
                        colors[
                            Random.nextInt(colors.size)
                        ],

                    rotation =
                        Random.nextFloat() * 360f,

                    rotationSpeed =
                        (Random.nextFloat() - 0.5f) * 22f,

                    shape = shape
                )
            )
        }
    }

    private fun startAnimation() {

        animator?.cancel()

        /*
         * 10 seconds.
         *
         * Physics continues for the whole duration.
         */
        animator =
            ValueAnimator.ofFloat(0f, 1f).apply {

                duration = 10_000L

                interpolator =
                    LinearInterpolator()

                addUpdateListener {

                    updatePhysics()

                    invalidate()
                }

                start()
            }
    }

    private fun updatePhysics() {

        /*
         * Lower gravity gives a proper
         * "fly → peak → fall" effect.
         */
        val gravity = 0.42f

        val iterator =
            particles.iterator()

        while (iterator.hasNext()) {

            val particle = iterator.next()

            /*
             * Move.
             */
            particle.x += particle.vx
            particle.y += particle.vy

            /*
             * Gravity.
             */
            particle.vy += gravity

            /*
             * Rotation.
             */
            particle.rotation +=
                particle.rotationSpeed

            /*
             * IMPORTANT:
             *
             * Do NOT fade these particles quickly.
             *
             * They should remain visible while
             * falling for the complete celebration.
             */
            particle.alpha = 255f

            /*
             * Only remove once they have actually
             * gone below the ConfettiView.
             */
            if (particle.y > height + 100f) {
                iterator.remove()
            }
        }
    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        for (particle in particles) {

            paint.color = particle.color

            paint.alpha =
                particle.alpha
                    .toInt()
                    .coerceIn(0, 255)

            canvas.save()

            canvas.translate(
                particle.x,
                particle.y
            )

            canvas.rotate(
                particle.rotation
            )

            when (particle.shape) {

                Shape.RIBBON -> {
                    drawRibbon(
                        canvas,
                        particle.size
                    )
                }

                Shape.STAR -> {
                    drawStar(
                        canvas,
                        particle.size
                    )
                }

                Shape.RECTANGLE -> {
                    drawRectangle(
                        canvas,
                        particle.size
                    )
                }
            }

            canvas.restore()
        }
    }

    /**
     * Draws an actual long party ribbon,
     * NOT a tiny dot.
     */
    private fun drawRibbon(
        canvas: Canvas,
        size: Float
    ) {

        val width = size * 0.38f
        val height = size * 1.8f

        val path = Path()

        path.moveTo(
            -width,
            -height / 2f
        )

        path.cubicTo(
            width,
            -height * 0.30f,
            -width,
            0f,
            width,
            height * 0.18f
        )

        path.cubicTo(
            -width,
            height * 0.32f,
            width,
            height * 0.43f,
            0f,
            height / 2f
        )

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = width
        paint.strokeCap = Paint.Cap.ROUND

        canvas.drawPath(
            path,
            paint
        )

        paint.style = Paint.Style.FILL
    }

    /**
     * Proper 5-point star.
     */
    private fun drawStar(
        canvas: Canvas,
        size: Float
    ) {

        starPath.reset()

        val outerRadius = size * 0.62f
        val innerRadius = outerRadius * 0.42f

        val points = 10

        for (i in 0 until points) {

            val angle =
                Math.toRadians(
                    (-90.0 + i * 36.0)
                )

            val radius =
                if (i % 2 == 0) {
                    outerRadius
                } else {
                    innerRadius
                }

            val x =
                cos(angle).toFloat() * radius

            val y =
                sin(angle).toFloat() * radius

            if (i == 0) {
                starPath.moveTo(x, y)
            } else {
                starPath.lineTo(x, y)
            }
        }

        starPath.close()

        canvas.drawPath(
            starPath,
            paint
        )
    }

    private fun drawRectangle(
        canvas: Canvas,
        size: Float
    ) {

        canvas.drawRect(
            -size * 0.22f,
            -size * 0.55f,
            size * 0.22f,
            size * 0.55f,
            paint
        )
    }

    /**
     * Stops the current particle animation.
     */
    fun stopAnimation() {

        animator?.cancel()
        animator = null
    }

    override fun onDetachedFromWindow() {

        stopAnimation()
        clearParticles()

        super.onDetachedFromWindow()
    }

    /**
     * Clears all existing ribbons/stars.
     */
    fun clearParticles() {

        particles.clear()

        invalidate()
    }
}