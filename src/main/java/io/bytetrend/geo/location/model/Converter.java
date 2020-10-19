package io.bytetrend.geo.location.model;

public interface Converter<T, U> {


    U convertTo(T t);
}
