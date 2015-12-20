package org.orrfellowship.scheduler.domain;

public class Candidate {

	private String name;
	private Integer interviewCount = 0;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getInterviewCount() {
		return interviewCount;
	}

	public void setInterviewCount(Integer interviewCount) {
		this.interviewCount = interviewCount;
	}
}
