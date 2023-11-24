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
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(unique = true)
    private String login;
    private String password;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private PersistentBag<UserRole> roles;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private PersistentBag<Teacher> teachers;
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private PersistentBag<Member> members;
}
