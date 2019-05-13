package classes.league;

import classes.team.Team;

public class Contract {
    private Team team;
    private int duration;
    private int yearsPassed = 0;
    private Contract loan = null;

    public Contract(Team team, int contractYears) {
        this.team = team;
        duration = contractYears;
    }

    public Team getCurrentTeam() {
        if (loan == null)
            return team;
        return loan.team;
    }

    public int getRemainingDuration() {
        if (loan == null)
            return getOriginalRemainingDuration();
        return loan.getOriginalRemainingDuration();
    }

    public boolean isLoan() {
        if (loan == null)
            return false;
        return true;
    }

    public Team getOriginalTeam() {
        return team;
    }

    public void resetLoan() {
        loan = null;
    }

    public void passYear() {
        yearsPassed++;
        if (loan != null)
            loan.passYear();
    }

    private int getOriginalRemainingDuration() {
        return duration - yearsPassed;
    }

    void renew(int duration) {
        this.duration += duration;
        System.out.println("contract renewed. new contract is valid for " + getOriginalRemainingDuration() + " years");
    }

    public Contract terminate() {
        if (loan != null) {
            loan = null;
            return this;
        }
        return null;
    }

    public void setLoan(Team team, int duration) {
        loan = new Contract(team, duration);
    }
}
