package com.orelzman.mymessages.presentation.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.common.api.ApiException
import com.orelzman.auth.domain.activity_result.ActivityResultContractImpl
import com.orelzman.mymessages.presentation.login.components.LoginButton
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@ExperimentalMaterialApi
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
                val account = task?.getResult(ApiException::class.java)
                if (account == null) {

                } else {
                        viewModel.onEvent(LoginEvents.AuthWithGmail)
                    }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if(viewModel.state.user != null) {
//            navigator.navigate(MainScreenDestination)
        } else {
            Text(text = "!LoggedIn")
        }
        LoginButton(text = "Login", isLoading = false) {
            authResultLauncher.launch(signInRequest)
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