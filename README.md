# Keita Backend Challenge


This is my response to a code challenge by Keita Mobility Factory.

The code structure is dependent and inspired by their ktor-template project.

### Building and running the project locally
The project comes with the gradle wrapper, so in order to build the project you can easily use the `gradlew` command.

* `gradlew run` - run the project (note that the project may have dependencies to other systems like RDMSs). Check the 
required environment variables on `/src/main/resources/application.conf`.
* `gradlew test` - run the tests
* `gradlew clean build` - do a local build (this will run the compilation and verification tasks, i.e., linter and tests)
