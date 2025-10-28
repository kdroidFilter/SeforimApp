package io.github.kdroidfilter.seforimapp.features.settings.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kdroidfilter.seforimapp.core.settings.AppSettings
import io.github.kdroidfilter.seforimapp.features.onboarding.userprofile.Community
import io.github.kdroidfilter.seforimapp.features.onboarding.userprofile.UserProfileEvents
import io.github.kdroidfilter.seforimapp.features.onboarding.userprofile.UserProfileState
import io.github.kdroidfilter.seforimapp.features.onboarding.userprofile.UserProfileViewModel
import io.github.kdroidfilter.seforimapp.framework.di.LocalAppGraph
import io.github.kdroidfilter.seforimapp.theme.PreviewContainer
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.ListComboBox
import org.jetbrains.jewel.ui.component.SpeedSearchArea
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import seforimapp.seforimapp.generated.resources.Res
import seforimapp.seforimapp.generated.resources.onboarding_user_community_ashkenaze
import seforimapp.seforimapp.generated.resources.onboarding_user_community_label
import seforimapp.seforimapp.generated.resources.onboarding_user_community_sefard
import seforimapp.seforimapp.generated.resources.onboarding_user_community_sepharade
import seforimapp.seforimapp.generated.resources.onboarding_user_first_name_label
import seforimapp.seforimapp.generated.resources.onboarding_user_last_name_label
import seforimapp.seforimapp.generated.resources.save_button

@Composable
fun ProfileSettingsScreen() {
    val viewModel: UserProfileViewModel = LocalAppGraph.current.userProfileViewModel
    val state by viewModel.state.collectAsState()
    val canSave = state.firstName.isNotBlank() && state.lastName.isNotBlank() && state.selectedCommunityIndex >= 0
    ProfileSettingsView(
        state = state,
        onEvent = viewModel::onEvent,
        onSave = {
            val firstName = state.firstName.trim()
            val lastName = state.lastName.trim()
            val community = state.communities.getOrNull(state.selectedCommunityIndex)
            AppSettings.setUserFirstName(firstName)
            AppSettings.setUserLastName(lastName)
            AppSettings.setUserCommunityCode(community?.name)
        },
        canSave = canSave
    )
}

@Composable
private fun ProfileSettingsView(
    state: UserProfileState,
    onEvent: (UserProfileEvents) -> Unit,
    onSave: () -> Unit,
    canSave: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.onboarding_user_first_name_label))
                val firstNameState = rememberTextFieldState(state.firstName)
                LaunchedEffect(state.firstName) {
                    if (firstNameState.text != state.firstName) firstNameState.setTextAndPlaceCursorAtEnd(state.firstName)
                }
                LaunchedEffect(firstNameState.text) {
                    val value = firstNameState.text.toString()
                    if (value != state.firstName) onEvent(UserProfileEvents.FirstNameChanged(value))
                }
                TextField(state = firstNameState, modifier = Modifier.widthIn(max = 240.dp))
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(Res.string.onboarding_user_last_name_label))
                val lastNameState = rememberTextFieldState(state.lastName)
                LaunchedEffect(state.lastName) {
                    if (lastNameState.text != state.lastName) lastNameState.setTextAndPlaceCursorAtEnd(state.lastName)
                }
                LaunchedEffect(lastNameState.text) {
                    val value = lastNameState.text.toString()
                    if (value != state.lastName) onEvent(UserProfileEvents.LastNameChanged(value))
                }
                TextField(state = lastNameState, modifier = Modifier.widthIn(max = 240.dp))
            }
        }

        Column(horizontalAlignment = Alignment.Start) {
            Text(stringResource(Res.string.onboarding_user_community_label))
            Spacer(Modifier.height(8.dp))
            val communityLabels = listOf(
                stringResource(Res.string.onboarding_user_community_sepharade),
                stringResource(Res.string.onboarding_user_community_ashkenaze),
                stringResource(Res.string.onboarding_user_community_sefard),
            )
            SpeedSearchArea(Modifier.widthIn(max = 200.dp)) {
                ListComboBox(
                    items = communityLabels,
                    selectedIndex = state.selectedCommunityIndex,
                    onSelectedItemChange = { index -> onEvent(UserProfileEvents.SelectCommunity(index)) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        DefaultButton(onClick = onSave, enabled = canSave) {
            Text(stringResource(Res.string.save_button))
        }
    }
}

@Composable
@Preview
private fun ProfileSettingsView_Preview() {
    PreviewContainer {
        ProfileSettingsView(
            state = UserProfileState(
                firstName = "אברהם",
                lastName = "כהן",
                communities = listOf(Community.SEPHARADE, Community.ASHKENAZE, Community.SEFARD),
                selectedCommunityIndex = 0
            ),
            onEvent = {},
            onSave = {},
            canSave = true
        )
    }
}
