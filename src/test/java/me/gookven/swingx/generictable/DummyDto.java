package me.gookven.swingx.generictable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.gookven.swingx.generictable.api.GenericTableColumn;

@ToString
@Getter
@Setter
class DummyDto {
    @GenericTableColumn(value = "id", visible = false)
    private long id;
    /**
     * Is final w/o setter
     */
    private final Gender gender;
    private String name;
    private int age;
    private Gender petGender;

    DummyDto(String name, int age, Gender gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
