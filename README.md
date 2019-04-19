## SwissBorg Btc/USD

This project was a tech challenge project for an interview.

## Challenge description
You will use the Bitfinex API to display different information on the screen. The top part should contain a summary of the BTCUSD pair (last price, volume, low, high, change), followed by the live order-book using a WebSocket. These information are available on the Bitfinex documentation at

[1] for the ticker and

[2] for the order-book.
Use the attached screenshot as an example (ignore trades tab), but feel free to be creative with your own UI!

[1] https://docs.bitfinex.com/v1/reference#ws-public-ticker

[2] https://docs.bitfinex.com/v1/reference#ws-public-order-books

## Specifications
You should try and follow these specifications, as they are the tools we use internally at SwissBorg. If you decide to do something differently, just let us know why in your readme file!

Use Rx/MVVM architecture

Use RxAndroid/RxKotlin where needed

OkHttp as networking library

Network change resiliency

Use a Fragment to display the UI

A few tests

## Languages, libraries and tools used

 * [Kotlin](https://kotlinlang.org/)
 * [androidX libraries](https://developer.android.com/jetpack/androidx)
 * [Android LifeCycle from Android Architecture Component](https://developer.android.com/topic/libraries/architecture)
 * [DataBinding](https://developer.android.com/topic/libraries/data-binding)
 * [Scarlet](https://github.com/Tinder/Scarlet)
 * [OkHttp](https://github.com/square/okhttp)
 * [Timber](https://github.com/JakeWharton/timber)
 * [Dagger2](https://github.com/google/dagger)
 * [RxJava](https://github.com/ReactiveX/RxJava)
 * [RxKotlin](https://github.com/ReactiveX/RxKotlin)
 * [RxAndroid](https://github.com/ReactiveX/RxAndroid)
 * [Mockito Kotlin](https://github.com/nhaarman/mockito-kotlin/)

## Screenshots

<a href="https://imgur.com/AcaLO5L"><img src="https://i.imgur.com/AcaLO5L.png?1" title="App"/></a>

## Implementation

* In this project I'm using MVVM as an application architecture.

* Using DataBinding to enhance the MVVM architecture by binding the `LiveData` models directly to the `Views` without explicitly set them in `Fragment` that also makes our code cleaner by removing the boilerplate binding code from the Kotlin code.

* Using Scarlet library by tinder to handle the WebSockets stuff. It's like Retrofit but for WebSockets.
Benefits of using Scarlet.

    1- Abstract away the complexity of dealing with WebSockets. like connection and re connection when the connection drops. We don't need to reinvent the weal here since it's a common use case.

    2- Integration with third part libraries like RxJava, OkHttp...etc

    3- Respect the Android LifeCycle. We will just tie it to the LifeCycle we want the WebSocket in.

    3- Well tested and maintained by a big company.

* Using Dagger for dependency injection that will make testing easier and our make code 
cleaner and more readable.

* We have our data source `BitfinexDataSource` that will communicate with the Scarlet service `BitfinexService` and convert the API responses to something the data layer can understand and deal with. This class is created because the issue with Bitfinex API responses are not designed to work well with modern serialization libraries like Gson so we parse the responses manually in the app and depending on the response type and body we will take the right action for it.

* The `BitfinexRepository` will communicate with `BitfinexDataSource` to map the ticker and order-book updates to UI models, set Rx threads and also provide a clean interface for the presentation layer to access the API functionality and methods.

* The `MainViewModel` will communicate with `BitfinexRepository` to mange Rx subscription LifeCycle and convert the Rx `Observable` to `LiveData` since it's better in the UI layer due to it's life cycle aware nature and play nice with the data binding library.
So a rule of thumb here is to use Rx in the data and domain layer. Use `LiveData` in the presentation(UI) layer of the application. 

* The `MainFragment` will just create the `MainViewModel`, set the `DataBinding` and wire them together. That's it! Pretty simple and has zero logic.

* Unit tests are written for all the data package that include all the mappers, models, `BitfinexDataSource` and `BitfinexRepository`. Basically all the classes that have logic in it.

* No need for UI testing here using Espresso. Since the UI is super simple, It just bind models to some `TextViews` but of course in a full production application UI and integration testing should be written.