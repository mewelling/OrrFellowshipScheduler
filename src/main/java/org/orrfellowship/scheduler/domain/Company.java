package org.orrfellowship.scheduler.domain;

import java.util.List;

public class Company {

	private String name;
	private List<Candidate> candidates;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

}
