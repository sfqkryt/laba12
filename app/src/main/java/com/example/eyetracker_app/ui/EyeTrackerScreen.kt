package com.example.eyetracker_app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyetracker_app.model.AppTab
import com.example.eyetracker_app.model.CustomGesture
import com.example.eyetracker_app.model.EyeTrackerUiState
import com.example.eyetracker_app.model.TRAINING_DURATION_SECONDS
import com.example.eyetracker_app.ui.theme.AppAccent
import com.example.eyetracker_app.ui.theme.AppAccentDark
import com.example.eyetracker_app.ui.theme.AppBackground
import com.example.eyetracker_app.ui.theme.AppBorder
import com.example.eyetracker_app.ui.theme.AppCard
import com.example.eyetracker_app.ui.theme.AppCard2
import com.example.eyetracker_app.ui.theme.AppMuted
import com.example.eyetracker_app.ui.theme.AppText

@Composable
fun EyeTrackerApp(viewModel: EyeTrackerViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060A13))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .systemBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .padding(bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                AppHeader(onSettingsClick = viewModel::showSettings)
                when (uiState.selectedTab) {
                    AppTab.Gestures -> GesturesScreen(
                        uiState = uiState,
                        onGesturesEnabledChange = viewModel::setEyeGesturesEnabled,
                        onGestureNameSelected = viewModel::selectGesture
                    )
                    AppTab.Focus -> FocusScreen(
                        uiState = uiState,
                        onEyeStrainPercentageChange = viewModel::setEyeStrainPercentage,
                        onRestNotificationEnabledChange = viewModel::setRestNotificationEnabled
                    )
                    AppTab.Training -> TrainingScreen(
                        remainingSeconds = uiState.remainingTrainingSeconds,
                        isRunning = uiState.isTrainingRunning,
                        onStart = viewModel::startTraining,
                        onPause = viewModel::pauseTraining
                    )
                    AppTab.Custom -> CustomScreen(
                        customGestureList = uiState.customGestureList,
                        onAddGesture = viewModel::addCustomGesture,
                        onDeleteGesture = viewModel::deleteCustomGesture
                    )
                }
            }
        }
        BottomNavigation(
            selected = uiState.selectedTab,
            onSelected = viewModel::selectTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (uiState.isSettingsDialogVisible) {
        AlertDialog(
            onDismissRequest = viewModel::hideSettings,
            containerColor = AppCard,
            title = { Text("EyeTrack Pro", color = AppText) },
            text = {
                Text(
                    "Нативное приложение на Kotlin + Jetpack Compose.\nАрхитектура: MVVM\nВерсия: 1.0.0",
                    color = AppMuted,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::hideSettings) {
                    Text("Закрыть", color = AppAccent)
                }
            }
        )
    }
}

@Composable
private fun AppHeader(onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StatusDot()
                Spacer(Modifier.width(6.dp))
                Text("СИМУЛЯТОР · АКТИВЕН", style = MaterialTheme.typography.labelSmall, color = AppAccent, letterSpacing = 1.sp)
            }
            Spacer(Modifier.height(5.dp))
            Text("EyeTrack Pro", color = AppText, fontSize = 28.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
        }
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AppCard)
                .border(1.dp, AppBorder, RoundedCornerShape(12.dp))
        ) {
            Text("⚙", color = AppAccent, fontSize = 18.sp)
        }
    }
}

