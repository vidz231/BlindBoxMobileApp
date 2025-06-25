package com.vidz.blindboxapp.presentation.app

import androidx.navigation.NavController
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import com.fpl.base.viewmodel.BaseViewModel
import com.vidz.base.navigation.DestinationRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BlindboxAppViewModel @Inject constructor() : BaseViewModel<
        BlindboxAppViewModel.BlindBoxViewEvent,
        BlindboxAppViewModel.BlindBoxViewState,
        BlindboxAppViewModel.BlindBoxViewModelState
        >(BlindBoxViewModelState()) {

    sealed class BlindBoxViewEvent : ViewEvent {
        data class ObserveNavDestination(
            val navController: NavController
        ) : BlindBoxViewEvent()
    }


    data class BlindBoxViewState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0
    ) : ViewState()

    data class BlindBoxViewModelState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0

    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return BlindBoxViewState(
                shouldShowBottomBar = shouldShowBottomBar,
                 currentNavIndex = currentNavIndex
            )
        }
    }

    override fun onTriggerEvent(event: BlindBoxViewEvent) {
        // Handle events here
        when (event) {
            is BlindBoxViewEvent.ObserveNavDestination -> handleShowBottomBar(event.navController)
        }
    }

    private fun handleShowBottomBar(controller: NavController) {
        val currentDestination = controller.currentDestination
        val allowShowBottomBar = listOf(
            DestinationRoutes.HOME_SCREEN_ROUTE,
            DestinationRoutes.SEARCH_SCREEN_ROUTE,
            DestinationRoutes.SETTING_SCREEN_ROUTE,
            DestinationRoutes.ORDER_SCREEN_ROUTE,
        )


        val shouldShowBottomBar = currentDestination?.route?.let { route ->
            route in allowShowBottomBar
        } == true
        viewModelState.update {
            it.copy(
                shouldShowBottomBar = shouldShowBottomBar,
                currentNavIndex = allowShowBottomBar.indexOf(currentDestination?.route)
                    .takeIf { it >= 0 } ?: 0
            )
        }
    }
}