public class Driver  {
    private static int numberOfTests = 0;
    private static int passedTests = 0;

    private static Expression add(Expression e1, Expression e2) {
        return new Addition(e1, e2);
    }

    private static Expression val(int n) {
        return new Constant(n);
    }

    private static Expression var(String x) {
        return new Variable(x);
    }

    private static void runSigmaTests() {
        
        Environment empty = new Environment();
        Environment env = new Environment();
        env.put("x", val(3));

        Environment symbolicEnv = new Environment();
        symbolicEnv.put("x", var("y"));
        symbolicEnv.put("y", val(3));

        Expression constantSigma = new Sigma("i", 1, val(42));
        test(empty, constantSigma, val(42));

        Expression singletonSigma = new Sigma("i", 1, var("x"));
        test(empty, singletonSigma, var("x"));
        test(env, singletonSigma, val(3));

        Expression emptySigma = new Sigma("ignored", 0, var("x"));
        test(empty, emptySigma, val(0));
        test(env, emptySigma, val(0));

        Expression reallyEmptySigma = new Sigma("ignored", -10, var("x"));
        test(empty, reallyEmptySigma, val(0));
        test(env, reallyEmptySigma, val(0));

        Expression sumUpToTen = new Sigma("i", 10, var("i"));
        test(empty, sumUpToTen, val(55));

        Expression fiveXs = new Sigma("i", 5, var("x"));
        Expression fiveXsSum = add(add(add(add(var("x"), var("x")),
                                           var("x")),
                                       var("x")),
                                   var("x"));
        test(empty, fiveXs, fiveXsSum);
        test(env, fiveXs, val(15));

        Expression fiveXPlusOnes = new Sigma("i", 5, add(var("x"), val(1)));
        Expression fiveXPlusOnesSum = add(add(add(add(add(var("x"), val(1)), add(var("x"), val(1))),
                                                  add(var("x"), val(1))),
                                              add(var("x"), val(1))),
                                          add(var("x"), val(1)));
        test(empty, fiveXPlusOnes, fiveXPlusOnesSum);
        test(env, fiveXPlusOnes, val(20));

        Expression threeXPlusI = new Sigma("i", 3, add(var("x"), var("i")));
        Expression threeXPlusISum = add(add(add(var("x"), val(1)),
                                            add(var("x"), val(2))),
                                        add(var("x"), val(3)));
        test(empty, threeXPlusI, threeXPlusISum);
        test(env, threeXPlusI, val(15));


        Expression shadowing = new Sigma("x", 5, var("x"));
        test(empty, shadowing, val(15));
        test(env, shadowing, val(15));

        Expression nested = new Sigma("i", 3,
                              new Sigma("j", 3,
                                add(var("i"), var("j"))));
        test(empty, nested, val(36));

        Expression parallel = add(singletonSigma, add(sumUpToTen, fiveXs));
        test(empty, parallel, add(var("x"), add(val(55), fiveXsSum)));
        test(env, parallel, val(73));

        Expression evaluateOnce = new Sigma("i", 3, var("x"));
        Expression evaluatedOnce = add(add(var("y"), var("y")),
                                       var("y"));
        test(symbolicEnv, evaluateOnce, evaluatedOnce);
        test(symbolicEnv, evaluateOnce.eval(symbolicEnv), val(9));
 
    }

    private static void runBasicTests() {
        Environment empty = new Environment();
        Environment env = new Environment();
        env.put("x", val(3));

        Expression e1 = add(val(2), var("x"));
        test(empty, e1, e1);
        test(env, e1, val(5));

        Expression e2 = add(var("x"), var("y"));
        test(empty, e2, e2);
        test(env, e2, add(val(3), var("y")));

        Expression e3 = add(add(val(2), var("x")), var("y"));
        test(empty, e3, e3);
        test(env, e3, add(val(5), var("y")));

        Expression e4 = add(add(val(2), val(3)), val(4));
        test(empty, e4, val(9));
        test(env, e4, val(9));
    }

    private static void runTests() {
        runBasicTests();
        runSigmaTests();
    }

    public static void main(String[] args){
        if(!checkAssertionsEnabled()) {
            System.out.println("Be sure to run this with assertions enabled! (java -ea Driver)");
            return;
        }
        runTests();
        printStatistics();
    }


    private static boolean checkAssertionsEnabled() {
        try {
            assert false;
        } catch (AssertionError err) {
            return true;
        }
        return false;
    }

    private static void printStatistics() {
        int failedTests = numberOfTests - passedTests;
        if(failedTests == 0) {
            System.out.println("All tests passed!");
        } else {
            System.out.println("Passed " + passedTests + "/" + numberOfTests + " tests");
            System.out.println("Failed " + failedTests + " test" + (failedTests == 1? "":"s") + " (see above)");
        }
    }

    private static void test(Environment env, Expression e, Expression expected) {
        numberOfTests++;

        Environment oldEnv = (Environment) env.clone();
        Expression result = e.eval(env);
        try {
            assert result.equals(expected) : env + " |- " + e + " ~~> " + result +
                                             " (expected: " + expected + ")";
            assert env.equals(oldEnv) : "Environment " + oldEnv + " was different " +
                                        "after evaluating " + e;
        } catch (AssertionError err) {
            System.out.println("TEST FAILED =====> " + err.getMessage());
            return;
        }
        passedTests++;
    }
}