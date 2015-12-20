import java.util.List;
import java.util.Map;
import org.orrfellowship.scheduler.components.InputReader;
import org.orrfellowship.scheduler.components.OutputWriter;
import org.orrfellowship.scheduler.domain.Candidate;
import org.orrfellowship.scheduler.domain.Company;
import org.orrfellowship.scheduler.domain.Schedule;

public class Main {
	public static void main(String[] args) {
		System.out.println("Welcome to the Orr Fellowship Finalist Day Scheduler");
		System.out.println("Usage: java -jar scheduler.jar 'preferences.csv' 'candidates.csv' 'outputPath' " +
						"num_companies num_slots num_iterations");

		String preferencesCSV = args[0];
		String candidateCSV = args[1];
		String outputPath = args[2];
		Integer numberOfCompanies = Integer.valueOf(args[3]);
		Integer numberOfSlots = Integer.valueOf(args[4]);
		Integer numberOfIterations;
		if (args.length > 5) {
			numberOfIterations = Integer.valueOf(args[5]);
		} else {
			numberOfIterations = 1000000;
		}

		InputReader reader = new InputReader(preferencesCSV, candidateCSV);
		OutputWriter writer = new OutputWriter(outputPath);
		List<Company> companyList;
		Map<String, Candidate> candidateMap;

		reader.getCompanyPreferencesFromFile();
		companyList = reader.getCompanies();
		candidateMap = reader.getCandidatesMap();

		Integer maxScore = 0;
		Schedule bestSchedule = null;
		for (int i=0; i<numberOfIterations; i++) {
			Schedule test = new Schedule(candidateMap, companyList, numberOfCompanies, numberOfSlots);
			Integer score = test.matchingScore(companyList);
			if (score > maxScore) {
				i=0;
				maxScore = score;
				System.out.println(score);
				bestSchedule = test;
				writer.writeScoresToFile(maxScore, bestSchedule, companyList);
				bestSchedule.fillInCandidates(candidateMap, companyList, outputPath);
			}
		}

		System.out.println("\nBEST SCORE: " + maxScore + "\n");

		System.out.println("Done");
	}
}
