package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun PitchImage(
    drawableName: String,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(
        drawableName,
        "drawable",
        context.packageName
    )

    val finalResId = if (resId != 0) resId else R.drawable.ic_launcher_background

    Image(
        painter = painterResource(id = finalResId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale
    )
}
