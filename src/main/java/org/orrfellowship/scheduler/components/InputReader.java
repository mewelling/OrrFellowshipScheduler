package org.orrfellowship.scheduler.components;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.orrfellowship.scheduler.domain.Candidate;
import org.orrfellowship.scheduler.domain.Company;

public class InputReader {

	private static final String CSV_SEPARATOR = ",";

	private String preferenceCSV;
	private String candidateCSV;

	private BufferedReader br = null;
	String line = "";

	private List<Candidate> candidates = Lists.newArrayList();
	private List<Company> companies = Lists.newArrayList();

	public InputReader(String preferenceCSV, String candidateCSV) {
		this.preferenceCSV = preferenceCSV;
		this.candidateCSV = candidateCSV;
	}

	public void getCompanyPreferencesFromFile() {

		try {

			br = new BufferedReader(new FileReader(preferenceCSV));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] input = line.split(CSV_SEPARATOR);
				Company company = new Company();
				company.setName(input[0]);

				List<Candidate> tempCandidateList = new ArrayList<Candidate>();
				for (int i=1; i<input.length; i++) {
					Candidate candidate = new Candidate();
					candidate.setName(input[i]);
					tempCandidateList.add(candidate);
				}
				company.setCandidates(tempCandidateList);
				companies.add(company);
			}

			br = new BufferedReader(new FileReader(candidateCSV));
			while ((line = br.readLine()) != null) {

				// use comma as separator
				String[] input = line.split(CSV_SEPARATOR);

				for (int i=0; i<input.length; i++) {
					Candidate candidate = new Candidate();
					candidate.setName(input[i]);
					candidates.add(candidate);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Map<String, Candidate> getCandidatesMap() {
		Map<String, Candidate> output = Maps.newHashMap();
		for (Candidate candidate : candidates) {
			output.put(candidate.getName(), candidate);
		}
		return output;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public List<Company> getCompanies() {
		return companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}
}
