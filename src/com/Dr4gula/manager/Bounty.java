package com.Dr4gula.manager;

import java.util.UUID;

public class Bounty {

    private UUID target;
    private UUID issuer;
    private double reward;
    private boolean announced;

    public Bounty(UUID target, UUID issuer, double reward, boolean announced) {
        this.target = target;
        this.issuer = issuer;
        this.reward = reward;
        this.announced = announced;
    }

    public UUID getTarget() {
        return target;
    }

    public void setTarget(UUID target) {
        this.target = target;
    }

    public UUID getIssuer() {
        return issuer;
    }

    public void setIssuer(UUID issuer) {
        this.issuer = issuer;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        if(reward < 0) {
            throw new IllegalArgumentException("A recompensa não pode ser negativa.");
        }
        this.reward = reward;
    }

    public boolean isAnnounced() {
        return announced;
    }

    public void setAnnounced(boolean announced) {
        this.announced = announced;
    }
}