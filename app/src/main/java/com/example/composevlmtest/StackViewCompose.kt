package com.example.composevlmtest

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun StackView(
    imgList: List<Int>,
    context: Context,
    viewModel: StackViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val position by viewModel.stackPosition
    val (text, setText) = remember { mutableStateOf("") }
    val currentItem by viewModel.currentItem
    val nextItem by viewModel.nextItem
    var screenWidth by remember {
        mutableStateOf(0f)
    }
    var alpah by remember {
        mutableStateOf(0f)
    }
    var buttonScale by remember {
        mutableStateOf(0f)
    }



    var rightOk by remember { mutableStateOf(false)}
    var leftOk by remember { mutableStateOf(false)}

    var buttonRightScaled = animateFloatAsState(
        if(rightOk){
            2f
        }else{
            1f
        }
    )

    var buttonLeftScaled = animateFloatAsState(
        if(leftOk){
            2f
        }else{
            1f
        }
    )
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .onSizeChanged {
            screenWidth = it.width.toFloat()
        }
        ) {
        if (position < 0) {
            Button(onClick = { viewModel.recyclerImgList() }) {
                Text(text = "다진 업데이트")
            }
        } else {

                testListStack(
                    nextItem = nextItem,
                    currentItem = currentItem,
                    onDismissedLeft = { viewModel.setCurrentPosition()
                        rightOk = false
                        leftOk = false               },
                    onDismissedRight = { viewModel.setCurrentPosition()
                        rightOk = false
                        leftOk = false},
                    onDragging = {
                        if(screenWidth *3 /5 < it){
                            alpah = if( (0.5f * (it - screenWidth *3 /5 ) /(screenWidth *2 /5)) +0.5f > 1f  ) 1f else  (0.5f * (it - screenWidth *3 /5 ) /(screenWidth *2 /5)) +0.5f
                            rightOk = true
                            leftOk = false
                        }else if(-screenWidth *3 /5 > it){
                            leftOk = true
                            rightOk = false
                        }else{
                            leftOk = false
                            rightOk = false
                        }
                        setText(it.toString())
                    },
                    replaceList = {
                        viewModel.recyclerImgList()
                    }
                )


            if(rightOk) {
                Text(text = "오른쪽으로 넘기기", modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .graphicsLayer {
                        alpha =
                            alpah
                    }
                    .background(
                        color = Color.Red.copy(alpha = alpah)
                    )
                )
            }

            Button(
                onClick = { /*TODO*/ },modifier = Modifier
                    .padding(30.dp)
                    .align(Alignment.BottomStart)
                    .scale(buttonLeftScaled.value)
            ) {
                Text(text = "좋아요")
            }

            Button(onClick = { /*TODO*/ },modifier = Modifier
                .padding(30.dp).align(Alignment.BottomEnd)
                .scale(buttonRightScaled.value)) {
                Text(text = "싫어요")
            }

            Text(text = text, modifier = Modifier.align(Alignment.BottomCenter))
        }


    }

}
@Composable
fun testListStack(nextItem : Int?, currentItem: Int?,onDismissedLeft : ()->Unit, onDismissedRight : () ->Unit ,
                  onDragging: (Float) -> Unit, replaceList : () -> Unit){

    if(currentItem !=null){
        if(nextItem != null){
            ImgViewBasic(drawable = nextItem)
        }else{
            ImgViewBasic(drawable = R.drawable.img_11)
        }

        ImgViewBasic(drawable = currentItem,
            modifier = Modifier
                .stackCardSwipeDragAmountTest(
                    onDismissedLeft = {
                        onDismissedLeft()
                    },
                    onDismissedRight = {onDismissedRight()
                    },
                    onDragging = {
                        onDragging(it)
                    }
                ),
        )
    }else{
        ImgViewBasic(drawable = R.drawable.img_11
        ,
        modifier = Modifier.clickable {
            replaceList()
        })
    }



}


@Composable
fun ImgViewBasic(position: Int? = null, drawable: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(30.dp)
    ) {
        Image(
            painter = painterResource(id = drawable), contentDescription = null, modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10.dp))
                .background(color = Color.Black)

        )
        position?.let{
            Text(
                text = position.toString(), modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(color = Color.White)
            )
        }
    }
}


