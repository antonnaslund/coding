
public class Sigma extends Expression   {
        protected Expression body;
        protected int limit;
        protected String iterationvariable; 

    public Sigma(String iterationvariable, int limit, Expression body)   {
        this.body = body;
        this.limit = limit;
        this.iterationvariable = iterationvariable;
    }

    @Override
    public String toString()   {
        return ("Sigma(" + iterationvariable + " -> " + limit + body + ")");
    }

    @Override
    public Expression eval(Environment env) {
        Environment newEnv = new Environment();
        Expression sum = new Constant(0);

        if (this.limit <= 0)    {
            return (sum);
        }
        for (int i = 0; i <= this.limit; i++)   {
            newEnv.put(iterationvariable, new Constant(i));
            Expression term = this.body.eval(newEnv);
            sum = new Addition(sum, term);
        }
        return sum.eval(env);
    }
}