package hu.elte.iszraai.university.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

@Entity
public class Student {

    @ManyToMany(mappedBy = "students")
    private Set<Course> studies;

    public Set<Course> getStudies() {
        return studies;
    }

    public void setStudies(Set<Course> studies) {
        this.studies = studies;
    }

}
