package com.orelzman.mymessages.presentation.components.scrollable_flowrow

import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ScrollableFlowRow(
    modifier: Modifier = Modifier,
    scrollDirection: ScrollDirection = ScrollDirection.Vertical,
    mainAxisSize: SizeMode = SizeMode.Wrap,
    mainAxisAlignment: FlowMainAxisAlignment = FlowMainAxisAlignment.Start,
    mainAxisSpacing: Dp = 0.dp,
    crossAxisAlignment: FlowCrossAxisAlignment = FlowCrossAxisAlignment.Start,
    crossAxisSpacing: Dp = 0.dp,
    lastLineMainAxisAlignment: FlowMainAxisAlignment = mainAxisAlignment,
    isOverscrollEffect: Boolean = false,
    content: @Composable () -> Unit
) {
    val state = rememberScrollState()

    val scrollModifier = if (scrollDirection == ScrollDirection.Vertical) {
        modifier.verticalScroll(
            state = state,
            enabled = true,
            flingBehavior = null,
            reverseScrolling = false
        )

    } else {
        modifier
            .horizontalScroll(
                state = state,
                enabled = true,
                flingBehavior = null,
                reverseScrolling = false
            )
    }
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides
                if (isOverscrollEffect) OverscrollConfiguration()
                else null
    ) {
        FlowRow(
            modifier = scrollModifier,
            mainAxisSize = mainAxisSize,
            mainAxisAlignment = mainAxisAlignment,
            mainAxisSpacing = mainAxisSpacing,
            crossAxisAlignment = crossAxisAlignment,
            crossAxisSpacing = crossAxisSpacing,
            lastLineMainAxisAlignment = lastLineMainAxisAlignment,
            content = content
        )
    }
}

@Suppress("unused")
enum class ScrollDirection {
    Vertical,
    Horizontal;
}