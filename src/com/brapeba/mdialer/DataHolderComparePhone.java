package com.brapeba.mdialer;

import java.util.Comparator;

public class DataHolderComparePhone implements Comparator<DataHolder>
{
	@Override public int compare(DataHolder a, DataHolder b) 
	{
		 return a.getPhone().compareToIgnoreCase(b.getPhone());
	  }
}
