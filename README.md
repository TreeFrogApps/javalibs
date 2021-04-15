# TreeFrogApps Java Libraries

### A collection of libraries for java platform written in Kotlin



- rxjava 3 utils
- javafx utils (including Dagger2 and RxJava3)

---
###### To use these libraries go to the packages tab and download the jar. 
###### An alternative using as library dependency this will require credentials available on request :


- repo url
- repo username
- repo password

project `build.gradle.kts` :
```
plugins {
    kotlin("jvm") version "1.4.32" apply false
}

repositories {
        ... other repositories
        maven {
            name = "GitHubPackages"
            url = [repo url]
            credentials {
                username = [repo username]
                password = [repo password]
            }
        }
    }
```

module : `build.gradle.kts`
````
plugins {
    ... other plugins
    application
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.kapt")
    id("org.openjfx.javafxplugin") version "0.0.9"
}

dependencies {
    ... other dependencies
    
    implementation("com.treefrogapps.rxjava3:rxjava3:x.x.x")
    implementation("com.treefrogapps.javafx:javafx:x.x.x")

    // Dagger
    val daggerVersion = "2.34"
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
}
````
---



### JavaFX Dagger Application Setup

1) Create `DaggerApplication` subclass :

```
class MyApp : DaggerApplication() {

    override fun component(): ApplicationInjector<out DaggerApplication> =
      DaggerMyAppComponent.factory().addApp(this)
}
```

2) Create the main `Application` Component
```
@Singleton
@Component
interface MyAppComponent : ApplicationInjector<MyApp> {

    @Component.Factory interface Factory {
        fun addApp(@BindsInstance app: Application): ApplicationInjector<MyMonitorApp>
    }
}
```
<br>

#### Controller Subcomponents and adding to parent `LayoutStage` and using `LayoutStageManager` to control this.

1) Create a scoped `LayoutController`
   
```
@ControllerScope 
class MyLayoutController @Inject constructor() : LayoutController() {

    override fun onInitialized(location: URL?) { }

    override fun onUpdate(args: Map<String, String>) { }

    override fun onStart() { }

    override fun onStop() { }
}
```

2) Create a `@Module` that supplies a `ControllerFactory`
   
```
@Module object MyLayoutModule {

    @Provides
    @ControllerScope
    fun factory(controller: Provider<MyLayoutController>): ControllerFactory<MyLayoutController> =
        object : ControllerFactory<MyLayoutController> {
            override fun layout(): String = "my_layout_resource"
            override fun callback(): Callback<Class<MyLayoutController>, MyLayoutController> =
                Callback<Class<MyLayoutController>, MyLayoutController> { controller.get() }
        }
}
```

3) Create a scoped Subcomponent Module
   
```
@Module(subcomponents = [MyLayoutSubcomponent::class])
abstract class MyLayoutBuilderModule {

    @Binds
    @IntoMap
    @ControllerKey(value = MyLayoutController::class)
    abstract fun factory(factory: MyLayoutSubcomponent.Factory): ControllerComponent.Factory<out DaggerController>

    @ControllerScope
    @Subcomponent(modules = [MyLayoutModule::class])
    interface MyLayoutSubcomponent : ControllerComponent<MyLayoutController> {

        @Subcomponent.Factory
        interface Factory : ControllerComponent.Factory<MyLayoutController>
    }
}
```

4) Create an Injectable Stage that extends `LayoutStage`

```
class MyLayoutStage @Inject constructor(
    schedulers: Rx3Schedulers,
    inflater: LayoutInflater,
    bundle: ResourceBundle)
    : LayoutStage(inflater, schedulers, bundle) {

    init {
        minWidth = 400.0;
        minHeight = 230.0;
    }
}
```

5) Create a Module that binds this to a `@StageKey` and add the subcomponent

```
@Module(includes = [MyLayoutBuilderModule::class])
abstract class MyLayoutStageModule {

    @Binds
    @IntoMap
    @StageKey(value = MyLayoutStage::class)
    abstract fun myStage(layoutStage: MyLayoutStage): LayoutStage
}
```

6) Lastly add your Module to the parent graph
```
@Singleton
@Component(
    modules = [
        OtherAppModule::class,
        MyLayoutStageModule::class
    ])
interface MyAppComponent : ApplicationInjector<MyApp> {

    @Component.Factory interface Factory {
        fun addApp(@BindsInstance app: Application): ApplicationInjector<MyMonitorApp>
    }
}
```

All interactions for loading `LayoutStage` and child `LayoutController` classes
and are performed through `LayoutStageManager`. Typical usage in main `DaggerApplication` class :

```
class MyApp : DaggerApplication() {

    // Inject the LayoutManager - this is a singleton class
    // and can be injected anywhere
    @Inject lateinit var layoutStageManager: LayoutStageManager

    override fun start(primaryStage: Stage) {
        super.start(primaryStage)

       // Use the manager to launch the required Stage and Controller for the stage.
       // Controllers are children of a stage - each stage can have n number of children
       // Each Stage has its own scene. The LayoutStageManager supports n number of Stages
        layoutStageManager.launch(
            MyLayoutStage::class.java,
            MyLayoutController::class.java,
            mapOf("my" to "arugments")))
    }

    override fun component(): ApplicationInjector<out DaggerApplication> =
        DaggerSensorMonitorAppComponent.factory().addApp(this)
}
```


