package io.xzxj.canal.core.entity;

public class Example extends Parent {

    private String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
