package com.orbit.navigation

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

class NavigationAccessibilityService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private var currentView: View? = null

    override fun onServiceConnected() {
        super.onServiceConnected()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showHandle()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun removeCurrentView() {
        currentView?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        currentView = null
    }

    private fun display(view: View, width: Int, height: Int) {
        removeCurrentView()

        val params = WindowManager.LayoutParams(
            dp(width),
            dp(height),
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.END or Gravity.CENTER_VERTICAL

        windowManager.addView(view, params)
        currentView = view
    }

    private fun createCircle(
        symbol: String,
        color: Int,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = symbol
            textSize = 22f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }

            setOnClickListener {
                action()
            }
        }
    }

    private fun showHandle() {
        val handle = TextView(this).apply {
            background = GradientDrawable().apply {
                setColor(Color.rgb(45, 115, 230))
                cornerRadius = dp(20).toFloat()
            }

            contentDescription = "Open Orbit Navigation"

            setOnClickListener {
                showBall()
            }
        }

        display(handle, 14, 70)
    }

    private fun showBall() {
        val ball = createCircle(
            "●",
            Color.rgb(35, 110, 230)
        ) {
            showMenu()
        }

        display(ball, 56, 56)
    }

    private fun showMenu() {
        val menu = FrameLayout(this)

        val buttons = listOf(
            Triple("△", Color.rgb(45, 120, 240)) {
                performGlobalAction(GLOBAL_ACTION_BACK)
                showHandle()
            },
            Triple("○", Color.rgb(35, 180, 110)) {
                performGlobalAction(GLOBAL_ACTION_HOME)
                showHandle()
            },
            Triple("□", Color.rgb(155, 80, 220)) {
                performGlobalAction(GLOBAL_ACTION_RECENTS)
                showHandle()
            }
        )

        val positions = listOf(
            Pair(72, 5),
            Pair(72, 135),
            Pair(5, 70)
        )

        buttons.forEachIndexed { index, item ->
            val button = createCircle(
                item.first,
                item.second,
                item.third
            )

            val params = FrameLayout.LayoutParams(
                dp(46),
                dp(46)
            )

            params.leftMargin = dp(positions[index].first)
            params.topMargin = dp(positions[index].second)

            menu.addView(button, params)
        }

        display(menu, 125, 190)
    }

    override fun onDestroy() {
        removeCurrentView()
        super.onDestroy()
    }
}
