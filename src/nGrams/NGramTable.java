package nGrams;

import java.util.ArrayList;


/*
 * Holds several N-grams in a table, such that if the N-gram N = n exists, then all N-grams exist from n - 1, n - 2, ... to n = 1
 */
class NGramTable
	{
		
		
		private ArrayList<ArrayList<NGram>>	_nGrams;
		private int							_termCount;
		
		
		/*
		 * Creates an empty N-Gram table
		 */
		public NGramTable()
			{
				_nGrams = new ArrayList<ArrayList<NGram>>();
				_termCount = 0;
			}
		
		/*
		 * Returns the N-Gramt table
		 */
		public ArrayList<ArrayList<NGram>> getNGrams()
			{
				return _nGrams;
			}
		
		
		/*
		 * Returns the number total number of words used to make the N-Grams.
		 * This is equal to  the sum of each (1-gram * its count)
		 */
		public int getTermCount()
			{
				return _termCount;
			}

		public void setTermCount(int termCount)
			{
				this._termCount = termCount;
			}
		
		/*
		 * Gets the order of the highest N-Gram contained within the table, e.g., if the N-grams N = 3, N = 2, and N = 1 exist in the table,
		 * this method returns 3
		 */
		public int getGramOrder()
		{
			return _nGrams.size();
		}
		
		
		
		
	}
