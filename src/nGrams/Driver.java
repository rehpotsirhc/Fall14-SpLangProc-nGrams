package nGrams;

import java.io.FileWriter;

class Driver
	{
		public static void main(String[] args) throws Exception
			{
				
				String toMatch1 = "so to";
				String toMatch2 = "the";
				String toMatch3 = "the moths";
				String toMatch4 = "their idol";
				String toMatch5 = "flame";
				String toMatch6 = "to say so";
				String toMatch7 = "he only knew";
				String toMatch8 = "on his wings";
				String toMatch9 = "at once into the";
				String toMatch10 = "and only he could";
				String toMatch11 = "one was chosen went";
				
				SeqTestingPair[] testPairs = new SeqTestingPair[2];
				

				testPairs[0] = new SeqTestingPair(NGramSentenceMode.Anywhere, "b");
				
				testPairs[1] = new SeqTestingPair(NGramSentenceMode.StartOfSentence, "c");
	
				
			
				String[] matchStrings = new String[11];
				matchStrings[0] = toMatch1;
				matchStrings[1] = toMatch2;
				matchStrings[2] = toMatch3;
				matchStrings[3] = toMatch4;
				matchStrings[4] = toMatch5;
				matchStrings[5] = toMatch6;
				matchStrings[6] = toMatch7;
				matchStrings[7] = toMatch8;
				matchStrings[8] = toMatch9;
				matchStrings[9] = toMatch10;
				matchStrings[10] = toMatch11;
				
				for(int i = 1; i <= matchStrings.length;i++)
					{
						for(int n = 2; n <=3; n++)
							{
								NGrams nGrams = new NGrams(n, "corpus.txt");
								
								for(SeqTestingPair pair: testPairs)
									{
										System.out.print("(" +i + pair.letter + n + ")  ");
										System.out.println(nGrams.NGramSeqProb(matchStrings[i - 1], pair.mode));
								
							
									}
							}
					}
				
				NGrams nGrams = new NGrams(3, "corpus.txt");
				
				 FileWriter fw = new FileWriter("OutputForTest.txt");
		            fw.write(nGrams.toString());
		            fw.close();

				
				
				
				
			}
	}
