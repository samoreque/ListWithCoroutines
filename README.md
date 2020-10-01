# Freshly Pressed

An Android App that lists the most recent stories from WordPress.com Discover.

## Resources

- [Discover REST endpoint][discover].
- [OKHttp][] for networking.

[discover]: https://public-api.wordpress.com/rest/v1.1/sites/discover.wordpress.com/posts/
[OKHttp]: http://square.github.io/okhttp/

## Building

1. Clone the git repository
2. Run `./gradlew build`

## Your Task

Display the "Featured Image" for each post that has one within the list of posts in the app.

* Treat this as if you own the app, and you'll be releasing the finished product and source code under your own name.
* Keep in mind:
    * Android's [Code Style Guidelines for Contributors](https://source.android.com/source/code-style.html)
    * i18n: every user visible string should be translatable.
    * Security, best practices, code documentation, etc.
* Don't be code-shy and feel free to explore other parts of the app, not only those you need to touch to solve your problem.
* If you spot any bugs or areas for improvement, please go ahead and make changes to polish up the app.
* Commit changes to the git repository that we provided to you, splitting up commits as necessary. We’d love to see a pull request with your changes.
* Feel free to change anything you want in this repository (project structure, build configuration, README, etc.).
* Let us know when you're done with the test.

Take the time you need, but while there’s no hard time limit most candidates usually spend about 4-6 hours to make their improvements.


## CODE TEST RESULT

To perform the code test I divided it into three subtasks and each of them has its own pull request to be able to review them more easily:

 - **DROID-01: Retrofit and Repository** Adds a repository pattern to separate the model from the view and implements the Retrofit library to map the API responses into the models.
 - **DROID-02: MVVM and Coroutines** Implements MVVM architecture to separate the business logic, model and views and also allow be aware of the view's lifecycle and also integrates coroutines approach to perform the network requests.
 - **DROID-03: UI/UX Improvements** Here, I implemented the main task -*Display the "Featured Image" for each post*-  and it also implements all the list screens best practices like *swipe to refresh* and *load more* behaviors.

If you want to see all my changes, I created a separated branch code_test_samoreque_all_changes with their respective pull request

### Next steps
For lack of time I couldn't implement the next best practices:

 - Dependency injection using Dagger
 - Search functionality
