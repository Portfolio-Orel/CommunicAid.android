package com.orelzman.mymessages.presentation.components.scrollable_flowrow

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode

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
    content: @Composable () -> Unit
) {
    val lineHeightPixels = with(LocalDensity.current) { 34.sp.toPx() }
    val scrollAmount = ((4 / 6) * lineHeightPixels).toInt()
    val scrollState = ScrollState(scrollAmount)
    val scrollStateNone = ScrollState(0)
    var scrollModifier = modifier
    scrollModifier = if(scrollDirection == ScrollDirection.Vertical) {
        scrollModifier.verticalScroll(scrollState)
    } else {
        scrollModifier.horizontalScroll(scrollState)
            .verticalScroll(scrollStateNone)
    }

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

enum class ScrollDirection {
    Vertical,
    Horizontal;
}