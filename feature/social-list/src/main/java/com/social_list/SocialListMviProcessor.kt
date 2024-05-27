package com.social_list

import com.mvi.MviProcessor
import com.onboarding.OnboardingController

class SocialListViewModel : MviProcessor<SocialListState, SocialListEvent, SocialListSingleEvent>() {

    override fun initialState(): SocialListState {
        return SocialListState()
    }

    override fun reduce(event: SocialListEvent, state: SocialListState): SocialListState {
        when (event) {
            is SocialListEvent.OnHomeManualClicked -> onHomeManualClicked()
        }
        return state
    }

    override suspend fun handleEvent(event: SocialListEvent, state: SocialListState): SocialListEvent? {
        return null
    }

    private fun onHomeManualClicked() {
        triggerSingleEvent(SocialListSingleEvent.StartOnboarding)
    }
}