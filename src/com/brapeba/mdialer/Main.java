/**
 * @author      Joanmi Bardera <joanmibb@gmail.com>
 * @version     1.3                
 * @since       2013-06-02          
 */

package com.brapeba.mdialer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class Main extends Activity implements OnItemClickListener
{
	public static String TAG="mDialer";
	public static List<DataHolder> contactData = new ArrayList<DataHolder>();
	public static Set<String> groupsAccounts = new HashSet<String>();
	public static List<String> gAcc;
	public static ListView listView;
	DataHolder obj;
	private int rawContacts=0,toRawContacts=0;
	public static int totalContacts=0;
	static MainCustomAdapter adapter;
	public SparseBooleanArray checked = new SparseBooleanArray();
	private ActionBar actionBar;
	Button buttonStartProgress;
	private ProgressDialog pd;
    private Context context;
	public static boolean firstExecution=false;
	public static boolean reloadDatabase=false;
	public static boolean renewMainView=false;
	public static boolean inBG=false; //to know if there is a bg process!
	private BackgroundAsyncTask task=null; 
	private Button sortNameButton,sortPhoneButton;
	private EditText editText;

	// activity cycle of life
	@Override public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.main);
		
	    // gets the activity's default ActionBar
	    actionBar = getActionBar();
	    actionBar.show();
	    
	    listView = (ListView) findViewById(android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setItemsCanFocus(false);
	}

	@SuppressWarnings("deprecation")
	@Override protected void onResume() 
	{
		super.onResume();
		checked.clear();
		listView.clearChoices();
		listView.invalidateViews();
		listView.requestLayout();
		listView.setTextFilterEnabled(true);
		if (!firstExecution) //to avoid re-reading database when changing orientation
		{
			task=(BackgroundAsyncTask)getLastNonConfigurationInstance();
			if (task==null) 
			{
				if (!inBG)
				{
					task=new BackgroundAsyncTask(this);
					task.execute();
				}
			} else 
			{
				task.attach(this);
			}
		} else
		{
			adapter = new MainCustomAdapter(Main.this, contactData);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(Main.this);	
			listView.setVisibility(View.VISIBLE);
			listView.setTextFilterEnabled(true);
			sortNameButton = (Button) findViewById(R.id.sort_name);
			sortNameButton.setOnClickListener(sortButtonsClickListener);
			sortPhoneButton = (Button) findViewById(R.id.sort_phone);
			sortPhoneButton.setOnClickListener(sortButtonsClickListener);
			
			// code below is for filterable -- not used because when filtering the position of each element displayed is different of selected
			// therefore getCheckedItemPositions() does not work
			
			actionBar.setCustomView(R.layout.filteractionview);
		    editText = (EditText) actionBar.getCustomView().findViewById(R.id.editTxt);
		    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		    
		    editText.addTextChangedListener(new TextWatcher() 
		    {
		    	@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		    	{
		    	}
		    	
		    	@Override public void onTextChanged(CharSequence s, int start, int before, int count) 
		    	{
		    	}

		    	@Override public void afterTextChanged(Editable s) 
		    	{
	    			adapter.getFilter().filter(s.toString());
		    	}
		    });
		}
	}

	@Override protected void onPause() 
	{
		super.onPause(); 
	}

	@Override protected void onDestroy() 
	{
		if (pd!=null) { pd.dismiss(); }
		super.onDestroy();
	}
	
	private class BackgroundAsyncTask extends AsyncTask<Void, Integer, Void> 
	{
		Main activity=null;
		
		BackgroundAsyncTask(Main activity) 
		{
		      attach(activity);
		}
		
		void attach(Main activity) 
		{
			this.activity=activity;
		}

		@Override protected void onCancelled()
		{
			// What to do if doInBackground() is cancelled??
			super.onCancelled();
		}
		
		@Override protected void onPostExecute(Void result) 
		{
			super.onPostExecute(result);
			if (activity==null) 
			{
				Log.w("RotationAsync", "onPostExecute() skipped -- no activity");
			} else 
			{
				listView.setVisibility(View.VISIBLE);

				// can't sort in background to prevent exception in case contactData added at same time
				// to sort by contact name:
				if (totalContacts>1) { Collections.sort(contactData,new DataHolderCompareName()); } 
				
				// to sort by phone:
				//if (totalContacts>1) { Collections.sort(contactData,new DataHolderComparePhone()); } 
				
				gAcc = new ArrayList<String>(groupsAccounts);

				makeToast(totalContacts+getString(R.string.string1), false);

				adapter = new MainCustomAdapter(Main.this, contactData);
				listView.setAdapter(adapter);
				listView.setTextFilterEnabled(true);
				listView.setOnItemClickListener(Main.this);	
				reloadDatabase=false;
				firstExecution=true;
				inBG=false;
				if (pd!=null) { pd.dismiss(); }
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR); // to allow again orientation changes
				
				sortNameButton = (Button) findViewById(R.id.sort_name);
				sortNameButton.setOnClickListener(sortButtonsClickListener);
				sortPhoneButton = (Button) findViewById(R.id.sort_phone);
				sortPhoneButton.setOnClickListener(sortButtonsClickListener);
				
				// code below is for filterable -- keep in mind that the position of each element displayed is different than those selected,
				// therefore getCheckedItemPositions() does not work
				
				actionBar.setCustomView(R.layout.filteractionview);
			    editText = (EditText) actionBar.getCustomView().findViewById(R.id.editTxt);
			    actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
			    
			    editText.addTextChangedListener(new TextWatcher() 
			    {
			    	@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) 
			    	{
			    	}
			    	
			    	@Override public void onTextChanged(CharSequence s, int start, int before, int count) 
			    	{
			    	}

			    	@Override public void afterTextChanged(Editable s) 
			    	{
		    			adapter.getFilter().filter(s.toString());
			    	}
			    });
			    
			}
		}

		@SuppressWarnings("deprecation")
		@Override protected void onPreExecute() 
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR); //to avoid orientation changes during asynctask
			inBG=true;
		    listView.setVisibility(View.GONE);
		    listView.setTextFilterEnabled(false);
			Cursor c =  managedQuery(ContactsContract.RawContacts.CONTENT_URI, null, null, null, null);
			toRawContacts = c.getCount(); // how many raw contacts there are
			pd = new ProgressDialog(context);
			pd.setTitle(getString(R.string.readDB));
			pd.setMessage(getString(R.string.string2)+toRawContacts+getString(R.string.string3));
			pd.setCancelable(false);
			pd.setIndeterminate(true);
			pd.show();
			super.onPreExecute();
		}

		@Override protected Void doInBackground(Void... params) 
		{
			// populating contactData list
			Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			totalContacts=0;
			while (phones.moveToNext())
			{
				totalContacts++;
				String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (phoneNumber.length()>0)
				{
					if (name==null) { name="0 --NULL--"; } 	// to avoid crash when sorting (comparing to null)
					DataHolder obj = new DataHolder(
							name							// name
							,phoneNumber					// all phone numbers as string, we don't know yet
							,null							// raw contact id
							,null							// contact id
							,null							// origin
							,rawContacts					// position
							,1);							// has phone numbers?
					contactData.add(obj);
				}
			}
			phones.close();

			return null;
		}

		@Override protected void onProgressUpdate(Integer... values) 
		{
			if (activity==null) 
			{
				Log.w("RotationAsync", "onProgressUpdate() skipped -- no activity");
			}
		}
	}
	
	OnClickListener sortButtonsClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) 
		{
			switch (v.getId()) 
			{
			case R.id.sort_name:
				if (totalContacts>1) 
				{ 
					Collections.sort(contactData,new DataHolderCompareName()); 
					checked.clear();
					listView.clearChoices();
					listView.invalidateViews();
					listView.requestLayout();
				} 
				break;
			case R.id.sort_phone:
				if (totalContacts>1) 
				{ 
					Collections.sort(contactData,new DataHolderComparePhone()); 
					checked.clear();
					listView.clearChoices();
					listView.invalidateViews();
					listView.requestLayout();
				} 
				break;
			default:
				break;
			}
		}
	};
	
	@Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
		//checked = listView.getCheckedItemPositions(); 
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) 
	{
		// use an inflater to populate the Action Bar with items
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override public boolean onOptionsItemSelected(MenuItem item)
	{
		int dC,i=0;
		// same as using a normal menu
		if (firstExecution) // do nothing until contacts loaded!
		{
			dC=listView.getCount();
			switch(item.getItemId()) 
			{
				case R.id.item_about: 
					Intent iMA = new Intent(this, About.class);
					startActivity(iMA);
					listView.requestLayout();
					return true;
				case R.id.item_settings: 
					Intent iMS = new Intent(this, Settings.class);
					startActivity(iMS);
					listView.requestLayout();
					return true;
				default:
					return super.onOptionsItemSelected(item);
			}
		} else return true;
	}
	
	private void makeToast(String message,boolean length)
	{
		if (length) Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		else Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
}
