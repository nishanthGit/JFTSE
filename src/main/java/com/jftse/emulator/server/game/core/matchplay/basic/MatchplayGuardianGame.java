package com.jftse.emulator.server.game.core.matchplay.basic;

import com.jftse.emulator.server.database.model.battle.GuardianStage;
import com.jftse.emulator.server.database.model.battle.WillDamage;
import com.jftse.emulator.server.game.core.matchplay.MatchplayGame;
import com.jftse.emulator.server.game.core.matchplay.PlayerReward;
import com.jftse.emulator.server.game.core.matchplay.battle.GuardianBattleState;
import com.jftse.emulator.server.game.core.matchplay.battle.PlayerBattleState;
import com.jftse.emulator.server.game.core.matchplay.battle.SkillCrystal;
import com.jftse.emulator.server.game.core.utils.BattleUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class MatchplayGuardianGame extends MatchplayGame {
    private List<Point> playerLocationsOnMap;
    private List<PlayerBattleState> playerBattleStates;
    private List<GuardianBattleState> guardianBattleStates;
    private List<SkillCrystal> skillCrystals;
    private List<WillDamage> willDamages;
    private short lastCrystalId = -1;
    private GuardianStage guardianStage;
    private GuardianStage bossGuardianStage;
    private boolean bossBattleActive;
    private byte lastGuardianServeSide;
    private int guardianLevelLimit;
    private Date stageStartTime;
    private int expPot;
    private int goldPot;

    public MatchplayGuardianGame() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        this.setStartTime(cal.getTime());
        this.setStageStartTime(cal.getTime());
        this.playerBattleStates = new ArrayList<>();
        this.guardianBattleStates = new ArrayList<>();
        this.skillCrystals = new ArrayList<>();
        this.playerLocationsOnMap = Arrays.asList(
                new Point(20, -75),
                new Point(-20, -75));
        this.willDamages = new ArrayList<>();
        this.setFinished(false);
    }

    public short damageGuardian(short guardianPos, int playerPos, short damage, boolean hasAttackerDmgBuff, boolean hasReceiverDefBuff) {
        int totalDamageToDeal = damage;
        PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                .filter(x -> x.getPosition() == playerPos)
                .findFirst()
                .orElse(null);
        boolean isNormalDamageSkill = Math.abs(damage) != 1;
        if (playerBattleState != null && isNormalDamageSkill) {
            totalDamageToDeal = BattleUtils.calculateDmg(playerBattleState.getStr(), damage, hasAttackerDmgBuff);
        }

        GuardianBattleState guardianBattleState = this.guardianBattleStates.stream()
                .filter(x -> x.getPosition() == guardianPos)
                .findFirst()
                .orElse(null);

        if (isNormalDamageSkill) {
            int damageToDeny = BattleUtils.calculateDef(guardianBattleState.getSta(), Math.abs(totalDamageToDeal), hasReceiverDefBuff);
            if (damageToDeny > Math.abs(totalDamageToDeal)) {
                totalDamageToDeal = -1;
            } else {
                totalDamageToDeal += damageToDeny;
            }
        }

        short newGuardianHealth = (short) (guardianBattleState.getCurrentHealth() + totalDamageToDeal);
        newGuardianHealth = newGuardianHealth < 0 ? 0 : newGuardianHealth;
        guardianBattleState.setCurrentHealth(newGuardianHealth);
        return newGuardianHealth;
    }

    public short damagePlayer(int guardianPos, int playerPos, short damage, boolean hasAttackerDmgBuff, boolean hasReceiverDefBuff) {
        int totalDamageToDeal = damage;
        GuardianBattleState guardianBattleState = this.guardianBattleStates.stream()
                .filter(x -> x.getPosition() == guardianPos)
                .findFirst()
                .orElse(null);

        boolean isNormalDamageSkill = Math.abs(damage) != 1;
        if (guardianBattleState != null && isNormalDamageSkill) {
            totalDamageToDeal = BattleUtils.calculateDmg(guardianBattleState.getStr(), damage, hasAttackerDmgBuff);
        }

        PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                .filter(x -> x.getPosition() == playerPos)
                .findFirst()
                .orElse(null);

        if (isNormalDamageSkill) {
            int damageToDeny = BattleUtils.calculateDef(playerBattleState.getSta(), Math.abs(totalDamageToDeal), hasReceiverDefBuff);
            if (damageToDeny > Math.abs(totalDamageToDeal)) {
                totalDamageToDeal = -1;
            } else {
                totalDamageToDeal += damageToDeny;
            }
        }

        short newPlayerHealth = (short) (playerBattleState.getCurrentHealth() + totalDamageToDeal);
        newPlayerHealth = newPlayerHealth < 0 ? 0 : newPlayerHealth;
        playerBattleState.setCurrentHealth(newPlayerHealth);
        return newPlayerHealth;
    }

    public short damageGuardianOnBallLoss(int guardianPos, int attackerPos, boolean hasAttackerWillBuff) {
        GuardianBattleState guardianBattleState = this.guardianBattleStates.stream()
                .filter(x -> x.getPosition() == guardianPos)
                .findFirst()
                .orElse(null);

        int lossBallDamage = 0;
        boolean servingGuardianScored = attackerPos == 4;
        if (servingGuardianScored) {
            lossBallDamage = (short) -(guardianBattleState.getMaxHealth() * 0.02);
        } else {
            PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                    .filter(x -> x.getPosition() == attackerPos)
                    .findFirst()
                    .orElse(null);
            if (playerBattleState != null) {
                WillDamage willDamage = this.getWillDamages().stream()
                        .filter(x -> x.getWill() == playerBattleState.getWill())
                        .findFirst()
                        .orElse(this.getWillDamages().get(0));
                lossBallDamage = -BattleUtils.calculateBallDamageByWill(willDamage, hasAttackerWillBuff);
            }
        }

        short newGuardianHealth = (short) (guardianBattleState.getCurrentHealth() + lossBallDamage);
        newGuardianHealth = newGuardianHealth < 0 ? 0 : newGuardianHealth;
        guardianBattleState.setCurrentHealth(newGuardianHealth);
        return newGuardianHealth;
    }

    public short damagePlayerOnBallLoss(int playerPos, int attackerPos, boolean hasAttackerWillBuff) {
        PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                .filter(x -> x.getPosition() == playerPos)
                .findFirst()
                .orElse(null);

        int lossBallDamage = 0;
        boolean servingGuardianScored = attackerPos == 4;
        if (servingGuardianScored) {
            lossBallDamage = (short) -(playerBattleState.getMaxHealth() * 0.1);
        } else {
            GuardianBattleState guardianBattleState = this.guardianBattleStates.stream()
                    .filter(x -> x.getPosition() == attackerPos)
                    .findFirst()
                    .orElse(null);
            if (guardianBattleState != null) {
                WillDamage willDamage = this.getWillDamages().stream()
                        .filter(x -> x.getWill() == guardianBattleState.getWill())
                        .findFirst()
                        .orElse(this.getWillDamages().get(0));
                lossBallDamage = -BattleUtils.calculateBallDamageByWill(willDamage, hasAttackerWillBuff);
            }
        }

        short newPlayerHealth = (short) (playerBattleState.getCurrentHealth() + lossBallDamage);
        newPlayerHealth = newPlayerHealth < 0 ? 0 : newPlayerHealth;
        playerBattleState.setCurrentHealth(newPlayerHealth);
        return newPlayerHealth;
    }

    public short healPlayer(int playerPos, short percentage) {
        PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                .filter(x -> x.getPosition() == playerPos)
                .findFirst()
                .orElse(null);
        short healthToHeal = (short) (playerBattleState.getMaxHealth() * (percentage / 100f));
        short currentHealth = (short) (playerBattleState.getCurrentHealth() < 0 ? 0 : playerBattleState.getCurrentHealth());
        short newPlayerHealth = (short) (currentHealth + healthToHeal);
        if (newPlayerHealth > playerBattleState.getMaxHealth()) {
            newPlayerHealth = playerBattleState.getMaxHealth();
        }

        playerBattleState.setCurrentHealth(newPlayerHealth);
        return newPlayerHealth;
    }

    public short healGuardian(int guardianPos, short percentage) {
        GuardianBattleState guardianBattleState = this.guardianBattleStates.stream()
                .filter(x -> x.getPosition() == guardianPos)
                .findFirst()
                .orElse(null);
        short healthToHeal = (short) (guardianBattleState.getMaxHealth() * (percentage / 100f));
        short currentHealth = (short) (guardianBattleState.getCurrentHealth() < 0 ? 0 : guardianBattleState.getCurrentHealth());
        short newGuardianHealth = (short) (currentHealth + healthToHeal);
        if (newGuardianHealth > guardianBattleState.getMaxHealth()) {
            newGuardianHealth = (short) guardianBattleState.getMaxHealth();
        }

        guardianBattleState.setCurrentHealth(newGuardianHealth);
        return newGuardianHealth;
    }

    public PlayerBattleState reviveAnyPlayer(short revivePercentage) {
        PlayerBattleState playerBattleState = getPlayerBattleStates().stream()
                .filter(x -> x.getCurrentHealth() < 1)
                .findFirst()
                .orElse(null);
        if (playerBattleState != null) {
            short newPlayerHealth = healPlayer(playerBattleState.getPosition(), revivePercentage);
            playerBattleState.setCurrentHealth(newPlayerHealth);
        }

        return playerBattleState;
    }

    public short getPlayerCurrentHealth(short playerPos) {
        PlayerBattleState playerBattleState = this.playerBattleStates.stream()
                .filter(x -> x.getPosition() == playerPos)
                .findFirst()
                .orElse(null);
        return playerBattleState.getCurrentHealth();
    }

    public long getStageTimePlayingInSeconds() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        long duration = cal.getTime().getTime() - this.getStageStartTime().getTime();
        return TimeUnit.MILLISECONDS.toSeconds(duration);
    }

    public void resetStageStartTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        setStageStartTime(cal.getTime());
    }

    public List<PlayerReward> getPlayerRewards() {
        List<PlayerReward> playerRewards = new ArrayList<>();
        this.playerBattleStates.forEach(x -> {
            PlayerReward playerReward = new PlayerReward();
            playerReward.setPlayerPosition(x.getPosition());
            playerReward.setBasicRewardExp(this.getExpPot());
            playerReward.setBasicRewardGold(this.getGoldPot());
            playerRewards.add(playerReward);
        });

        return playerRewards;
    }

    @Override
    public long getTimeNeeded() {
        return getEndTime().getTime() - getStartTime().getTime();
    }

    @Override
    public boolean isRedTeam(int playerPos) {
        return false;
    }

    @Override
    public boolean isBlueTeam(int playerPos) {
        return false;
    }
}
