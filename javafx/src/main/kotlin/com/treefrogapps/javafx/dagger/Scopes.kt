package com.treefrogapps.javafx.dagger

import javax.inject.Scope

interface Scopes {

    @Scope
    @Retention(AnnotationRetention.RUNTIME)
    annotation class InitializableScope
}