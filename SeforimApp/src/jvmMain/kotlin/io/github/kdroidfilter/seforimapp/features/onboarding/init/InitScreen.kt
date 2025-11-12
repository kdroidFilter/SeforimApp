package io.github.kdroidfilter.seforimapp.features.onboarding.init

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.kdroidfilter.seforimapp.core.settings.AppLanguage
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
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
    val selectedLanguage = AppSettings.languageFlow.collectAsState().value
    InitView(
        language = selectedLanguage,
        onLanguageSelected = { AppSettings.setLanguage(it) },
        onNext = {
            navController.navigate(OnBoardingDestination.LicenceScreen)
        }
    )
}

@Composable
fun InitView(
    language: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onNext: () -> Unit
) {
    OnBoardingScaffold(
        title = stringResource(Res.string.onboarding_init_welcome_title),
        bottomAction = {
            DefaultButton({ onNext() }) { Text(stringResource(Res.string.onboarding_init_start)) }
        }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(Res.string.language_selection_prompt_english),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Res.string.language_selection_prompt_hebrew),
                textAlign = TextAlign.Center
            )
            LanguageToggle(
                language = language,
                onLanguageSelected = onLanguageSelected
            )
            Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(Res.string.onboarding_init_welcome_subtitle),
            fontSize = JewelTheme.typography.h4TextStyle.fontSize,
            textAlign = TextAlign.Center
        )
        Image(
            painter = painterResource(Res.drawable.zayit_transparent),
            contentDescription = null,
            modifier = Modifier.size(176.dp)
        )
        Text(stringResource(Res.string.onboarding_setup_guide))
        }
    }
}

@Composable
private fun LanguageToggle(language: AppLanguage, onLanguageSelected: (AppLanguage) -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        LanguageToggleChip(
            text = stringResource(Res.string.language_option_hebrew),
            selected = language == AppLanguage.HEBREW,
            onClick = { onLanguageSelected(AppLanguage.HEBREW) },
            shape = shape
        )
        LanguageToggleChip(
            text = stringResource(Res.string.language_option_english),
            selected = language == AppLanguage.ENGLISH,
            onClick = { onLanguageSelected(AppLanguage.ENGLISH) },
            shape = shape
        )
    }
}

@Composable
private fun LanguageToggleChip(text: String, selected: Boolean, onClick: () -> Unit, shape: RoundedCornerShape) {
    val background = if (selected) JewelTheme.globalColors.primary else JewelTheme.globalColors.panelBackground
    val borderColor = if (selected) JewelTheme.globalColors.primary else JewelTheme.globalColors.borders.normal
    val contentColor = if (selected) Color.White else JewelTheme.globalColors.text.normal
    Text(
        text = text,
        color = contentColor,
        modifier = Modifier
            .clip(shape)
            .background(background)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}

@Preview
@Composable
fun InitScreenPreview() {
    PreviewContainer { InitView(AppLanguage.HEBREW, {}, {}) }
}