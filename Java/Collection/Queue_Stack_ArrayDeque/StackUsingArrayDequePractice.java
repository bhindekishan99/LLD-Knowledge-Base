package Java.Collection.Queue_Stack_ArrayDeque;

import java.util.ArrayDeque;
import java.util.Deque;

public class StackUsingArrayDequePractice {

    public static void main(String []args){

        Deque<Integer> stk = new ArrayDeque<>();

        stk.push(12);
        stk.push(15);

        stk.pop();
        stk.pop();


    }
}

