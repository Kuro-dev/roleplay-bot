package org.kurodev.rp.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.kurodev.rp.db.util.ColorConverter;
import org.springframework.lang.NonNull;

import java.awt.*;

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
    private String backstory;
    private String avatarUrl;

    @Convert(converter = ColorConverter.class)
    private Color color;
}
