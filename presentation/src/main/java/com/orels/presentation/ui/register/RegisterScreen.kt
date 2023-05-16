package com.orels.presentation.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.orels.domain.model.entities.Gender
import com.orels.domain.util.Screen
import com.orels.domain.util.Validators
import com.orels.domain.util.extension.takeLastOrEmpty
import com.orels.domain.util.extension.takeOrEmpty
import com.orels.presentation.R
import com.orels.presentation.ui.BackPressHandler
import com.orels.presentation.ui.components.Input
import com.orels.presentation.ui.main.components.ActionButton
import java.util.*

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var backPressed by remember { mutableStateOf(false) }

    val state = viewModel.state

    BackPressHandler {
        backPressed = true
        if (state.stage != (registrationProcess.firstOrNull() ?: Stage.BASIC_INFORMATION)) {
            viewModel.onEvent(RegisterEvent.PreviousStage)
        } else {
            navController.popBackStack()
        }
    }

    Column {
        AnimateContent(
            shouldShow = state.stage == Stage.BASIC_INFORMATION,
        ) {
            GetBasicInformation(
                gender = state.gender,
                firstName = state.firstName,
                lastName = state.lastName,
                onNameEntered = { firstName, lastName, gender ->
                    viewModel.onEvent(RegisterEvent.SetFirstName(firstName))
                    viewModel.onEvent(RegisterEvent.SetLastName(lastName))
                    viewModel.onEvent(RegisterEvent.SetGender(gender))
                    viewModel.onEvent(RegisterEvent.CompleteRegistration)
                },
                isLoading = state.isLoading,
            )
        }
        AnimateContent(
            shouldShow = state.stage == Stage.EMAIL_NUMBER,
        ) {
            GetEmailAndPhoneNumber(
                email = state.email,
                phoneNumber = state.phoneNumber,
                isLoading = state.isLoading,
                onDetailsEntered = { mail, number ->
                    viewModel.onEvent(RegisterEvent.SetEmail(mail))
                    viewModel.onEvent(RegisterEvent.SetPhoneNumber(number))
                    viewModel.onEvent(RegisterEvent.Register)
                },
                validateEmail = { Validators.isEmailValid(it) },
                validatePhoneNumber = { Validators.isPhoneNumberValid(it) }
            )
        }

        AnimateContent(shouldShow = state.stage == Stage.CONFIRMATION && state.isLoading.not()) {
            ConfirmationCodeDialog(code = state.code) {
                viewModel.onEvent(RegisterEvent.ConfirmCode(it))
            }
        }

        AnimateContent(shouldShow = state.stage == Stage.DONE) {
            DoneContent(
                onDone = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                isLoading = state.isLoading
            )
        }

        if (state.error != null) {
            Text(
                text = stringResource(id = state.error),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.background)
            )
        }
    }
}

@Composable
fun GetBasicInformation(
    onNameEntered: (firstName: String, lastName: String, gender: Gender) -> Unit,
    gender: Gender? = null,
    firstName: String = "",
    lastName: String = "",
    isLoading: Boolean = false
) {
    val firstNameValue = remember { mutableStateOf(firstName) }
    val firstNameError = remember { mutableStateOf(false) }

    val lastNameValue = remember { mutableStateOf(lastName) }
    val lastNameError = remember { mutableStateOf(false) }

    val genderValue = remember { mutableStateOf(gender) }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.lets_get_to_know),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Input(
            title = stringResource(R.string.first_name),
            placeholder = stringResource(R.string.placeholder_first_name),
            minLines = 1,
            maxLines = 1,
            isError = firstNameError.value,
            initialText = firstNameValue.value,
            isPassword = false,

            onTextChange = {
                firstNameValue.value = it
                it
            }
        )
        Input(
            title = stringResource(R.string.last_name),
            placeholder = stringResource(R.string.placeholder_last_name),
            minLines = 1,
            maxLines = 1,
            isError = lastNameError.value,
            initialText = lastNameValue.value,
            isPassword = false,
            onTextChange = {
                lastNameValue.value = it
                it
            }
        )
        SubTitle(text = stringResource(R.string.gender))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Gender.values().forEach { gender ->
                GenderContainer(
                    gender = gender,
                    selected = genderValue.value == gender,
                    onClick = {
                        genderValue.value = it
                    },
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        ActionButton(
            onClick = {
                lastNameError.value = false
                firstNameError.value = false
                if (firstNameValue.value.isNotBlank() && lastNameValue.value.isNotBlank() && genderValue.value != null) {
                    onNameEntered(
                        firstNameValue.value,
                        lastNameValue.value,
                        genderValue.value!!,
                    )
                } else {
                    firstNameError.value = firstNameValue.value.isBlank()
                    lastNameError.value = lastNameValue.value.isBlank()
                }
            }, text = stringResource(R.string.next),
            isLoading = isLoading
        )
    }
}


