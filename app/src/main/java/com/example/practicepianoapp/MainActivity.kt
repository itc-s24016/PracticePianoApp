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
import androidx.compose.foundation.layout.Spacer
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.AudioAttributes.USAGE_GAME
import android.media.SoundPool
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf

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

    // 白・黒の鍵盤の音源定義
    // ド レ ミ ファ ソ ラ シ ド
    val whiteRawIds = remember {
        listOf(R.raw.c1, R.raw.d1, R.raw.e1, R.raw.f1, R.raw.g1, R.raw.a1, R.raw.b1, R.raw.c2)
    }
    // ド# レ# null ファ# ソ# ラ# null
    val blackRawIds = remember {
        listOf(R.raw.c1s, R.raw.d1s, null, R.raw.f1s, R.raw.g1s, R.raw.a1s, null)
    }

    // Sound Pool の初期化
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(10)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(USAGE_GAME)
                    .setContentType(CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()
    }

    val whiteKeys = remember { mutableStateListOf<Int>() }
    val blackKeys = remember { mutableStateListOf<Int?>() }

    // 白・黒の鍵盤の音源IDを返す
    LaunchedEffect(Unit) {
        whiteRawIds.forEach { id ->
            whiteKeys.add(soundPool.load(context, id, 1))
        }
        blackRawIds.forEach { id ->
            if (id != null) {
                blackKeys.add(soundPool.load(context, id, 1))
            } else {
                blackKeys.add(null)
            }
        }
    }

    // SoundPool のリソースを解放する
    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    // 白・黒健のレイアウト
    Box(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxSize()) {
            whiteKeys.forEach { soundId ->
                KeyBox(
                    modifier = Modifier
                        .padding(1.dp)
                        .weight(1f)
                        .fillMaxHeight(),
                    normalColor = Color.White,
                    pressedColor = Color.LightGray,
                    onPlay = {
                        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                    }
                )
            }
        }
        Row(modifier = Modifier.fillMaxSize()) {
            blackKeys.forEach { soundId ->
                if (soundId == null) {
                    Spacer(modifier = Modifier.weight(0.5f))
                } else {
                    KeyBox(
                        modifier = Modifier
                            .padding(15.dp, 1.dp, 15.dp, 0.dp)
                            .weight(1f)
                            .fillMaxHeight(0.55f),
                        normalColor = Color.Black,
                        pressedColor = Color.Gray,
                        onPlay = {
                            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.weight(0.5f))
        }
    }
}

// 一つの鍵盤
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
                            awaitRelease() // 指を話すまで待機
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