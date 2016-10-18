package hu.elte.iszraai.university.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Professor {

    @ManyToMany
    private Set<Course> teaches;

    public Set<Course> getTeaches() {
        return teaches;
    }

    public void setTeaches(Set<Course> teaches) {
        this.teaches = teaches;
    }

}
