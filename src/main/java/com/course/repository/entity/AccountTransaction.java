package com.course.repository.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class AccountTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "source_acc")
    private String sourceAcc;

    @Column(name = "destination_acc")
    private String destinationAcc;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "ref")
    private String ref;

    @Enumerated(EnumType.STRING)
    @Column(name = "tran_type", length = 20)
    private Type tranType = Type.TRANSFER;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    // Enum for account types
    public enum Type {
        TRANSFER, DEPOSIT, WITHDRAWAL
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSourceAcc() {
        return sourceAcc;
    }

    public void setSourceAcc(String sourceAcc) {
        this.sourceAcc = sourceAcc;
    }

    public String getDestinationAcc() {
        return destinationAcc;
    }

    public void setDestinationAcc(String destinationAcc) {
        this.destinationAcc = destinationAcc;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public Type getTranType() {
        return tranType;
    }

    public void setTranType(Type tranType) {
        this.tranType = tranType;
    }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

}
