/**
 * @author      Joanmi Bardera <joanmibb@gmail.com>
 * @version     1.0                
 * @since       2013-06-02          
 */

package com.brapeba.mdialer;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;

public class About extends Activity 
{
    @Override public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        ActionBar actionBar = getActionBar();
		//actionBar.setDisplayShowTitleEnabled(false); // hiding the title
		actionBar.setDisplayHomeAsUpEnabled(true); // showing "navigate up"
		actionBar.show();
    }
}