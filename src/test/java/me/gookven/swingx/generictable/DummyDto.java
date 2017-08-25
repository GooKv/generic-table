package me.gookven.swingx.generictable;

import me.gookven.swingx.generictable.api.GenericTableColumn;

class DummyDto {
    private final Gender gender;
    private long id;
    private String name;
    private int age;
    private Gender petGender;

    DummyDto(String name, int age, Gender gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    @GenericTableColumn(value = "id", visible = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public Gender getPetGender() {
        return petGender;
    }

    public void setPetGender(Gender petGender) {
        this.petGender = petGender;
    }

    @Override
    public String toString() {
        return "DummyDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender=" + gender +
                ", petGender=" + petGender +
                '}';
    }
}
