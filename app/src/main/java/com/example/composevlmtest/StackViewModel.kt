package com.example.composevlmtest

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class StackViewModel :ViewModel(){
    var imgList = listOf<Int>(
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4
    )

    var currentItemPosition = mutableStateOf( imgList.size -1)

    fun setCurrentPosition() {
        val p = currentItemPosition.value
        currentItemPosition.value = p - 1
        try {
            currentItem.value = imgList[currentItemPosition.value]
        } catch (e: Exception) {
            currentItem.value = null
        }
        try {
            nextItem.value = imgList[currentItemPosition.value - 1]
        } catch (e: Exception) {
            nextItem.value = null
        }

    }


    val currentItem = mutableStateOf<Int?>(imgList[imgList.size - 1])
    val nextItem = mutableStateOf<Int?>(imgList[imgList.size - 2])


    val stackPosition = mutableStateOf(imgList.size - 1)
    fun setStackPosition(position: Int) {
        stackPosition.value = position
    }

    fun recyclerImgList() {
        imgList = listOf<Int>(
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_8,
            R.drawable.img_9,
            R.drawable.img_5,
            R.drawable.img_6,
            R.drawable.img_8,
            R.drawable.img_9
        )
        currentItemPosition.value = (imgList.size-1)
        try {
            currentItem.value = imgList[imgList.size - 1]
        }catch (e:Exception){
            currentItem.value = null
        }
        try {
            nextItem.value = imgList[imgList.size - 2]
        }catch (e:Exception){
            nextItem.value = null
        }

    }
}