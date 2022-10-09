# broad
Lily Nolting
mlilynolting@pm.me

## Summary
This toy program accesses the public MBTA API and shows some information about the Boston subway network. You can give it a couple of stops and it will tell you how to travel between them.

## Running the app
You can find the compiled application in `/broad/app/build/distributions/app.zip` (there's also a `.tar`). Extract the ZIP file and run `bin/app` to play with it.

## Building the app
To run from this source code, you'll need Gradle 5.7.1 and Java SDK 17 (I used SDKMAN to manage both).
From the `/broad` directory, input `./gradlew run` for a quick build, `./gradlew test` to run unit tests, or `./gradlew build` to build a JAR (the executable will pop up in `/broad/app/build/distributions/app.zip`).

### Authenticating
The app connects to the MBTA server to authenticate, using an API key I registered at `https://api-v3.mbta.com/`.
