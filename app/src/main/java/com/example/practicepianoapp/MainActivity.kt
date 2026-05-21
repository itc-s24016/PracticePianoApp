package com.example.practicepianoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.practicepianoapp.ui.theme.PracticePianoAppTheme

import android.media.MediaPlayer
import androidx.compose.material3.Button
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.pointer.pointerInput

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PracticePianoAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Main(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Main(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val whiteRawIds = remember {
        listOf(R.raw.c1, R.raw.d1, R.raw.e1, R.raw.f1, R.raw.g1, R.raw.a1, R.raw.b1, R.raw.c2)
    }

    val whiteKeys = remember {
        whiteRawIds.map { id ->
            MediaPlayer.create(context, id)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            whiteKeys.forEach { it.release() }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            whiteKeys.forEach { mediaPlayer ->
                KeyBox(
                    modifier = Modifier
                        .padding(1.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    normalColor = Color.White,
                    pressedColor = Color.LightGray,
                    onPlay = {
                        mediaPlayer.seekTo(0)
                        mediaPlayer.start()
                    }
                )
            }
        }
    }
}

@Composable
fun KeyBox(
    modifier: Modifier = Modifier,
    normalColor: Color,
    pressedColor: Color,
    onPlay: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .border(1.dp, Color.Gray)
            .background(if (pressed) pressedColor else normalColor)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        onPlay()
                        try {
                            awaitRelease()
                        } finally {
                            pressed = false
                        }
                    }
                )
            }
    )
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    PracticePianoAppTheme {
        Main()
    }
}