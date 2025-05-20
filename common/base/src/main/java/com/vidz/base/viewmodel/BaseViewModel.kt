package com.fpl.base.viewmodel

import android.R.id.message
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fpl.base.interfaces.ViewEvent
import com.fpl.base.interfaces.ViewModelState
import com.fpl.base.interfaces.ViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<VE : ViewEvent, VS : ViewState, VMS : ViewModelState>(
    initState: VMS
) : ViewModel() {

    protected val viewModelState = MutableStateFlow(initState)

    // UI state exposed to the UI
    val uiState: StateFlow<VS> = viewModelState
        .map { it.toUiState() as VS }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState() as VS
        )


    abstract fun onTriggerEvent(event: VE)

//    protected fun <T> Result<T>.reduce(
//        onInit: ((Any?) -> Unit)? = null,
//        onSuccess: (Boolean, T) -> Unit,
//        onError: (String, Result.Error) -> Unit,
//        onDone: () -> Unit
//    ) {
//        when (this) {
//            is Result.Init -> onInit?.invoke(this.initData)
//            is Result.Success -> onSuccess(isCached, successData)
//            is Result.Error -> onError.invoke(message, this)
//            is Result.Done -> onDone.invoke()
//        }
//    }


}