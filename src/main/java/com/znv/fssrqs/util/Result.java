package com.znv.fssrqs.util;

import org.springframework.util.Assert;

/**
 * Created by dongzelong on  2019/6/25 12:32.
 *
 * @author dongzelong
 * @version 1.0
 * @Description TODO
 */
public class Result<T, E> {
    private T value;
    private E error;

    private Result(T value, E error) {
        Assert.isTrue(value != null ^ error != null);
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> ok(T value) {
        Assert.notNull(value, "result can't be empty");
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> err(E error) {
        Assert.notNull(error, "error can't be empty");
        return new Result<>(null, error);
    }

    public boolean isOk() {
        return value != null;
    }

    public boolean isErr() {
        return error != null;
    }

    public T value() {
        Assert.isTrue(isOk());
        return value;
    }

    public E error() {
        Assert.isTrue(isErr());
        return error;
    }

    public interface Mapping<A, B> {
        B map(A a);
    }

    public E unwrapCheck(Mapping<T, E> mapping) {
        if (isErr()) {
            return error;
        } else {
            return mapping.map(value);
        }
    }
}
