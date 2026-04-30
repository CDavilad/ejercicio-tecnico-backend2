package com.banco.msclientes.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Persona {

    @NotBlank
    @Column(name = "clienteid", nullable = false, unique = true, length = 50)
    private String clienteId;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String contrasena;

    @NotNull
    @Column(nullable = false)
    private Boolean estado = true;
}
