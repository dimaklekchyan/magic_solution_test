package com.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.core.R
import com.core.ui.composable.ActionButton
import com.core.ui.theme.MagicDownloaderTheme
import kotlinx.coroutines.launch
import kotlin.math.abs

private enum class OnboardingLayoutElement {
    AccentuatedBackground,
    DescriptionBlock,
    UpStraightArrow,
    DownStraightArrow,
    RightUpCurvedArrow,
    LeftUpCurvedArrow,
    RightDownCurvedArrow,
    LeftDownCurvedArrow,
}

private const val ANIMATION_DURATION_MS = 500

/**
 * Layout that shadows content, highlight a composable element witch associated with current step
 * and automatically shows description with arrows on appropriate place
 * @param modifier modifier to customize layout
 * @param controller manages state of onboarding
 * @param backgroundColor color covering the content
 * @param dashColor dashed line color around the highlighting element
 * @param content your content
 * @See rememberOnboardingController
 * @see Modifier.onboardingStep
 * @sample Sample
 * */
@Composable
fun OnboardingLayout(
    modifier: Modifier = Modifier,
    controller: OnboardingController,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    dashColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    val state = controller.state

    CompositionLocalProvider(
        LocalOnboardingController provides controller
    ) {
        OnboardingLayout(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier),
            state = state,
            backgroundColor = backgroundColor,
            dashColor = dashColor,
            content = content,
            onNextClick = remember(controller) {{
                controller.goToNextStep()
            }}
        )
    }
}

@Composable
private fun OnboardingLayout(
    modifier: Modifier = Modifier,
    state: OnboardingState,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    dashColor: Color = Color.White,
    content: @Composable () -> Unit,
    onNextClick: () -> Unit
) {
    Box(modifier = Modifier.then(modifier)) {
        content()
        AnimatedVisibility(
            visible =  state.currentStep != null,
            enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
            exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)),
        ) {
            state.currentStep?.let {
                Layout(
                    content = {
                        OnboardingLayoutContent(
                            state = state,
                            backgroundColor = backgroundColor,
                            dashColor = dashColor,
                            onNextClick = onNextClick
                        )
                    },
                    measurePolicy = onboardingMeasurePolicy { state.highlightFigure.rect }
                )
            }
        }
    }
}

@Composable
private fun OnboardingLayoutContent(
   state: OnboardingState,
   backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
   dashColor: Color = Color.White,
   onNextClick: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.currentStep) {
        alpha.animateTo(1f, animationSpec = tween(ANIMATION_DURATION_MS))
    }
    Background(
        highlightFigure = state.highlightFigure,
        backgroundColor = backgroundColor,
        dashColor = dashColor,
        modifier = Modifier.layoutId(OnboardingLayoutElement.AccentuatedBackground),
        highlightingAlpha = alpha.value
    )
    DescriptionBlock(
        step = state.currentStep!!,
        isLast = state.isLastStep,
        modifier = Modifier
            .alpha(alpha.value)
            .layoutId(OnboardingLayoutElement.DescriptionBlock),
        onNextClick = remember {{
            scope.launch {
                alpha.animateTo(0f, animationSpec = tween(ANIMATION_DURATION_MS))
                onNextClick()
            }
        }}
    )
    Arrow(
        resId = R.drawable.ic_straight_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(20.dp)
            .layoutId(OnboardingLayoutElement.UpStraightArrow),
    )
    Arrow(
        resId = R.drawable.ic_straight_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(20.dp)
            .rotate(180f)
            .layoutId(OnboardingLayoutElement.DownStraightArrow),
    )
    Arrow(
        resId = R.drawable.ic_curved_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(vertical = 10.dp)
            .layoutId(OnboardingLayoutElement.LeftUpCurvedArrow),
    )
    Arrow(
        resId = R.drawable.ic_curved_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(vertical = 10.dp)
            .rotate(180f)
            .layoutId(OnboardingLayoutElement.RightDownCurvedArrow),
    )
    Arrow(
        resId = R.drawable.ic_curved_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(vertical = 10.dp)
            .scale(-1f, 1f)
            .layoutId(OnboardingLayoutElement.RightUpCurvedArrow),
    )
    Arrow(
        resId = R.drawable.ic_curved_arrow,
        modifier = Modifier
            .alpha(alpha.value)
            .padding(vertical = 10.dp)
            .scale(1f, -1f)
            .layoutId(OnboardingLayoutElement.LeftDownCurvedArrow),
    )
}

