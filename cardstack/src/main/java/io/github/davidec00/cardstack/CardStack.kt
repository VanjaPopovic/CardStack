package io.github.davidec00.cardstack

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlin.math.roundToInt

/**
 * A stack of cards that can be dragged.
 * If they are dragged after a [thresholdConfig] or exceed the [velocityThreshold] the card is swiped.
 *
 * @param items Cards to show in the stack.
 * @param thresholdConfig Specifies where the threshold between the predefined Anchors is. This is represented as a lambda
 * that takes two float and returns the threshold between them in the form of a [ThresholdConfig].
 * @param velocityThreshold The threshold (in dp per second) that the end velocity has to exceed
 * in order to swipe, even if the positional [thresholds] have not been reached.
 * @param enableButtons Show or not the buttons to swipe or not
 * @param onSwipeLeft Lambda that executes when the animation of swiping left is finished
 * @param onSwipeRight Lambda that executes when the animation of swiping right is finished
 * @param onEmptyRight Lambda that executes when the cards are all swiped
 */
@ExperimentalMaterialApi
@Composable
inline fun <reified T> CardStack(
    modifier: Modifier = Modifier,
    items: MutableList<T>,
    noinline thresholdConfig: (Float, Float) -> ThresholdConfig = { _, _ -> FractionalThreshold(0.2f) },
    velocityThreshold: Dp = 125.dp,
    enableButtons: Boolean = false,
    crossinline onSwipeLeft: (item: T) -> Unit = {},
    crossinline onSwipeRight: (item: T) -> Unit = {},
    crossinline onSwipeTop: (item: T) -> Unit = {},
    onEmptyStack: (lastItem: T) -> Unit = {},
    noinline topView: (@Composable () -> Unit)? = null,
    noinline rightView: (@Composable () -> Unit)? = null,
    noinline leftView: (@Composable () -> Unit)? = null,
    crossinline renderItem: @Composable (T) -> Unit
) {

    var i by remember { mutableStateOf(items.size - 1) }

    if (i == -1) {
        onEmptyStack(items.last())
    }

    val cardStackController = rememberCardStackController()
    cardStackController.onSwipeLeft = {
        onSwipeLeft(items[i])
        i--
    }
    cardStackController.onSwipeRight = {
        onSwipeRight(items[i])
        i--
    }
    cardStackController.onSwipeTop = {
        onSwipeTop(items[i])
        i--
    }
    val alphaLeft: Float by animateFloatAsState(if (cardStackController.getDirection() == Direction.Left) cardStackController.getRatio() else 0.0f)
    val alphaRight: Float by animateFloatAsState(if (cardStackController.getDirection() == Direction.Right) cardStackController.getRatio() else 0.0f)
    val alphaTop: Float by animateFloatAsState(if (cardStackController.getDirection() == Direction.Top) cardStackController.getRatio() else 0.0f)
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        val (buttons, stack) = createRefs()

        if (enableButtons) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(buttons) {
                        bottom.linkTo(parent.bottom)
                        top.linkTo(stack.bottom)
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { if (i >= 0) cardStackController.swipeLeft() },
                    backgroundColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(5.dp)
                ) {
                    Icon(Icons.Filled.ThumbDownAlt, contentDescription = "", tint = Color.Red)
                }
                Spacer(modifier = Modifier.width(70.dp))
                FloatingActionButton(
                    onClick = { if (i >= 0) cardStackController.swipeRight() },
                    backgroundColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(5.dp)
                ) {
                    Icon(Icons.Filled.ThumbUpAlt, contentDescription = "", tint = Color.Green)
                }
            }
        }

        Box(modifier = Modifier
            .constrainAs(stack) {
                top.linkTo(parent.top)
            }
            .draggableStack(
                controller = cardStackController,
                thresholdConfig = thresholdConfig,
                velocityThreshold = velocityThreshold
            )
            .fillMaxHeight(0.8f)
        ) {
            items.asReversed().forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .moveTo(
                            x = if (index == i) cardStackController.offsetX.value else 0f,
                            y = if (index == i) cardStackController.offsetY.value else 0f
                        )
                        .visible(visible = index == i || index == i - 1)
                        .graphicsLayer(
                            rotationZ = if (index == i) cardStackController.rotation.value else 0f,
                            scaleX = if (index < i) cardStackController.scale.value else 1f,
                            scaleY = if (index < i) cardStackController.scale.value else 1f
                        )
                        .shadow(4.dp, RoundedCornerShape(10.dp))
                ) {
//                    Card(
//                        modifier = Modifier,
//                        item
//                    )
                    renderItem(item)
                    if (rightView != null) {
                        Box(Modifier.graphicsLayer(alpha = alphaRight)) {
                            rightView()
                        }
                    }
                    if (leftView != null) {
                        Box(Modifier.graphicsLayer(alpha = alphaLeft)) {
                            leftView()
                        }
                    }
                    if (topView != null) {
                        Box(Modifier.graphicsLayer(alpha = alphaTop)) {
                            topView()
                        }
                    }


                }


            }
        }
    }
}



fun Modifier.moveTo(
    x: Float,
    y: Float
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    layout(placeable.width, placeable.height) {
        placeable.placeRelative(x.roundToInt(), y.roundToInt())
    }
})

fun Modifier.visible(
    visible: Boolean = true
) = this.then(Modifier.layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    if (visible) {
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    } else {
        layout(0, 0) {}
    }
})

