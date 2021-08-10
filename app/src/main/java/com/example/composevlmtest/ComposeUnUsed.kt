package com.example.composevlmtest

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


fun Modifier.swipeToDismiss(
    onDismissed: () -> Unit
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }

    pointerInput(Unit) {
        // Used to calculate fling decay.
        val decay = splineBasedDecay<Float>(this)
        // Use suspend functions for touch events and the Animatable.
        coroutineScope {
            while (true) {
                // Detect a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                val velocityTracker = VelocityTracker()
                // Stop any ongoing animation.
                offsetX.stop()
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        // Update the animation value with touch events.
                        launch {
                            offsetX.snapTo(
                                offsetX.value + change.positionChange().x
                            )
                        }
                        velocityTracker.addPosition(
                            change.uptimeMillis,
                            change.position
                        )
                    }
                }
                // No longer receiving touch events. Prepare the animation.
                val velocity = velocityTracker.calculateVelocity().x
                val targetOffsetX = decay.calculateTargetValue(
                    offsetX.value,
                    velocity
                )
                // The animation stops when it reaches the bounds.
                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )
                launch {
                    if (targetOffsetX.absoluteValue <= size.width) {
                        // Not enough velocity; Slide back.
                        offsetX.animateTo(
                            targetValue = 0f,
                            initialVelocity = velocity
                        )
                    } else {
                        // The element was swiped away.
                        //  offsetX.snapTo(0f)
                        offsetX.animateDecay(velocity, decay)

//                        val alpha: Float by animateFloatAsState(
//                            targetValue = if (isOpaque.value) 1f else 0.2f,
//                            animationSpec = tween(
//                                durationMillis = 3000, // animation duration
//                                easing = FastOutSlowInEasing // animation easing
//                            ),
//                            // animation finished listener
//                            finishedListener = {
//                                result.value = "Finished at $it"
//                            }
//                        )
                        onDismissed()

                    }
                }
            }
        }
    }
        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
}


fun Modifier.stackCardSwipeDragAmount(
    onDismissedLeft: () -> Unit,
    onDismissedRight: () -> Unit,
    onDragging: (String) -> Unit
): Modifier = composed {

    val offsetXY = remember {Animatable(Offset(0f,0f), Offset.VectorConverter)}
    var screenWidth by remember{ mutableStateOf(0) }
    var screenHeight by remember{ mutableStateOf(0) }
    var screenEndRatio by remember{ mutableStateOf(0.3) }
    var offsetX by remember { mutableStateOf(0f) }
    val offsetY by remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }


    onSizeChanged {
        screenWidth = it.width
        screenHeight = it.height
    }
        .offset {
            IntOffset(
                offsetXY.value.x.roundToInt(),
                offsetXY.value.y.roundToInt()
            )
        }//offsetX.value.roundToInt(), offsetY.value.roundToInt()
        .rotate(rotation)
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
//            detectDragGestures { change, dragAmount ->  }
            coroutineScope {
                while (true) {
                    val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                    val velocityTracker = VelocityTracker()
                    offsetXY.stop()
                    awaitPointerEventScope {
                        horizontalDrag(pointerId) { change ->
                            // Update the animation value with touch events.
                            launch {
                                offsetX += change.positionChange().x
                                val y = offsetX * offsetX / 100
                                rotation = 60f * (offsetX) / screenWidth
//                                onDragging("${offsetX}  / $rotation  /  $screenWidthd")
                                offsetXY.snapTo(Offset(offsetX, y))
                            }
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position
                            )
                        }
                        // No longer receiving touch events. Prepare the animation.
                        val velocity = velocityTracker.calculateVelocity().x
                        // The animation stops when it reaches the bounds.
                        val targetOffsetX = decay.calculateTargetValue(
                            offsetXY.value.x,
                            velocity
                        )

                        launch {
                            if (targetOffsetX.absoluteValue >= size.width) {
                                if (offsetX > 0) { // 우측
                                    offsetXY.animateTo(
                                        Offset(screenWidth.toFloat(), 0f)
                                    )

                                    offsetX = 0F
                                    rotation = 0f
                                    offsetXY.snapTo(Offset.Zero)
                                    onDismissedRight()
                                } else { // 좌측
                                    // offsetX.animateTo(
                                    //     - screenWidth.toFloat()
                                    // )
                                    // offsetX.snapTo(0f)
                                    // offsetY.snapTo(0f)
                                    // rotation = 0f
                                    // onDismissedLeft()
                                }
                                // Not enough velocity; Slide back.

                            } else {
                                //if(offsetX.value > 500){ // 우측
                                //    offsetX.animateTo(
                                //        screenWidth.toFloat()
                                //    )
                                //    offsetX.snapTo(0f)
                                //    offsetY.snapTo(0f)
                                //    rotation = 0f
                                //    onDismissedRight()
                                //}else if(offsetX.value < -500){ // 좌측
                                //    offsetX.animateTo(
                                //        - screenWidth.toFloat()
                                //    )
                                //    offsetX.snapTo(0f)
                                //    offsetY.snapTo(0f)
                                //    rotation = 0f
                                //    onDismissedLeft()
                                offsetXY.animateTo(
                                    targetValue = Offset.Zero
                                )
                                offsetX = 0F
                                rotation = 0f
                            }
//                            else{


                            //}
                        }
                    }
                }
            }

        }
}


