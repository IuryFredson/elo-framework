package com.mentormatch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "alunos")
public class Aluno extends ParticipanteMentoria { }
