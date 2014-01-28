package org.mam.eti.pg.gda.pl.marblegame;

public class Achievement {
	private int score = 0;
	private int comboAchievementCounter = 0;
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = this.score + score;
	}
	public int getComboAchievementCounter() {
		return comboAchievementCounter;
	}
	public void setComboAchievementCounter(int comboAchievementCounter) {
		this.comboAchievementCounter = this.comboAchievementCounter + comboAchievementCounter;
	}
	public void resetCombo() {
		this.comboAchievementCounter = 0;
	}
	
	public void resetScore() {
		this.score = 0;
	}
}
