package com.brapeba.mdialer;

import java.util.Comparator;

public class DataHolderCompareName implements Comparator<DataHolder>
{
	@Override public int compare(DataHolder a, DataHolder b) 
	{
		 return a.getName().compareToIgnoreCase(b.getName());
	  }
}
