**Build Library**

to run use the following command

"./gradlew clean build test --refresh-dependencies"

**prerequisites** 
- Java 12
- Gradle

**Assumptions**

Not to merge orders that have same price but different order type (BUY, SELL).

**Notes**

Use case for parallelisation on getting buy and sell orders was due to buy orders and sell orders in these use cases 
do not have a relation yet and can be processed concurrently.
