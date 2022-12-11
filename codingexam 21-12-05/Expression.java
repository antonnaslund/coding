import java.util.HashMap;

public abstract class Expression {
    public abstract Expression eval(Environment env);

    public boolean isConstant() {
        return false;
    }
    
    public int getValue() {
        throw new NoValueException();
    }
}

class NoValueException extends RuntimeException {}

class Environment extends HashMap<String, Expression> { }

class Addition extends Expression {
    protected Expression left;
    protected Expression right;

    public Addition(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Expression eval(Environment env) {
        Expression lval = this.left.eval(env);
        Expression rval = this.right.eval(env);

        if(lval.isConstant() && rval.isConstant()){
            return new Constant(lval.getValue() + rval.getValue());
        } else if (lval.isConstant() && lval.getValue() == 0) {
            return rval;
        } else if (rval.isConstant() && rval.getValue() == 0) {
            return lval;
        } else {
            return new Addition(lval, rval);
        }
    }

    @Override
    public String toString() {
        return "(" + left + " + " + right + ")";
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Addition) {
            Addition a = (Addition) other;
            return this.left.equals(a.left) && this.right.equals(a.right);
        }
        return false;
    }
}

class Constant extends Expression {
    protected int value;

    public Constant(int value) {
        this.value = value;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public Expression eval(Environment env) {
        return new Constant(this.value);
    }

    @Override
    public String toString() {
        return "" + this.value;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Constant) {
            Constant c = (Constant) other;
            return this.value == c.value;
        }
        return false;
    }
}

class Variable extends Expression {
    protected String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public Expression eval(Environment env) {
        Expression value = env.get(this.name);
        if(value != null){
            return value;
        } else {
            return new Variable(this.name);
        }
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Variable) {
            Variable v = (Variable) other;
            return this.name.equals(v.name);
        }
        return false;
    }
}