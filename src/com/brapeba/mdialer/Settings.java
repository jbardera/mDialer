/**
 * @author      Joanmi Bardera <joanmibb@gmail.com>
 * @version     1.2              
 * @since       2013-06-02          
 */

package com.brapeba.mdialer;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Settings extends Activity 
{
    @Override public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowTitleEnabled(false); // hiding the title
		actionBar.setDisplayHomeAsUpEnabled(true); // showing "navigate up"
		actionBar.show();
		LinearLayout rootLL = (LinearLayout) findViewById(R.id.Tsettings);
		TextView mTextView4 = new TextView(this);
		mTextView4.setText(getString(R.string.string13)+Environment.getExternalStorageDirectory().getAbsolutePath());
		rootLL.addView(mTextView4);
    }
}