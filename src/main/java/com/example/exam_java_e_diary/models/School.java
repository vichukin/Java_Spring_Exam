package com.example.exam_java_e_diary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentBag;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL)
    private PersistentBag<SchoolSubject> subjects;
    @OneToMany(mappedBy = "school",cascade = CascadeType.ALL)
    private PersistentBag<Member> members;
    @OneToMany(mappedBy = "school",cascade = CascadeType.ALL)
    private PersistentBag<Teacher> teachers;
    @OneToMany(mappedBy = "school",cascade = CascadeType.ALL)
    private PersistentBag<SchoolClass> classes;
    @OneToMany(mappedBy = "school",cascade = CascadeType.ALL)
    private PersistentBag<Lesson> lessons;

}
