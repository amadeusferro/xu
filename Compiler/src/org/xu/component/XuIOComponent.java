package org.xu.component;

public abstract class XuIOComponent<T extends XuIOComponent<T>> implements Cloneable {

    @Override
    public abstract XuIOComponent<T> clone();
}
