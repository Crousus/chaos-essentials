package de.chaosolymp.chaosessentials.tokens;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

public class Token {
    private String player;
    private String uuid;
    private String command;
    private Timestamp creation;
    private Timestamp redeem;
    private LocalDate validUntil;
    private String redeemUuid;
    private boolean multiUse;

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Timestamp getCreation() {
        return creation;
    }

    public void setCreation(Timestamp creation) {
        this.creation = creation;
    }

    public Timestamp getRedeem() {
        return redeem;
    }

    public void setRedeem(Timestamp redeem) {
        this.redeem = redeem;
    }

    public String getRedeemUuid() {
        return redeemUuid;
    }

    public void setRedeemUuid(String redeemUuid) {
        this.redeemUuid = redeemUuid;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public Date getValidAsDate() {
        return Date.valueOf(validUntil);
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isMultiUse() {
        return multiUse;
    }

    public void setMultiUse(boolean multiUse) {
        this.multiUse = multiUse;
    }
}
