# broad
Lily Nolting
mlilynolting@pm.me

## Summary
This toy program accesses the public MBTA API and shows some information about the Boston subway network. You can give it a couple of stops and it will tell you how to travel between them.

## Running the app
You can find the compiled application in `/broad/app/build/distributions/app.zip` (there's also a `.tar`). Extract the ZIP file and run `bin/app` to play with it.

### The interface
The app will print some information about the MBTA system, and then prompt you for input: enter the name of an MBTA stop to be the origin of your trip, then another name to be the destination. You can type 'L' instead to see a list of all the stops (helpful when I forget that it's 'Sullivan Square' and not just 'Sullivan'). You can also type 'Q' to quit, but I don't see why you would :(

## Building the app
To run from this source code, you'll need Gradle 5.7.1 and Java SDK 17 (I used SDKMAN to manage both).
From the `/broad` directory, input `./gradlew run` for a quick build, `./gradlew test` to run unit tests, or `./gradlew build` to build a JAR (the executable will pop up in `/broad/app/build/distributions/app.zip`).

### Authenticating
The app connects to the MBTA server to authenticate, using an API key I registered at `https://api-v3.mbta.com/`.
