package com.cqsd;

/**
 * @author caseycheng
 * @date 2023/6/30-12:09
 **/
public class TestString {
    public static void main(String[] args) {
        String foo = "foo";
        foo(foo);
        System.out.println(foo);
    }

    private static void foo(String foo) {
        foo="bar";
    }
}