fun Modifier.stackCardSwipe(
    screenWidth: Int,
    onDismissedLeft: () -> Unit,
    onDismissedRight: () -> Unit,
    onDragging: (String) -> Unit
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var rotation by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()

    offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
        .rotate(rotation)
        .draggable(
            state = rememberDraggableState { delta ->
                coroutineScope.launch {
                    val y = 0 + delta / 1.5f
                    val x = 0 + delta / 1.5
                    if (delta > 0) {
                        offsetY.snapTo(offsetY.value + y)
                    } else {
                        offsetY.snapTo(offsetY.value - y)
                    }
                    offsetY.snapTo(y)
                    offsetX.snapTo(offsetX.value + (delta / 1.5f))


                    onDragging("offX : ${offsetX.value} / offY : ${offsetY.value} / Y : $y / X : $x")
                    rotation = 45f * (offsetX.value) / screenWidth
                }
            },
            orientation = Orientation.Horizontal,
            onDragStopped = {
                coroutineScope.launch {
                    if (offsetX.value > 500) {
                        offsetX.animateTo(
                            screenWidth.toFloat()
                        )
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                        rotation = 0f
                        onDismissedRight()
                    } else if (offsetX.value < -500) {
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                        rotation = 0f
                        onDismissedLeft()
                    } else {
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                        rotation = 0f
                    }
                }
            }
        )
}

//                        modifier = Modifier
//                            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
//                            .rotate(rotation)
//                            .draggable(
//                                state = rememberDraggableState { delta ->
//                                    coroutineScope.launch {
//                                        if (po == index) {
//                                            offsetX.snapTo(offsetX.value + (delta/1.5f))
//                                            offsetY.snapTo(offsetY.value - delta/3 )
//                                            rotation = 45f*(offsetX.value)/screenWidth
//                                        }
//                                    }
//                                },
//                                orientation = Orientation.Horizontal,
//                                onDragStopped = {
//                                    val p = po - 1
//                                    coroutineScope.launch {
//
//                                        Toast.makeText(context, "${rotation}",Toast.LENGTH_SHORT).show()
//                                        rotation = 0f
//                                        if (abs(offsetX.value) > 500) {
//                                            offsetX.snapTo(0f)
//                                            offsetY.snapTo(0f )
//                                            offsetX.animateTo().endState
//                                            viewModel.setStackPosition(p)
//                                        } else {
//                                            offsetY.snapTo(0f )
//                                            offsetX.snapTo(0f)
//                                        }
//                                    }
//                                }
//                            ),