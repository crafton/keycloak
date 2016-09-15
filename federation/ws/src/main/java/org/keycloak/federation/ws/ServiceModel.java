package org.keycloak.federation.ws;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceModel {

    private String firstName;
    private String surName;
    private Integer age;

    public ServiceModel() {
    }

    public ServiceModel(String firstName, String surName, Integer age) {
        this.firstName = firstName;
        this.surName = surName;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceModel)) return false;

        ServiceModel that = (ServiceModel) o;

        return firstName.equals(that.firstName);

    }

    @Override
    public int hashCode() {
        return firstName.hashCode();
    }
}
