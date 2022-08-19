package com.orelzman.mymessages.domain.system.phone_call.exceptions

object WaitingThenRingingException: Exception("The state was RINGING after a WAITING state")