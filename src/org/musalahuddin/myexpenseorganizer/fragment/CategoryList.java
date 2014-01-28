package org.musalahuddin.myexpenseorganizer.fragment;

import org.musalahuddin.myexpenseorganizer.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class CategoryList extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View v = inflater.inflate(R.layout.main, null, false);
	    //ExpandableListView lv = (ExpandableListView) v.findViewById(R.id.list);
	    
	    //lv.setEmptyView(v.findViewById(R.id.empty));
		return v;
	}

}
