package com.orelzman.mymessages.domain.service.phone_call.exceptions

object WaitingThenRingingException: Exception("The state was RINGING after a WAITING state")