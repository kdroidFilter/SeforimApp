package io.github.kdroidfilter.seforimapp.features.onboarding.typeofinstall

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.kdroidfilter.seforimapp.icons.Download_for_offline
import io.github.kdroidfilter.seforimapp.features.onboarding.navigation.OnBoardingDestination
import io.github.kdroidfilter.seforimapp.features.onboarding.navigation.ProgressBarState
import io.github.kdroidfilter.seforimapp.features.onboarding.ui.components.OnBoardingScaffold
import io.github.kdroidfilter.seforimapp.framework.di.LocalAppGraph
import io.github.kdroidfilter.seforimapp.icons.Unarchive
import io.github.kdroidfilter.seforimapp.theme.PreviewContainer
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.path
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography
import seforimapp.seforimapp.generated.resources.*

@Composable
fun TypeOfInstallationScreen(navController: NavController, progressBarState: ProgressBarState = ProgressBarState) {
    LaunchedEffect(Unit) {
        progressBarState.setProgress(0.3f)
    }
    val viewModel: TypeOfInstallationViewModel = LocalAppGraph.current.typeOfInstallationViewModel
    val pickZstLauncher = rememberFilePickerLauncher(
        type = FileKitType.File(extensions = listOf("zst"))
    ) { file ->
        val path = file?.path
        if (path != null) {
            viewModel.onEvent(TypeOfInstallationEvents.OfflineFileChosen(path))
            // Jump to the start of the Extract step when selecting an offline archive
            progressBarState.setProgress(0.8f)
            // Move forward and clear previous onboarding steps so back is disabled
            navController.navigate(OnBoardingDestination.ExtractScreen) {
                popUpTo<OnBoardingDestination.InitScreen> { inclusive = true }
            }
        }
    }
    TypeOfInstallationView(
        onOnlineInstallation = {
            // Move forward and clear previous onboarding steps so back is disabled
            navController.navigate(OnBoardingDestination.DatabaseOnlineInstallerScreen) {
                popUpTo<OnBoardingDestination.InitScreen> { inclusive = true }
            }
        },
        onOfflineInstallation = { pickZstLauncher.launch() }
    )
}

@Composable
private fun TypeOfInstallationView(
    onOnlineInstallation: () -> Unit = {},
    onOfflineInstallation: () -> Unit = {}
) {
    OnBoardingScaffold(title = stringResource(Res.string.installation_title)) {
        Row(modifier = Modifier.fillMaxSize()) {
            InstallationTypeColumn(
                // Offline
                title = stringResource(Res.string.installation_offline_title),
                icon = Unarchive,
                description = stringResource(Res.string.installation_offline_desc),
                buttonAction = { onOfflineInstallation() },
                buttonText = stringResource(Res.string.installation_offline_button)
            )
            Divider(orientation = Orientation.Vertical, modifier = Modifier.fillMaxHeight().width(1.dp))
            InstallationTypeColumn(
                // Online
                title = stringResource(Res.string.installation_online_title),
                icon = Download_for_offline,
                description = stringResource(Res.string.installation_online_desc),
                buttonAction = { onOnlineInstallation() },
                buttonText = stringResource(Res.string.installation_online_button)
            )
        }
    }
}


@Composable
private fun RowScope.InstallationTypeColumn(
    title: String,
    icon: ImageVector,
    description: String,
    buttonText: String,
    buttonAction: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .weight(1f)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, fontSize = JewelTheme.typography.h1TextStyle.fontSize)
        Icon(icon, title, modifier = Modifier.size(72.dp), tint = JewelTheme.globalColors.text.normal)
        Text(
            description,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        DefaultButton(buttonAction) {
            Text(buttonText)
        }
    }
}

@Composable
@Preview
fun TypeOfInstallationScreenPreview() {
    PreviewContainer {
        TypeOfInstallationView()
    }
}