fun Modifier.stackCardSwipeDragAmountTest(
    onDismissedLeft: () -> Unit,
    onDismissedRight: () -> Unit,
    onDragging: (Float) -> Unit,

): Modifier = composed {
    val offsetXY = remember {Animatable(Offset(0f,0f), Offset.VectorConverter)}
    var screenWidth by remember{ mutableStateOf(0)}
    var offsetX by  remember { mutableStateOf(0f) }
    var rotation by remember { mutableStateOf(0f) }
//    var draggingLeftRotation by remember {
//        mutableStateOf(-30f)
//    }
//    var draggingRightRotation by remember {
//        mutableStateOf(30f)
//    }
    var draggingState by remember {
        mutableStateOf(DraggingState.Default)
    }

    val rotateDefault = animateFloatAsState(
        when(draggingState){
            DraggingState.Default ->{
                rotation
            }
            DraggingState.Dragging ->{
                rotation
            }
            DraggingState.ToCenter ->{
                0f
            }
            DraggingState.ToNextRight ->{
                0f//draggingRightRotation
            }
            DraggingState.ToNextLeft ->{
                0f//draggingLeftRotation
            }
        }
      )

    onSizeChanged {
        screenWidth = it.width
    }
        .offset {
            IntOffset(
                offsetXY.value.x.roundToInt(),
                offsetXY.value.y.roundToInt()
            )
        }//offsetX.value.roundToInt(), offsetY.value.roundToInt()
        .rotate(rotateDefault.value)
        .pointerInput(Unit) {
            val decay = splineBasedDecay<Float>(this)
            coroutineScope {
                while (true) {
                    val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                    val velocityTracker = VelocityTracker()

                    draggingState = DraggingState.Dragging
                    offsetXY.stop()
                    offsetX = offsetXY.value.x
                    rotation = 60f * (offsetX) / screenWidth

                    awaitPointerEventScope {
                        horizontalDrag(pointerId) { change ->
                            launch {
                                offsetX += change.positionChange().x
                                onDragging(offsetX)
                                val y = offsetX * offsetX * 4 / (3 * screenWidth)
                                rotation = 60f * (offsetX) / screenWidth
                                offsetXY.snapTo(Offset(offsetX, y))
                            }
                            velocityTracker.addPosition(
                                change.uptimeMillis,
                                change.position
                            )
                        }

                        val velocity = velocityTracker.calculateVelocity().x
                        val targetOffsetX = decay.calculateTargetValue(
                            offsetXY.value.x,
                            velocity
                        )

                        launch {
                            if (targetOffsetX.absoluteValue >= size.width) {
                                if (offsetX > 0) { // 우측
                                    draggingState = DraggingState.ToNextRight
                                    offsetXY.animateTo(
                                        Offset(screenWidth.toFloat(), screenWidth / 3.toFloat())
                                    )

                                    onDismissedRight()
                                } else { // 좌측
                                    draggingState = DraggingState.ToNextLeft
                                    offsetXY.animateTo(
                                        Offset(-screenWidth.toFloat(), screenWidth / 3.toFloat())
                                    )
                                    onDismissedLeft()
                                }
                            } else {
                                if (offsetX > 3 * screenWidth / 5) { //우측
                                    draggingState = DraggingState.ToNextRight
                                    offsetXY.animateTo(
                                        Offset(screenWidth.toFloat(), screenWidth / 3.toFloat())
                                    )
                                    onDismissedRight()
                                } else if (offsetX < -3 * screenWidth / 5) {
                                    draggingState = DraggingState.ToNextLeft
                                    offsetXY.animateTo(
                                        Offset(-screenWidth.toFloat(), screenWidth / 3.toFloat())
                                    )
                                    onDismissedLeft()
                                } else {
                                    draggingState = DraggingState.ToCenter
                                    offsetXY.animateTo(
                                        targetValue = Offset.Zero
                                    )
                                }
                            }

                            offsetX = 0F
                            rotation = 0f
                            draggingState = DraggingState.Default
                            offsetXY.snapTo(
                                targetValue = Offset.Zero
                            )
                        }
                    }
                }
            }

        }
}
