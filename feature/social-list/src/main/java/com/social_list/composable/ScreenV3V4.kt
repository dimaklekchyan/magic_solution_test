package com.social_list.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.core.R
import com.core.ui.composable.Content
import com.core.ui.composable.MagicMenu
import com.core.ui.composable.MagicMenuItem
import com.core.ui.composable.input_link.GlobalInputLinkNew
import com.core.ui.model.Social
import com.core.ui.modifier.clickableSingle
import com.core.ui.theme.MagicDownloaderTheme
import com.onboarding.LocalOnboardingController
import com.onboarding.OnboardingStep
import com.onboarding.onboardingStep
import com.social_list.SocialListEvent

@Composable
internal fun ScreenV3V4(
    onClick: (SocialListEvent) -> Unit,
) {
    val scrollState = rememberScrollState()
    var isShowLogo by remember { mutableStateOf(true) }

    val onboardingController = LocalOnboardingController.current

    Content {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 4.dp),
                verticalAlignment = Alignment.Top,
                modifier = Modifier
                    .align(alignment = Alignment.End)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(size = 48.dp)
                        .clip(shape = MaterialTheme.shapes.extraLarge)
                        .clickableSingle { }
                        .padding(all = 4.dp)
                        .padding(all = 6.dp)
                        .border(width = 2.dp, shape = CircleShape, color = Color(0xFFFF9901))
                        .onboardingStep(
                            controller = onboardingController,
                            stepKey = "info",
                            highlightingRadius = 28.dp,
                            highlightingPadding = 5.dp
                        )
                )

                MagicMenu(
                    items = listOf(
                        MagicMenuItem.Tutorial to { },
                        MagicMenuItem.HomeManual to { onClick(SocialListEvent.OnHomeManualClicked) },
                    )
                )
            }

            Spacer(modifier = Modifier.height(height = 5.dp))

            Spacer(modifier = Modifier.weight(weight = 5f))

            if (isShowLogo) {
                Logo()
            }

            Spacer(modifier = Modifier.height(height = 10.dp))

            Spacer(modifier = Modifier.weight(weight = 10f))

            Box(
                contentAlignment = Alignment.TopStart,
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Min)
            ) {
                GlobalInputLinkNew(
                    type = remember { mutableStateOf(Social.Websites) },
                    placeholderText = R.string.search_or_type_url,
                    readOnly = true,
                    modifier = Modifier
                        .onboardingStep(
                            controller = onboardingController,
                            stepKey = "input_link",
                            highlightingRadius = 50.dp
                        )
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clickableSingle(ripple = false) { }
                )
            }

            Spacer(modifier = Modifier.height(height = 14.dp))

            Spacer(modifier = Modifier.weight(weight = 10f))

            Text(
                text = stringResource(id = R.string.or_download_from),
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight(500),
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(height = 10.dp))

            Spacer(modifier = Modifier.weight(weight = 6f))

            SocialsV4(
                modifier = Modifier.onboardingStep(
                    controller = onboardingController,
                    stepKey = "social_media",
                    highlightingRadius = 24.dp
                )
            )

            Spacer(modifier = Modifier.height(height = 16.dp))

            NativeAdView()
        }
    }

    LaunchedEffect(key1 = scrollState.maxValue) {
        if (scrollState.maxValue > 0) isShowLogo = false
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenNewPreview() {
    MagicDownloaderTheme {
        ScreenV3V4 {}
    }
}