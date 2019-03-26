#### Description

#### Repository

- **repositoryURL**: https://github.com/my-repository/my-project
The Git repository from which to get the source code to build the application that caused the stacktrace.

- **pomPath**: module-folder/pom.xml
The path of the pom.xml of the application to build inside the repository.

- **branch**: master
The branch to checkout to build the correct version of the application.

#### Reproduction parameters

- **targetFrame**: 1
 The target frame.

- **population**: 100 (Optional)
The size of the population that evolves during the search with a default value of 100.

- **search_budget**: 1800 (Optional)
The search budget in seconds with a default value of 1800.

- **global_timeout**: 1800 (Optional)
The global timeout in seconds, after which the execution stops if the search is stuck with a default value of 1800 (the timeout is only reached if the search does not improve after 1800 seconds).

- **test_dir**: crash-reproduction-tests (Optional)
The directory where the tests are generated with a default value of crashreproduction-tests.

#### crashLog

The log with the stack trace to consider.
