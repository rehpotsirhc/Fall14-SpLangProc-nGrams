/**
 * 
 */
package nGrams;

/**
 * @author Christopher Johnson
 *
 *
 *Represents a single N-gram, e.g., a bi-gram
 */
class NGram
	{
		
		private String[] _words;
		private int		_count;
		
		/*
		 * Creates an N-gram with the specified words. To create a bigram pass two words, to create a trigram pass three words, etc.
		 */
		public NGram(String[] words)
			{
				_words = words;
				_count ++;
			}

		/*
		 * Gets a word in the N-gram at the specified index. The first word is at n = 0
		 */
		public String getWord(int n)
			{
				return _words[n];
			}
		
		/*
		 * Gets all the words comprising the N-gram
		 */
		public String[] getWords()
		{
			return _words;
		}
		
		/*
		 * This is can used to keep track of how many times this N-gram has occurred in a corpus
		 */
		public int getCount()
			{
				return _count;
			}

		
		/*
		 * Increment the count of this N-gram. 
		 */
		public void incrementCount()
			{
				_count++;
			}
		
	
		/*
		 * Returns the order of the N-Gram, e.g., returns 3 for a trigram
		 */
		public int getGramOrder()
		{
			return _words.length;
		}

		
	}
