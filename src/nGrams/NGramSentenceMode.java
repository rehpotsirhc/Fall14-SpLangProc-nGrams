package nGrams;

/*
 * Used to configure the way probabilities of word sequences are calculated. 
 */
enum NGramSentenceMode
	{
		//Only matches if the word sequence occurs at the beginning of at least one sentence in the corpus
		StartOfSentence,
		
		//Matches sequences found anywhere in the corpus
		Anywhere
		
	}
