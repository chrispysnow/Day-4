# Template for creating Java testable project challenges

This Gradle project is used as the basis for creating and verifying Java
testable project challenges. Don't modify this repository, simply reset
the Git history, create a new repository and push to initialize it.

## Usage

1. In Github, create a new empty, private repository in the `gSchool` organization.  Don't add a README or License file.
1. Copy the repo URL
1. Clone *_this_* repo and `cd` into it.  Then run:

    ```
    rm -rf .git
    git init .
    git add .
    git remote add origin <url to your github repository>
    git push --set-upstream origin master
    ```

1. Edit `settings.gradle` and change the project name to your project

    ```
    rootProject.name = 'put-your-project-name-here'
    ```
    
1. Replace this README with your own README
1. Write reflection-based tests in `assessment/java/com/galvanize/SolutionTest.java`

The `Dockerfile` and `test.sh` files are based on the [`learn-docker-testing` java configuration](https://github.com/gSchool/learn-docker-testing/tree/master/java):

## How it works

Students will write code in `src` (both `src/main` and `src/test`).

Students will run tests using `./test.sh`, which is exactly what Learn runs:

- Run the instructor's tests in `assessment/java/com/galvanize`
- Run student's tests with coverage
- Run the coverage report

Students may also run `./gradlew assess` at any point.

If there are no student tests remove the line `assess.finalizedBy(test)` and all the `JaCoCo` configuration from the `build.gradle` file.