private fun onboardingMeasurePolicy(
    highlightRectProvider: () -> Rect,
): MeasureScope.(measurables: List<Measurable>, constraints: Constraints) -> MeasureResult {
    return { measurables, constraints ->
        val backgroundPlaceable = measurables
            .first { it.layoutId == OnboardingLayoutElement.AccentuatedBackground }
            .measure(constraints)
        val descriptionPlaceable = measurables
            .first { it.layoutId == OnboardingLayoutElement.DescriptionBlock }
            .measure(constraints)

        val rect = highlightRectProvider()
        val center = constraints.maxWidth.toFloat() / 2

        val arrowPlaceableToPosition: Pair<Placeable, IntOffset>
        val descriptionPlaceableToPosition: Pair<Placeable, IntOffset>

        if (center in rect.left..rect.right) {
            val upStraightArrowPlaceable = measurables
                .first { it.layoutId == OnboardingLayoutElement.UpStraightArrow }
                .measure(constraints)
            val downStraightArrowPlaceable = measurables
                .first { it.layoutId == OnboardingLayoutElement.DownStraightArrow }
                .measure(constraints)

            val descriptionWithArrowHeight = descriptionPlaceable.height + upStraightArrowPlaceable.height
            val spaceUnderRect = constraints.maxHeight.toFloat() - rect.bottom
            val isEnoughSpaceUnderRect = spaceUnderRect >= descriptionWithArrowHeight
            val xArrowOffset = constraints.maxWidth / 2 - upStraightArrowPlaceable.width / 2

             if (isEnoughSpaceUnderRect) {
                 val arrowOffset = IntOffset(xArrowOffset, rect.bottom.toInt())
                 val descriptionOffset = IntOffset(0, arrowOffset.y + upStraightArrowPlaceable.height)
                 arrowPlaceableToPosition = upStraightArrowPlaceable to arrowOffset
                 descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
            } else {
                 val arrowOffset = IntOffset(xArrowOffset, rect.top.toInt() - downStraightArrowPlaceable.height)
                 val descriptionOffset = IntOffset(0, arrowOffset.y - descriptionPlaceable.height)
                 arrowPlaceableToPosition = downStraightArrowPlaceable to arrowOffset
                 descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
            }
        } else {
            val rightUpCurvedArrowPlaceable = measurables
                .first { it.layoutId == OnboardingLayoutElement.RightUpCurvedArrow }
                .measure(constraints)

            val descriptionWithArrowHeight = descriptionPlaceable.height + rightUpCurvedArrowPlaceable.height
            val spaceUnderRect = constraints.maxHeight.toFloat() - rect.bottom
            val isEnoughSpaceUnderRect = spaceUnderRect >= descriptionWithArrowHeight

            when {
                //upper right corner
                rect.left > center && isEnoughSpaceUnderRect -> {
                    val arrowOffset = IntOffset(rect.left.toInt() - rightUpCurvedArrowPlaceable.width, rect.center.y.toInt())
                    val descriptionOffset = IntOffset(0, arrowOffset.y + rightUpCurvedArrowPlaceable.height)
                    arrowPlaceableToPosition = rightUpCurvedArrowPlaceable to arrowOffset
                    descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
                }
                //bottom right corner
                rect.left > center -> {
                    val rightDownCurvedArrowPlaceable = measurables
                        .first { it.layoutId == OnboardingLayoutElement.RightDownCurvedArrow }
                        .measure(constraints)
                    val arrowOffset = IntOffset(rect.left.toInt() - rightDownCurvedArrowPlaceable.width, rect.center.y.toInt() - rightDownCurvedArrowPlaceable.height)
                    val descriptionOffset = IntOffset(0, arrowOffset.y - descriptionPlaceable.height)
                    arrowPlaceableToPosition = rightDownCurvedArrowPlaceable to arrowOffset
                    descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
                }
                //upper left corner
                isEnoughSpaceUnderRect -> {
                    val leftUpCurvedArrowPlaceable = measurables
                        .first { it.layoutId == OnboardingLayoutElement.LeftUpCurvedArrow }
                        .measure(constraints)

                    val arrowOffset = IntOffset(rect.right.toInt(), rect.center.y.toInt())
                    val descriptionOffset = IntOffset(0, arrowOffset.y + rightUpCurvedArrowPlaceable.height)
                    arrowPlaceableToPosition = leftUpCurvedArrowPlaceable to arrowOffset
                    descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
                }
                //bottom left corner
                else -> {
                    val leftDownCurvedArrowPlaceable = measurables
                        .first { it.layoutId == OnboardingLayoutElement.LeftDownCurvedArrow }
                        .measure(constraints)

                    val arrowOffset = IntOffset(rect.right.toInt(), rect.center.y.toInt() - leftDownCurvedArrowPlaceable.height)
                    val descriptionOffset = IntOffset(0, arrowOffset.y - descriptionPlaceable.height)
                    arrowPlaceableToPosition = leftDownCurvedArrowPlaceable to arrowOffset
                    descriptionPlaceableToPosition = descriptionPlaceable to descriptionOffset
                }
            }
        }

        layout(constraints.maxWidth, constraints.maxHeight) {
            backgroundPlaceable.placeRelative(x = 0, y = 0)
            descriptionPlaceableToPosition.first.placeRelative(descriptionPlaceableToPosition.second)
            arrowPlaceableToPosition.let { it.first.placeRelative(it.second) }
        }
    }
}

