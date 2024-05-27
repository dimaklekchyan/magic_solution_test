package com.social_list

import com.mvi.ScreenEvent
import com.mvi.ScreenSingleEvent
import com.mvi.ScreenState

class SocialListState : ScreenState

sealed class SocialListEvent : ScreenEvent {
    data object OnHomeManualClicked: SocialListEvent()
}

sealed class SocialListSingleEvent : ScreenSingleEvent {
    data object StartOnboarding: SocialListSingleEvent()
}