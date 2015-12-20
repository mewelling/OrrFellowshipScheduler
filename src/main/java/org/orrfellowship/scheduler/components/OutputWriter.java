package org.orrfellowship.scheduler.components;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.orrfellowship.scheduler.domain.Candidate;
import org.orrfellowship.scheduler.domain.Company;
import org.orrfellowship.scheduler.domain.Schedule;

public class OutputWriter {

	private static final String CSV_SEPARATOR = ",";
	private String outputDirectory;

	public OutputWriter(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	public void writeScheduleToFile(Integer score, Schedule schedule, List<Company> companyList) {
		String outputCSV = this.outputDirectory + "final_schedule_" +
				score + "_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SS").format(new Date()) + ".csv";

		try
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputCSV),"UTF-8"));
			for (int i=0; i<companyList.size(); i++) {
				StringBuffer oneLine = new StringBuffer();
				oneLine.append(companyList.get(i).getName());
				oneLine.append(CSV_SEPARATOR);
				for (Candidate candidate : schedule.getSchedule()[i]) {
					if (candidate == null) {
						oneLine.append("");
					} else {
						oneLine.append(candidate.getName());
					}
					oneLine.append(CSV_SEPARATOR);
				}
				bw.write(oneLine.toString());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch (UnsupportedEncodingException e) {}
		catch (FileNotFoundException e){}
		catch (IOException e){}
	}

	public void writeScoresToFile(Integer score, Schedule schedule, List<Company> companyList) {
		String outputCSV = this.outputDirectory + "final_scores_" + score
				+ "_" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SS").format(new Date()) + ".csv";

		try
		{
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputCSV),"UTF-8"));
			for (int i=0; i<companyList.size(); i++) {
				StringBuffer oneLine = new StringBuffer();
				oneLine.append(companyList.get(i).getName());
				oneLine.append(CSV_SEPARATOR);

				int counter = 0;
				int companyScore = 0;
				for (Candidate candidate : schedule.getSchedule()[i]) {
					Boolean zero = false;
					counter = 0;
					List<Candidate> candidateList = companyList.get(i).getCandidates();
					for (Candidate temp : candidateList) {
						if (candidate == null) {
							oneLine.append("");
							zero = true;
							break;
						}
						if (temp.getName().equals(candidate.getName())) {
							oneLine.append(15-counter);
							companyScore += (15-counter);
							zero = true;
							break;
						}
						counter++;
					}
					if(!zero) {
						oneLine.append(0);
					}
					if (candidate != null ) {
						oneLine.append(" ");
						oneLine.append(candidate.getName());
					}
					oneLine.append(CSV_SEPARATOR);
				}
				oneLine.append(companyScore);
				bw.write(oneLine.toString());
				bw.newLine();
			}
			bw.flush();
			bw.close();
		}
		catch (UnsupportedEncodingException e) {}
		catch (FileNotFoundException e){}
		catch (IOException e){}
	}
}
