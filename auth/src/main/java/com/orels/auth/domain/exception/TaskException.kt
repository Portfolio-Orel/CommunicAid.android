package com.orels.auth.domain.exception

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status

class TaskException(status: Status): ApiException(status)