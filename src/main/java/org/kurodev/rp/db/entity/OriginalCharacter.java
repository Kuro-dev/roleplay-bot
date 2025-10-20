package org.kurodev.rp.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.NonNull;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "original_character")
public final class OriginalCharacter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long characterId;
    @NonNull
    private Long userId;
    private String name;
    private Integer age;
    private String gender;
    private String description;
    private String avatarUrl;
}
