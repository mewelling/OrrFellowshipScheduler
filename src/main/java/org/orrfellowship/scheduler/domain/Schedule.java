package org.orrfellowship.scheduler.domain;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.orrfellowship.scheduler.components.OutputWriter;

public class Schedule {

	private Integer numCompanies;
	private Integer companySlots;

	private Candidate[][] schedule;
	private Candidate[][] inverted;

	private Integer failCount;

	public Schedule(Candidate[][] schedule, Candidate[][] inverted, Integer numCompanies, Integer companySlots ) {
		this.numCompanies = numCompanies;
		this.companySlots = companySlots;
		this.schedule = schedule;
		this.inverted = inverted;
	}

	public Schedule(Map<String, Candidate> candidates, List<Company> companies, Integer numCompanies,
			Integer companySlots ) {

		this.numCompanies = numCompanies;
		this.companySlots = companySlots;
		this.schedule = new Candidate[numCompanies][companySlots];
		this.inverted = new Candidate[companySlots][numCompanies];

		Boolean finished = false;
		Boolean breakout = false;
		failCount = 0;

		resetSchedulesAndCounters(candidates);
		List<Candidate> shuffledCandidates = new ArrayList<Candidate>(candidates.values());
		Collections.shuffle(Arrays.asList(shuffledCandidates), new Random(System.nanoTime()));

		List<Integer> companyIndexes = Lists.newArrayList();
		for (int k=0; k<numCompanies; k++) {
			companyIndexes.add(k);
		}

		List<Integer> slotIndexes = Lists.newArrayList();
		for (int k=0; k<companySlots; k++) {
			slotIndexes.add(k);
		}

		while (!finished) {

			finished = true;

			Collections.shuffle((companyIndexes), new Random(System.nanoTime()));

			for (int scheduleIndex = 0; scheduleIndex < numCompanies; scheduleIndex++) {

				int companyIndex = companyIndexes.get(scheduleIndex);
				List<Candidate> preferenceList = companies.get(companyIndex).getCandidates();

				Collections.shuffle((slotIndexes), new Random(System.nanoTime()));

				for (int slotIndex1 = 0; slotIndex1 < companySlots; slotIndex1++) {

					int slotIndex = slotIndexes.get(slotIndex1);
					if (schedule[companyIndex][slotIndex] == null) {
						// Loop through the preference list in order
						int counter;
						for (counter = 0; counter < preferenceList.size(); counter++) {
							Candidate current = candidates.get(preferenceList.get(counter).getName());

							// Candidate might not have been given an interview, so continue
							if (current == null) {
								continue;
							}

							// Find a candidate that has less than MAX interviews
							if (current.getInterviewCount() < 4) {
								// Then we need to make sure they aren't already scheduled elsewhere
								if (!Arrays.asList(schedule[companyIndex]).contains(current) &&
										!Arrays.asList(inverted[slotIndex]).contains(current)) {
									current.setInterviewCount(current.getInterviewCount() + 1);
									Arrays.asList(schedule[companyIndex]).set(slotIndex, current);
									Arrays.asList(inverted[slotIndex]).set(companyIndex, current);
									finished = false;
									breakout = true;
									break;
								}
							}
						}
						if (breakout) {
							breakout = false;
							break;
						}
					}
				}
			}
		}
	}

