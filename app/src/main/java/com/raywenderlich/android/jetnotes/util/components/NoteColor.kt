package com.raywenderlich.android.jetnotes.util.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NoteColor(){
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.Red)
            .clip(CircleShape)
            .background(Color.Yellow)
    )
}


@Preview
@Composable
fun NoteColorPreview(){
    NoteColor()
}
