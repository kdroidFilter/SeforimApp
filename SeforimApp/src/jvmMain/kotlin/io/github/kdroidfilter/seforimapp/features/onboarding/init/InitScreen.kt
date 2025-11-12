package io.github.kdroidfilter.seforimapp.features.onboarding.init

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.kdroidfilter.seforimapp.features.onboarding.ui.components.OnBoardingScaffold
import io.github.kdroidfilter.seforimapp.features.onboarding.navigation.OnBoardingDestination
import io.github.kdroidfilter.seforimapp.features.onboarding.navigation.ProgressBarState
import io.github.kdroidfilter.seforimapp.theme.PreviewContainer
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import seforimapp.seforimapp.generated.resources.*

@Composable
fun InitScreen(navController: NavController, progressBarState: ProgressBarState = ProgressBarState) {
    LaunchedEffect(Unit) {
        progressBarState.resetProgress()
    }
    InitView(onNext = {
        navController.navigate(OnBoardingDestination.LicenceScreen)
    })
}

@Composable
fun InitView(onNext: () -> Unit) {
    OnBoardingScaffold(
        title = stringResource(Res.string.onboarding_init_welcome_title),
        bottomAction = {
            DefaultButton({ onNext() }) { Text(stringResource(Res.string.onboarding_init_start)) }
        }
    ) {
        Text(
            text = stringResource(Res.string.onboarding_init_welcome_subtitle),
            fontSize = JewelTheme.typography.h4TextStyle.fontSize,
            textAlign = TextAlign.Center
        )
        val infiniteTransition = rememberInfiniteTransition(label = "oliveBranch")
        val floatingOffset by infiniteTransition.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "floatingOffset"
        )
        val shakeRotation by infiniteTransition.animateFloat(
            initialValue = -4f,
            targetValue = 4f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1400, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "shakeRotation"
        )
        Image(
            painter = painterResource(Res.drawable.zayit_transparent),
            contentDescription = null,
            modifier = Modifier
                .size(176.dp)
                .offset(y = floatingOffset.dp)
                .graphicsLayer { rotationZ = shakeRotation }
        )
        Text(stringResource(Res.string.onboarding_setup_guide))
    }
}

@Preview
@Composable
fun InitScreenPreview() {
    PreviewContainer { InitView({}) }
}