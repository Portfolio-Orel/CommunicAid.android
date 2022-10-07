package com.orels.auth.di

import android.content.Context
import com.orels.auth.AuthActivity
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [AuthModuleDependencies::class])
interface AuthComponent {

    fun inject(activity: AuthActivity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(loginModuleDependencies: AuthModuleDependencies): Builder
        fun build(): AuthComponent
    }
}