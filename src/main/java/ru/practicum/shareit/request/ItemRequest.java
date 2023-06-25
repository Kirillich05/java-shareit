package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */

@Entity
@Table(name = "requests", schema = "public")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column
    String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id")
    User requestor;
}
