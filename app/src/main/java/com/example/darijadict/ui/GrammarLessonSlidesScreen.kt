package com.example.darijadict.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.darijadict.components.AudioPlayer
import com.example.darijadict.viewmodel.EntryViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun GrammarLessonSlidesScreen(
    navController: NavController,
    viewModel: EntryViewModel,
    lessonId: String
) {
    val context = LocalContext.current
    val slides by viewModel.getGrammarLessonSlides(lessonId).observeAsState(emptyList())
    
    if (slides.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val pagerState = rememberPagerState()
        
        Column(Modifier.fillMaxSize()) {
            HorizontalPager(
                count = slides.size,
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val slide = slides[page]
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = slide.text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        if (slide.needsAudio == true && !slide.audioFile.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Listen to pronunciation:", style = MaterialTheme.typography.labelMedium)
                            val audioUrl = "https://firebasestorage.googleapis.com/v0/b/darija-grammar.firebasestorage.app/o/audio%2Fgrammar_1%2F${slide.audioFile}?alt=media&token=e9e69642-704f-44aa-87f1-d0857607c5bf"
                            AudioPlayer(audioUrl)
                        }
                    }
                }
            }
            
            // Page indicator
            Row(
                Modifier
                    .height(16.dp)
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(slides.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.surfaceVariant
                    
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, contentDescription = null)
            Text(message)
        }
    }
}
