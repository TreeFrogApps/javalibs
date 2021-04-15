# TreeFrogApps Java Libraries

### A collection of libraries for java platform written in Kotlin



- rxjava 3 utils
- javafx utils (including Dagger2 and RxJava3)

---

<br>

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

<br>

###### To use these libraries go to the packages tab and read individual instructions.  This will require a public key which should NOT be included in any committed code, if so keys are automatically revoked.