	public void fillInCandidates(Map<String, Candidate> candidates, List<Company> companies, String outputDir) {

		OutputWriter writer = new OutputWriter(outputDir);

		for (int zz=0 ; zz<250; zz++) {

			Candidate[][] newSchedule = new Candidate[numCompanies][companySlots];
			Candidate[][] newInverted = new Candidate[companySlots][numCompanies];
			for (int slotIndex = 0; slotIndex < companySlots; slotIndex++) {
				for (int companyIndex = 0; companyIndex < numCompanies; companyIndex++) {
					Arrays.asList(newSchedule[companyIndex]).set(slotIndex, null);
					Arrays.asList(newInverted[slotIndex]).set(companyIndex, null);
					Arrays.asList(newSchedule[companyIndex])
							.set(slotIndex, Arrays.asList(schedule[companyIndex]).get(slotIndex));
					Arrays.asList(newInverted[slotIndex])
							.set(companyIndex, Arrays.asList(inverted[slotIndex]).get(companyIndex));
				}
			}

			List<Candidate> remainingCandidates = Lists.newArrayList();
			for (Candidate candidate : candidates.values()) {
				for (int i = candidate.getInterviewCount(); i < 4; i++) {
					remainingCandidates.add(candidate);
				}
			}
			Collections.shuffle((remainingCandidates), new Random(System.nanoTime()));

			List<Integer> companyIndexes = Lists.newArrayList();
			for (int k = 0; k < numCompanies; k++) {
				companyIndexes.add(k);
			}

			Collections.shuffle((companyIndexes), new Random(System.nanoTime()));
			Boolean fail = false;
			for (int scheduleIndex = 0; scheduleIndex < numCompanies; scheduleIndex++) {

				int companyIndex = companyIndexes.get(scheduleIndex);

				for (int slotIndex = 0; slotIndex < companySlots; slotIndex++) {

					if (newSchedule[companyIndex][slotIndex] == null) {

						int index;
						int numCandidates = remainingCandidates.size();
						for (index = 0; index < numCandidates; index++) {
							Candidate current = remainingCandidates.get(index);

							// Then we need to make sure they aren't already scheduled elsewhere
							if (!Arrays.asList(newSchedule[companyIndex]).contains(current) &&
									!Arrays.asList(newInverted[slotIndex]).contains(current)) {
								Arrays.asList(newSchedule[companyIndex]).set(slotIndex, current);
								Arrays.asList(newInverted[slotIndex]).set(companyIndex, current);
								remainingCandidates.remove(index);
								break;
							}
						}
						if (index == numCandidates) {
							fail = true;
						}
					}
				}
			}
			if (fail) {
				continue;
			}

			Schedule test = new Schedule(newSchedule, newInverted, numCompanies, companySlots);

			if (test.checkInterviewCounts()) {
				writer.writeScheduleToFile(test.matchingScore(companies), test, companies);
			}
		}
	}

	private void resetSchedulesAndCounters(Map<String, Candidate> candidateMap) {
		for (int slotIndex = 0; slotIndex < companySlots; slotIndex++) {
			for (int companyIndex = 0; companyIndex < numCompanies; companyIndex++) {
				Arrays.asList(schedule[companyIndex]).set(slotIndex, null);
				Arrays.asList(inverted[slotIndex]).set(companyIndex, null);
			}
		}

		for (Map.Entry<String, Candidate> entry : candidateMap.entrySet()) {
			entry.getValue().setInterviewCount(0);
		}
	}


	public Integer matchingScore(List<Company> companies) {
		Integer score = 0;
		for (int company = 0; company < numCompanies; company++) {
			for (int slot = 0; slot < companySlots; slot++) {
				Company currCompany = companies.get(company);
				List<Candidate> candidateList = currCompany.getCandidates();
				Candidate who = schedule[company][slot];
				Integer index = -1;
				for (int i=0 ; i < candidateList.size() ; i++) {
					Candidate temp = candidateList.get(i);
					if (who != null && temp.getName().equals(who.getName())) {
						index = i;
						break;
					}
				}
				if (index != -1) {
					score += 15 - index;
				}
			}
		}

		return score;
	}

	Boolean checkInterviewCounts() {

		Map<String, Candidate> candMap = Maps.newHashMap();
		for (int companyIndex = 0; companyIndex < numCompanies; companyIndex++) {
			for (int slotIndex = 0; slotIndex < companySlots; slotIndex++) {
				Candidate current = schedule[companyIndex][slotIndex];
				if (current == null) {
					return false;
				}

				if (candMap.containsKey(current.getName())) {
					Candidate thisCand = candMap.get(current.getName());
					candMap.get(current.getName()).setInterviewCount(thisCand.getInterviewCount()+1);
				} else {
					Candidate thisCand = new Candidate();
					thisCand.setName(current.getName());
					thisCand.setInterviewCount(1);
					candMap.put(current.getName(), thisCand);
				}

				if (candMap.get(current.getName()).getInterviewCount() > 4) {
					return false;
				}
			}
		}

		return true;
	}

	public Candidate[][] getSchedule() {
		return schedule;
	}

	public void setSchedule(Candidate[][] schedule) {
		this.schedule = schedule;
	}

	public Candidate[][] getInverted() {
		return inverted;
	}

	public void setInverted(Candidate[][] inverted) {
		this.inverted = inverted;
	}

	public Integer getFailCount() {
		return failCount;
	}

	public void setFailCount(Integer failCount) {
		this.failCount = failCount;
	}
}
