package com.orels.auth.domain.exception

abstract class AuthException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class CodeDeliveryFailureException: AuthException()
    class CodeExpiredException: AuthException()
    class CodeMismatchException: AuthException("Code mismatch")
    class CouldNotRefreshTokenException: AuthException()
    class InvalidPasswordException: AuthException()
    class LimitExceededException: AuthException()
    class NotAuthorizedException: AuthException()
    class UnknownRegisterException: AuthException("Unknown register exception")
    class UsernameExistsException: AuthException()
    class UsernamePasswordAuthException(exception: Exception) : AuthException(cause = exception)
    class UserNotConfirmedException: AuthException("User is not confirmed.")
    class UserNotFoundException: AuthException()
    class WrongCredentialsException: AuthException()
}
