/**
 * @author      Joanmi Bardera <joanmibb@gmail.com>
 * @version     1.0                
 * @since       2013-06-02          
 */

package com.brapeba.mdialer;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Filterable;

public class MainCustomAdapter extends BaseAdapter implements Filterable
{
	public Context mContext;
	private List<DataHolder> dataList;
	private List<DataHolder> origDataList;
	private LayoutInflater mInflater = null;
	private Filter dataFilter;

	public MainCustomAdapter(Context context,List<DataHolder> contactData) 
	{
		super();
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.dataList = contactData;
		this.origDataList = contactData;
	}
	
	static class ViewHolder 
	{
        TextView vName;
        TextView vPhone;
    }

	public void resetData() 
	{
		dataList = origDataList;
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder=null;
		
		if(convertView==null) 
		{  
			convertView = mInflater.inflate(R.layout.element_mainmenu, null);
			holder = new ViewHolder();
			holder.vName = (TextView) convertView.findViewById(R.id.title);
			holder.vPhone = (TextView) convertView.findViewById(R.id.subtitle);
			convertView.setTag(holder);
		} else 
		{
			holder = (ViewHolder) convertView.getTag();
		}
		
		DataHolder dcard = (DataHolder) getItem(position);

		holder.vName.setText(dcard.getName());
		holder.vPhone.setText(dcard.getPhone());
    	
        return convertView;
	}
	

	public int getCount() 
	{
		return dataList.size(); 
	}

	public DataHolder getItem(int position) 
	{
		return dataList.get(position); 
	}

	public long getItemId(int position) 
	{
		return dataList.indexOf(getItem(position)); 
	}
	
	private class DataFilter extends Filter 
	{
		@Override protected FilterResults performFiltering(CharSequence constraint) 
		{
		    FilterResults results = new FilterResults();
		    // We implement here the filter logic
		    if (constraint == null || constraint.length() == 0) 
		    {
		        // No filter implemented so we return all the list
		        results.values = origDataList;
		        results.count = origDataList.size();
		    } else 
		    {
		        // Filtering operation
		        List<DataHolder> nDataList = new ArrayList<DataHolder>();
		        
		        for ( DataHolder p : dataList) 
		        {
		            if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
		            {
		                nDataList.add(p);
		            }
		        }
		        results.values = nDataList;
		        results.count = nDataList.size();
		    }
		    return results;
		}

		@Override protected void publishResults(CharSequence constraint, FilterResults results) 
		{
		    // Now we have to inform the adapter about the new list filtered
		    if (results.count == 0) notifyDataSetInvalidated();
		    else 
		    {
		    	dataList = (List<DataHolder>) results.values;
		        notifyDataSetChanged();
		    }
		}
	}
	
	@Override public Filter getFilter() 
	{
	    if (dataFilter == null) dataFilter = new DataFilter();
	    return dataFilter;
	}
}
