This is a library with three helpful classes that you can easily employ in your project:
  com.apps.util.Console
  com.apps.util.Prompter
  com.apps.util.SplashApp

The API docs are included (see doc/), and contain class and method descriptions,
as well as example usages.

The JAR file is included in lib/, and the recommended configuration is to copy the lib
directory into your project root directory, then add the JAR as a dependency:
  right-click on the JAR -> Add as Library

A standalone application is also included that you can use to see how the splash screen works.
To run it, open a command prompt to this 'app-utils-1.1' directory and execute the 'run' script.

The API docs have all the details, but here's a summary:

Console
  All-static utility class with methods for clearing the console and emitting blank lines.
    Console.clear();
    Console.blankLines(2);

Prompter
  Wraps a java.util.Scanner and provides a high-level interface for prompting the user
  for input and getting the response.  The Scanner is provided to the Prompter ctor,
  and can be configured to read from System.in (the console), for normal application use,
  or from an input-file, for automated unit testing without human involvement.
  
  Prompter prompter = new Prompter(new Scanner(System.in));
  String name = prompter.prompt("Please enter your name: ");
  String age  = prompter.prompt("Please enter your age:  ", "\\d+", "That is not a valid age!");

SplashApp
  This is an interface for your application main-class.  You implement SplashApp, override
  the start() method, and also provide a main() method.
  
  This provides easy access to the built-in JVM splash image feature, which is just like it sounds:
    Shows a splash image during application launch, which is configured with a JVM launch option.
    $ java -splash:images/java.png com.myproject.MyProjectApp
  
  SplashApp goes further, allowing you to provide additional images that will advance
  after a brief pause interval (which is configurable).
  
  class MyProjectApp implements SplashApp {
    
    @Override
    public void start() {
      Game game = new Game();
      game.initialize();
    }
  
    public static void main(String[] args) {
      MyProjectApp app = new MyProjectApp();
      app.welcome("images/logo.jpg", "images/credits.png");
      app.start();
    }
  }
