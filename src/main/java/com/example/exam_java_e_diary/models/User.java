package com.example.exam_java_e_diary.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.collection.spi.PersistentBag;

import java.util.stream.Collectors;

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
    public static boolean isAdmin(User user)
    {
        if(user==null)
            return false;
        var roles = user.getRoles();
        if(roles==null)
            return false;
        return roles.stream().filter(t->t.getRole().getName().equals("Admin")).collect(Collectors.toList()).size()>0;
    }
    public static boolean isDirector(User user, School school)
    {
        var directors= school.getTeachers().stream().filter(t->t.isDirector()).collect(Collectors.toList());
        var check = directors.stream().filter(t->t.getUser().getId()==user.getId()).collect(Collectors.toList());
        return check.size()>0;
    }
}
