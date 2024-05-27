package com.onboarding

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * To associate composable function with step
 * @param controller to set figure of element to highlight
 * @param stepKey to associate composable element with step
 * @param highlightingRadius is radius of element. Unfortunately it is not available to get CornerRadius by modifier
 * @param highlightingPadding is padding between dash and element
 * */
fun Modifier.onboardingStep(
    controller: OnboardingController,
    stepKey: String,
    highlightingRadius: Dp = 0.dp,
    highlightingPadding: Dp = 0.dp,
): Modifier {
    return if (controller.state.currentStep?.key == stepKey) {
        this.onGloballyPositioned {
            val rect: Rect = it.boundsInRoot()
            controller.setHighlightFigure(
                HighlightFigure(rect, highlightingRadius, highlightingPadding)
            )
        }
    } else this
}