@Composable
private fun GesturesScreen(
    uiState: EyeTrackerUiState,
    onGesturesEnabledChange: (Boolean) -> Unit,
    onGestureNameSelected: (String) -> Unit
) {
    AppCard(highlight = true) {
        Label("УМНОЕ УПРАВЛЕНИЕ")
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Жесты глазами", color = AppText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            AppSwitch(checked = uiState.areEyeGesturesEnabled, onCheckedChange = onGesturesEnabledChange)
        }
        Spacer(Modifier.height(6.dp))
        MutedText("Проверяйте естественные сценарии управления в интерактивной симуляции взгляда.")
    }

    AppCard {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { EyeSimulationVisual() }
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(size = 8.dp)
            Spacer(Modifier.width(6.dp))
            Label("GAZE SIMULATOR", bottom = 0.dp)
        }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        GestureCard("◯", "Круг", "Скриншот", uiState.selectedGestureName == "Круг", Modifier.weight(1f)) { onGestureNameSelected("Круг") }
        GestureCard("↔", "Влево — вправо", "Симуляция", uiState.selectedGestureName == "Смахи", Modifier.weight(1f)) { onGestureNameSelected("Смахи") }
        GestureCard("↑", "Вверх — в центр", "Проверка", uiState.selectedGestureName == "Вверх", Modifier.weight(1f)) { onGestureNameSelected("Вверх") }
    }

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Label("ПРОВЕРКА ЖЕСТА", bottom = 0.dp)
            Spacer(Modifier.weight(1f))
            Text("↗", color = AppAccent, fontSize = 20.sp)
        }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("Круг", "Смахи", "Вверх").forEach { gestureName ->
                GhostButton(
                    text = gestureName,
                    selected = uiState.selectedGestureName == gestureName,
                    onClick = { onGestureNameSelected(gestureName) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FocusScreen(
    uiState: EyeTrackerUiState,
    onEyeStrainPercentageChange: (Int) -> Unit,
    onRestNotificationEnabledChange: (Boolean) -> Unit
) {
    AppCard {
        Label("САМОЧУВСТВИЕ")
        Text("Усталость глаз", color = AppText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        MutedText("Оценка автоматически обновляется во время сессии. При необходимости скорректируйте её вручную.")
    }

    AppCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Label("АВТООЦЕНКА НАГРУЗКИ", bottom = 0.dp)
            Text("⟳", color = AppAccent, fontSize = 20.sp)
        }
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)) {
            Text(uiState.eyeStrainPercentage.toString(), color = AppAccent, fontSize = 44.sp, fontWeight = FontWeight.Bold)
            Text("%", color = AppMuted, fontSize = 22.sp, modifier = Modifier.padding(bottom = 5.dp, start = 2.dp))
        }
        Box(Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF1A2942))) {
            Box(Modifier.fillMaxWidth(uiState.eyeStrainPercentage / 100f).height(6.dp).background(Brush.horizontalGradient(listOf(AppAccentDark, AppAccent))))
        }
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(size = 6.dp)
            Spacer(Modifier.width(6.dp))
            MutedText("Данные автооценки носят информационный характер", small = true)
        }
    }

    AppCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f)) {
                Label("ОТДЫХ ПРИ ПРИЩУРИВАНИИ")
                MutedText("Включите уведомление, чтобы видеть, когда нагрузка идёт на глаза.", small = true)
            }
            AppSwitch(checked = uiState.isRestNotificationEnabled, onCheckedChange = onRestNotificationEnabledChange)
        }
        Spacer(Modifier.height(10.dp))
        MutedText("Уведомление придёт, если прищуривание укажет на необходимость отдыха.", small = true)
    }

    AppCard {
        Label("☰ ИЗМЕНИТЬ ОЦЕНКУ ВРУЧНУЮ")
        MutedText("Перетащите шкалу, если ваша оценка отличается от авто-оценки сессии.", small = true)
        Slider(
            value = uiState.eyeStrainPercentage.toFloat(),
            onValueChange = { onEyeStrainPercentageChange(it.toInt()) },
            valueRange = 0f..100f,
            modifier = Modifier.padding(top = 8.dp),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = AppAccent,
                activeTrackColor = AppAccent,
                inactiveTrackColor = Color(0xFF1A2942)
            )
        )
    }

    AppCard(accent = true) {
        Label("☀ СОВЕТ НА СЕЙЧАС")
        Text("Небольшая нагрузка. Переведите взгляд вдаль на 20 секунд.", color = AppText, fontSize = 14.sp)
    }
}

