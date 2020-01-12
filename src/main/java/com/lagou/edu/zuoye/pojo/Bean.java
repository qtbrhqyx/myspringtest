package com.lagou.edu.zuoye.pojo;

import java.util.HashSet;
import java.util.Set;

public class Bean {
    String id;
    String name;
    Set<Class> dependencies = new HashSet();
    boolean singleton;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Set<Class> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Set<Class> dependencies) {
        this.dependencies = dependencies;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
