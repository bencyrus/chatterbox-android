package com.chatterboxtalk.ui.views

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Hammer
import compose.icons.fontawesomeicons.solid.Clone
import compose.icons.fontawesomeicons.solid.WaveSquare
import com.chatterboxtalk.core.security.TokenManager
import com.chatterboxtalk.ui.theme.AppColors
import com.chatterboxtalk.ui.theme.AppTypography

sealed class TabDestination(val route: String) {
    data object History : TabDestination("history")
    data object Subjects : TabDestination("subjects")
    data object Settings : TabDestination("settings")
    data object Debug : TabDestination("debug")
}

@Composable
fun RootTabView(tokenManager: TokenManager) {
    val navController = rememberNavController()
    val ctx = LocalContext.current
    val items = listOf(TabDestination.History, TabDestination.Subjects, TabDestination.Settings, TabDestination.Debug)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val density = LocalDensity.current

    data class TabMetrics(val left: androidx.compose.ui.unit.Dp, val width: androidx.compose.ui.unit.Dp, val height: androidx.compose.ui.unit.Dp)
    val tabMetrics = remember { mutableStateMapOf<String, TabMetrics>() }
    var containerPosInRoot by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController,
            startDestination = TabDestination.Subjects.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(TabDestination.History.route) {
                Box(modifier = Modifier.padding(bottom = 80.dp)) {
                    // Placeholder for HistoryView
                    Text("History View Placeholder", modifier = Modifier.align(Alignment.Center))
                }
            }
            composable(TabDestination.Subjects.route) {
                Box(modifier = Modifier.padding(bottom = 80.dp)) {
                    SubjectsView(tokenManager)
                }
            }
            composable(TabDestination.Settings.route) {
                Box(modifier = Modifier.padding(bottom = 80.dp)) {
                    SettingsView(tokenManager)
                }
            }
            composable(TabDestination.Debug.route) {
                Box(modifier = Modifier.padding(bottom = 80.dp)) {
                    // Placeholder for DebugView
                    Text("Debug View Placeholder", modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Custom Floating Bottom Navigation Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                // Slightly taller so the selected pill has breathing room.
                .height(72.dp)
                .drawBehind {
                    drawIntoCanvas { canvas ->
                        val paint = Paint()
                        val frameworkPaint = paint.asFrameworkPaint()
                        frameworkPaint.color = Color.Black.copy(alpha = 0.1f).toArgb()
                        frameworkPaint.setShadowLayer(
                            16.dp.toPx(),
                            0f,
                            0f,
                            Color.Black.copy(alpha = 0.25f).toArgb()
                        )
                        canvas.drawRoundRect(
                            0f,
                            0f,
                            size.width,
                            size.height,
                            size.height / 2,
                            size.height / 2,
                            paint
                        )
                    }
                }
                .clip(CircleShape)
                .background(AppColors.PageBackground),
            contentAlignment = Alignment.Center
        ) {
            // Inner container so we can animate a single moving "pill" (iOS-like slide) without flashing.
            Box(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .onGloballyPositioned { coords ->
                        containerPosInRoot = coords.positionInRoot()
                    }
            ) {
                val selectedRoute = items.firstOrNull { item ->
                    currentDestination?.hierarchy?.any { it.route == item.route } == true
                }?.route

                val metrics = selectedRoute?.let { tabMetrics[it] }
                val indicatorLeft by animateDpAsState(
                    targetValue = metrics?.left ?: 0.dp,
                    // Faster, smoother slide (less lag / no “flash then move” feel).
                    animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing),
                    label = "indicatorLeft"
                )
                val indicatorWidth by animateDpAsState(
                    targetValue = metrics?.width ?: 0.dp,
                    animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing),
                    label = "indicatorWidth"
                )
                val indicatorHeight by animateDpAsState(
                    targetValue = metrics?.height ?: 0.dp,
                    animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing),
                    label = "indicatorHeight"
                )

                if (metrics != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .graphicsLayer {
                                translationX = with(density) { indicatorLeft.toPx() }
                            }
                            .size(width = indicatorWidth, height = indicatorHeight)
                            .clip(CircleShape)
                            .background(AppColors.BadgeBackground)
                    )
                }

                // Drive icon/text highlight from the moving pill to avoid “selected first, then move” flash.
                val indicatorLeftPx = with(density) { indicatorLeft.toPx() }
                val indicatorWidthPx = with(density) { indicatorWidth.toPx() }
                val indicatorRightPx = indicatorLeftPx + indicatorWidthPx

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        val label = when (item) {
                            TabDestination.History -> Strings.Tabs.history(ctx)
                            TabDestination.Subjects -> Strings.Tabs.subjects(ctx)
                            TabDestination.Settings -> Strings.Tabs.settings(ctx)
                            TabDestination.Debug -> Strings.Tabs.debug(ctx)
                        }
                        val iconVector = when (item) {
                            TabDestination.History -> FontAwesomeIcons.Solid.WaveSquare
                            TabDestination.Subjects -> FontAwesomeIcons.Solid.Clone
                            TabDestination.Settings -> FontAwesomeIcons.Solid.Cog
                            TabDestination.Debug -> FontAwesomeIcons.Solid.Hammer
                        }

                        val metricsForItem = tabMetrics[item.route]
                        val overlapFraction: Float = if (metricsForItem != null && indicatorWidthPx > 0f) {
                            val tabLeftPx = with(density) { metricsForItem.left.toPx() }
                            val tabWidthPx = with(density) { metricsForItem.width.toPx() }.coerceAtLeast(1f)
                            val tabRightPx = tabLeftPx + tabWidthPx
                            val overlap = (kotlin.math.min(indicatorRightPx, tabRightPx) - kotlin.math.max(indicatorLeftPx, tabLeftPx))
                                .coerceAtLeast(0f)
                            (overlap / tabWidthPx).coerceIn(0f, 1f)
                        } else {
                            // Don't instantly flip colors before the pill reaches the tab.
                            0f
                        }

                        val tint = lerp(AppColors.TextSecondary, AppColors.TextPrimary, overlapFraction)
                        val scale = 1f + 0.02f * overlapFraction
                        val interactionSource = remember(item.route) { MutableInteractionSource() }

                        Box(
                            modifier = Modifier
                                .onGloballyPositioned { coords ->
                                    val pos = coords.positionInRoot()
                                    val leftPx = pos.x - containerPosInRoot.x
                                    with(density) {
                                        tabMetrics[item.route] = TabMetrics(
                                            left = leftPx.toDp(),
                                            width = coords.size.width.toDp(),
                                            height = coords.size.height.toDp()
                                        )
                                    }
                                }
                                .clip(CircleShape)
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                }
                                // Disable ripple press indication (it reads as a "flash" on tap).
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    if (!selected) navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                                .padding(vertical = 8.dp, horizontal = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = label,
                                    tint = tint,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = label,
                                    style = if (overlapFraction >= 0.6f) {
                                        AppTypography.caption.copy(fontWeight = FontWeight.Medium)
                                    } else {
                                        AppTypography.caption
                                    },
                                    color = tint
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