@Composable
private fun TrainingScreen(remainingSeconds: Int, isRunning: Boolean, onStart: () -> Unit, onPause: () -> Unit) {
    Label("ПЕРЕЗАГРУЗКА ЗРЕНИЯ", centered = true)
    Text("Мягкий фокус", color = AppText, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    MutedText("Следуйте за точкой: она покажет, куда спокойно переводить взгляд.", small = true, centered = true)

    Box(Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) { TimerRing(remainingSeconds) }
    Label("СМОТРЕТЬ В ЦЕНТР", centered = true)
    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("◎", color = AppAccent, fontSize = 18.sp)
            Spacer(Modifier.width(6.dp))
            Text("Следуйте за точкой", color = AppText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(6.dp))
        MutedText("Двигайте только глазами и задерживайте взгляд на каждой позиции несколько секунд.", small = true)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PrimaryButton(
            text = when {
                isRunning -> "▶ Идёт…"
                remainingSeconds == 0 -> "▶ Заново"
                else -> "▶ Начать"
            },
            onClick = onStart,
            modifier = Modifier.weight(1f)
        )
        GhostButton("⏸ Пауза", onClick = onPause, modifier = Modifier.weight(1.2f))
    }
}

@Composable
private fun CustomScreen(
    customGestureList: List<CustomGesture>,
    onAddGesture: (CustomGesture) -> Unit,
    onDeleteGesture: (Int) -> Unit
) {
    var gestureNameInput by rememberSaveable { mutableStateOf("") }
    var selectedEyeMovement by rememberSaveable { mutableStateOf("Круг") }
    var isMovementMenuExpanded by remember { mutableStateOf(false) }
    var isGestureNameInvalid by remember { mutableStateOf(false) }

    AppCard(highlight = true) {
        Label("⚙ ПЕРСОНАЛЬНАЯ НАСТРОЙКА")
        Text("Свои жесты", color = AppText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        MutedText("Создайте понятное для себя название и выберите движение взгляда для проверки.", small = true)
    }

    AppCard {
        Text("Название жеста", color = AppMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 2.dp, bottom = 6.dp))
        OutlinedTextField(
            value = gestureNameInput,
            onValueChange = { gestureNameInput = it; isGestureNameInvalid = false },
            placeholder = { Text("Например: открыть заметки", color = AppMuted) },
            singleLine = true,
            isError = isGestureNameInvalid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppAccent,
                unfocusedBorderColor = AppBorder,
                errorBorderColor = Color(0xFFE05A5A),
                focusedTextColor = AppText,
                unfocusedTextColor = AppText,
                cursorColor = AppAccent
            )
        )
        Text("Движение взгляда", color = AppMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 14.dp, bottom = 6.dp))
        Box {
            OutlinedTextField(
                value = selectedEyeMovement,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { Text("⌄", color = AppMuted, fontSize = 18.sp) },
                modifier = Modifier.fillMaxWidth().clickable { isMovementMenuExpanded = true },
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppAccent,
                    unfocusedBorderColor = AppBorder,
                    focusedTextColor = AppText,
                    unfocusedTextColor = AppText
                )
            )
            DropdownMenu(
                expanded = isMovementMenuExpanded,
                onDismissRequest = { isMovementMenuExpanded = false },
                modifier = Modifier.background(AppCard)
            ) {
                listOf("Круг", "Влево — вправо", "Вверх — вниз").forEach { movementOption ->
                    DropdownMenuItem(text = { Text(movementOption, color = AppText) }, onClick = {
                        selectedEyeMovement = movementOption
                        isMovementMenuExpanded = false
                    })
                }
            }
        }
        PrimaryButton(
            text = "+ Добавить жест",
            onClick = {
                if (gestureNameInput.trim().isEmpty()) {
                    isGestureNameInvalid = true
                } else {
                    onAddGesture(CustomGesture(gestureNameInput.trim(), selectedEyeMovement))
                    gestureNameInput = ""
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp)
        )
    }

    AppCard {
        Label("☰ МОИ ЖЕСТЫ")
        if (customGestureList.isEmpty()) {
            MutedText("Пока нет своих жестов. Добавьте первый.", small = true)
        } else {
            customGestureList.forEachIndexed { gestureIndex, customGesture ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppCard2)
                        .border(1.dp, AppBorder, RoundedCornerShape(12.dp))
                        .padding(10.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(customGesture.gestureName, color = AppText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        MutedText(customGesture.eyeMovement, small = true)
                    }
                    IconButton(
                        onClick = { onDeleteGesture(gestureIndex) },
                        modifier = Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(AppCard)
                    ) {
                        Text("✕", color = AppAccent, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigation(selected: AppTab, onSelected: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0E1A2C))
            .border(1.dp, AppBorder, RoundedCornerShape(20.dp))
            .padding(6.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        AppTab.entries.forEach { tab ->
            val isTabActive = selected == tab
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isTabActive) Color(0xFF143D38) else Color.Transparent)
                    .clickable { onSelected(tab) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(tab.displayIcon, color = if (isTabActive) AppAccent else AppMuted, fontSize = 16.sp)
                Spacer(Modifier.height(3.dp))
                Text(tab.displayTitle, color = if (isTabActive) AppAccent else AppMuted, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun AppCard(
    modifier: Modifier = Modifier,
    highlight: Boolean = false,
    accent: Boolean = false,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val cardBackground = when {
        accent -> Brush.verticalGradient(listOf(Color(0xFF0F2A2A), Color(0xFF0F2A2A)))
        highlight -> Brush.verticalGradient(listOf(Color(0xFF12263A), Color(0xFF0E1A2C)))
        else -> Brush.verticalGradient(listOf(AppCard, AppCard))
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(cardBackground)
            .border(1.dp, if (accent) Color(0xFF1E4D45) else if (highlight) Color(0xFF1F3A5A) else AppBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        content = content
    )
}

@Composable
private fun AppSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = Modifier.scale(0.9f),
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = AppAccent,
            uncheckedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFF223049),
            uncheckedBorderColor = Color.Transparent
        )
    )
}

@Composable
private fun Label(text: String, centered: Boolean = false, bottom: Dp = 8.dp) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth().padding(bottom = bottom),
        textAlign = if (centered) TextAlign.Center else TextAlign.Start
    )
}

@Composable
private fun MutedText(text: String, small: Boolean = false, centered: Boolean = false) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = if (small) 12.sp else 13.sp,
        lineHeight = if (small) 17.sp else 19.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = if (centered) TextAlign.Center else TextAlign.Start
    )
}