@Composable
private fun Background(
    modifier: Modifier = Modifier,
    highlightFigure: HighlightFigure,
    backgroundColor: Color = Color.Black.copy(alpha = 0.5f),
    dashColor: Color = Color.White,
    highlightingAlpha: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {} //To avoid gestures
            .drawWithCache {
                val paddingPx = highlightFigure.padding.toPx()
                val radiusPx = highlightFigure.radius.toPx()

                val cornerRadius = CornerRadius(radiusPx, radiusPx)
                val rect = highlightFigure.rect

                val appropriateRect = rect.copy(
                    left = rect.left - paddingPx,
                    top = rect.top - paddingPx,
                    right = rect.right + paddingPx,
                    bottom = rect.bottom + paddingPx
                )

                val dashWidth = 1.dp.toPx()
                val dashRect = Rect(
                    top = appropriateRect.top - dashWidth,
                    bottom = appropriateRect.bottom + dashWidth,
                    left = appropriateRect.left - dashWidth,
                    right = appropriateRect.right + dashWidth
                )

                val patchAlpha = backgroundColor.alpha * abs(highlightingAlpha - 1)

                this.onDrawBehind {
                    val path = Path().apply {
                        addRoundRect(RoundRect(rect = appropriateRect, cornerRadius = cornerRadius))
                    }
                    clipPath(
                        path = path,
                        clipOp = ClipOp.Difference
                    ) {
                        drawRect(
                            color = backgroundColor,
                            size = size
                        )
                    }
                    drawRoundRect(
                        color = dashColor.copy(alpha = highlightingAlpha),
                        topLeft = dashRect.topLeft,
                        size = dashRect.size,
                        cornerRadius = cornerRadius,
                        style = Stroke(
                            width = dashWidth,
                            pathEffect = PathEffect.dashPathEffect(
                                intervals = floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                                phase = 15.dp.toPx()
                            )
                        )
                    )
                    drawRoundRect(
                        color = backgroundColor.copy(alpha = patchAlpha),
                        topLeft = appropriateRect.topLeft,
                        size = appropriateRect.size,
                        cornerRadius = cornerRadius,
                    )
                }
            }
            .then(modifier)
    )
}

@Composable
private fun DescriptionBlock(
    modifier: Modifier = Modifier,
    step: OnboardingStep,
    isLast: Boolean,
    onNextClick: () -> Unit
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = step.description,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        val buttonResIs by remember(isLast) {
            mutableIntStateOf(
                if (isLast) {
                    R.string.onboarding_dialog_button_get_started
                } else {
                    R.string.onboarding_dialog_button_next
                }
            )
        }

        ActionButton(
            text = stringResource(id = buttonResIs),
            height = 40.dp,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 68.dp),
            onClick = onNextClick
        )
    }
}

@Composable
private fun Arrow(
    modifier: Modifier = Modifier,
    resId: Int,
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = "arrow",
        modifier = modifier
    )
}

@Composable
private fun Sample() {
    // Creating of controller with list of steps
    val controller = rememberOnboardingController(
        steps = listOf(
            OnboardingStep("key1", "Description1"),
            OnboardingStep("key2", "Description2"),
        )
    )

    // Example of starting onboarding
    LaunchedEffect(Unit) {
        controller.start()
    }

    // Just wrap your composable with OnboardingLayout
    OnboardingLayout(controller = controller) {

        Column {
            Text(
                // Use onboardingStep modifier to associate composable element with onboarding step
                // That is it!
                modifier = Modifier.onboardingStep(
                    controller = controller,
                    stepKey = "key1"
                ),
                text = "Some text"
            )
            Button(
                modifier = Modifier.onboardingStep(
                    controller = controller,
                    stepKey = "key2"
                ),
                onClick = {},
                content = {
                    Text(text = "Some button")
                }
            )
        }

    }
}