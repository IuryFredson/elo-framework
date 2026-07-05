package com.mentormatch.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "mentores")
public class Mentor extends ParticipanteMentoria { }
