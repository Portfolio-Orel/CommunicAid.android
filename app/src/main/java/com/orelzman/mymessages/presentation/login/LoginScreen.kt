package com.orelzman.mymessages.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ApiException
import com.orelzman.auth.domain.activity_result.ActivityResultContractImpl
import com.orelzman.auth.domain.exception.TaskException
import com.orelzman.mymessages.presentation.destinations.MainScreenDestination
import com.orelzman.mymessages.presentation.login.components.LoginButton
import com.orelzman.mymessages.ui.theme.MyMessagesTheme
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ExperimentalPermissionsApi
@Composable
@Destination(start = true)
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.state
    val signInRequest = 1


    val authResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContractImpl()) { task ->
            try {
                val account = task?.getResult(TaskException::class.java)
                if (account != null) {
                    viewModel.onEvent(LoginEvents.AuthWithGmail(account))
                }
            } catch (e: ApiException) {
                println(e.message)
            }
        }

    MyMessagesTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (state.user != null) {
                navigator.navigate(MainScreenDestination)
            } else {
                Text(text = "!LoggedIn")
            }
            LoginButton(text = "Login", isLoading = false) {
                authResultLauncher.launch(signInRequest)
            }
        }
//        Button(
//            modifier =
//            Modifier
//                .width(128.dp)
//                .height(48.dp)
//
//            ,
//            onClick = {
//                viewModel.onEvent(LoginEvents.AuthWithGmail(context))
//            }) {
//            if(!state.isLoading) {
//                Text("Login")
//            } else {
//                CircularProgressIndicator(
//                    modifier = Modifier.padding(bottom = 12.dp),
//                    color = Color.White
//                )
//            }
//        }
    }
}