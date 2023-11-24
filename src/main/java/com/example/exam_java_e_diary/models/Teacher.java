package com.example.exam_java_e_diary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentBag;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Teachers")
public class Teacher implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String fullname;
    private boolean isDirector;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacher")
    private PersistentBag<SchoolClass> classes;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "teacher")
    private PersistentBag<Lesson> lessons;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "school_id")
    private School school;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
}
