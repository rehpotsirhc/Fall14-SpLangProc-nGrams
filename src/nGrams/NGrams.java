/**
 * 
 */
package nGrams;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


/**
 * @author Christopher Johnson
 * 
 * This class is capable of computing N-Grams of any order from a given corpus and computing
 * the probabilities of word sequences using the computed N-grams
 * 
 * It can be used by instantiating it or used statically
 * 
 * Example of using it by instantiating an NGrams object:
 * 
 *
 * 
 * NGrams nGrams = new NGrams(N, "corpus.txt"); 	// "N" is the desired N-gram order and "corpus.txt" is the complete file path of the corpus
 * 	nGrams.NGramSeqProb("string to match", mode) 	// "string to match" is the string you want to generate the probability of and "mode" is used to specify how matches are found, "StartOfSentence" or "Anywhere"
 *  												// It will used the N passed in the constructor to determine the type of N-Grams to use
 *  
 *  nGrams.bigramSeqProb("string to match");	 	// This version can be used to force it to use bigrams, regardless of the N passed in the constructor. There is also a trigram version. If N < 3 and you use the trigram version, the N-Grams will
 *  												// be recalculated using trigrams. Uses "StartOfSentence" as the mode
 *  
 *  nGrams = new NGrams("corpus.txt"); 				// There is also a constructor that just takes the path to the corpus. Defaults to computing bigrams
 *  
 *  nGrams.NGramSeqProb("string to match", mode); 	// Using this method in conjuction with the previous constructor will use bigrams to compute the probability
 *  
 *  nGrams.computeNGrams(N, corpusFilePath); 		// This can be used to generate a new set of NGrams instead of having to re-instantiate the NGrams object. There's also computeBigrams and computeTriGrams methods
 *  
 *  
 *  Exampe of using it statically:
 *  
 * NGrams.NGramSeqProb(NGrams.ComputeNGramTable(N, "corpus.txt"), "string to match", mode);  // The method ComputeNGramTable returns the N-Grams, which can be passed to the method NGramSeqProb to generate the probability
 *  
 *  */
