package com.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.core.R
import com.core.ui.utils.isLandscape
import com.home.bottom_bar.AppBottomBar
import com.home.bottom_bar.AppBottomBarItem
import com.home.nav_graph.homeNavGraph
import com.onboarding.OnboardingLayout
import com.onboarding.OnboardingStep
import com.onboarding.rememberOnboardingController

@Composable
internal fun HomeScreen(parentNavController: NavController) {
    Screen(parentNavController)
}

@Composable
private fun Screen(parentNavController: NavController) {
    val navController: NavHostController = rememberNavController()

    fun NavHostController.bottomBarClick(item: AppBottomBarItem, currentRoute: String?) {
        if (item.route == currentRoute) return

        try {
            navigate(item.route) {
                popUpTo(AppBottomBarItem.SocialList.route)

                launchSingleTop = true
                restoreState = true
            }
        } catch (ignore: Exception) {
        }
    }

    val onboardingController = rememberOnboardingController(
        steps = listOf(
            OnboardingStep("input_link", stringResource(id = R.string.onboarding_dialog_input_link_description)),
            OnboardingStep("social_media", stringResource(id = R.string.onboarding_dialog_social_media_description)),
            OnboardingStep("info", stringResource(id = R.string.onboarding_dialog_info_description)),
            OnboardingStep("downloads", stringResource(id = R.string.onboarding_dialog_downloads_link_description)),
        )
    )

    OnboardingLayout(
        controller = onboardingController
    ) {
        Scaffold(
            bottomBar = {
                AppBottomBar(
                    navController = navController,
                    onClick = navController::bottomBarClick
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            val paddingBottom =
                if (isLandscape()) 0.dp
                else paddingValues.calculateBottomPadding()

            NavHost(
                navController = navController,
                startDestination = AppBottomBarItem.SocialList.route,
                builder = { homeNavGraph(navController = navController, parentNavController = parentNavController) },
                modifier = Modifier.padding(bottom = paddingBottom)
            )
        }
    }
}