@Composable
fun GetEmailAndPhoneNumber(
    onDetailsEntered: (email: String, number: String) -> Unit,
    validateEmail: (String) -> Boolean,
    isEmailDisabled: Boolean = false,
    isPhoneNumberDisabled: Boolean = false,
    validatePhoneNumber: (String) -> Boolean,
    isLoading: Boolean = false,
    email: String = "",
    phoneNumber: String = "",
) {
    val number = remember { mutableStateOf(phoneNumber) }
    val numberError = remember { mutableStateOf(false) }
    val emailValue = remember { mutableStateOf(email) }
    val emailError = remember { mutableStateOf(false) }

    val numberFocusRequester = remember { FocusRequester() }

    val onDone = {
        emailError.value = false
        numberError.value = false
        if (!validateEmail(emailValue.value)) {
            emailError.value = true
        }
        if (!validatePhoneNumber(number.value)) {
            numberError.value = true
        }
        if (!emailError.value && !numberError.value && emailValue.value.isNotBlank() && number.value.isNotBlank()) {
            onDetailsEntered(emailValue.value, number.value)
        } else {
            emailError.value = emailValue.value.isBlank()
            numberError.value = number.value.isBlank()
        }
    }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.login_details),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Input(
            title = stringResource(R.string.email),
            minLines = 1,
            maxLines = 1,
            isError = emailError.value,
            initialText = emailValue.value,
            imeAction = ImeAction.Next,
            keyboardActions = KeyboardActions(
                onNext = {
                    numberFocusRequester.requestFocus()
                }
            ),
            isPassword = false,
            onTextChange = {
                emailValue.value = it
                emailError.value = false
                it.replace(" ", "")
                it
            },
            enabled = !isEmailDisabled
        )
        Input(
            title = stringResource(R.string.phone_number),
            minLines = 1,
            maxLines = 1,
            isError = numberError.value,
            initialText = number.value,
            isPassword = false,
            onTextChange = {
                number.value = it
                it.replace(" ", "")
                it
            },
            keyboardType = KeyboardType.Phone,
            imeAction = ImeAction.Done,
            keyboardActions = KeyboardActions(
                onDone = {
                    onDone()
                }
            ),
            enabled = !isPhoneNumberDisabled,
            focusRequester = numberFocusRequester
        )

        Spacer(modifier = Modifier.height(32.dp))
        ActionButton(
            onClick = {
                onDone()
            }, text = stringResource(R.string.next),
            isLoading = isLoading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationCodeDialog(
    code: String,
    onConfirm: (code: String) -> Unit,
) {
    val codeInputs = 6
    val codeValues = remember {
        mutableStateListOf(
            code.takeOrEmpty(0),
            code.takeOrEmpty(1),
            code.takeOrEmpty(2),
            code.takeOrEmpty(3),
            code.takeOrEmpty(4),
            code.takeOrEmpty(5),
        )
    }
    val focusRequesters = remember { List(codeInputs) { FocusRequester() } }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val spaceBetweenInputs = 4.dp
    val inputSize = (screenWidth - spaceBetweenInputs * (codeInputs * 2) - 16.dp) / (codeInputs)
    val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(cursorColor = Color.Transparent)

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.insert_sms_code),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(horizontalArrangement = Arrangement.Center) {
                for (i in 0 until codeInputs) {
                    OutlinedTextField(
                        value = codeValues[i],
                        onValueChange = { newValue ->
                            codeValues[i] = newValue.takeLastOrEmpty() ?: ""
                            if (newValue.isNotEmpty() && i < codeInputs - 1) {
                                focusRequesters[i + 1].requestFocus()
                            }
                        },
                        modifier = Modifier
                            .padding(horizontal = spaceBetweenInputs)
                            .size(inputSize, 64.dp)
                            .focusRequester(focusRequesters[i]),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Phone,
                            imeAction = if (i < codeInputs - 1) ImeAction.Next else ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                focusRequesters[i + 1].requestFocus()
                            },
                            onDone = {
                                onConfirm(codeValues.joinToString(""))
                            }
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        colors = textFieldColors
                    )
                }
            }
            ActionButton(
                onClick = {
                    onConfirm(codeValues.joinToString(""))
                },
                modifier = Modifier.padding(vertical = 16.dp),
                text = stringResource(R.string.confirm)
            )
        }
    }
}

@Composable
fun AnimateContent(
    shouldShow: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = shouldShow,
        enter = slideInHorizontally(
            initialOffsetX = { fullWidth -> 2 * fullWidth },
            animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing)
        )
    ) {
        content()
    }
}

@Composable
fun SubTitle(
    text: String,
) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Start
    )
}

@Composable
fun GenderContainer(
    gender: Gender,
    selected: Boolean,
    onClick: (Gender) -> Unit,
) {
    val drawable = when (gender) {
        Gender.Male -> R.drawable.male
        Gender.Female -> R.drawable.female
        Gender.None -> R.drawable.sex_none
    }
    val contentDescription = when (gender) {
        Gender.Male -> R.string.male
        Gender.Female -> R.string.female
        Gender.None -> R.string.none
    }

    val selectedColors = listOf(
        Color(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f).value),
        Color(MaterialTheme.colorScheme.primary.copy(alpha = 0.85f).value),
        Color(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f).value),
    )
    val unselectedColors = listOf(
        Color(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f).value),
        Color(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f).value),
        Color(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f).value),
    )
    val colors = if (selected) selectedColors else unselectedColors

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = colors,
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY,
                    ),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(6.dp)
                .clickable { onClick(gender) }
        ) {
            Image(
                modifier = Modifier.size(64.dp),
                painter = painterResource(id = drawable),
                contentDescription = stringResource(contentDescription),
            )
        }
        Text(
            text = if (selected) stringResource(id = contentDescription) else stringResource(id = R.string.empty_string),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}


@Composable
fun DoneContent(onDone: () -> Unit, isLoading: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            text = stringResource(R.string.done_exclamation),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = stringResource(R.string.account_created_successfully),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Thin,
        )
        ActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp),
            onClick = { onDone() },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            text = stringResource(R.string.lets_go),
            isLoading = isLoading,
        )
    }
}