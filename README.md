# Weather App

## Overview
This is a modular Android application that displays weather data using the OpenWeatherMap API. The project follows a clean and scalable architecture, leveraging modern Android development technologies.

## Features
- Fetch and display current weather data from OpenWeatherMap API
- Modular architecture for better scalability and maintainability
- Dependency injection using Hilt
- Declarative UI with Jetpack Compose
- State management using the MVI (Model-View-Intent) pattern
- Offline support using a local database (Room)
- Unit and UI testing setup

## Architecture
The application follows a modular architecture, with the following modules:

- **app**: The main application module, serving as the entry point.
- **data**: Handles data sources, including API communication (via Retrofit) and local storage (Room).
- **domain**: Contains business logic and use cases.
- **presentation**: Manages UI components, ViewModel, and state handling with Jetpack Compose.
- **core**: Includes shared utilities, constants, and common resources.

## Tech Stack
- **Hilt**: Dependency injection framework
- **Jetpack Compose**: Modern declarative UI framework
- **MVI (Model-View-Intent)**: For structured state management
- **Retrofit**: API client for network requests
- **Coroutines & Flow**: For asynchronous and reactive programming
- **Room**: Local database for offline support
- **JUnit, MockK, Espresso**: For unit and UI testing

## Testing
- Unit tests are written using JUnit and MockK.
- UI tests use Espresso for Compose-based testing.

Run tests using:
```sh
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```

## License
This project is licensed under the MIT License - see the LICENSE file for details.

---

This project follows best practices for Android development and is structured for scalability and maintainability. Contributions and improvements are welcome!