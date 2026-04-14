package com.example.seguimiento.features.PongGame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.seguimiento.R
import com.example.seguimiento.features.PongGame.viewmodel.PongViewModel
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Obstacle(
    val pos: Offset,
    val type: ObstacleType,
    val id: Long = Random.nextLong()
)

enum class ObstacleType { BAD, GOOD }

@Composable
fun PongScreen(
    viewModel: PongViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Constantes del Juego
    val paddleWidth = 220f
    val paddleHeight = 45f
    val ballRadius = 35f 
    val obstacleRadius = 35f
    
    var paddleX by remember { mutableStateOf(-1f) } // -1 para inicializar al centro una sola vez
    var ballPos by remember { mutableStateOf(Offset(300f, 300f)) }
    var ballVelocity by remember { mutableStateOf(Offset(12f, 12f)) }
    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }
    
    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }

    LaunchedEffect(uiState.isGameOver) {
        if (!uiState.isGameOver) {
            if (screenWidth > 0) ballPos = Offset(screenWidth / 2f, 300f)
            ballVelocity = Offset(12f, 12f)
            obstacles = emptyList()
            var lastObstacleTime = 0L
            
            while (true) {
                delay(16) 
                val currentTime = System.currentTimeMillis()
                
                if (screenWidth > 0 && screenHeight > 0) {
                    var nextX = ballPos.x + ballVelocity.x
                    var nextY = ballPos.y + ballVelocity.y
                    
                    // Colisión con Paredes Laterales - CORREGIDA
                    if (nextX - ballRadius < 0) {
                        nextX = ballRadius
                        ballVelocity = ballVelocity.copy(x = kotlin.math.abs(ballVelocity.x))
                    } else if (nextX + ballRadius > screenWidth) {
                        nextX = screenWidth - ballRadius
                        ballVelocity = ballVelocity.copy(x = -kotlin.math.abs(ballVelocity.x))
                    }
                    
                    if (nextY - ballRadius < 0) {
                        nextY = ballRadius
                        ballVelocity = ballVelocity.copy(y = kotlin.math.abs(ballVelocity.y))
                    }
                    
                    // Colisión con Raqueta (Hueso) - DETECCIÓN MEJORADA
                    val paddleY = screenHeight - 250f
                    
                    if (ballVelocity.y > 0 && 
                        nextY + ballRadius >= paddleY && 
                        ballPos.y + ballRadius <= paddleY + paddleHeight) {
                        
                        if (nextX >= paddleX && nextX <= paddleX + paddleWidth) {
                            nextY = paddleY - ballRadius
                            ballVelocity = ballVelocity.copy(y = -kotlin.math.abs(ballVelocity.y) * 1.02f)
                            val hitCenter = paddleX + paddleWidth / 2
                            val hitOffset = (nextX - hitCenter) / (paddleWidth / 2)
                            ballVelocity = ballVelocity.copy(x = ballVelocity.x + hitOffset * 10f)
                            
                            viewModel.updateScore(1)
                        }
                    }
                    
                    if (nextY - ballRadius > screenHeight) {
                        viewModel.onGameOver()
                        break
                    }
                    ballPos = Offset(nextX, nextY)

                    // Generar Obstáculos
                    if (currentTime - lastObstacleTime > 3500) {
                        val type = if (Random.nextFloat() > 0.5f) ObstacleType.BAD else ObstacleType.GOOD
                        obstacles = obstacles + Obstacle(Offset(Random.nextFloat() * (screenWidth - 100f) + 50f, -50f), type)
                        lastObstacleTime = currentTime
                    }

                    val updatedObstacles = mutableListOf<Obstacle>()
                    obstacles.forEach { obs ->
                        val nextObsY = obs.pos.y + 5f 
                        
                        if (nextObsY + obstacleRadius >= paddleY && 
                            nextObsY - obstacleRadius <= paddleY + paddleHeight &&
                            obs.pos.x >= paddleX && obs.pos.x <= paddleX + paddleWidth) {
                            
                            if (obs.type == ObstacleType.BAD) {
                                viewModel.updateScore(-2)
                            } else {
                                viewModel.updateScore(3)
                            }
                        } else if (nextObsY < screenHeight + 100f) {
                            updatedObstacles.add(obs.copy(pos = obs.pos.copy(y = nextObsY)))
                        }
                    }
                    obstacles = updatedObstacles
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(screenWidth) {
                if (screenWidth <= 0) return@pointerInput
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    paddleX = (paddleX + dragAmount.x).coerceIn(0f, screenWidth - paddleWidth)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.fondo3),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            if (screenWidth != size.width || screenHeight != size.height) {
                screenWidth = size.width
                screenHeight = size.height
                if (paddleX == -1f) paddleX = (screenWidth - paddleWidth) / 2
            }

            val pY = screenHeight - 250f

            val boneColor = Color(0xFFFDF5E6)
            drawRoundRect(
                color = boneColor,
                topLeft = Offset(paddleX + 25f, pY + 12f),
                size = Size(paddleWidth - 50f, paddleHeight - 24f),
                cornerRadius = CornerRadius(15f, 15f)
            )
            drawCircle(boneColor, radius = 28f, center = Offset(paddleX + 25f, pY + 8f))
            drawCircle(boneColor, radius = 28f, center = Offset(paddleX + 25f, pY + 32f))
            drawCircle(boneColor, radius = 28f, center = Offset(paddleX + paddleWidth - 25f, pY + 8f))
            drawCircle(boneColor, radius = 28f, center = Offset(paddleX + paddleWidth - 25f, pY + 32f))

            drawContext.canvas.nativeCanvas.drawText(
                "🥎",
                ballPos.x - 35f,
                ballPos.y + 30f,
                android.graphics.Paint().apply {
                    textSize = 100f
                }
            )

            obstacles.forEach { obs ->
                val emoji = if (obs.type == ObstacleType.BAD) "💩" else "🎁"
                drawContext.canvas.nativeCanvas.drawText(
                    emoji,
                    obs.pos.x - 30f,
                    obs.pos.y + 20f,
                    android.graphics.Paint().apply {
                        textSize = 90f
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 64.dp)
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Pongs", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "${uiState.score}", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Black)
        }

        if (uiState.isGameOver) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.8f)), contentAlignment = Alignment.Center) {
                Card(modifier = Modifier.padding(32.dp), shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(stringResource(id = R.string.pong_game_over_title), fontSize = 30.sp, fontWeight = FontWeight.Black, color = Color(0xFFE67E22))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(stringResource(id = R.string.pong_catch_toy), fontSize = 18.sp, color = Color.DarkGray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(id = R.string.pong_pongs_performed, uiState.score), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(id = R.string.pong_points_earned, uiState.score), fontSize = 18.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { viewModel.resetGame() }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22)), shape = RoundedCornerShape(16.dp)) {
                            Text(stringResource(id = R.string.pong_retry_btn), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onNavigateBack) {
                            Text(stringResource(id = R.string.pong_back_profile_btn), color = Color.Gray, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
