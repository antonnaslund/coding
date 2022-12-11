# Instructions

Open a terminal and immediately write `mkdir kodprov211215`. Go to
this directory with `cd kodprov211215`. Download the code exam to
your computer with the following command:

    curl -L --remote-name http://eliasc.github.io/ioopm/kolyada.zip

You now have a zip file with the code for the exam. Unpack it with
`unzip kolyada.zip` (and enter the password given in the lab room)
and you will get a directory `handout` containing the task to be
solved, as well as a `Makefile` used for handing in the code exam.

## Handing in

Go to the directory `kodprov211215`. If you are unsure about where
you are in the file system you can write `cd; cd kodprov211215`.
Write `make handin` to hand in the code exam. This will create a
zip file containing the file you are supposed to hand in (and no
other files), and this file will be saved in a location where we
can correct it.

## General rules

- The same rules as for a written exam applies: no mobile phones,
  no text messaging, no conversations with anyone except the
  course staff, regardless of medium.
- You must be able to identify yourself.
- You are allowed to bring a hand-written paper (maximum size A4).
  You can write on both sides but cannot bring a magnifier.
- You are allowed to bring a book, physically or on a tablet or
  laptop. You are not allowed to run any other programs on this
  machine, and you cannot use them for anything other than reading
  course literature.
- You must write all the code yourself, except the code that is
  given.

# Sigma operation for a symbolic calculator

You have been given a small library implementing a symbolic
calculator, which is similar to the first Java assignment but a
lot simpler. It currently only supports addition, integer
constants and named variables. Note that it does not even support
assignment! Instead, during evaluation, variables are looked up in
a fixed hash map that is provided as an argument to the `eval`
method. For simplicity, we also do not deal with parsing, but only
interact with the library by building syntax trees manually.

It is a good idea to read the existing code first so that you form
a picture of how it compares to your own implementation of the
symbolic calculator (there may be minor differences).

## Task

Your task is to implement the Sigma operation, also know as the
sum operation. You probably recognize it from your math classes:

$$
\overset{5}{\underset{i = 1}{\Sigma}} i^2
$$

It should be read as "let $i$ go from 1 to 5 and sum the squares
of $i$":

$$
\overset{5}{\underset{i = 1}{\Sigma}} i^2 = 1^2 + 2^2 + 3^2 + 4^2 + 5^2 = 55
$$

In our symbolic setting the operation is more interesting as the
body of the Sigma can also contain variables! If $x$ is not bound,
we could have

$$
\overset{3}{\underset{i = 1}{\Sigma}} x + i = (((x + 1) + (x + 2)) + (x + 3))
$$

If, however, $x$ is bound to 3, we would have

$$
\overset{3}{\underset{i = 1}{\Sigma}} x + i = (((3 + 1) + (3 + 2)) + (3 + 3)) = 15
$$

You will implement Sigmas by adding a class `Sigma` (in
`Sigma.java`) that inherits from the base class `Expression`. For
simplicity, we will assume that we always start the sum from 1 and
go up to *and including* the limit. If the limit is smaller or
equal to zero, the result should be `0`.

The class should have whatever fields you deem necessary and a
constructor taking the name of the iteration variable ($i$ above),
an integer upper limit and an `Expression` which is the body of
the sum. The class should also override the `eval` method.
Implementing a `toString` method is not mandatory, but is likely
to help with debugging. In my implementation, I print
$\underset{i = 1}{\overset{3}{\Sigma}} i + x$ as `Sigma(i -> 3, i + x)`.

It is important that any variables in the environment are *not*
overwritten by the iteration variable in a Sigma, and also that
the iteration variable is not left in the environment after
evaluation. See the **Pointers** section for hints on how to
achieve this.

**Note that you should only hand in `Sigma.java`. Any changes you
make to other files will not be included in your hand-in.**

Running `make test` will run all the tests in the file
`Driver.java`. If you run it directly, you should see an error
message reminding you to uncomment the tests in the method
`runSigmaTests`:

    $ make test
    javac *.java
    java -ea Driver
    Exception in thread "main" java.lang.AssertionError: Remove this line
    and uncomment the tests when you have implemented the Sigma class
        at Driver.runSigmaTests(Driver.java:76)
        at Driver.runTests(Driver.java:145)
        at Driver.main(Driver.java:153)
    make: *** [test] Error 1

When you have finished your implementation, uncommented the tests
and removed the crashing line, the output from `make test` should
look like this:

    $ make test
    javac *.java
    java -ea Driver
    All tests passed!

When you are debugging your implementation, you may see messages
like this:

    $ make test
    javac *.java
    java -ea Driver
    TEST FAILED =====> {x=3} |- Sigma(i -> 3, (x + i)) ~~> 10 (expected: 15)
    Passed 24/25 tests
    Failed 1 test (see above)

The notation `{x=3} |- Sigma(i -> 3, (x + i)) ~~> 15 (expected: 10)`
should be understood as "While running under an environment where
`x` maps to `3`, the expression `Sigma(i -> 3, (x + i))` evaluated
to `15`, while it was expected to evaluate to `10`". Note that
your message may look slightly different depending on how you have
implemented `toString()`. The test program also checks that the
environment has not changed after evaluating an expression.

## Files

- `Expression.java` -- A file containing the classes `Expression`,
  `Addition`, `Constant`, `Variable`, `Environment` (an alias of
  `HashMap<String, Expression>)` and `NoValueException` (thrown
  when calling `getValue()` on something that is not a `Constant`).
- `Driver.java` -- Tests for the symbolic calculator. Note that it
  uses helper functions `add`, `var` and `val` to simplify
  building syntax trees. Writing `add(var("x"), val(2))` is the
  same as writing `new Addition(new Variable("x"), new Constant(2))`.
- `Sigma.java`  -- This is where you should implement your solution.
- `Makefile` -- A (quite stupid) makefile that supports the following targets
  - `all`   -- Compile all the files
  - `test`  -- Build and run the tests
  - `clean` -- Remove any built files

## Pointers

- One way to implement evaluation of Sigma without messing with
  the existing environment is to do it in two steps:
  - First you create a big (nested) `Addition` node representing
    the "unfolding" of the sum in the following way:

    For each possible value of the iteration variable (going from
    1 to the upper limit), evaluate the body with a new, empty
    environment containing only the current value of the iteration
    variable (this will leave other variables unevaluated). After
    each evaluation, create a new `Addition` node adding the last
    result to the accumulated sum. The starting value of this sum
    can be the `Constant` 0.
  - Once you have an expression representing the unfolded sum, you
    evaluate this expression once with the original environment.
    This will reduce the sum and evaluate any bound variables
    within it.
- The tests assume that the sum in Sigma is left associative. This
  means that a sum of three $x$'s is represented as $((x + x) + x)$,
  as opposed to $(x + (x + x))$. If you have tests failing due to
  this, try changing the order of the arguments to `new Addition`
  when you're building the unfolded sum in the previous pointer.
- The class `Sigma` can be implemented in less than 50 lines
  without using any clever tricks.
- Use `@Override` when overriding methods so that you get a
  compilation error if you are actually overloading them.