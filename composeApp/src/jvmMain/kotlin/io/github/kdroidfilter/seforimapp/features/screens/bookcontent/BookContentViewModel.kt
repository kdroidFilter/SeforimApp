package io.github.kdroidfilter.seforimapp.features.screens.bookcontent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.SplitPaneState

class BookContentViewModel : ViewModel() {

    @OptIn(ExperimentalSplitPaneApi::class)
    private val _splitPaneState = MutableStateFlow(
        SplitPaneState(
            initialPositionPercentage = 0f,
            moveEnabled = true,
        )
    )

    @OptIn(ExperimentalSplitPaneApi::class)
    val splitPaneState = _splitPaneState.asStateFlow()


}