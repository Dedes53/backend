package federicolepore.backend.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
    @Column(nullable = false)
    private String title;
    private String normalizedTitle;
    @Column(nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Skill() {
    }

    public Skill(Category category, Type type, String title, String description) {
        this.category = category;
        this.type = type;
        this.title = title;
        this.description = description;
    }


    public UUID getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getNormalizedTitle() {
        return normalizedTitle;
    }

    public void setNormalizedTitle(String normalizedTitle) {
        this.normalizedTitle = normalizedTitle;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @PrePersist
    @PreUpdate
    private void normalize() {
        if (title != null) this.normalizedTitle = title.trim().toLowerCase();
    }


    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
