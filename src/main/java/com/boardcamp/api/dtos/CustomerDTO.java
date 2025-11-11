package com.boardcamp.api.dtos;

public class CustomerDTO {

    private String name;
    private String phone;
    private String cpf;

    public CustomerDTO(String name, String phone, String cpf) {
        this.name = name;
        this.phone = phone;
        this.cpf = cpf;
    }
    
    public CustomerDTO() {} 

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}