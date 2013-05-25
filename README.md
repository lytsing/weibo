# Weibo for Android

This projects is being designed to be a light-weight, fast user experience open Weibo alternative for Android. 

* Follow [Android Patterns](http://www.androidpatterns.com/).
* Use lots of Open Source.
* No annoying ads.
* High performance.

![TimeLine Image](http://lytsing.org/images/weibo1.png)  ![Status detail Image](http://lytsing.org/images/weibo2.png) 

### Want to contribute?

GitHub has some great articles on [how to get started with Git and GitHub](http://help.github.com/) and how to [fork a project](http://help.github.com/forking/).

Contributers are recommended to fork the app on GitHub (but don't have too). Create a feature branch, push the branch to git hub, press Pull Request and write a simple explanation.

One fix per commit. If say a a commit closes the open issue 12. Just add `closes #12` in your commit message to close that issue automagically.

All code that is contributed must be compliant with [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).

## Build Instructions ##

Dependent three-part libraries projects:

* ActionBarSherlock http://actionbarsherlock.com/
* android-actionbar https://github.com/johannilsson/android-actionbar
* MenuDrawer https://github.com/SimonVT/android-menudrawer
* android-pulltorefresh-and-loadmore https://github.com/shontauro/android-pulltorefresh-and-loadmore

Just git clone them, and import into weibo project.

Instructions for building on the command line with ant can be found below.

### Building with Ant ###

Once you have the Android SDK installed along with the library dependencies,
run the following command from the root directory of the WordPress for Android
project:

    android update project -p .

This will create a `local.properties` file that is specific for your setup.
You can then build the project by running:

    ant debug

You can install the package onto a connected device or a virtual device by
running:

    ant installd

Run all in one command:
	
	./build.sh


Also see the full Android documentation, [Building and Running from the Command
Line][command-line].

[command-line]: http://developer.android.com/tools/building/building-cmdline.html


## Code Style Guidelines

Contributers are recommended to follow the Android [Code Style Guidelines](http://source.android.com/source/code-style.html). 

In short that is;

* Indentation: 4 spaces, no tabs.
* Field names: Non-public, non-static fields start with m.
* Braces: Opening braces don't go on their own line.
* Acronyms are words: Treat acronyms as words in names, yielding XmlHttpRequest, getUrl(), etc.
* Consistency: Look at what's around you!

Have fun and remember we do this in our spare time so don't be too serious :)

## License
Copyright (c) 2012 [Lytsing Huang](http://lytsing.org)

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)

