package net.optifine.shaders;

import java.util.ArrayDeque;
import java.util.Deque;

public class ProgramStack
{
    private final Deque<Program> stack = new ArrayDeque();

    public void push(Program p)
    {
        this.stack.addLast(p);

        if (this.stack.size() > 100)
        {
            throw new RuntimeException("Program stack overflow: " + this.stack.size());
        }
    }

    public Program pop()
    {
        if (this.stack.isEmpty())
        {
            throw new RuntimeException("Program stack empty");
        }
        else
        {
            Program program = this.stack.pollLast();
            return program;
        }
    }
}