class NGrams
	{
		private final static String	_start				= "<START> ";
		private final static String	_splitSentenceChars	= "\\.\\?\\!";
		private final static String	_allowedWordChars	= "a-zA-Z0-9 \\-";
		private NGramTable			_nGramTable;
		private String				_corpusFilePath;

		
		/*
		 * Loads from a file
		 */
		private static String LoadFromFile(String filePath) throws IOException
			{
				Scanner s = new Scanner(new FileReader(filePath));
				String text = "";
				while (s.hasNextLine())
					{
						String line = s.nextLine().trim();
						if (line.equals(""))
							continue;
						else
							text += line + " ";
					}
				s.close();
				return text;
			}

		/*
		 * Normalizes the text corpus text by removing extra spaces and any characters besides those defined by the 
		 * partial regular expressions _splitSentenceChars and _allowedWordChars
		 */
		private static String Normalize(String text, String splitChars, String allowedChars)
			{
				return text.trim().toLowerCase().replaceAll("[^" + allowedChars + splitChars + "]", "").replaceAll("\\s+", " ");
			}
		
		/*
		 * Parses the text into sentences, splitting off of periods, question marks and exclamation points (_splitSentenceChars)
		 */
		private static String[] GetSentences(String text, String splitChars, String allowedChars) throws IOException
			{
				String output = Normalize(text, allowedChars, splitChars);
				return output.split("[^" + splitChars + "]");
			}

		/*
		 * Parses the text sentences into words and adds the special start token (_start)
		 */
		private static String[] SentenceToWords(String[] sentences, int index, String startToken)
			{
				String sentence = startToken + "" + sentences[index].trim();
				return sentence.split(" ");
			}

		/*
		 * Some necessary set up
		 */
		private static void SetUp(int N, ArrayList<ArrayList<NGram>> nGrams)
			{
				for (int i = 0; i < N; i++)
					{
						nGrams.add(new ArrayList<NGram>());
					}
			}
		
		/*
		 * Used to compute the number of words in the corpus. 
		 */
		private static int ComputeTermCount(String[] sentences)
			{
				int count = 0;
				for (String s : sentences)
					{
						count += s.trim().split(" ").length;
					}
				return count + 2 * (sentences.length);
			}
		
		/*
		 * Returns an N-length subsequence of an array of strings, starting at a given index
		 */
		private static String[] GetNWordSubSequence(String words[], int N, int startIndex)
			{
				String[] NWords = new String[N];
				for (int n = 0; n < N; n++)
					{
						NWords[n] = words[startIndex + n];
					}
				return NWords;
			}
		
		/*
		 * Searches if an N-gram exists in a list with the given set of words. If it does, it increments the count of the N-Gram. If not, adds a new N-gram to the list using the given set of words
		 */
		private static void ModifyNGrams(ArrayList<NGram> nGrams, String[] NWords, int n, int numberOfSentences)
			{
				boolean match = false;
				for (NGram ng : nGrams)
					{
						match = SearchNGram(ng, NWords, n);
						if (match)
							{
								if (!(ng.getWords().length == 1 && ng.getWord(0).equals(_start.trim()) && ng.getCount() >= numberOfSentences))
									{
										ng.incrementCount();
									}
								break;
							}
					}
				if (!match)
					{
						AddNGram(nGrams, NWords, n);
					}
			}

		/*
		 * Determines if the given N-gram contains the given subset of words 
		 */
		private static boolean SearchNGram(NGram ng, String[] NWords, int n)
			{
				boolean match = false;
				for (int d = n; d >= 0; d--)
					{
						if (NWords[d].equalsIgnoreCase(ng.getWord(d)))
							match = true;
						else
							{
								match = false;
								break;
							}
					}
				return match;
			}

		/*
		 * Adds a new N-gram to the list comprising the given words
		 */
		private static void AddNGram(ArrayList<NGram> nGrams, String[] NWords, int n)
			{
				String[] wordsToAdd = new String[n + 1];
				for (int d = 0; d <= n; d++)
					{
						wordsToAdd[d] = NWords[d];
					}
				NGram newNGram = new NGram(wordsToAdd);
				nGrams.add(newNGram);
			}

		/*
		 * Computes the N-Gram table from the given text. If N = 3, computes trigrams, bigrams and unigrams, if N = 2, computes bigrams and unigrams, etc.
		 */
		private static NGramTable ComputeNGrams(int N, String text, String startToken) throws IOException
			{
				String[] sentences = GetSentences(text, _allowedWordChars, _splitSentenceChars);
				NGramTable nGramTable = new NGramTable();
				nGramTable.setTermCount(ComputeTermCount(sentences));
				SetUp(N, nGramTable.getNGrams());
				String startTokenTmp = startToken;
				for (int c = 1; c < (N - 1); c++)
					{
						startToken += startTokenTmp;
					}
				for (int s = 0; s < sentences.length; s++)
					{
						int tempN = N;
						String[] words = SentenceToWords(sentences, s, startToken);
						for (int w = 0; w < words.length; w++)
							{
								// before the "N - 1" to last word (e.g., before
								// the 2nd to last word, leaving 3 words left,
								// which would be for building a trigram)
								while (w >= (words.length - (tempN - 1)))
									{
										tempN--;
									}
								String[] NWords = GetNWordSubSequence(words, tempN, w);
								for (int n = 0; n < tempN; n++)
									{
										ModifyNGrams(nGramTable.getNGrams().get(n), NWords, n, sentences.length);
									}
							}
					}
				return nGramTable;
			}

		private static NGramTable ComputeNGrams(int N, String text) throws IOException
			{
				return ComputeNGrams(N, text, _start);
			}

		
		/*
		 * Determines if the given N-gram list contains an N-gram with the given set of words
		 */
		private static NGram FindMatch(ArrayList<NGram> nGrams, String[] wordsToMatch)
			{
				for (NGram possMatch : nGrams)
					{
						if (SearchNGram(possMatch, wordsToMatch, wordsToMatch.length - 1)) { return possMatch; }
					}
				return null;
			}

		/*
		 * Returns a subset of the given string array of length N
		 */
		private static String[] GetWordSubSet(int length, String[] original)
			{
				String[] subset = new String[length];
				for (int i = 0; i <= (length - 1); i++)
					{
						subset[i] = original[i];
					}
				return subset;
			}

		
		private static double CalcProb(int count1, int count2)
			{
				return (double) count1 / (double) count2;
			}

		/*
		 * Calculates the probability of the passed NGram, by first finding a matching N-1Gram and then dividing their counts
		 */
		private static double MatchAndCalcProb(String[] wordsToMatch, int N, NGram nGramMatch, NGramTable nGramTable)
			{
				String[] n1GramWords = GetWordSubSet(N, wordsToMatch);
				NGram lessNGramMatch = FindMatch(nGramTable.getNGrams().get(N - 1), n1GramWords);
				return CalcProb(nGramMatch.getCount(), lessNGramMatch.getCount());
			}

		/*
		 * Decrements N until it is the no bigger than the number of words in the given string
		 */
		private static int ReduceN(int N, String words)
			{
				while (words.split(" ").length < N)
					N--;
				return N;
			}

		/*
		 * Constructs an NGram table with the given corpus. N must be at least 1
		 */
		public NGrams(int N, String corpusFilePath) throws Exception
			{
				if (N < 1) throw new Exception("N must be at least 1");
				_nGramTable = new NGramTable();
				computeNGrams(N, corpusFilePath);
				_corpusFilePath = corpusFilePath;
			}

		/*
		 * Constructs a Bigram table with the given corpus
		 */
		public NGrams(String corpusFilePath) throws Exception
			{
				this(2, corpusFilePath);
			}


		/*
		 * Computes a Bigram table with the given corpus
		 */
		public void computeBigrams(String corpusFilePath) throws IOException
			{
				computeNGrams(2, corpusFilePath);
				
			}
		/*
		 * Computes a Ttrigram table with the given corpus
		 */
		public void computeTrigrams(String corpusFilePath) throws IOException
			{
				computeNGrams(2, corpusFilePath);
				
			}

		/*
		 * Computes a Quadrgram table with the given corpus
		 */
		public void computeQuadrgrams(String corpusFilePath) throws IOException
			{
				computeNGrams(2, corpusFilePath);
				
			}

		/*
		 * Computes a Pentagram table with the given corpus
		 */
		public void computePentagrams(String corpusFilePath) throws IOException
			{
				computeNGrams(2, corpusFilePath);
				
			}
		
		
		/*
		 * Computes an NGram table with the given corpus
		 */
		public void computeNGrams(int N, String corpusFilePath) throws IOException
			{
				String text = LoadFromFile(corpusFilePath);
				_nGramTable = ComputeNGrams(N, text);
				_corpusFilePath = corpusFilePath;
			}
		
		/*
		 * Calculates the probability that the given string of words has of occurring using NGrams, where N is equal to the order of the last computed NGram
		 * Uses NGramSentenceMode to determine how to consider a match
		 */
		public double NGramSeqProb(String words, NGramSentenceMode mode) throws Exception
			{
				return NGramSeqProb(_nGramTable, words, mode);
			}

		/*
		 * Calculates the probability that the given string of words has of occurring using BiGrams
		 * Uses NGramSentenceMode.StartOfSentence 
		 */
		public double bigramSeqProb(String words) throws Exception
		{
			computeNGrams(2, _corpusFilePath);
			
			return NGramSeqProb(words, NGramSentenceMode.StartOfSentence);
		}

		/*
		 * Calculates the probability that the given string of words has of occurring using TriGrams
		 * Uses NGramSentenceMode.StartOfSentence 
		 */
		public double trigramSeqProb(String words) throws Exception
			{
				computeNGrams(3, _corpusFilePath);
				
				return NGramSeqProb(words, NGramSentenceMode.StartOfSentence);
			}
		
		
		/* Calculates the probability that the given string of words has of occurring using the passed NGramTable
		 * Uses NGramSentenceMode to determine how to consider a match
		 */
		public static double NGramSeqProb(NGramTable nGramTable, String words, NGramSentenceMode mode) throws IOException
			{
				double probability = 1;
				int N = nGramTable.getGramOrder();
				N = ReduceN(N, words);
				String startToken = "";
				if (mode == NGramSentenceMode.StartOfSentence)
					{
						startToken = _start;
					}
				NGramTable nGramTableInput = ComputeNGrams(N, words, startToken);
				ArrayList<ArrayList<NGram>> nGramListInput = nGramTableInput.getNGrams();
				ArrayList<ArrayList<NGram>> nGramListCorpus = nGramTable.getNGrams();
				int v = nGramTable.getTermCount();
				if (mode == NGramSentenceMode.Anywhere)
					{
						int upperBound = N - 1;
						if (N == 1) upperBound = 1;
						for (int n = 0; n < upperBound; n++)
							{
								String[] gramWords = nGramListInput.get(n).get(0).getWords();
								NGram corpusNGramMatch = FindMatch(nGramListCorpus.get(n), gramWords);
								if (corpusNGramMatch != null)
									{
										if (n == 0)
											{
												probability *= CalcProb(corpusNGramMatch.getCount(), v);
											} else
											{
												probability *= MatchAndCalcProb(gramWords, n, corpusNGramMatch, nGramTable);
											}
									} else
									{
										probability *= 0;
									}
							}
					}
				for (NGram nGram : nGramListInput.get(N - 1))
					{
						if (N == 1)
							{
								break;
							}
						String[] nGramWords = nGram.getWords();
						NGram corpusNGramMatch = FindMatch(nGramListCorpus.get(N - 1), nGramWords);
						if (corpusNGramMatch != null)
							{
								probability *= MatchAndCalcProb(nGramWords, N - 1, corpusNGramMatch, nGramTable);
							} else
							{
								probability *= 0;
							}
					}
				return probability;
			}

		/*
		 * Computes an NGram table with the given corpus and N-Gram order
		 */
		public static NGramTable ComputeNGramTable(int N, String corpusFilePath) throws Exception
			{
				if (N < 1) throw new Exception("N must be at least 1");
				String text = LoadFromFile(corpusFilePath);
				return ComputeNGrams(N, text);
			}

		
		@Override
		/*
		 * Prints the N-Gram table
		 */
		public String toString()
			{
				String output = "";
				for (int i = 0; i < _nGramTable.getNGrams().size(); i++)
					{
						output += i + 1 + "-GRAM\n";
						int countNGrams = 0;
						for (NGram ngram : _nGramTable.getNGrams().get(i))
							{
								output += "(" + ++countNGrams + ")";
								for (int w = 0; w < (ngram.getGramOrder()); w++)
									{
										output += " " + ngram.getWord(w);
									}
								output += " " + ngram.getCount() + "\n";
							}
						output += "\n\n\n======================================================================\n\n\n";
					}
				return output;
			}
	}
