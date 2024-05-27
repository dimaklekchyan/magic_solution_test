package com.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class OnboardingStep(
    val key: String,
    val description: String
)

internal data class HighlightFigure(
    val rect: Rect,
    val radius: Dp,
    val padding: Dp
) {
    companion object {
        val None = HighlightFigure(
            rect = Rect(0f, 0f, 0f, 0f),
            radius = 0.dp,
            padding = 0.dp
        )
    }
}

@Stable
internal data class OnboardingState(
    val steps: List<OnboardingStep>,
    val currentStep: OnboardingStep? = null,
    val isLastStep: Boolean = false,
    val highlightFigure: HighlightFigure = HighlightFigure.None
)

@Stable
class OnboardingController(
    steps: List<OnboardingStep>
) {

    internal var state by mutableStateOf(OnboardingState(steps))
        private set

    fun start() {
        if (state.currentStep == null) {
            goToNextStep()
        } else {
            throw IllegalStateException("Can't start onboarding while in process")
        }
    }

    internal fun setHighlightFigure(figure: HighlightFigure) {
        state = state.copy(highlightFigure = figure)
    }

    internal fun goToNextStep() {
        val indexOfCurrentStep = state.steps.indexOf(state.currentStep)

        val nextStep = when(indexOfCurrentStep) {
            -1 -> state.steps[0]
            state.steps.lastIndex -> null
            else -> {
                val nextIndex = indexOfCurrentStep + 1
                state = state.copy(isLastStep = nextIndex == state.steps.lastIndex)
                state.steps[nextIndex]
            }
        }

        state = state.copy(currentStep = nextStep)
    }
}

/** To create controller and set list of steps */
@Composable
fun rememberOnboardingController(
    steps: List<OnboardingStep>
): OnboardingController {
    return remember {
        OnboardingController(steps)
    }
}

val LocalOnboardingController = staticCompositionLocalOf<OnboardingController> {
    error("No onboarding controller provided")
}