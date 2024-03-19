package ClosestSchools;
/*
* Author: 
* Implements the closest pair of points recursive algorithm
* on locations of K-12 schools in Vermont obtained from http://geodata.vermont.gov/datasets/vt-school-locations-k-12

*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;
import java.io.File;


public class Main {


	public static void main(String[] args) throws IOException{

		//Creates an ArrayList containing School objects from the .csv file
		// Based on https://stackoverflow.com/questions/49599194/reading-csv-file-into-an-arrayliststudent-java
		String line = null;
		ArrayList<School> schoolList = new ArrayList <School>();
		// You may have to adjust the file address in the following line to your computer
		BufferedReader br = new BufferedReader(new FileReader("src\\ClosestSchools\\Data\\VT_School_Locations__K12.csv"));
		if ((line=br.readLine())==null){
			return;
		}
		while ((line = br.readLine())!=null) {
			String[] temp = line.split(",");
			schoolList.add(new School(temp[4],Double.parseDouble(temp[0]),Double.parseDouble(temp[1])));
		}


		//Preprocess the data to create two sorted arrayLists (one by X-coordinate and one by Y-coordinate):
		ArrayList<School> Xsorted = new ArrayList <School>();
		ArrayList<School> Ysorted = new ArrayList <School>();
		Collections.sort(schoolList, new SortbyX());
		Xsorted.addAll(schoolList);
		Collections.sort(schoolList, new SortbyY());
		Ysorted.addAll(schoolList);




		//Run the Recursive Algorithm
		School[] cp = new School[2];
		cp = ClosestPoints(Xsorted,Ysorted);
		if(cp[0]!=null)
			System.out.println("The two closest schools are "+ cp[0].name + " and " + cp[1].name +".");
		

	}

	public static School[] ClosestPoints(ArrayList<School> sLx, ArrayList<School> sLy){
		// Recursive divide and conquer algorithm for closest points
		// sLx should be sorted by x coordinate and sLy should be sorted by y coordinate
		// Returns an array containing the two closest School objects


		
		
		
		// Base case Brute force solve min distance.
		// should always be at least 2 schools inside the list
		School[] closestPair = new School[2];
		int numberOfSchools = sLx.size();
		double minDistance = distance(sLx.get(0), sLx.get(1));

		if (numberOfSchools <= 3) {
			for(int holdPointer = 0; holdPointer < numberOfSchools; holdPointer++){
				for(int movingPointer = holdPointer + 1; movingPointer < numberOfSchools; movingPointer++){
					double calculatedDistance = distance(sLx.get(holdPointer), sLx.get(movingPointer));
					if(calculatedDistance <= minDistance){ // ensures at least 1 assignment is made.
						minDistance = calculatedDistance;
						closestPair[0] = sLx.get(holdPointer);
						closestPair[1] = sLx.get(movingPointer);
					}
				}
			}
			return closestPair;
		}
		
		// Divide
		double midline = 0;
		if(sLx.size() % 2 == 0){
			double number1 = sLx.get((int) Math.floor((sLx.size() - 1)/2)).getX();
			double number2 = sLx.get((sLx.size() - 1)/2 + 1).getX();
			midline = (number1 + number2)/2;
		}
		else{
			double number1 = sLx.get((sLx.size() - 2)/2).getX();
			double number2 = sLx.get((sLx.size() - 2)/2 + 1).getX();
			midline = (number1 + number2)/2;

		}


		ArrayList<School> XL = new ArrayList<>();
		ArrayList<School> XR = new ArrayList<>();
		ArrayList<School> YL = new ArrayList<>();
		ArrayList<School> YR = new ArrayList<>();

		for(int i = 0; i < sLx.size(); i++){
			if(sLx.get(i).getX() < midline){
				XL.add(sLx.get(i));
			}
			else{
				XR.add(sLx.get(i));
			}
			
		}
		for(int i = 0; i < sLy.size(); i++){
			if(sLy.get(i).getX() < midline){
				YL.add(sLy.get(i));
			}
			else{
				YR.add(sLy.get(i));
			}
		}

		// Conquer
		// Calculate the distance of the min on the left and right
		School[] leftMinPoints = ClosestPoints(XL, YL);
		School[] rightMinPoints = ClosestPoints(XR, YR);
		double leftMinDistance = distance(leftMinPoints[0], leftMinPoints[1]);
		double rightMinDistance = distance(rightMinPoints[0], rightMinPoints[1]);
		double currentMinDistance = Math.min(leftMinDistance, rightMinDistance);
		School[] returnArray = (leftMinDistance <= rightMinDistance) ? leftMinPoints : rightMinPoints;


		// Combine - begin to search points within leftRightMinDistance of the midline.
		// Create the list of points that are within leftRightMinDistance of the Midline
		ArrayList<School> deltaFromMidline = new ArrayList<>(); // delta is leftRightMinDistance
		for(School school : sLy){
			if(Math.abs(school.getX()) - midline <= currentMinDistance){
				deltaFromMidline.add(school);
			}
		}

		for(int p1 = 0; p1 < deltaFromMidline.size() - 1; p1++ ){
			int stopIndex = (p1 + 7 <= deltaFromMidline.size()) ? p1 + 7 : deltaFromMidline.size();
			for(int p2 = p1 + 1; p2 < stopIndex; p2++){

				double distanceBetweenMidline = distance(deltaFromMidline.get(p1), deltaFromMidline.get(p2));
				if(distanceBetweenMidline < currentMinDistance){
					currentMinDistance = distanceBetweenMidline;
					returnArray[0] = deltaFromMidline.get(p1);
					returnArray[1] = deltaFromMidline.get(p2);
				}
			}
		}
		return returnArray;

	}



	// Calculate distance of two points using the distance formula
	public static double distance(School p1, School p2){
		double calculatedDistance = Math.sqrt(Math.pow((p2.getX() - p1.getX()),2) + Math.pow((p2.getY() - p1.getY()),2) );
		return calculatedDistance;
	}
}