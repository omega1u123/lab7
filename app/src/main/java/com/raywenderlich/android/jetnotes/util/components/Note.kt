package com.raywenderlich.android.jetnotes.util.components

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import com.raywenderlich.android.jetnotes.R
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raywenderlich.android.jetnotes.theme.rwGreen



@Composable
fun Note() {
    val backgroundShape: RoundedCornerShape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .padding(8.dp)
            .shadow(1.dp, backgroundShape)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .background(Color.White, backgroundShape)
    ) {
       NoteColor(
           color = rwGreen,
           size = 40.dp,
           padding = 4.dp,
           border = 1.dp
       )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = stringResource(id = R.string.header), maxLines = 1)
            Text(text = stringResource(id = R.string.content), maxLines = 1)
        }
        Checkbox(
            checked = false,
            onCheckedChange = { },
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
@Preview
@Composable
private fun NotePreview(){ Note()
}