@Composable
private fun StatusDot(size: Dp = 8.dp) {
    Box(Modifier.size(size).clip(CircleShape).background(AppAccent))
}

@Composable
private fun EyeSimulationVisual() {
    Box(Modifier.padding(vertical = 4.dp).size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF2A4568),
                radius = size.minDimension / 2 - 1.dp.toPx(),
                style = Stroke(1.dp.toPx(), pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
            )
        }
        Box(
            Modifier
                .size(width = 130.dp, height = 70.dp)
                .clip(RoundedCornerShape(50))
                .background(Brush.radialGradient(listOf(Color(0xFFD7F6EE), Color(0xFFA7E6D8), Color(0xFF6EC8B6))))
        )
        Box(
            Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(Color(0xFF0D9488), Color(0xFF115E59))))
        )
    }
}

@Composable
private fun GestureCard(icon: String, title: String, subtitle: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AppCard)
            .border(1.dp, if (isSelected) AppAccent else AppBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(12.dp, 10.dp)
    ) {
        Text(icon, color = AppAccent, fontSize = 18.sp, modifier = Modifier.padding(bottom = 6.dp))
        Text(title, color = AppText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2)
        Text(subtitle, color = AppMuted, fontSize = 11.sp)
    }
}

@Composable
private fun TimerRing(remainingSeconds: Int) {
    Box(Modifier.size(180.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val ringStrokeWidthPx = 8.dp.toPx()
            drawArc(Color(0xFF1A2942), -90f, 360f, false, style = Stroke(ringStrokeWidthPx, cap = StrokeCap.Round))
            drawArc(AppAccent, -90f, 360f * remainingSeconds / TRAINING_DURATION_SECONDS, false, style = Stroke(ringStrokeWidthPx, cap = StrokeCap.Round))
        }
        Box(
            Modifier
                .size(164.dp)
                .clip(CircleShape)
                .background(AppBackground)
                .border(1.dp, AppBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(formatTime(remainingSeconds), color = AppText, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text("ДО ЗАВЕРШЕНИЯ", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String = "%02d:%02d".format(totalSeconds / 60, totalSeconds % 60)

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AppAccent, contentColor = Color(0xFF06231E))
    ) { Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
private fun GhostButton(text: String, selected: Boolean = false, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
        border = BorderStroke(1.dp, if (selected) AppAccent else AppBorder),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xFF143D38) else Color.Transparent,
            contentColor = if (selected) AppAccent else AppText
        )
    ) { Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1) }
}
