package es.unizar.webeng.lab3

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
data class Employee(
    var name: String,
    var role: String,
    @Id
    @GeneratedValue
    var id: Long? = null,
)
