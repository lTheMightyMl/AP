package classes.league;

import classes.player.role.Defender;
import classes.player.role.Goalkeeper;
import classes.player.role.Midfielder;
import classes.player.role.Striker;
import classes.team.Formation;

class Match {
    private static final int NUMBER_OF_TEAMS = 2;
    private static final int TOO_MUCH_AGGRESSION = 85;
    private int[] numberOfGoals = {0, 0};
    private Formation[] formations = new Formation[2];
    private Formation attackerFormation;
    private Formation defenderFormation;
    private int attacker;
    private int defender;
    private Goalkeeper defenderGoalkeeper;

    Match(Formation team1Formation, Formation team2Formation) {
        if (team1Formation.isNotComplete() || team2Formation.isNotComplete())
            return;
        formations[0] = team1Formation;
        formations[1] = team2Formation;
        applyConditions();
        for (int i = 0; i < 2; i++) {
            attacker = i;
            attackerFormation = formations[attacker];
            defender = NUMBER_OF_TEAMS - 1 - attacker;
            defenderFormation = formations[defender];
            defenderGoalkeeper = defenderFormation.getGoalkeepers().get(Formation.NUMBER_OF_MAIN_GOALKEEPERS - 1);
            attackStrikers();
            attackMidfielders();
            attackHeader();
            takePenalty();
        }
    }

    private void attackStrikers() {
        int defenderGoalkeeperReactions = defenderGoalkeeper.getReactions();
        for (Striker striker : attackerFormation.getStrikers())
            if (striker.getFinishing() > defenderGoalkeeperReactions)
                numberOfGoals[attacker]++;
        if (attackerFormation.getStrikersFinishingAverage() - defenderGoalkeeperReactions > 5)
            numberOfGoals[attacker] += 2;
    }

    private void attackMidfielders() {
        int defenderGoalkeeperShotSaving = defenderGoalkeeper.getShotSaving();
        for (Midfielder midfielder : attackerFormation.getMidfielders())
            if (midfielder.getShooting() > defenderGoalkeeperShotSaving)
                numberOfGoals[attacker]++;
    }

    private void attackHeader() {
        int defenderGoalkeeperReactionsSquared = defenderGoalkeeper.getReactions();
        defenderGoalkeeperReactionsSquared *= defenderGoalkeeperReactionsSquared;
        double attackerMidfieldersCrossingAverage = attackerFormation.getMidfieldersCrossingAverage();
        for (Striker striker : attackerFormation.getStrikers())
            if (striker.getHeading() * attackerMidfieldersCrossingAverage > defenderGoalkeeperReactionsSquared)
                numberOfGoals[attacker]++;
        for (Defender defenderPlayer : attackerFormation.getDefenders())
            if (defenderPlayer.getHeading() * attackerMidfieldersCrossingAverage > defenderGoalkeeperReactionsSquared)
                numberOfGoals[attacker]++;
    }

    private void takePenalty() {
        int defenderGoalkeeperPenaltySaving = defenderGoalkeeper.getPenaltySaving();
        if (defenderFormation.getDefendersAggressionAverage() > TOO_MUCH_AGGRESSION)
            if (attackerFormation.getStrikersMaxPenalties() > defenderGoalkeeperPenaltySaving)
                numberOfGoals[attacker]++;
    }

    private void applyConditions() {
        for (int i = 0; i < NUMBER_OF_TEAMS; i++) {
            formations[i].boostStrikers();
            int opponent = NUMBER_OF_TEAMS - 1 - i;
            formations[i].weakenStrikers(formations[opponent]);
        }
    }

    int getNumberOfTeam1Goals() {
        return numberOfGoals[0];
    }

    int getNumberOfTeam2Goals() {
        return numberOfGoals[1];
    }
}
